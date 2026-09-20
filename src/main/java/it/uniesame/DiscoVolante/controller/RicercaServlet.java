package it.uniesame.DiscoVolante.controller;

import it.uniesame.DiscoVolante.model.Prodotto;
import it.uniesame.DiscoVolante.model.dao.ProdottoDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/ricerca")
public class RicercaServlet extends HttpServlet {

    //Usiamo doGet perché le ricerche devono poter essere salvate nei preferiti o condivise via URL
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("q");

        ProdottoDAO dao = new ProdottoDAO();
        List<Prodotto> prodotti;

        //Se la ricerca è vuota, mostro tutto altrimenti filtro
        if (query == null || query.trim().isEmpty()) {
            prodotti = dao.doRetrieveAll();
        } else {
            prodotti = dao.doRetrieveByNomeOrArtista(query);
        }

        request.setAttribute("prodotti", prodotti);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}