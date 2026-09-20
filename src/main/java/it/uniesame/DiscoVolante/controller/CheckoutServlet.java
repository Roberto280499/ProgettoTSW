package it.uniesame.DiscoVolante.controller;

import it.uniesame.DiscoVolante.model.Account;
import it.uniesame.DiscoVolante.model.Carrello;
import it.uniesame.DiscoVolante.model.dao.OrdineDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import it.uniesame.DiscoVolante.model.Prodotto;
import it.uniesame.DiscoVolante.model.dao.ProdottoDAO;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private ProdottoDAO prodottoDAO;
    private OrdineDAO ordineDAO;

    //Costruttore Tomcat
    public CheckoutServlet() {
        this.prodottoDAO = new ProdottoDAO();
        this.ordineDAO = new OrdineDAO();
    }

    // Costruttore Test (Dependency Injection Doppia)
    public CheckoutServlet(ProdottoDAO prodottoDAO, OrdineDAO ordineDAO) {
        this.prodottoDAO = prodottoDAO;
        this.ordineDAO = ordineDAO;
    }

    //Mostra la pagina di checkout solo se loggato e carrello pieno
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");
        Carrello carrello = (Carrello) session.getAttribute("carrello");

        if (account == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        if (carrello == null || carrello.getNumeroArticoli() == 0) {
            response.sendRedirect("carrello.jsp");
            return;
        }

        for (Prodotto pCarrello : carrello.getProdotti().keySet()) {
            Prodotto pDb = prodottoDAO.doRetrieveById(pCarrello.getId_prodotto());
            if (pDb == null || !pDb.isVisibile() || carrello.getProdotti().get(pCarrello) > pDb.getDisponibilità()) {
                response.sendRedirect("carrello");
                return;
            }
        }

        request.getRequestDispatcher("checkout.jsp").forward(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");
        Carrello carrello = (Carrello) session.getAttribute("carrello");

        if (account == null || carrello == null || carrello.getProdotti().isEmpty()) {
            response.sendRedirect("index.jsp");
            return;
        }

        //Controllo disponibilità finale
        for (Map.Entry<Prodotto, Integer> entry : carrello.getProdotti().entrySet()) {
            Prodotto pNelCarrello = entry.getKey();
            int quantitaRichiesta = entry.getValue();

            Prodotto pDalDB = prodottoDAO.doRetrieveById(pNelCarrello.getId_prodotto());

            if (pDalDB == null || !pDalDB.isVisibile() || quantitaRichiesta > pDalDB.getDisponibilità()) {
                String msg = "Siamo spiacenti, il prodotto '" + pNelCarrello.getTitolo() + "' non è più disponibile.";
                request.setAttribute("errore", msg);
                request.getRequestDispatcher("checkout.jsp").forward(request, response);
                return;
            }
        }

        //Recupero Dati
        String via = request.getParameter("via");
        String civico = request.getParameter("civico");
        String citta = request.getParameter("citta");
        String cap = request.getParameter("cap");
        String numeroCarta = request.getParameter("numeroCarta");

        String erroreValidazione = null;

        if (cap == null || !cap.matches("\\d{5}")) {
            erroreValidazione = "Il CAP deve contenere esattamente 5 numeri.";
        } else if (numeroCarta == null || numeroCarta.replaceAll("\\s+", "").length() < 13 || !numeroCarta.replaceAll("\\s+", "").matches("\\d+")) {
            erroreValidazione = "Numero carta di credito non valido.";
        } else if (via == null || via.trim().isEmpty() || civico == null || civico.trim().isEmpty() || citta == null || citta.trim().isEmpty()) {
            erroreValidazione = "Tutti i campi dell'indirizzo sono obbligatori.";
        }

        if (erroreValidazione != null) {
            request.setAttribute("errore", erroreValidazione);
            request.getRequestDispatcher("checkout.jsp").forward(request, response);
            return;
        }

        //Salvataggio Ordine usando ordineDAO
        try {
            ordineDAO.doSave(account, carrello, via, citta, cap, civico, numeroCarta);
            session.removeAttribute("carrello");
            response.sendRedirect("conferma.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore durante il pagamento: " + e.getMessage());
            request.getRequestDispatcher("checkout.jsp").forward(request, response);
        }
    }
}