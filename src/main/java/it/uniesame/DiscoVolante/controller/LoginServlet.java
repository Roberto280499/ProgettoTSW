package it.uniesame.DiscoVolante.controller;

import it.uniesame.DiscoVolante.model.Account;
import it.uniesame.DiscoVolante.model.dao.AccountDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

//URL = http://localhost:8080/DiscoVolante/login.jsp
//admin: admin@disco.com / admin123
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private AccountDAO accountDAO;

    // Costruttore per Tomcat
    public LoginServlet() {
        this.accountDAO = new AccountDAO();
    }

    // Costruttore per i Test (Dependency Injection)
    public LoginServlet(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Recupera i parametri
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        //il DAO cerca per email nel DB
        Account account = accountDAO.doRetrieveByEmailPassword(email, password);
        if (account != null) {
            HttpSession session = request.getSession();
            session.setAttribute("account", account);

            if (account.isAdminFlag()) {
                //Dashboard Admin (Scenario 2)
                response.sendRedirect("adminDashboard.jsp");
            } else {
                //Dashboard Cliente (Scenario S1)
                response.sendRedirect("index.jsp");
            }

        } else {
            //Accesso errato (Scenario Eccezione)
            request.setAttribute("errorMessage", "Email o Password errati!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}