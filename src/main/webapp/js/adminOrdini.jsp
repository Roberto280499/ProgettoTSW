<%@ page import="it.uniesame.DiscoVolante.model.Ordine" %>
<%@ page import="java.util.List" %>
<%@ page import="it.uniesame.DiscoVolante.model.Account" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Account account = (Account) session.getAttribute("account");
    if (account == null || !account.isAdminFlag()) { response.sendRedirect("login.jsp"); return; }

    List<Ordine> ordini = (List<Ordine>) request.getAttribute("ordini");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Gestione Ordini - Admin</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="navbar">
    <a href="index.jsp">
        <img src="${pageContext.request.contextPath}/images/logo.png" alt="Disco Volante Logo" class="navbar-logo">
    </a>
    <div class="navbar-links">
        <span class="user-info">Admin: <b><%= account.getNickname() %></b></span>
        <a href="adminDashboard.jsp">🔙 Dashboard</a>
        <a href="logout" class="btn btn-danger" style="margin-left: 15px;">Disconnetti</a>
    </div>
</div>

<div class="main-container">

    <h2 class="section-title">📦 Gestione Ordini Dei Clienti</h2>

    <div class="table-container">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Cliente</th>
                    <th>Data</th>
                    <th>Totale</th>
                    <th>Stato Attuale</th>
                    <th>Azione</th>
                    <th>Dettagli</th>
                </tr>
            </thead>
            <tbody>
                <% if (ordini != null) {
                    for (Ordine o : ordini) {
                        // Logica per colorare i badge dello stato
                        String badgeClass = "status-creato";
                        if("SPEDITO".equals(o.getStato())) badgeClass = "status-spedito";
                        if("CONSEGNATO".equals(o.getStato())) badgeClass = "status-consegnato";
                %>
                <tr>
                    <td>#<%= o.getId_ordine() %></td>
                    <td>
                        <strong><%= o.getUtente().getNickname() %></strong><br>
                        <small style="color: #888;"><%= o.getUtente().getEmail() %></small>
                    </td>
                    <td><%= o.getData_ordine() %></td>
                    <td>€ <%= String.format("%.2f", o.getCosto_totale()) %></td>
                    <td>
                        <span class="status-badge <%= badgeClass %>"><%= o.getStato() %></span>
                    </td>
                    <td>
                        <form action="${pageContext.request.contextPath}/adminOrdini" method="post" style="display: flex; gap: 5px; align-items: center; margin: 0;">
                            <input type="hidden" name="idOrdine" value="<%= o.getId_ordine() %>">
                            <select name="nuovoStato" class="search-input" style="padding: 5px; border-radius: 4px; width: auto;">
                                <option value="CREATO" <%= "CREATO".equals(o.getStato()) ? "selected" : "" %>>CREATO</option>
                                <option value="SPEDITO" <%= "SPEDITO".equals(o.getStato()) ? "selected" : "" %>>SPEDITO</option>
                                <option value="CONSEGNATO" <%= "CONSEGNATO".equals(o.getStato()) ? "selected" : "" %>>CONSEGNATO</option>
                                <option value="ANNULLATO" <%= "ANNULLATO".equals(o.getStato()) ? "selected" : "" %>>ANNULLATO</option>
                            </select>
                            <button type="submit" class="btn" style="padding: 5px 10px; background-color: #28a745; color: white;">💾</button>
                        </form>
                    </td>
                    <td>
                        <a href="dettaglioOrdine?id=<%= o.getId_ordine() %>" class="btn btn-primary" style="padding: 5px 10px; font-size: 0.8em;">
                            Vedi 🔍
                        </a>
                    </td>
                </tr>
                <% } } else { %>
                    <tr><td colspan="7" style="text-align: center; padding: 20px;">Nessun ordine trovato.</td></tr>
                <% } %>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>