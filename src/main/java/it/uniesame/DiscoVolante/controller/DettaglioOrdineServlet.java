package it.uniesame.DiscoVolante.controller;

import it.uniesame.DiscoVolante.model.Account;
import it.uniesame.DiscoVolante.model.DettaglioOrdine;
import it.uniesame.DiscoVolante.model.Indirizzo;
import it.uniesame.DiscoVolante.model.Pagamento;
import it.uniesame.DiscoVolante.model.dao.OrdineDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/dettaglioOrdine")
public class DettaglioOrdineServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        //Recupero l'ID dell'ordine dalla query string
        String idStr = request.getParameter("id");
        if(idStr == null || idStr.isEmpty()) {
            response.sendRedirect("storicoOrdini");
            return;
        }

        int idOrdine = Integer.parseInt(idStr);
        int idOrdineControllo = Integer.parseInt(request.getParameter("id"));

        OrdineDAO dao = new OrdineDAO();

        Indirizzo indirizzo = dao.doRetrieveIndirizzoByOrdine(idOrdineControllo);
        Pagamento pagamento = dao.doRetrievePagamentoByOrdine(idOrdineControllo);
        String carta = dao.doRetrieveNumeroCartaByOrdine(idOrdineControllo);

        request.setAttribute("indirizzo", indirizzo);
        request.setAttribute("pagamento", pagamento);
        request.setAttribute("cartaMascherata", carta);
        request.setAttribute("idOrdine", idOrdineControllo);

        //Recupero la lista dei prodotti
        List<DettaglioOrdine> dettagli = dao.doRetrieveDettagli(idOrdine);

        //Passo i dati alla JSP
        request.setAttribute("dettagli", dettagli);
        request.setAttribute("idOrdine", idOrdine);
        request.getRequestDispatcher("dettaglioOrdine.jsp").forward(request, response);
    }
}