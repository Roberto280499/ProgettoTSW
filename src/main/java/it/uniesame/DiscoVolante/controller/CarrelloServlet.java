package it.uniesame.DiscoVolante.controller;

import it.uniesame.DiscoVolante.model.Carrello;
import it.uniesame.DiscoVolante.model.Prodotto;
import it.uniesame.DiscoVolante.model.dao.ProdottoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/carrello")
public class CarrelloServlet extends HttpServlet {
    private ProdottoDAO prodottoDAO;

    // Costruttore Tomcat
    public CarrelloServlet() {
        this.prodottoDAO = new ProdottoDAO();
    }

    //Costruttore Test
    public CarrelloServlet(ProdottoDAO prodottoDAO) {
        this.prodottoDAO = prodottoDAO;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int quantitaNelCarrello = 0;

        Carrello carrello = (Carrello) session.getAttribute("carrello");
        if (carrello == null) {
            carrello = new Carrello();
            session.setAttribute("carrello", carrello);
        }

        String action = request.getParameter("action");
        String idStr = request.getParameter("id_prodotto");
        String provenienza = request.getParameter("provenienza");

        if (idStr != null && !idStr.isEmpty()) {
            long id = Long.parseLong(idStr);
            Prodotto p = prodottoDAO.doRetrieveById((int)id);

            if (p != null) {
                if ("aggiungi".equals(action)) {
                    if (carrello.getProdotti().containsKey(p)) {
                        quantitaNelCarrello = carrello.getProdotti().get(p);
                    }

                    if (quantitaNelCarrello + 1 > p.getDisponibilità()) {
                        String messaggioErrore = "Non puoi aggiungere altri '" + p.getTitolo() + "'. Disponibilità massima: " + p.getDisponibilità();
                        session.setAttribute("erroreCarrello", messaggioErrore);
                    } else {
                        carrello.aggiungiProdotto(p);
                    }
                } else if ("rimuovi".equals(action)) {
                    carrello.rimuoviProdotto(p);
                } else if ("rimuoviTutto".equals(action)) {
                    carrello.rimuoviTutto(p);
                }
            }
        }

        if ("carrello".equals(provenienza)) {
            response.sendRedirect("carrello.jsp");
        } else {
            response.sendRedirect("index.jsp");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Carrello carrello = (Carrello) session.getAttribute("carrello");

        if (carrello != null && !carrello.getProdotti().isEmpty()) {
            List<Prodotto> prodottiDaRimuovere = new ArrayList<>();

            for (Prodotto pInCarrello : carrello.getProdotti().keySet()) {
                Prodotto pAggiornato = prodottoDAO.doRetrieveById(pInCarrello.getId_prodotto());

                if (pAggiornato == null || !pAggiornato.isVisibile()) {
                    prodottiDaRimuovere.add(pInCarrello);
                } else {
                    pInCarrello.setDisponibilità(pAggiornato.getDisponibilità());
                    pInCarrello.setPrezzo(pAggiornato.getPrezzo());
                    pInCarrello.setVisibile(pAggiornato.isVisibile());
                }
            }
            for (Prodotto pDaRimuovere : prodottiDaRimuovere) {
                carrello.rimuoviTutto(pDaRimuovere);
            }
            if (!prodottiDaRimuovere.isEmpty()) {
                request.setAttribute("warning", "Alcuni prodotti sono stati rimossi dal carrello perché non più disponibili.");
            }
        }

        request.getRequestDispatcher("carrello.jsp").forward(request, response);
    }
}