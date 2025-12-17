<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="fr">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Connexion - Cospace</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        </head>

        <body>
            <div class="auth-page">
                <div class="auth-card">
                    <div class="auth-header">
                        <h2>Bienvenue chez Cospace</h2>
                        <p>Connectez-vous pour réserver votre espace</p>
                    </div>

                    <c:if test="${not empty loginError}">
                        <div class="alert alert-error">
                            ${loginError}
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/login" method="post">
                        <div class="form-group">
                            <label for="username" class="form-label">Nom d'utilisateur</label>
                            <input type="text" id="username" name="username" class="form-control" required />
                        </div>

                        <div class="form-group">
                            <label for="password" class="form-label">Mot de passe</label>
                            <input type="password" id="password" name="password" class="form-control" required />
                        </div>

                        <button type="submit" class="btn btn-primary btn-block">Se connecter</button>

                        <p class="auth-footer">
                            Pas de compte ?
                            <a href="${pageContext.request.contextPath}/register"
                                style="color: var(--primary); text-decoration: none; font-weight: 500;">
                                Créer un compte
                            </a>
                        </p>
                    </form>
                </div>
            </div>
        </body>

        </html>