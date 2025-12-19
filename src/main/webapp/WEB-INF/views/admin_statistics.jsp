<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="fr">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Statistiques - Admin</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
            <!-- Chart.js -->
            <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
            <style>
                .stats-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
                    gap: 1.5rem;
                    margin-bottom: 2rem;
                }

                .chart-container {
                    background: white;
                    border-radius: var(--radius-md);
                    box-shadow: var(--shadow-sm);
                    padding: 1.5rem;
                    margin-bottom: 2rem;
                }

                .filter-btn {
                    padding: 0.5rem 1rem;
                    border: 1px solid var(--border-color);
                    background: white;
                    color: var(--text-main);
                    border-radius: var(--radius-sm);
                    text-decoration: none;
                    margin-right: 0.5rem;
                    font-size: 0.9rem;
                }

                .filter-btn.active {
                    background: var(--primary-color);
                    color: white;
                    border-color: var(--primary-color);
                }
            </style>
        </head>

        <body>
            <jsp:include page="/WEB-INF/views/common/navbar.jsp">
                <jsp:param name="activePage" value="statistics" />
            </jsp:include>

            <div class="app-container">
                <div class="container" style="padding: 2rem;">

                    <div
                        style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
                        <div>
                            <h1 style="font-size: 2rem; margin-bottom: 0.5rem;">Statistiques</h1>
                            <p style="color: var(--text-muted);">Analyse détaillée de l'activité</p>
                        </div>
                        <div>
                            <a href="?period=week"
                                class="filter-btn ${currentPeriod == 'week' ? 'active' : ''}">Semaine</a>
                            <a href="?period=month"
                                class="filter-btn ${currentPeriod == 'month' ? 'active' : ''}">Mois</a>
                            <a href="?period=year"
                                class="filter-btn ${currentPeriod == 'year' ? 'active' : ''}">Année</a>
                        </div>
                    </div>

                    <!-- Key Metrics -->
                    <div class="stats-grid">
                        <div class="card" style="padding: 1.5rem; text-align: center;">
                            <h3 style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 0.5rem;">Total
                                Réservations</h3>
                            <p style="font-size: 2rem; font-weight: bold; color: var(--primary-color); margin: 0;">
                                ${stats.totalReservations}
                            </p>
                        </div>
                        <div class="card" style="padding: 1.5rem; text-align: center;">
                            <h3 style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 0.5rem;">Confirmées
                            </h3>
                            <p style="font-size: 2rem; font-weight: bold; color: #16a34a; margin: 0;">
                                ${stats.confirmedReservations}
                            </p>
                        </div>
                        <div class="card" style="padding: 1.5rem; text-align: center;">
                            <h3 style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 0.5rem;">Annulées
                            </h3>
                            <p style="font-size: 2rem; font-weight: bold; color: #dc2626; margin: 0;">
                                ${stats.cancelledReservations}
                            </p>
                        </div>
                        <div class="card" style="padding: 1.5rem; text-align: center;">
                            <h3 style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 0.5rem;">Top Salle
                            </h3>
                            <p
                                style="font-size: 1.5rem; font-weight: bold; margin: 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                ${stats.topRoomName}
                            </p>
                        </div>
                    </div>

                    <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 2rem; margin-bottom: 2rem;">
                        <!-- Trends Chart -->
                        <div class="chart-container">
                            <h3 style="margin-bottom: 1rem;">Évolution des réservations</h3>
                            <canvas id="trendChart"></canvas>
                        </div>

                        <!-- Peak Hours Chart -->
                        <div class="chart-container">
                            <h3 style="margin-bottom: 1rem;">Heures de pointe</h3>
                            <canvas id="peakChart"></canvas>
                        </div>
                    </div>

                    <!-- Top Clients Table -->
                    <div class="card">
                        <div style="padding: 1.5rem; border-bottom: 1px solid var(--border-color);">
                            <h3 style="margin: 0;">Clients les plus fidèles
                                <c:choose>
                                    <c:when test="${currentPeriod == 'week'}">(Semaine)</c:when>
                                    <c:when test="${currentPeriod == 'month'}">(Mois)</c:when>
                                    <c:when test="${currentPeriod == 'year'}">(Année)</c:when>
                                </c:choose>
                            </h3>
                        </div>
                        <div class="table-wrapper">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>Client</th>
                                        <th>Réservations</th>
                                        <th>Total Dépensé</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="client" items="${stats.topClients}">
                                        <tr>
                                            <td>
                                                <strong>${client.fullName}</strong>
                                            </td>
                                            <td>${client.reservationCount}</td>
                                            <td>${client.totalSpent} DT</td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty stats.topClients}">
                                        <tr>
                                            <td colspan="3" style="text-align:center; color: var(--text-muted);">Aucune
                                                donnée.</td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>

                </div>
            </div>

            <!-- Chart Data Preparation -->
            <script>
                // Data from server
                // Convert Map to Arrays for Chart.js
                const trendLabels = [];
                const trendData = [];
                <c:forEach var="entry" items="${stats.trendData}">
                    trendLabels.push("${entry.key}");
                    trendData.push(${entry.value});
                </c:forEach>

                const peakLabels = [];
                const peakData = [];
                <c:forEach var="entry" items="${stats.peakHours}">
                    peakLabels.push("${entry.key}h");
                    peakData.push(${entry.value});
                </c:forEach>

                // Trend Chart (Line)
                const ctxTrend = document.getElementById('trendChart').getContext('2d');
                new Chart(ctxTrend, {
                    type: 'line',
                    data: {
                        labels: trendLabels,
                        datasets: [{
                            label: 'Réservations',
                            data: trendData,
                            borderColor: '#10b981', // Emerald 500
                            backgroundColor: 'rgba(16, 185, 129, 0.1)',
                            tension: 0.3,
                            fill: true
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: { display: false }
                        },
                        scales: {
                            y: { beginAtZero: true, ticks: { precision: 0 } }
                        }
                    }
                });

                // Peak Chart (Bar)
                const ctxPeak = document.getElementById('peakChart').getContext('2d');
                new Chart(ctxPeak, {
                    type: 'bar',
                    data: {
                        labels: peakLabels,
                        datasets: [{
                            label: 'Réservations',
                            data: peakData,
                            backgroundColor: '#3b82f6', // Blue 500
                            borderRadius: 4
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: { display: false }
                        },
                        scales: {
                            y: { beginAtZero: true, ticks: { precision: 0 } }
                        }
                    }
                });
            </script>
        </body>

        </html>