package it.uniesame.DiscoVolante.model.dao;

import it.uniesame.DiscoVolante.model.Prodotto;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ProdottoDAO {
    //Metodo per ottenere una connessione al Database dal Pool di Tomcat.
    public Connection getConnection() throws SQLException {
        try {
            //TENTATIVO 1: JNDI
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            DataSource ds = (DataSource) envCtx.lookup("jdbc/DiscoVolanteDB"); // <-- VERIFICA QUESTO NOME nel tuo context.xml
            return ds.getConnection();

        } catch (NamingException e) {
            //TENTATIVO 2: JDBC DIRETT
            try {
                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://localhost:5432/disco_volante";
                String user = "postgres";
                String password = "admin";
                return DriverManager.getConnection(url, user, password);

            } catch (ClassNotFoundException ex) {
                throw new SQLException("Driver Database non trovato: " + ex.getMessage());
            }
        }
    }

    //Recupera tutti i prodotti dal database
    public List<Prodotto> doRetrieveAll() {
        List<Prodotto> prodotti = new ArrayList<>();
        String sql = "SELECT * FROM prodotto WHERE visibile = true";

        // Chiude automaticamente la connessione alla fine, anche se ci sono errori.
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Scorriamo i risultati della query riga per riga
            while (rs.next()) {
                Prodotto p = new Prodotto();

                // Copiamo i dati dalla colonna SQL all'oggetto Java
                p.setId_prodotto((int) rs.getLong("id_prodotto"));
                p.setTitolo(rs.getString("titolo"));
                p.setArtista(rs.getString("artista"));
                p.setPrezzo(rs.getFloat("prezzo"));
                p.setDisponibilita(rs.getInt("disponibilita"));
                p.setTipo(rs.getString("tipo"));
                p.setVisibile(rs.getBoolean("visibile"));

                // Aggiungiamo il prodotto alla lista
                prodotti.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return prodotti;
    }

    //Esegue l'inserimento
    public void doSave(Prodotto p) {
        //Refactoring dovuto al test TC 9.2
        if (p.getTitolo() == null || p.getTitolo().trim().isEmpty() ||
                p.getTipo() == null || p.getTipo().trim().isEmpty()) {
            throw new RuntimeException("Errore: Compilare tutti i campi");
        }
        //Refactoring dovuto al test TC 9.3
        if (p.getPrezzo() <= 0) {
            throw new RuntimeException("Errore: Il prezzo deve essere positivo");
        }
        //Refactoring dovuto al test TC 9.4
        if (p.getDisponibilita() < 0) {
            throw new RuntimeException("Errore: la quantità deve essere positiva");
        }

        String sql = "INSERT INTO prodotto (titolo, artista, prezzo, disponibilita, tipo, visibile) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getTitolo());
            ps.setString(2, p.getArtista());
            ps.setFloat(3, p.getPrezzo());
            ps.setInt(4, p.getDisponibilita());
            ps.setString(5, p.getTipo());
            ps.setBoolean(6, p.isVisibile());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Esegue l'eliminazione di un prodotto
    public void doDelete(int idProdotto) {
        //Refactoring dovuto al test TC 10.1
        String sql = "UPDATE prodotto SET visibile = false WHERE id_prodotto = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProdotto);

            int righeCancellate = ps.executeUpdate();

            //Refactoring dovuto al test TC 10.2
            if (righeCancellate == 0) {
                throw new RuntimeException("Impossibile eliminare: prodotto non esistente");
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("foreign key") || e.getSQLState().equals("23503")) {
                throw new RuntimeException("Impossibile eliminare: il prodotto è presente in ordini passati");
            }
            throw new RuntimeException(e);
        }
    }

    //Cerca un prodotto specifico per ID
    public Prodotto doRetrieveById(long id) {
        String sql = "SELECT * FROM prodotto WHERE id_prodotto = ?";
        Prodotto p = null;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p = new Prodotto();
                    p.setId_prodotto(rs.getInt("id_prodotto"));
                    p.setTitolo(rs.getString("titolo"));
                    p.setArtista(rs.getString("artista"));
                    p.setPrezzo(rs.getFloat("prezzo"));
                    p.setDisponibilita(rs.getInt("disponibilita"));
                    p.setTipo(rs.getString("tipo"));
                    p.setVisibile(rs.getBoolean("visibile"));
                    return p;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //Aggiorna un prodotto esistente.
    public void doUpdate(Prodotto p) throws SQLException{
        String sql = "UPDATE prodotto SET titolo=?, artista=?, prezzo=?, disponibilita=?, tipo=? WHERE id_prodotto=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getTitolo());
            ps.setString(2, p.getArtista());
            ps.setFloat(3, p.getPrezzo());
            ps.setInt(4, p.getDisponibilita());
            ps.setString(5, p.getTipo());
            ps.setLong(6, p.getId_prodotto()); // Fondamentale: l'ID per la WHERE

            //Refactoring dovuto dal test TC 4.2
            int righeAggiornate = ps.executeUpdate();
            if (righeAggiornate == 0) {
                throw new RuntimeException("Errore: Prodotto non trovato (ID: " + p.getId_prodotto() + ")");
            }
        }
    }

    public List<Prodotto> doRetrieveByNomeOrArtista(String testo) {
        List<Prodotto> prodotti = new java.util.ArrayList<>();

        //Cerco dove il titolo o l'artista contengono la stringa
        String sql = "SELECT * FROM prodotto WHERE LOWER(titolo) LIKE ? OR LOWER(artista) LIKE ?";

        try (java.sql.Connection con = getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            //Aggiungo i caratteri jolly prima e dopo per cercare
            String likeString = "%" + testo.toLowerCase() + "%";

            ps.setString(1, likeString);
            ps.setString(2, likeString);

            java.sql.ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Prodotto p = new Prodotto();
                p.setId_prodotto(rs.getInt("id_prodotto"));
                p.setTitolo(rs.getString("titolo"));
                p.setArtista(rs.getString("artista"));
                p.setPrezzo(rs.getFloat("prezzo"));
                p.setDisponibilita(rs.getInt("disponibilita"));
                p.setTipo(rs.getString("tipo"));
                prodotti.add(p);
            }
            return prodotti;

        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Metodo per l'Admin perchè lui vede TUTTO
    public List<Prodotto> doRetrieveAllAdmin() {
        List<Prodotto> prodotti = new ArrayList<>();
        // Nota: Tolgo la clausola WHERE per prendere tutto
        String sql = "SELECT * FROM prodotto ORDER BY id_prodotto ASC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Prodotto p = new Prodotto();
                p.setId_prodotto(rs.getInt("id_prodotto"));
                p.setTitolo(rs.getString("titolo"));
                p.setArtista(rs.getString("artista"));
                p.setPrezzo(rs.getFloat("prezzo"));
                p.setDisponibilita(rs.getInt("disponibilita"));
                p.setTipo(rs.getString("tipo"));
                p.setVisibile(rs.getBoolean("visibile")); // Importante!
                prodotti.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return prodotti;
    }

    //Metodo per Ripristinare la visibilità
    public void doRestore(int idProdotto) {
        String sql = "UPDATE prodotto SET visibile = true WHERE id_prodotto = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProdotto);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}