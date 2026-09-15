package it.uniesame.DiscoVolante.controller;

import it.uniesame.DiscoVolante.model.Account;
import it.uniesame.DiscoVolante.model.dao.AccountDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/cambioPassword")
public class CambioPasswordServlet extends HttpServlet {
    private AccountDAO accountDAO;

    // Costruttore Tomcat
    public CambioPasswordServlet() {
        this.accountDAO = new AccountDAO();
    }

    // Costruttore Test
    public CambioPasswordServlet(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        //Controllo robustezza se l'attributo in sessione non è castabile o è null
        Object sessionObj = session.getAttribute("account");
        if (!(sessionObj instanceof Account)) {
            response.sendRedirect("login.jsp");
            return;
        }

        Account account = (Account) sessionObj;

        String vecchiaPass = request.getParameter("vecchiaPass");
        String nuovaPass = request.getParameter("nuovaPass");

        //Refactoring dato dal test TC 7.3
        if (!account.getPassword_hash().equals(vecchiaPass)) {
            request.setAttribute("errore", "Errore: password errata.");
            request.getRequestDispatcher("profilo.jsp").forward(request, response);
            return;
        }

        //Refactoring dato dal test TC 7.2
        if (nuovaPass == null || nuovaPass.length() < 8) {
            request.setAttribute("errore", "Errore: password non conforme alle polizze di sicurezza (minimo 8 caratteri).");
            request.getRequestDispatcher("profilo.jsp").forward(request, response);
            return;
        }

        // Controllo Extra: Nuova uguale alla vecchia (Opzionale ma utile)
        if (vecchiaPass.equals(nuovaPass)) {
            request.setAttribute("errore", "La nuova password non può essere uguale a quella attuale.");
            request.getRequestDispatcher("profilo.jsp").forward(request, response);
            return;
        }

        //Refactoring dato dal test TC 7.1
        try {
            accountDAO.doUpdatePassword(account.getId_account(), account.getPassword_hash(), nuovaPass);
            account.setPassword_hash(nuovaPass);
            session.setAttribute("account", account); // Opzionale, ma buona prassi
            request.setAttribute("successo", "Password aggiornata con successo!");
            request.getRequestDispatcher("profilo.jsp").forward(request, response);
        } catch(SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore Database: impossibile salvare la password.");
            request.getRequestDispatcher("profilo.jsp").forward(request, response);
        }
    }
}
