package tn.isimg.coworking.servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tn.isimg.coworking.dao.ReservationDao;
import tn.isimg.coworking.model.Reservation;
import tn.isimg.coworking.model.User;

@WebServlet("/reservations/cancel")
public class ReservationCancelServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ReservationDao reservationDao;

    @Override
    public void init() throws ServletException {
        this.reservationDao = new ReservationDao();
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idParam = request.getParameter("id");
        String errorMessage = null;

        try {
            int reservationId = Integer.parseInt(idParam);

            // Récupérer la réservation
            Reservation reservation = reservationDao.findById(reservationId);

            if (reservation == null) {
                errorMessage = "Réservation introuvable.";
            } else {
                // Règle 1: Vérifier que l'utilisateur est propriétaire OU admin
                boolean isOwner = reservation.getUserId() == currentUser.getId();
                boolean isAdmin = "ADMIN".equals(currentUser.getRole());

                if (!isOwner && !isAdmin) {
                    errorMessage = "Vous n'êtes pas autorisé à annuler cette réservation.";
                } else {
                    // Règle 2: Empêcher l'annulation si la réservation est déjà commencée
                    if (reservation.getStartDateTime().isBefore(java.time.LocalDateTime.now())) {
                        errorMessage = "Impossible d'annuler une réservation déjà commencée ou passée.";
                    } else {
                        // Tout est OK, on annule
                        boolean success = reservationDao.cancel(reservationId);
                        if (!success) {
                            errorMessage = "Erreur lors de l'annulation.";
                        } else {
                            // Envoi de l'email d'annulation
                            try {
                                tn.isimg.coworking.dao.UserDao userDao = new tn.isimg.coworking.dao.UserDao();
                                //récupérer l'utilisateur de la réservation
                                User targetUser = (reservation.getUserId() == currentUser.getId()) ? currentUser
                                        : userDao.findById(reservation.getUserId());

                                if (targetUser != null) {
                                    tn.isimg.coworking.utils.EmailService emailService = new tn.isimg.coworking.utils.EmailService();
                                    emailService.sendReservationCancellation(targetUser, reservation);
                                }
                            } catch (Exception e) {
                                e.printStackTrace(); // Log only, don't fail the request
                            }
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            errorMessage = "ID de réservation invalide.";
        }

        // Rediriger avec message d'erreur si nécessaire
        if (errorMessage != null) {
            session.setAttribute("errorMessage", errorMessage);
        } else {
            session.setAttribute("successMessage", "Réservation annulée avec succès.");
        }

        // Rediriger vers la bonne page selon le rôle
        if ("ADMIN".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/reservations/mine");
        }
    }
}
