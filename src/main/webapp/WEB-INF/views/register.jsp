<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="fr">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Inscription - Cospace</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        </head>

        <body>
            <div class="auth-page">
                <div class="auth-card">
                    <div class="auth-header">
                        <h2>Rejoignez Cospace</h2>
                        <p>Créez votre compte en quelques secondes</p>
                    </div>

                    <c:if test="${not empty registerError}">
                        <div class="alert alert-error">
                            ${registerError}
                        </div>
                    </c:if>

                    <c:if test="${not empty registerSuccess}">
                        <div class="alert alert-success">
                            ${registerSuccess}
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/register" method="post">
                        <div class="form-group">
                            <label for="fullName" class="form-label">Nom complet</label>
                            <input type="text" id="fullName" name="fullName" class="form-control" required />
                        </div>

                        <div class="form-group">
                            <label for="username" class="form-label">Nom d'utilisateur</label>
                            <input type="text" id="username" name="username" class="form-control" required />
                        </div>

                        <div class="form-group">
                            <label for="email" class="form-label">Email</label>
                            <input type="email" id="email" name="email" class="form-control" required />
                        </div>

                        <div class="form-group">
                            <label for="password" class="form-label">Mot de passe</label>
                            <input type="password" id="password" name="password" class="form-control" required />
                        </div>

                        <button type="submit" class="btn btn-primary btn-block">S'inscrire</button>

                        <p class="auth-footer">
                            Déjà un compte ?
                            <a href="${pageContext.request.contextPath}/login"
                                style="color: var(--primary); text-decoration: none; font-weight: 500;">
                                Se connecter
                            </a>
                        </p>
                    </form>
                </div>
            </div>
        </body>

        </html>