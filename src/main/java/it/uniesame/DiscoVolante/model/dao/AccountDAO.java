package it.uniesame.DiscoVolante.model.dao;

import it.uniesame.DiscoVolante.model.Account;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;

public class AccountDAO {

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

    //Verifica le credenziali usando EMAIL e PASSWORD
    public Account doRetrieveByEmailPassword(String email, String password) {
        String sql = "SELECT * FROM account WHERE email = ? AND password_hash = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account();
                    account.setId_account(rs.getInt("id_account"));
                    account.setNickname(rs.getString("nickname"));
                    account.setEmail(rs.getString("email"));
                    account.setPassword_hash(rs.getString("password_hash"));
                    account.setAdmin_flag(rs.getBoolean("admin_flag"));
                    return account;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Controlla se un'email è già presente nel DB.
    public Account doRetrieveByEmail(String email) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM account WHERE email = ?")) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Account account = new Account();
                account.setId_account(rs.getInt("id_account"));
                account.setEmail(rs.getString("email"));
                account.setNickname(rs.getString("nickname")); // Recupera nickname

                // CORREZIONE NOMI COLONNE
                account.setPassword_hash(rs.getString("password_hash"));
                account.setAdmin_flag(rs.getBoolean("admin_flag"));

                return account;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Salva un nuovo account cliente nel database.
    public void doSave(Account account) throws SQLException {
        String sql = "INSERT INTO account (nickname, email, password_hash, admin_flag) VALUES (?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, account.getNickname()); // Nickname è obbligatorio (NOT NULL)
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPassword_hash());
            ps.setBoolean(4, account.isAdminFlag());

            ps.executeUpdate();

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    //Aggiorna la password di un account
    public void doUpdatePassword(int idAccount,String vecchiaPassword, String nuovaPassword) throws SQLException {
        //Refactoring dovuto dal test TC 7.2
        if (nuovaPassword == null || nuovaPassword.length() < 8) {
            throw new RuntimeException("Errore: password non conforme alle polizze di sicurezza");
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE account SET password_hash = ? WHERE id_account = ? AND password_hash = ?")) {

            ps.setString(1, nuovaPassword);
            ps.setInt(2, idAccount);
            ps.setString(3, vecchiaPassword);

            int righeAggiornate = ps.executeUpdate();

            //Refactoring dovuti dal test TC 7.3
            if (righeAggiornate == 0) {
                //Se aggiorna 0 righe significa che la coppia
                throw new RuntimeException("Errore: password errata");
            }

        }
    }

    //Cancella un account dal database
    public void doDelete(int idAccount) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM account WHERE id_account = ?")) {

            ps.setInt(1, idAccount);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore cancellazione account: " + e.getMessage(), e);
        }
    }
}
