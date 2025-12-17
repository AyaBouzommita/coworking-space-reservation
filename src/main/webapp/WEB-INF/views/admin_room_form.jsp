<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="fr">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Admin - Salle - Cospace</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        </head>

        <body style="background-color: var(--bg-main);">

            <!-- Navigation -->
            <jsp:include page="/WEB-INF/views/common/navbar.jsp">
                <jsp:param name="activePage" value="admin" />
            </jsp:include>

            <div class="container" style="padding-top: 3rem; padding-bottom: 3rem;">
                <div class="auth-card" style="margin: 0 auto; max-width: 600px;">
                    <div class="auth-header" style="text-align: left; margin-bottom: 2rem;">
                        <h2 style="font-size: 1.8rem; color: var(--text-main);">
                            <c:choose>
                                <c:when test="${mode == 'edit'}">Modifier la salle</c:when>
                                <c:otherwise>Nouvelle salle</c:otherwise>
                            </c:choose>
                        </h2>
                        <p style="color: var(--text-muted); margin-top: 0.5rem;">
                            Remplissez les informations ci-dessous
                        </p>
                    </div>

                    <form action="${pageContext.request.contextPath}/admin/rooms" method="post" class="form"
                        enctype="multipart/form-data">
                        <input type="hidden" name="action" value="${mode}" />
                        <c:if test="${mode == 'edit'}">
                            <input type="hidden" name="id" value="${room.id}" />
                        </c:if>

                        <div class="form-group">
                            <label for="name" class="form-label">Nom de la salle</label>
                            <input type="text" id="name" name="name" class="form-control" value="${room.name}"
                                placeholder="Ex: Meeting Room Alpha" required />
                        </div>

                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                            <div class="form-group">
                                <label for="type" class="form-label">Type</label>
                                <select id="type" name="type" class="form-control" required>
                                    <option value="">Sélectionner...</option>
                                    <option value="MEETING" ${room.type=='MEETING' ? 'selected' : '' }>Meeting Room
                                    </option>
                                    <option value="PHONE_BOX" ${room.type=='PHONE_BOX' ? 'selected' : '' }>Phone Box
                                    </option>
                                    <option value="TRAINING" ${room.type=='TRAINING' ? 'selected' : '' }>Training Room
                                    </option>
                                    <option value="DESK" ${room.type=='DESK' ? 'selected' : '' }>Open Desk</option>
                                    <option value="VISIO" ${room.type=='VISIO' ? 'selected' : '' }>Visio Room</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label for="capacity" class="form-label">Capacité (pers.)</label>
                                <input type="number" id="capacity" name="capacity" class="form-control" min="1"
                                    value="${room.capacity}" required />
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="zone" class="form-label">Zone</label>
                            <select id="zone" name="zone" class="form-control" required>
                                <option value="">Sélectionner...</option>
                                <option value="SILENT" ${room.zone=='SILENT' ? 'selected' : '' }>Zone Silencieuse
                                </option>
                                <option value="CASUAL" ${room.zone=='CASUAL' ? 'selected' : '' }>Zone Détente (Casual)
                                </option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="pricePerHour" class="form-label">Prix par heure (€)</label>
                            <input type="number" id="pricePerHour" name="pricePerHour" class="form-control" min="0"
                                step="0.5" value="${room.pricePerHour}" required />
                        </div>

                        <div class="form-group">
                            <label for="equipment" class="form-label">Équipements (séparés par virgule)</label>
                            <input type="text" id="equipment" name="equipment" class="form-control"
                                value="${room.equipment}" placeholder="Ex: TV, Whiteboard, Projector" />
                        </div>

                        <div class="form-group">
                            <label for="image" class="form-label">Image de la salle (recommandé)</label>
                            <input type="file" id="image" name="image" class="form-control"
                                accept="image/png, image/jpeg, image/jpg" />
                            <p style="font-size: 0.8rem; color: var(--text-muted); margin-top: 0.5rem;">
                                Si vous n'ajoutez pas d'image, une image par défaut sera utilisée selon le type de
                                salle.
                            </p>
                        </div>

                        <div style="margin-top: 2rem; display: flex; gap: 1rem;">
                            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline"
                                style="flex: 1; text-align: center; text-decoration: none;">Annuler</a>
                            <button type="submit" class="btn btn-primary" style="flex: 2;">
                                <c:choose>
                                    <c:when test="${mode == 'edit'}">Enregistrer les modifications</c:when>
                                    <c:otherwise>Créer la salle</c:otherwise>
                                </c:choose>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </body>

        </html>