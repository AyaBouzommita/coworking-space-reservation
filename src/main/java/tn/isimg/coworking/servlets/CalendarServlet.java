package tn.isimg.coworking.servlets;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tn.isimg.coworking.dao.ReservationDao;
import tn.isimg.coworking.dao.RoomDao;
import tn.isimg.coworking.model.Reservation;
import tn.isimg.coworking.model.User;

@WebServlet("/calendar")
public class CalendarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ReservationDao reservationDao;
    private RoomDao roomDao;

    @Override
    public void init() throws ServletException {
        this.reservationDao = new ReservationDao();
        this.roomDao = new RoomDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Charger la liste des salles pour le filtre
        request.setAttribute("rooms", roomDao.findAll());

        // Récupérer le filtre roomId s'il existe
        String roomIdParam = request.getParameter("roomId");
        List<Reservation> reservations = reservationDao.findAll();

        if (roomIdParam != null && !roomIdParam.isEmpty()) {
            try {
                int roomId = Integer.parseInt(roomIdParam);
                // Filtrer la liste (ou utiliser une méthode DAO spécifique si dispo)
                reservations = reservations.stream()
                        .filter(r -> r.getRoomId() == roomId)
                        .toList(); // Java 16+ ou .collect(Collectors.toList()) pour Java 8
                request.setAttribute("selectedRoomId", roomId);
            } catch (NumberFormatException e) {
                // Ignore invalid ID
            }
        }

        request.setAttribute("reservations", reservations);

        request.getRequestDispatcher("/WEB-INF/views/calendar.jsp").forward(request, response);
    }
}
