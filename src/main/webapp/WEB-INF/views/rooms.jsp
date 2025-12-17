<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="fr">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Cospace - Coworking Premium</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        </head>

        <body>
            <!-- Navigation -->
            <jsp:include page="/WEB-INF/views/common/navbar.jsp">
                <jsp:param name="activePage" value="home" />
            </jsp:include>

            <!-- Hero Section -->
            <div class="hero"
                style="background-image: url('${pageContext.request.contextPath}/images/booking-bg.png');">
                <div class="container">
                    <div style="max-width: 600px;">
                        <h1>Comfortable<br>Coworking Space.</h1>
                        <p style="font-size: 1.1rem; margin: 1.5rem 0;">
                            Our coworking spaces let you choose to work with others in an open-plan area, or a shared
                            office.
                        </p>
                        <div style="display: flex; gap: 1rem;">
                            <a href="#rooms" class="btn btn-primary">BOOK A TOUR</a>
                            <a href="#features" class="btn btn-outline"
                                style="background: rgba(255,255,255,0.9); color: var(--text-main);">About Us</a>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Space Overview Section -->
            <div class="section" id="rooms">
                <div class="container">
                    <div class="section-header">
                        <p class="section-title">Nos Espaces</p>
                        <h2>Space Overview</h2>
                        <p style="max-width: 600px; margin: 0 auto; color: var(--text-muted);">
                            Generate Lorem Ipsum Dolor Sit Amet, Consectetur Adipiscing Elit, Sed Do Eiusmod Tempor
                            Incididunt Ut Labore Et Dolore
                        </p>
                    </div>

                    <!-- Advanced Filters -->
                    <div class="card" style="margin-bottom: 2rem;">
                        <form method="GET" action="${pageContext.request.contextPath}/rooms#rooms">
                            <div
                                style="display: grid; grid-template-columns: 2fr 1fr 1fr 1fr auto; gap: 1rem; align-items: end;">

                                <!-- Search by Name -->
                                <div class="form-group" style="margin: 0;">
                                    <label for="search" class="form-label">🔍 Rechercher</label>
                                    <input type="text" id="search" name="search" class="form-control"
                                        placeholder="Nom de la salle..." value="${currentSearch}">
                                </div>

                                <!-- Filter by Date (Start) -->
                                <div class="form-group" style="margin: 0;">
                                    <label for="startDateTime" class="form-label">Début souhaité</label>
                                    <input type="datetime-local" id="startDateTime" name="startDateTime"
                                        class="form-control" value="${currentStartDateTime}">
                                </div>

                                <!-- Filter by Date (End) -->
                                <div class="form-group" style="margin: 0;">
                                    <label for="endDateTime" class="form-label">Fin souhaitée</label>
                                    <input type="datetime-local" id="endDateTime" name="endDateTime"
                                        class="form-control" value="${currentEndDateTime}">
                                </div>

                                <!-- Filter by Zone -->
                                <div class="form-group" style="margin: 0;">
                                    <label for="zone" class="form-label">Zone</label>
                                    <select id="zone" name="zone" class="form-control">
                                        <option value="">Toutes</option>
                                        <option value="SILENT" ${currentZone=='SILENT' ? 'selected' : '' }>Silent
                                        </option>
                                        <option value="CASUAL" ${currentZone=='CASUAL' ? 'selected' : '' }>Casual
                                        </option>
                                    </select>
                                </div>

                                <!-- Filter by Capacity -->
                                <div class="form-group" style="margin: 0;">
                                    <label for="capacity" class="form-label">Capacité min.</label>
                                    <select id="capacity" name="capacity" class="form-control">
                                        <option value="">Toutes</option>
                                        <option value="1" ${currentCapacity=='1' ? 'selected' : '' }>1+</option>
                                        <option value="2" ${currentCapacity=='2' ? 'selected' : '' }>2+</option>
                                        <option value="5" ${currentCapacity=='5' ? 'selected' : '' }>5+</option>
                                        <option value="10" ${currentCapacity=='10' ? 'selected' : '' }>10+</option>
                                    </select>
                                </div>

                                <!-- Submit Button -->
                                <button type="submit" class="btn btn-primary" style="white-space: nowrap;">
                                    Filtrer
                                </button>
                            </div>
                        </form>
                    </div>

                    <c:if test="${empty rooms}">
                        <div class="card" style="text-align: center; padding: 3rem;">
                            <p style="color: var(--text-muted);">Aucune salle disponible pour le moment.</p>
                        </div>
                    </c:if>

                    <c:if test="${not empty rooms}">
                        <div class="rooms-grid">
                            <c:forEach var="room" items="${rooms}">
                                <article class="room-card">
                                    <c:set var="roomImg" value="meeting-alpha.png" />
                                    <c:if test="${room.name.toLowerCase().contains('phone')}">
                                        <c:set var="roomImg" value="phoneBox1.png" />
                                    </c:if>
                                    <c:if test="${room.name.toLowerCase().contains('training')}">
                                        <c:set var="roomImg" value="trainning-room.png" />
                                    </c:if>
                                    <c:if test="${room.name.toLowerCase().contains('silent')}">
                                        <c:set var="roomImg" value="meeting-silent.png" />
                                    </c:if>
                                    <c:if
                                        test="${room.name.toLowerCase().contains('open') || room.name.toLowerCase().contains('desk')}">
                                        <c:set var="roomImg" value="open-desk-zoneA.png" />
                                    </c:if>

                                    <img src="${pageContext.request.contextPath}/images/rooms/room_${room.id}.png"
                                        onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/${roomImg}';"
                                        alt="${room.name}" class="room-img" loading="lazy">

                                    <div class="room-details">
                                        <div
                                            style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 1rem;">
                                            <h3 class="room-title">${room.name}</h3>
                                            <span class="badge-${room.zone == 'SILENT' ? 'silent' : 'casual'}"
                                                style="font-size: 0.75rem; padding: 0.25rem 0.75rem; border-radius: 999px; font-weight: 600; text-transform: uppercase;">
                                                ${room.zone}
                                            </span>
                                        </div>

                                        <p class="room-info" style="margin-bottom: 0.5rem;">
                                            <strong>Type:</strong> ${room.type} | <strong>Capacité:</strong>
                                            ${room.capacity} pers.
                                        </p>
                                        <p class="room-info" style="margin-bottom: 1.5rem;">
                                            <strong>Équipements:</strong> ${room.equipment}
                                        </p>

                                        <div
                                            style="display: flex; align-items: center; justify-content: space-between; padding-top: 1rem; border-top: 1px solid var(--border-light);">
                                            <div>
                                                <span class="price-tag">${room.pricePerHour}€</span>
                                                <span style="color: var(--text-muted); font-size: 0.9rem;">/heure</span>
                                            </div>
                                            <a href="${pageContext.request.contextPath}/reservations/new?roomId=${room.id}"
                                                class="btn btn-primary">
                                                Réserver →
                                            </a>
                                        </div>
                                    </div>
                                </article>
                            </c:forEach>
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- Footer -->
            <footer style="background: var(--bg-section); border-top: 1px solid var(--border-light); padding: 3rem 0;">
                <div class="container" style="text-align: center;">
                    <p style="color: var(--text-muted);">&copy; 2024 Cospace. All rights reserved.</p>
                </div>
            </footer>
        </body>

        </html>