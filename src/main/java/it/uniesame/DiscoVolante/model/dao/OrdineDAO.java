package it.uniesame.DiscoVolante.model.dao;

import it.uniesame.DiscoVolante.model.*;

import java.util.Arrays;
import java.util.List;
import java.sql.*;
import java.util.Map;

// Estendo AccountDAO per ereditare getConnection()
public class OrdineDAO extends AccountDAO {

    // Lista degli stati ammessi
    private static final List<String> STATI_VALIDI = Arrays.asList("CREATO", "PAGATO", "SPEDITO", "CONSEGNATO", "ANNULLATO");

    public void doSave(Account utente, Carrello carrello, String via, String citta, String cap, String civico, String numeroCarta) {
        // Refactoring dovuto dal test TC 3.4
        if (carrello == null || carrello.getProdotti().isEmpty()) {
            throw new RuntimeException("Impossibile salvare un ordine con carrello vuoto!");
        }

        // Sanitizzazione: rimozione di spazi e trattini
        if (numeroCarta != null) {
            numeroCarta = numeroCarta.replaceAll("\\s+", "").replaceAll("-", "").trim();
        }

        // Refactoring dovuto dal test TC 3.6 + validazione cifre
        if (numeroCarta == null || numeroCarta.length() != 16 || !numeroCarta.matches("\\d{16}")) {
            throw new RuntimeException("Errore Pagamento: Numero carta non valido (richieste 16 cifre)");
        }

        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);

            // 1. SALVATAGGIO INDIRIZZO
            String sqlIndirizzo = "INSERT INTO indirizzo (via, citta, cap, paese, civico, fk_cliente) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement psIndirizzo = con.prepareStatement(sqlIndirizzo, Statement.RETURN_GENERATED_KEYS);
            psIndirizzo.setString(1, via);
            psIndirizzo.setString(2, citta);
            psIndirizzo.setString(3, cap);
            psIndirizzo.setString(4, "Italia");
            psIndirizzo.setString(5, civico);
            psIndirizzo.setObject(6, null); // Per ora slegato dal cliente per evitare duplicati unici

            psIndirizzo.executeUpdate();
            ResultSet rsInd = psIndirizzo.getGeneratedKeys();
            int idIndirizzo = 0;
            if (rsInd.next()) idIndirizzo = rsInd.getInt(1);
            psIndirizzo.close();

            // 2. SALVATAGGIO CARTA DI CREDITO
            String circuito = "Generico";
            if (numeroCarta.startsWith("4")) circuito = "Visa";
            else if (numeroCarta.startsWith("5")) circuito = "Mastercard";

            String sqlCarta = "INSERT INTO carta_credito (numero_carta, circuito) VALUES (?, ?)";
            PreparedStatement psCarta = con.prepareStatement(sqlCarta, Statement.RETURN_GENERATED_KEYS);
            psCarta.setString(1, numeroCarta);
            psCarta.setString(2, circuito);

            psCarta.executeUpdate();
            ResultSet rsCarta = psCarta.getGeneratedKeys();
            int idCarta = 0;
            if (rsCarta.next()) idCarta = rsCarta.getInt(1);
            psCarta.close();

            // 3. SALVATAGGIO ORDINE
            int idCliente = getOrCreateCliente(con, utente);

            String sqlOrdine = "INSERT INTO ordine (fk_cliente, fk_indirizzo, totale, stato) VALUES (?, ?, ?, ?)";
            PreparedStatement psOrdine = con.prepareStatement(sqlOrdine, Statement.RETURN_GENERATED_KEYS);
            psOrdine.setInt(1, idCliente);
            psOrdine.setInt(2, idIndirizzo);
            psOrdine.setFloat(3, carrello.getTotale());
            psOrdine.setString(4, "PAGATO"); // Impostiamo subito PAGATO visto che creiamo il pagamento

            psOrdine.executeUpdate();
            ResultSet rsOrd = psOrdine.getGeneratedKeys();
            int idOrdine = 0;
            if (rsOrd.next()) idOrdine = rsOrd.getInt(1);
            psOrdine.close();

            // 4. SALVATAGGIO PAGAMENTO
            String sqlPagamento = "INSERT INTO pagamento (fk_ordine, fk_carta_credito, importo, stato, data_pagamento) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
            PreparedStatement psPagamento = con.prepareStatement(sqlPagamento);
            psPagamento.setInt(1, idOrdine);
            psPagamento.setInt(2, idCarta);
            psPagamento.setFloat(3, carrello.getTotale());
            psPagamento.setString(4, "CONFERMATO");

