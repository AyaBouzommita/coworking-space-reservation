<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="fr">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Admin - Cospace</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        </head>

        <body>
            <!-- Navigation -->
            <!-- Navigation -->
            <jsp:include page="/WEB-INF/views/common/navbar.jsp">
                <jsp:param name="activePage" value="admin" />
            </jsp:include>

            <div class="app-container">
                <div class="container" style="padding: 3rem 1.5rem;">

                    <div style="margin-bottom: 3rem;">
                        <h1 style="font-size: 2.5rem;">Tableau de bord</h1>
                        <p style="color: var(--text-muted);">Gestion des salles, utilisateurs et réservations</p>
                    </div>

                    <!-- Messages d'erreur -->
                    <c:if test="${not empty sessionScope.errorMessage}">
                        <div class="alert alert-error" style="margin-bottom: 1.5rem;">
                            ${sessionScope.errorMessage}
                        </div>
                        <c:remove var="errorMessage" scope="session" />
                    </c:if>

                    <!-- Messages de succès -->
                    <c:if test="${not empty sessionScope.successMessage}">
                        <div class="alert alert-success" style="margin-bottom: 1.5rem;">
                            ${sessionScope.successMessage}
                        </div>
                        <c:remove var="successMessage" scope="session" />
                    </c:if>

                    <!-- Salles -->
                    <section class="card" style="margin-bottom: 2rem;">
                        <div
                            style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
                            <h2 style="margin: 0;">Salles</h2>
                            <a href="${pageContext.request.contextPath}/admin/rooms?action=new" class="btn btn-primary">
                                + Nouvelle salle
                            </a>
                        </div>

                        <c:if test="${empty rooms}">
                            <p style="color: var(--text-muted);">Aucune salle.</p>
                        </c:if>

                        <c:if test="${not empty rooms}">
                            <div class="table-wrapper">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Nom</th>
                                            <th>Type</th>
                                            <th>Capacité</th>
                                            <th>Zone</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="room" items="${rooms}">
                                            <tr>
                                                <td>${room.id}</td>
                                                <td><strong>${room.name}</strong></td>
                                                <td>${room.type}</td>
                                                <td>${room.capacity}</td>
                                                <td>
                                                    <span class="badge-${room.zone == 'SILENT' ? 'silent' : 'casual'}">
                                                        ${room.zone}
                                                    </span>
                                                </td>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/admin/rooms?action=edit&id=${room.id}"
                                                        class="btn btn-outline"
                                                        style="padding: 0.375rem 0.75rem; font-size: 0.85rem; margin-right: 0.5rem;">
                                                        Modifier
                                                    </a>
                                                    <form action="${pageContext.request.contextPath}/admin/rooms"
                                                        method="post" style="display:inline;"
                                                        onsubmit="return confirm('Supprimer cette salle ?');">
                                                        <input type="hidden" name="action" value="delete" />
                                                        <input type="hidden" name="id" value="${room.id}" />
                                                        <button type="submit" class="btn btn-outline"
                                                            style="padding: 0.375rem 0.75rem; font-size: 0.85rem; border-color: #dc2626; color: #dc2626;">
                                                            Supprimer
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:if>
                    </section>

                    <!-- Utilisateurs -->
                    <section class="card" style="margin-bottom: 2rem;">
                        <h2 style="margin-bottom: 1.5rem;">Utilisateurs</h2>

                        <c:if test="${empty users}">
                            <p style="color: var(--text-muted);">Aucun utilisateur.</p>
                        </c:if>

                        <c:if test="${not empty users}">
                            <div class="table-wrapper">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Username</th>
                                            <th>Nom complet</th>
                                            <th>Email</th>
                                            <th>Rôle</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="u" items="${users}">
                                            <tr>
                                                <td>${u.id}</td>
                                                <td><strong>${u.username}</strong></td>
                                                <td>${u.fullName}</td>
                                                <td>${u.email}</td>
                                                <td>
                                                    <span class="status-badge"
                                                        style="background: var(--bg-section); color: var(--text-main);">
                                                        ${u.role}
                                                    </span>
                                                </td>
                                                <td>
                                                    <form action="${pageContext.request.contextPath}/admin/users"
                                                        method="post" style="display:inline; margin-right: 0.5rem;">
                                                        <input type="hidden" name="action" value="toggleRole" />
                                                        <input type="hidden" name="id" value="${u.id}" />
                                                        <input type="hidden" name="currentRole" value="${u.role}" />
                                                        <button type="submit" class="btn btn-outline"
                                                            style="padding: 0.375rem 0.75rem; font-size: 0.85rem;">
                                                            Changer rôle
                                                        </button>
                                                    </form>

                                                    <form action="${pageContext.request.contextPath}/admin/users"
                                                        method="post" style="display:inline;"
                                                        onsubmit="return confirm('Supprimer cet utilisateur ?');">
                                                        <input type="hidden" name="action" value="delete" />
                                                        <input type="hidden" name="id" value="${u.id}" />
                                                        <button type="submit" class="btn btn-outline"
                                                            style="padding: 0.375rem 0.75rem; font-size: 0.85rem; border-color: #dc2626; color: #dc2626;">
                                                            Supprimer
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:if>
                    </section>

                    <!-- Réservations -->
                    <section class="card">
                        <h2 style="margin-bottom: 1.5rem;">Réservations</h2>

                        <c:if test="${empty reservations}">
                            <p style="color: var(--text-muted);">Aucune réservation.</p>
                        </c:if>

                        <c:if test="${not empty reservations}">
                            <div class="table-wrapper">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>User ID</th>
                                            <th>Room ID</th>
                                            <th>Début</th>
                                            <th>Fin</th>
                                            <th>Statut</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="res" items="${reservations}">
                                            <tr>
                                                <td>${res.id}</td>
                                                <td>${res.userId}</td>
                                                <td>${res.roomId}</td>
                                                <td>${res.startDateTime}</td>
                                                <td>${res.endDateTime}</td>
                                                <td>
                                                    <span
                                                        class="status-badge status-${res.status == 'CONFIRMED' ? 'confirmed' : 'pending'}">
                                                        ${res.status}
                                                    </span>
                                                </td>
                                                <td>
                                                    <c:if
                                                        test="${res.status == 'CONFIRMED' || res.status == 'PENDING'}">
                                                        <form
                                                            action="${pageContext.request.contextPath}/reservations/cancel"
                                                            method="post" style="display:inline;">
                                                            <input type="hidden" name="id" value="${res.id}" />
                                                            <button type="submit" class="btn btn-outline"
                                                                style="padding: 0.375rem 0.75rem; font-size: 0.85rem; border-color: #dc2626; color: #dc2626;"
                                                                onclick="return confirm('Annuler cette réservation ?');">
                                                                Annuler
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:if>
                    </section>

                </div>
            </div>
        </body>

        </html>