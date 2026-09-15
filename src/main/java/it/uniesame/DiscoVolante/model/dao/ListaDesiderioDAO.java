package it.uniesame.DiscoVolante.model.dao;

import it.uniesame.DiscoVolante.model.Prodotto;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//Estendo per usare getConnection()
public class ListaDesiderioDAO extends AccountDAO {

    //Recupera tutti i prodotti nella lista desideri di un utente
    public List<Prodotto> doRetrieveByAccount(int idAccount) {
        List<Prodotto> prodotti = new ArrayList<>();
        String sql = "SELECT p.* FROM lista_desiderio l " +
                "JOIN prodotto p ON l.fk_prodotto = p.id_prodotto " +
                "JOIN cliente c ON l.fk_cliente = c.id_cliente " +
                "WHERE c.fk_account = ? AND p.visibile = true";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAccount);
            ResultSet rs = ps.executeQuery();

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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return prodotti;
    }

    //Aggiunge un prodotto alla lista desideri
    public boolean addProdotto(int idAccount, int idProdotto) {
        String sql = "INSERT INTO lista_desiderio (fk_cliente, fk_prodotto) VALUES (?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int idCliente = getClienteId(con, idAccount);
            ps.setInt(1, idCliente);
            ps.setInt(2, idProdotto);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().equals("23505")) {
                return false;
            }
            throw new RuntimeException(e);
        }
    }

    //Rimuove un prodotto dalla lista desideri
    public void removeProdotto(int idAccount, int idProdotto) {
        String sql = "DELETE FROM lista_desiderio WHERE fk_cliente = ? AND fk_prodotto = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int idCliente = getClienteId(con, idAccount);
            ps.setInt(1, idCliente);
            ps.setInt(2, idProdotto);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Metodo di aiuto per trovare l'ID Cliente dato l'ID Account
    private int getClienteId(Connection con, int idAccount) throws SQLException {
        String sql = "SELECT id_cliente FROM cliente WHERE fk_account = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAccount);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id_cliente");
        }
        throw new SQLException("Cliente non trovato per Account " + idAccount);
    }
}