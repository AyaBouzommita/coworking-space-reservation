<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="jakarta.tags.core" %>
		<!DOCTYPE html>
		<html lang="fr">

		<head>
			<meta charset="UTF-8">
			<meta name="viewport" content="width=device-width, initial-scale=1.0">
			<title>Mes réservations - Cospace</title>
			<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
		</head>

		<body>
			<!-- Navigation -->
			<!-- Navigation -->
			<jsp:include page="/WEB-INF/views/common/navbar.jsp">
				<jsp:param name="activePage" value="my_reservations" />
			</jsp:include>

			<div class="app-container">
				<div class="container" style="padding: 4rem 1.5rem;">

					<div style="margin-bottom: 2rem;">
						<h2>Mes réservations</h2>
						<p style="color: var(--text-muted);">Gérez vos réservations d'espaces de travail</p>
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

					<c:if test="${empty reservations}">
						<div class="card" style="text-align: center; padding: 4rem;">
							<p style="color: var(--text-muted); margin-bottom: 1rem;">Aucune réservation pour le moment.
							</p>
							<a href="${pageContext.request.contextPath}/reservations/new" class="btn btn-primary">Créer
								une réservation</a>
						</div>
					</c:if>

					<c:if test="${not empty reservations}">
						<div class="table-wrapper">
							<table class="table">
								<thead>
									<tr>
										<th>ID</th>
										<th>Salle</th>
										<th>Début</th>
										<th>Fin</th>
										<th>Statut</th>
										<th>Options</th>
										<th>Prix Total</th>
										<th>Actions</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="res" items="${reservations}" varStatus="status">
										<tr
											style="${status.first ? 'background-color: #f0fdf4; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06); border-left: 4px solid var(--primary);' : ''}">
											<td><strong>#${res.id}</strong>
												<c:if test="${status.first}">
													<span
														style="font-size: 0.75rem; background: var(--primary); color: white; padding: 0.1rem 0.4rem; border-radius: 4px; margin-left: 0.5rem;">NOUVEAU</span>
												</c:if>
											</td>
											<td>${res.roomName != null ? res.roomName : 'Salle inconnue'}</td>
											<td>${res.startDateTime}</td>
											<td>${res.endDateTime}</td>
											<td>
												<span
													class="status-badge status-${res.status == 'CONFIRMED' ? 'confirmed' : 'pending'}">
													${res.status}
												</span>
											</td>
											<td>
												<span style="font-size: 0.85rem; color: var(--text-muted);">
													${res.selectedEquipments != null &&
													!res.selectedEquipments.isEmpty() ? res.selectedEquipments : '-'}
												</span>
											</td>
											<td><strong>${res.totalPrice} €</strong></td>
											<td>
												<c:if test="${res.status == 'CONFIRMED' || res.status == 'PENDING'}">
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
				</div>
			</div>

			<footer
				style="background: var(--bg-section); border-top: 1px solid var(--border-light); padding: 2rem 0; margin-top: 4rem;">
				<div class="container" style="text-align: center;">
					<p style="color: var(--text-muted);">&copy; 2024 Cospace. All rights reserved.</p>
				</div>
			</footer>
		</body>

		</html>