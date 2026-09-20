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

@WebServlet("/cancellaAccount")
public class CancellazioneAccountServlet extends HttpServlet {
    private AccountDAO accountDAO;

    //Costruttore Tomcat
    public CancellazioneAccountServlet() {
        this.accountDAO = new AccountDAO();
    }

    //Costruttore Test
    public CancellazioneAccountServlet(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            //Cancello dal DB usando il DAO
            accountDAO.doDelete(account.getId_account());

            //Distruggo la sessione (Logout forzato)
            session.invalidate();

            //Rindirizzamento alla home/login
            response.sendRedirect("login.jsp?msg=AccountCancellato");

        } catch (Exception e) {
            //Se fallisce avviso l'utente
            e.printStackTrace();
            request.setAttribute("errore", "Impossibile cancellare l'account: ci sono ordini attivi.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("profilo.jsp");
            dispatcher.forward(request, response);
        }
    }
}