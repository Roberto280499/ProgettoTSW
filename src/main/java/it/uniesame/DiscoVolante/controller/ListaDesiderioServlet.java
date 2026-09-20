package it.uniesame.DiscoVolante.controller;

import it.uniesame.DiscoVolante.model.Account;
import it.uniesame.DiscoVolante.model.Prodotto;
import it.uniesame.DiscoVolante.model.dao.ListaDesiderioDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/listaDesiderio")
public class ListaDesiderioServlet extends HttpServlet {
    private ListaDesiderioDAO listaDesiderioDAO;

    //Costruttore Tomcat
    public ListaDesiderioServlet() {
        this.listaDesiderioDAO = new ListaDesiderioDAO();
    }

    //Costruttore Test
    public ListaDesiderioServlet(ListaDesiderioDAO listaDesiderioDAO) {
        this.listaDesiderioDAO = listaDesiderioDAO;
    }

    //Mostra la pagina della lista desideri
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            response.sendRedirect("login.jsp");
            return;
        }


        List<Prodotto> listaDesiderio = listaDesiderioDAO.doRetrieveByAccount(account.getId_account());

        request.setAttribute("listaDesiderio", listaDesiderio);
        request.getRequestDispatcher("listaDesiderio.jsp").forward(request, response);
    }

    //Aggiunge o Rimuove prodotti
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String idStr = request.getParameter("idProdotto");

        if (idStr != null) {
            int idProdotto = Integer.parseInt(idStr);
            if ("aggiungi".equals(action)) {
                boolean inserito = listaDesiderioDAO.addProdotto(account.getId_account(), idProdotto);
                if (inserito) {
                    //Refactoring dovuto al test TC 11.1
                    session.setAttribute("wishlistMsg", "Prodotto aggiunto alla lista dei desideri! ❤️");
                } else {
                    //Refactoring dovuto al test TC 11.2
                    session.setAttribute("wishlistMsg", "Prodotto già presente nella lista desideri");
                }
            }
            else if ("rimuovi".equals(action)) {
                listaDesiderioDAO.removeProdotto(account.getId_account(), idProdotto);
                session.setAttribute("wishlistMsg", "Prodotto rimosso dai preferiti.");
                session.setAttribute("wishlistType", "info");
            }
        }

        //Redirect
        String provenienza = request.getParameter("provenienza");
        if ("listaDesiderio".equals(provenienza)) {
            response.sendRedirect("listaDesiderio");
        } else {
            response.sendRedirect("index.jsp");
        }
    }
}