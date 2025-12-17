package tn.isimg.coworking.servlets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tn.isimg.coworking.dao.ReservationDao;
import tn.isimg.coworking.dao.RoomDao;
import tn.isimg.coworking.model.Reservation;
import tn.isimg.coworking.model.Room;
import tn.isimg.coworking.model.User;

@WebServlet("/rooms")
public class RoomListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private RoomDao roomDao;
    private ReservationDao reservationDao;

    @Override
    public void init() throws ServletException {
        this.roomDao = new RoomDao();
        this.reservationDao = new ReservationDao();
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // 1) Vérifier que l'utilisateur est connecté (optionnel pour voir la liste)
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        // On ne redirige plus si null, on laisse l'accès public

        // 2) Récupérer les paramètres de filtrage
        String searchQuery = request.getParameter("search");
        // String filterType = request.getParameter("type"); // Removed
        String filterZone = request.getParameter("zone");
        String filterCapacity = request.getParameter("capacity");
        String startStr = request.getParameter("startDateTime");
        String endStr = request.getParameter("endDateTime");

        // 3) Charger toutes les salles
        List<Room> roomList = roomDao.findAll();

        // 4) Appliquer les filtres de base
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            final String query = searchQuery.toLowerCase();
            roomList = roomList.stream()
                    .filter(room -> room.getName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        if (filterZone != null && !filterZone.isEmpty()) {
            roomList = roomList.stream()
                    .filter(room -> room.getZone().equalsIgnoreCase(filterZone))
                    .collect(Collectors.toList());
        }

        if (filterCapacity != null && !filterCapacity.isEmpty()) {
            try {
                final int minCapacity = Integer.parseInt(filterCapacity);
                roomList = roomList.stream()
                        .filter(room -> room.getCapacity() >= minCapacity)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // Ignore invalid capacity filter
            }
        }

        // 5) Appliquer le filtre de disponibilité (Date/Heure)
        if (startStr != null && !startStr.isEmpty() && endStr != null && !endStr.isEmpty()) {
            try {
                LocalDateTime start = LocalDateTime.parse(startStr);
                LocalDateTime end = LocalDateTime.parse(endStr);

                // Récupérer toutes les réservations actives (CONFIRMED ou PENDING)
                List<Reservation> allReservations = reservationDao.findAll();

                // Filtrer pour ne garder que les salles SANS conflit
                roomList = roomList.stream()
                        .filter(room -> isRoomAvailable(room, allReservations, start, end))
                        .collect(Collectors.toList());

            } catch (Exception e) {
                // Erreur de parsing, on ignore le filtre date
            }
        }

        // 6) Passer les filtres courants à la vue
        request.setAttribute("currentSearch", searchQuery != null ? searchQuery : "");
        request.setAttribute("currentZone", filterZone != null ? filterZone : "");
        request.setAttribute("currentCapacity", filterCapacity != null ? filterCapacity : "");
        request.setAttribute("currentStartDateTime", startStr != null ? startStr : "");
        request.setAttribute("currentEndDateTime", endStr != null ? endStr : "");

        // 7) Passer la liste filtrée
        request.setAttribute("rooms", roomList);

        // 8) Forward vers la vue
        request.getRequestDispatcher("/WEB-INF/views/rooms.jsp")
                .forward(request, response);
    }

    // Helper pour vérifier la disponibilité
    private boolean isRoomAvailable(Room room, List<Reservation> reservations, LocalDateTime start, LocalDateTime end) {
        for (Reservation res : reservations) {
            // Si c'est la bonne salle et que le statut n'est pas annulé
            if (res.getRoomId() == room.getId() && !"CANCELLED".equals(res.getStatus())) {
                // Vérifier chevauchement
                // (StartA < EndB) and (EndA > StartB)
                if (start.isBefore(res.getEndDateTime()) && end.isAfter(res.getStartDateTime())) {
                    return false; // Conflit trouvé, salle non disponible
                }
            }
        }
        return true; // Aucun conflit trouvé
    }
}
