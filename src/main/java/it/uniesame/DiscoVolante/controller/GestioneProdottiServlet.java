package it.uniesame.DiscoVolante.controller;

import it.uniesame.DiscoVolante.model.Account;
import it.uniesame.DiscoVolante.model.Prodotto;
import it.uniesame.DiscoVolante.model.dao.ProdottoDAO;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/gestioneProdotti")
public class GestioneProdottiServlet extends HttpServlet {
    private ProdottoDAO prodottoDAO;

    //Costruttore Tomcat
    public GestioneProdottiServlet() {
        this.prodottoDAO = new ProdottoDAO();
    }

    //Costruttore Test
    public GestioneProdottiServlet(ProdottoDAO prodottoDAO) {
        this.prodottoDAO = prodottoDAO;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");
        if (account == null || !account.isAdminFlag()) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("modifica".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                long id = Long.parseLong(idStr);
                Prodotto p = prodottoDAO.doRetrieveById(id);
                request.setAttribute("prodottoDaModificare", p);

                RequestDispatcher dispatcher = request.getRequestDispatcher("modificaProdotto.jsp");
                dispatcher.forward(request, response);
                return;
            }
        }

        List<Prodotto> prodotti = prodottoDAO.doRetrieveAllAdmin();
        request.setAttribute("prodotti", prodotti);

        RequestDispatcher dispatcher = request.getRequestDispatcher("gestioneProdotti.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        float prezzo = 0;
        int disponibilita = 0;

        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");
        if (account == null || !account.isAdminFlag()) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");

        //Refactoring dovuto dal test TC 10.1
        if ("rimuovi".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                try {
                    prodottoDAO.doDelete(Integer.parseInt(idStr));
                    response.sendRedirect("gestioneProdotti");
                } catch (RuntimeException e) {
                    //Catturiamo l'errore del DAO e usiamo doGet per ricaricare la tabella e mostrare l'errore
                    request.setAttribute("erroreAdmin", e.getMessage());
                    doGet(request, response);
                }
            } else {
                response.sendRedirect("gestioneProdotti");
            }
            return;
        }
        else if ("ripristina".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                prodottoDAO.doRestore(Integer.parseInt(idStr));
            }
            response.sendRedirect("gestioneProdotti");
            return;
        }

        // GESTIONE UPDATE / SAVE
        String idStr = request.getParameter("idProdotto");
        String titolo = request.getParameter("titolo");
        String artista = request.getParameter("artista");
        String prezzoStr = request.getParameter("prezzo");
        String dispStr = request.getParameter("disponibilita");
        String tipo = request.getParameter("tipo");

        String erroreValidazione = null;

        try {
            prezzo = Float.parseFloat(prezzoStr);
            disponibilita = Integer.parseInt(dispStr);
            if (prezzo < 1) {
                erroreValidazione = "Il prezzo deve essere positivo";
            }
            else if (disponibilita < 0) {
                erroreValidazione = "la quantità deve essere positiva";
            }
            else if (titolo == null || titolo.trim().isEmpty() || artista == null || artista.trim().isEmpty()) {
                erroreValidazione = "Compilare tutti i campi";
            }

        } catch (NumberFormatException e) {
            erroreValidazione = "Prezzo o Disponibilità non validi (inserire solo numeri).";
        }

        if (erroreValidazione != null) {
            request.setAttribute("erroreAdmin", erroreValidazione);
            doGet(request, response); // Richiama doGet per ricaricare la pagina
            return;
        }

        Prodotto p = new Prodotto();
        p.setTitolo(titolo);
        p.setArtista(artista);
        p.setTipo(tipo);
        p.setVisibile(true);
        p.setPrezzo(prezzo);
        p.setDisponibilità(disponibilita);

        if (idStr != null && !idStr.isEmpty()) {
            // CASO MODIFICA (UPDATE)
            p.setId_prodotto(Integer.parseInt(idStr));
            try {
                // UPDATE
                prodottoDAO.doUpdate(p);
            } catch (RuntimeException e) {
                //Refactoring dovuto dal test TC 4.2
                request.setAttribute("erroreAdmin", e.getMessage());
                doGet(request, response);
                return;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            //CASO INSERIMENTO (SAVE)
            try {
                prodottoDAO.doSave(p);
            } catch (RuntimeException e) {
                request.setAttribute("erroreAdmin", e.getMessage());
                doGet(request, response);
                return;
            }
        }

        response.sendRedirect("gestioneProdotti");
    }
}