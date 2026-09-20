package it.uniesame.DiscoVolante.controller;

import it.uniesame.DiscoVolante.model.Prodotto;
import it.uniesame.DiscoVolante.model.dao.ProdottoDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

// Questa annotazione mappa la servlet all'URL /testDB
//URL = http://localhost:8080/DiscoVolante/testDB
@WebServlet("/testDB")
public class TestDatabaseServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Impostiamo il tipo di risposta come HTML
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            out.println("<html><body>");
            out.println("<h1>Test Connessione Database</h1>");
            out.println("<h3>Tentativo di lettura prodotti...</h3>");

            // 1. Creiamo il DAO
            ProdottoDAO dao = new ProdottoDAO();

            // 2. Chiamiamo il metodo che accede al DB
            List<Prodotto> prodotti = dao.doRetrieveAll();

            // 3. Verifichiamo cosa abbiamo trovato
            if (prodotti.isEmpty()) {
                out.println("<p>Connessione OK, ma non ci sono prodotti nel DB!</p>");
            } else {
                out.println("<p style='color:green'>Connessione RIUSCITA! Ecco i prodotti trovati:</p>");
                out.println("<ul>");
                for (Prodotto p : prodotti) {
                    out.println("<li>" + p.getId_prodotto() + ": <b>" + p.getTitolo() + "</b> - " + p.getArtista() + " (" + p.getTipo() + ")</li>");
                }
                out.println("</ul>");
            }
            out.println("</body></html>");

        } catch (Exception e) {
            // Se qualcosa va storto, stampiamo l'errore sulla pagina
            out.println("<h2 style='color:red'>ERRORE CRITICO</h2>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
    }
}