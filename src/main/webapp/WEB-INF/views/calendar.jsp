<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <% // Prévention cache pour éviter le retour arrière après déconnexion
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate" ); // HTTP 1.1
            response.setHeader("Pragma", "no-cache" ); // HTTP 1.0 response.setDateHeader("Expires", 0); // Proxies %>
            <!DOCTYPE html>
            <html>

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Calendrier - CoSpace</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
                <!-- FullCalendar CSS -->
                <link href='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.10/main.min.css' rel='stylesheet' />
                <script src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.10/index.global.min.js'></script>
                <style>
                    .fc-event {
                        cursor: pointer;
                        border: none;
                        padding: 2px 4px;
                        font-size: 0.85rem;
                    }

                    .fc-event-main {
                        font-weight: 500;
                    }

                    .status-confirmed {
                        background-color: #10b981;
                        color: white;
                    }

                    .status-pending {
                        background-color: #f59e0b;
                        color: white;
                    }

                    .status-cancelled {
                        background-color: #ef4444;
                        color: white;
                        text-decoration: line-through;
                        opacity: 0.6;
                    }

                    #calendar-container {
                        background: white;
                        padding: 2rem;
                        border-radius: var(--radius);
                        box-shadow: var(--shadow-sm);
                    }
                </style>
            </head>

            <body>

                <!-- Navbar -->
                <!-- Navigation -->
                <!-- Navigation -->
                <jsp:include page="/WEB-INF/views/common/navbar.jsp">
                    <jsp:param name="activePage" value="calendar" />
                </jsp:include>

                <div class="app-container">
                    <div class="container" style="padding: 3rem 1.5rem;">

                        <div
                            style="margin-bottom: 2rem; display: flex; justify-content: space-between; align-items: end;">
                            <div>
                                <h1>Calendrier des réservations</h1>
                                <p style="color: var(--text-muted);">Visualisez l'occupation des salles en temps réel
                                </p>
                            </div>

                            <div style="display: flex; gap: 1rem;">
                                <span style="display:flex; align-items:center; gap:0.5rem;"><span
                                        style="width:12px; height:12px; background:#10b981; border-radius:50%;"></span>
                                    Confirmé</span>
                                <span style="display:flex; align-items:center; gap:0.5rem;"><span
                                        style="width:12px; height:12px; background:#f59e0b; border-radius:50%;"></span>
                                    En attente</span>
                            </div>
                        </div>

                        <!-- Filter Card -->
                        <div class="card" style="margin-bottom: 2rem;">
                            <form action="${pageContext.request.contextPath}/calendar" method="get">
                                <div style="display: flex; align-items: flex-end; gap: 1rem;">
                                    <div class="form-group" style="margin: 0; flex-grow: 1;">
                                        <label for="roomFilter" class="form-label">Filtrer par salle</label>
                                        <select id="roomFilter" name="roomId" class="form-control"
                                            onchange="this.form.submit()">
                                            <option value="">Toutes les salles</option>
                                            <c:forEach var="room" items="${rooms}">
                                                <option value="${room.id}" ${room.id==selectedRoomId ? 'selected' : ''
                                                    }>
                                                    ${room.name}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 0.7rem;">
                                        Sélectionnez une salle pour voir ses disponibilités spécifiques.
                                    </div>
                                </div>
                            </form>
                        </div>

                        <div id="calendar-container">
                            <div id="calendar"></div>
                        </div>

                    </div>
                </div>

                <script>
                    document.addEventListener('DOMContentLoaded', function () {
                        var calendarEl = document.getElementById('calendar');
                        var calendar = new FullCalendar.Calendar(calendarEl, {
                            initialView: 'timeGridWeek',
                            headerToolbar: {
                                left: 'prev,next today',
                                center: 'title',
                                right: 'dayGridMonth,timeGridWeek,timeGridDay'
                            },
                            locale: 'fr',
                            slotMinTime: '08:00:00',
                            slotMaxTime: '20:00:00',
                            allDaySlot: false,
                            height: 'auto',
                            events: [
                                <c:forEach var="res" items="${reservations}">
                                    {
                                        title: '${res.roomName != null ? res.roomName : "Salle #" + res.roomId}', // Affiche le nom de la salle
                                    start: '${res.startDateTime}',
                                    end: '${res.endDateTime}',
                                    className: 'status-${res.status.toLowerCase()}',
                                    extendedProps: {
                                        status: '${res.status}',
                                    user: '${res.userId}'
                        }
                    },
                                </c:forEach>
                            ],
                            eventClick: function (info) {
                                alert('Salle: ' + info.event.title + '\nDébut: ' + info.event.start.toLocaleString() + '\nFin: ' + info.event.end.toLocaleString() + '\nStatut: ' + info.event.extendedProps.status);
                            }
                        });
                        calendar.render();
                    });
                </script>
            </body>

            </html>