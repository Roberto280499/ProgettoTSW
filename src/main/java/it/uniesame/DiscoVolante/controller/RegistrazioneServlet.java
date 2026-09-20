
package it.uniesame.DiscoVolante.controller;

import it.uniesame.DiscoVolante.model.Account;
import it.uniesame.DiscoVolante.model.dao.AccountDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/registrazione")
public class RegistrazioneServlet extends HttpServlet {
    private AccountDAO accountDAO;

    //Costruttore vuoto per Tomcat
    public RegistrazioneServlet() {
        this.accountDAO = new AccountDAO();
    }

    //Costruttore per i TEST
    public RegistrazioneServlet(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nickname = request.getParameter("nickname");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confPassword = request.getParameter("confPassword");

        String errore = null;

        if (password == null || password.length() < 8) {
            errore = "La password deve contenere almeno 8 caratteri.";
        }
        else if (!password.equals(confPassword)) {
            errore = "Le password non coincidono.";
        }
        else if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errore = "Formato email non corretto.";
        }
        else if (accountDAO.doRetrieveByEmail(email) != null) {
            errore = "Email già registrata.";
        }
        else if (nickname == null || nickname.trim().isEmpty()) {
            errore = "Il nickname è obbligatorio.";
        }

        if (errore != null) {
            request.setAttribute("errore", errore);
            RequestDispatcher dispatcher = request.getRequestDispatcher("registrazione.jsp");
            dispatcher.forward(request, response);
            return;
        }

        try {
            //Refactoring dovuto al Integration-Test TC 1.3 e TC 1.5
            if (accountDAO.doRetrieveByEmail(email) != null) {
                request.setAttribute("errore", "Email già registrata.");
                request.getRequestDispatcher("registrazione.jsp").forward(request, response);
                return;
            }

            // Salvataggio
            Account account = new Account();
            account.setNickname(nickname);
            account.setEmail(email);
            account.setPassword_hash(password);
            account.setAdmin_flag(false);
            accountDAO.doSave(account);

            //Login automatico dopo registrazione
            HttpSession session = request.getSession();
            session.setAttribute("account", account);
            response.sendRedirect("index.jsp");

        } catch (SQLException e) {
            //Catturiamo l'eccezione SQL
            e.printStackTrace();
            if (e.getMessage().contains("unique") || e.getMessage().contains("duplicate")) {
                request.setAttribute("errore", "Username o Email già presenti (Errore DB).");
            } else {
                request.setAttribute("errore", "Errore durante la registrazione nel database.");
            }
            request.getRequestDispatcher("registrazione.jsp").forward(request, response);
        }
    }
}