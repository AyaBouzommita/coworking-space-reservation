<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <nav class="navbar">
            <div class="container nav-content">
                <a href="${pageContext.request.contextPath}/rooms" class="nav-logo">Cospace</a>

                <ul class="nav-links">
                    <li><a href="${pageContext.request.contextPath}/rooms"
                            class="nav-link ${param.activePage == 'home' ? 'active' : ''}">Home</a></li>
                    <li><a href="${pageContext.request.contextPath}/calendar"
                            class="nav-link ${param.activePage == 'calendar' ? 'active' : ''}">Calendrier</a></li>
                </ul>

                <div class="nav-actions">
                    <c:choose>
                        <c:when test="${not empty sessionScope.currentUser}">
                            <span style="margin-right: 1rem; color: var(--text-main); font-weight: 500;">
                                ${sessionScope.currentUser.username}
                            </span>

                            <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                                <a href="${pageContext.request.contextPath}/admin/dashboard"
                                    class="nav-link ${param.activePage == 'admin' ? 'active' : ''}">Tableau de bord</a>
                                <a href="${pageContext.request.contextPath}/admin/statistics"
                                    class="nav-link ${param.activePage == 'statistics' ? 'active' : ''}">Statistiques</a>
                            </c:if>

                            <a href="${pageContext.request.contextPath}/reservations/mine"
                                class="nav-link ${param.activePage == 'my_reservations' ? 'active' : ''}">Mes
                                Réservations</a>
                            <a href="${pageContext.request.contextPath}/reservations/new" class="btn btn-primary">Book
                                Now</a>
                            <a href="${pageContext.request.contextPath}/logout"
                                style="color: var(--text-muted); font-size: 0.9rem; text-decoration: none; margin-left: 0.5rem;">Déconnexion</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/login" class="nav-link">Connexion</a>
                            <a href="${pageContext.request.contextPath}/register" class="btn btn-primary">S'inscrire</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </nav>