            psPagamento.executeUpdate();
            psPagamento.close();

            // 5. SALVATAGGIO DETTAGLI E STOCK
            String sqlDettaglio = "INSERT INTO dettaglio_ordine (fk_ordine, fk_prodotto, quantita, prezzo_unit) VALUES (?, ?, ?, ?)";
            String sqlUpdateStock = "UPDATE prodotto SET disponibilita = disponibilita - ? WHERE id_prodotto = ?";

            PreparedStatement psDettaglio = con.prepareStatement(sqlDettaglio);
            PreparedStatement psStock = con.prepareStatement(sqlUpdateStock);

            for (Map.Entry<Prodotto, Integer> entry : carrello.getProdotti().entrySet()) {
                Prodotto p = entry.getKey();
                int qta = entry.getValue();

                // Dettaglio
                psDettaglio.setInt(1, idOrdine);
                psDettaglio.setInt(2, p.getId_prodotto());
                psDettaglio.setInt(3, qta);
                psDettaglio.setFloat(4, p.getPrezzo());
                psDettaglio.executeUpdate();

                // Stock
                psStock.setInt(1, qta);
                psStock.setInt(2, p.getId_prodotto());
                psStock.executeUpdate();
            }

            // Se siamo arrivati qui senza errori, confermiamo tutto!
            con.commit();

        } catch (SQLException e) {
            // Se qualcosa va storto, annulliamo tutto facendo il Rollback
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Errore salvataggio ordine: " + e.getMessage(), e);
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo helper per gestire la tabella 'cliente' che fa da ponte tra 'account' e 'ordine'
    private int getOrCreateCliente(Connection con, Account account) throws SQLException {
        // Cerco se esiste già un cliente per questo account
        String checkSql = "SELECT id_cliente FROM cliente WHERE fk_account = ?";
        try (PreparedStatement ps = con.prepareStatement(checkSql)) {
            ps.setInt(1, account.getId_account());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_cliente");
            }
        }

        // Se non esiste, lo creo al volo (usando dati dummy o presi dall'account)
        String sqlCarta = "INSERT INTO carta_credito (numero_carta, circuito) VALUES ('0000000000000000', 'DEFAULT')";
        int idCarta = 0;
        try (PreparedStatement psCarta = con.prepareStatement(sqlCarta, Statement.RETURN_GENERATED_KEYS)) {
            psCarta.executeUpdate();
            ResultSet rsCarta = psCarta.getGeneratedKeys();
            if (rsCarta.next()) idCarta = rsCarta.getInt(1);
        }

        String insertSql = "INSERT INTO cliente (nome, cognome, fk_account, fk_carta_credito) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getNickname());
            ps.setString(2, "Utente");
            ps.setInt(3, account.getId_account());
            ps.setInt(4, idCarta);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Impossibile creare cliente");
    }

    public List<Ordine> doRetrieveByAccount(Account account) {
        List<Ordine> ordini = new java.util.ArrayList<>();

        String sql = "SELECT o.* FROM ordine o " +
                "JOIN cliente c ON o.fk_cliente = c.id_cliente " +
                "WHERE c.fk_account = ? " +
                "ORDER BY o.data_ordine DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, account.getId_account());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Ordine o = new Ordine();
                o.setId_ordine(rs.getInt("id_ordine"));
                o.setCosto_totale(rs.getFloat("totale"));
                o.setData_ordine(rs.getDate("data_ordine"));
                o.setStato(rs.getString("stato"));
                ordini.add(o);
            }
            return ordini;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DettaglioOrdine> doRetrieveDettagli(int idOrdine) {
        List<DettaglioOrdine> dettagli = new java.util.ArrayList<>();

        String sql = "SELECT d.quantita, d.prezzo_unit, p.id_prodotto, p.titolo, p.artista, p.tipo " +
                "FROM dettaglio_ordine d " +
                "JOIN prodotto p ON d.fk_prodotto = p.id_prodotto " +
                "WHERE d.fk_ordine = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idOrdine);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DettaglioOrdine d = new DettaglioOrdine();
                d.setId_ordine_associato(idOrdine);
                d.setQuantita(rs.getInt("quantita"));
                d.setPrezzo_intero(rs.getFloat("prezzo_unit"));

                Prodotto p = new Prodotto();
                p.setId_prodotto(rs.getInt("id_prodotto"));
                p.setTitolo(rs.getString("titolo"));
                p.setArtista(rs.getString("artista"));
                p.setTipo(rs.getString("tipo"));

                d.setProdotto(p);

                dettagli.add(d);
            }
            return dettagli;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Recupera tutti gli ordini di tutti i clienti(ADMIN)
    public List<Ordine> doRetrieveAll() {
        List<Ordine> ordini = new java.util.ArrayList<>();

        // Seleziono l'ordine e i dati dell'account collegato
        String sql = "SELECT o.*, a.id_account, a.email, a.nickname " +
                "FROM ordine o " +
                "JOIN cliente c ON o.fk_cliente = c.id_cliente " +
                "JOIN account a ON c.fk_account = a.id_account " +
                "ORDER BY o.data_ordine DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Ordine o = new Ordine();
                o.setId_ordine(rs.getInt("id_ordine"));
                o.setCosto_totale(rs.getFloat("totale"));
                o.setData_ordine(rs.getDate("data_ordine"));
                o.setStato(rs.getString("stato"));

                // Popolo l'oggetto Account dentro l'ordine per visualizzare l'email dell'utente
                Account utente = new Account();
                utente.setId_account(rs.getInt("id_account"));
                utente.setEmail(rs.getString("email"));
                utente.setNickname(rs.getString("nickname"));
                o.setUtente(utente);

                ordini.add(o);
            }
            return ordini;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Aggiorna lo stato di un ordine(ADMIN)
    public void doUpdateStato(int idOrdine, String nuovoStato) {
        // Refactoring dovuta al test 6.3
        if (!STATI_VALIDI.contains(nuovoStato)) {
            throw new RuntimeException("Stato non valido o transizione negata: " + nuovoStato);
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE ordine SET stato = ? WHERE id_ordine = ?")) {

            ps.setString(1, nuovoStato);
            ps.setInt(2, idOrdine);

            int righeAggiornate = ps.executeUpdate();
            // Refactoring dovuta al test TC 6.2
            if (righeAggiornate == 0) {
                throw new RuntimeException("Ordine non trovato: ID " + idOrdine);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Ricavo l'indirizzo di spedizione di un determinato ordine
    public Indirizzo doRetrieveIndirizzoByOrdine(int idOrdine) {
        String sql = "SELECT i.* FROM indirizzo i " +
                "JOIN ordine o ON o.fk_indirizzo = i.id_indirizzo " +
                "WHERE o.id_ordine = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idOrdine);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Indirizzo ind = new Indirizzo();
                ind.setId_indirizzo(rs.getInt("id_indirizzo"));
                ind.setVia(rs.getString("via"));
                ind.setCitta(rs.getString("citta"));
                ind.setCap(rs.getString("cap"));
                ind.setCivico(rs.getString("civico"));
                ind.setPaese(rs.getString("paese"));
                return ind;
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Ricavo le informazioni della carta usata nel checkout
    public String doRetrieveNumeroCartaByOrdine(int idOrdine) {
        String sql = "SELECT c.numero_carta FROM carta_credito c " +
                "JOIN pagamento p ON p.fk_carta_credito = c.id_carta " +
                "WHERE p.fk_ordine = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idOrdine);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String numero = rs.getString("numero_carta");

                // Maschero la carta mostro solo gli ultimi 4 numeri
                if (numero != null && numero.length() > 4) {
                    return "**** **** **** " + numero.substring(numero.length() - 4);
                }
                return "Carta ****";
            }
            return "N/D";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Info non disponibili";
        }
    }

    public Pagamento doRetrievePagamentoByOrdine(int idOrdine) {
        String sql = "SELECT * FROM pagamento WHERE fk_ordine = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idOrdine);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Pagamento p = new Pagamento();
                p.setId_pagamento(rs.getInt("id_pagamento"));
                p.setImporto(rs.getFloat("importo"));
                p.setData_pagamento(rs.getTimestamp("data_pagamento"));
                p.setStato(rs.getString("stato"));
                p.setId_ordine_associato(rs.getInt("fk_ordine"));
                p.setId_carta_credito_usata(rs.getInt("fk_carta_credito"));
                return p;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}