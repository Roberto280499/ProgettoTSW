<%@ page import="it.uniesame.DiscoVolante.model.Account" %>
<%@ page import="jakarta.servlet.http.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // CONTROLLO SICUREZZA
    Account account = (Account) session.getAttribute("account");
    if (account == null || !account.isAdminFlag()) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard Admin - Disco Volante</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <div class="navbar">
        <a href="adminDashboard.jsp">
            <img src="${pageContext.request.contextPath}/images/logo.png" alt="Disco Volante Logo" class="navbar-logo">
        </a>

        <div class="navbar-links">
            <span class="user-info">Benvenuto, <b><%= account.getNickname() %></b> (Admin)</span>

            <a href="logout" class="btn btn-danger" style="margin-left: 15px;">Disconnetti</a>
        </div>
    </div>

    <div class="main-container">

        <h2 class="section-title">Dashboard Amministratore</h2>
        <p style="color: #666; margin-bottom: 30px;">Pannello di controllo per la gestione del negozio.</p>

        <div class="grid">

            <div class="card">
                <div class="card-body">
                    <div style="font-size: 3em; margin-bottom: 10px;">💿</div>
                    <h3>Gestione Prodotti</h3>
                    <p>Aggiungi, modifica o rimuovi album dal catalogo.</p>
                    <a href="gestioneProdotti" class="btn btn-primary" style="display: inline-block; margin-top: 10px;">Vai ai Prodotti</a>
                </div>
            </div>

            <div class="card">
                <div class="card-body">
                    <div style="font-size: 3em; margin-bottom: 10px;">📦</div>
                    <h3>Gestione Ordini</h3>
                    <p>Visualizza gli ordini dei clienti e aggiorna lo stato.</p>
                    <a href="adminOrdini" class="btn btn-primary" style="display: inline-block; margin-top: 10px;">Vai agli Ordini</a>
                </div>
            </div>

        </div>
    </div>

</body>
</html>
