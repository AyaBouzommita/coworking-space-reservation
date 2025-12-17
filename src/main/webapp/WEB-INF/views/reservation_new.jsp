<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="fr">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Réserver - Cospace</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        </head>

        <body>
            <!-- Navigation -->
            <!-- Navigation -->
            <jsp:include page="/WEB-INF/views/common/navbar.jsp">
                <jsp:param name="activePage" value="reservation_new" />
            </jsp:include>

            <!-- Split Layout: Image LEFT | Form RIGHT -->
            <div class="split-layout">
                <!-- Left: Image -->
                <!-- Determine background image -->
                <c:set var="bgImg" value="booking-bg.png" />
                <c:if test="${not empty selectedRoom}">
                    <c:set var="rName" value="${selectedRoom.name.toLowerCase()}" />
                    <c:if test="${rName.contains('phone')}">
                        <c:set var="bgImg" value="phoneBox1.png" />
                    </c:if>
                    <c:if test="${rName.contains('training')}">
                        <c:set var="bgImg" value="trainning-room.png" />
                    </c:if>
                    <c:if test="${rName.contains('silent')}">
                        <c:set var="bgImg" value="meeting-silent.png" />
                    </c:if>
                    <c:if test="${rName.contains('meeting') && !rName.contains('silent')}">
                        <c:set var="bgImg" value="meeting-alpha.png" />
                    </c:if>
                    <c:if test="${rName.contains('open') || rName.contains('desk')}">
                        <c:set var="bgImg" value="open-desk-zoneA.png" />
                    </c:if>
                </c:if>

                <div class="split-image">
                    <img src="${pageContext.request.contextPath}/images/rooms/room_${selectedRoom != null ? selectedRoom.id : '0'}.png"
                        onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/${bgImg}';"
                        style="width: 100%; height: 100%; object-fit: cover;" alt="Room View" />
                </div>

                <!-- Right: Form -->
                <div class="split-content">
                    <div style="width: 100%; max-width: 500px;">
                        <h2 style="margin-bottom: 0.5rem;">Book a Tour</h2>
                        <p style="color: var(--text-muted); margin-bottom: 2rem;">
                            Réservez votre espace de travail dès maintenant
                        </p>

                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-error">
                                <c:out value="${errorMessage}" escapeXml="false" />
                            </div>
                        </c:if>

                        <c:if test="${not empty successMessage}">
                            <div class="alert alert-success">
                                ${successMessage}
                            </div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/reservations/new" method="post">



                            <div class="form-group">
                                <label for="roomId" class="form-label">Sélectionner une salle :</label>
                                <select id="roomId" name="roomId" class="form-control" required>
                                    <option value="">-- Choisir une salle --</option>
                                    <c:forEach var="room" items="${rooms}">
                                        <option value="${room.id}" ${room.id==selectedRoomId ? 'selected' : '' }>
                                            ${room.name} - ${room.type} (${room.capacity} places) -
                                            ${room.pricePerHour}€/h
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                                <div class="form-group">
                                    <label for="start" class="form-label">Date et heure de début :</label>
                                    <input type="datetime-local" id="start" name="start" class="form-control"
                                        required />
                                </div>

                                <div class="form-group">
                                    <label for="end" class="form-label">Date et heure de fin :</label>
                                    <input type="datetime-local" id="end" name="end" class="form-control" required />
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Options supplémentaires :</label>
                                <div
                                    style="display:flex; flex-direction:column; gap:0.5rem; background: var(--input-bg); padding: 1rem; border-radius: 0.5rem; border: 1px solid var(--border-color);">
                                    <label style="display:flex; align-items:center; gap:0.5rem; font-size:0.95rem;">
                                        <input type="checkbox" name="selectedEquipments" value="Projecteur"> Projecteur
                                        HD (5€)
                                    </label>
                                    <label style="display:flex; align-items:center; gap:0.5rem; font-size:0.95rem;">
                                        <input type="checkbox" name="selectedEquipments" value="Tableau Blanc"> Tableau
                                        Blanc (5€)
                                    </label>
                                    <label style="display:flex; align-items:center; gap:0.5rem; font-size:0.95rem;">
                                        <input type="checkbox" name="selectedEquipments" value="Café/Thé"> Service
                                        Café/Thé (5€)
                                    </label>
                                    <label style="display:flex; align-items:center; gap:0.5rem; font-size:0.95rem;">
                                        <input type="checkbox" name="selectedEquipments" value="Ecran Sup"> Écran
                                        supplémentaire 24" (5€)
                                    </label>
                                </div>
                            </div>

                            <div style="margin: 1.5rem 0; display: flex; align-items: start; gap: 0.75rem;">
                                <input type="checkbox" id="terms" required style="margin-top: 4px;">
                                <label for="terms" style="font-size: 0.9rem; color: var(--text-secondary);">
                                    I agree to the <a href="#" style="color: var(--primary);">Terms of service</a> and
                                    <a href="#" style="color: var(--primary);">Privacy policy</a> of Cospace company.
                                </label>
                            </div>

                            <button type="submit" class="btn btn-primary btn-block" style="margin-top: 1rem;">
                                SUBMIT
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </body>

        </html>