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

@WebServlet("/adminOrdini")
public class AdminOrdiniServlet extends HttpServlet {
    private OrdineDAO ordineDAO;

    //Costruttore Tomcat
    public AdminOrdiniServlet() {
        this.ordineDAO = new OrdineDAO();
    }

    //Costruttore Test (Dependency Injection)
    public AdminOrdiniServlet(OrdineDAO ordineDAO) {
        this.ordineDAO = ordineDAO;
    }

    //Mostra la lista di tutti gli ordini
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        //Controllo di sicurezza
        if (account == null || !account.isAdminFlag()) {
            response.sendRedirect("login.jsp");
            return;
        }

        List<Ordine> ordini = ordineDAO.doRetrieveAll();

        request.setAttribute("ordini", ordini);
        request.getRequestDispatcher("adminOrdini.jsp").forward(request, response);
    }

    //Cambia lo stato
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        //Controllo di sicurezza
        if (account == null || !account.isAdminFlag()) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idStr = request.getParameter("idOrdine");
        String nuovoStato = request.getParameter("nuovoStato");

        if (idStr != null && nuovoStato != null) {
            try {
                //Tenta l'aggiornamento e se va bene, ricarica la pagina pulita
                ordineDAO.doUpdateStato(Integer.parseInt(idStr), nuovoStato);
                response.sendRedirect("adminOrdini");

            } catch (RuntimeException e) {
                e.printStackTrace();
                request.setAttribute("errore", e.getMessage());
                doGet(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("adminOrdini");
            }
        } else {
            response.sendRedirect("adminOrdini");
        }
    }
}