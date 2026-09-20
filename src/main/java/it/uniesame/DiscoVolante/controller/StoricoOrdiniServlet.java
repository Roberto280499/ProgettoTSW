package it.uniesame.DiscoVolante.controller;

import it.uniesame.DiscoVolante.model.Account;
import it.uniesame.DiscoVolante.model.Ordine;
import it.uniesame.DiscoVolante.model.dao.OrdineDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/storicoOrdini")
public class StoricoOrdiniServlet extends HttpServlet {
    private OrdineDAO ordineDAO;

    //Costruttore Tomcat
    public StoricoOrdiniServlet() {
        this.ordineDAO = new OrdineDAO();
    }

    // ostruttore Test
    public StoricoOrdiniServlet(OrdineDAO ordineDAO) {
        this.ordineDAO = ordineDAO;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        //Stampa di controllo
        if (account != null) {
            System.out.println("Account in sessione: " + account.getNickname());
            System.out.println("ID Account in sessione: " + account.getId_account());
        }

        //Se non sei loggato, vai al login
        if (account == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        List<Ordine> ordini = ordineDAO.doRetrieveByAccount(account);

        request.setAttribute("ordini", ordini);
        request.getRequestDispatcher("storicoOrdini.jsp").forward(request, response);
    }
}