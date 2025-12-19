package tn.isimg.coworking.servlets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import tn.isimg.coworking.model.Room;
import tn.isimg.coworking.model.User;

@WebServlet("/reservations/new")
public class ReservationNewServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private RoomDao roomDao;
    private ReservationDao reservationDao;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    public void init() throws ServletException {
        this.roomDao = new RoomDao();
        this.reservationDao = new ReservationDao();
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<Room> rooms = roomDao.findAll();
        request.setAttribute("rooms", rooms);

        String roomIdParam = request.getParameter("roomId");
        if (roomIdParam != null && !roomIdParam.isEmpty()) {
            request.setAttribute("selectedRoomId", roomIdParam);
            try {
                int rId = Integer.parseInt(roomIdParam);
                Room selectedRoom = roomDao.findById(rId);
                request.setAttribute("selectedRoom", selectedRoom);
            } catch (NumberFormatException e) {
                // Ignore invalid ID
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/reservation_new.jsp")
                .forward(request, response);
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

        String roomIdParam = request.getParameter("roomId");
        String startParam = request.getParameter("start");
        String endParam = request.getParameter("end");

        List<Room> rooms = roomDao.findAll();
        request.setAttribute("rooms", rooms);

        try {
            int roomId = Integer.parseInt(roomIdParam);
            LocalDateTime start = LocalDateTime.parse(startParam, formatter);
            LocalDateTime end = LocalDateTime.parse(endParam, formatter);

            // Capture selected equipments
            // Capture selected equipments
            String[] equipments = request.getParameterValues("selectedEquipments");
            String equipmentsStr = (equipments != null) ? String.join(", ", equipments) : "";

            // Fetch room to get price per hour
            Room room = roomDao.findById(roomId);

            // Calculate duration in hours
            long durationInHours = java.time.Duration.between(start, end).toHours();
            if (durationInHours < 1)
                durationInHours = 1; // Minimum 1 hour

            // Calculate total price: (Price/Hour * Duration) + (Options * 5)
            double optionsPrice = (equipments != null) ? equipments.length * 5.0 : 0.0;
            double roomPrice = (room != null) ? room.getPricePerHour() * durationInHours : 0.0;
            double totalPrice = roomPrice + optionsPrice;

            Reservation r = new Reservation();
            r.setUserId(currentUser.getId());
            r.setRoomId(roomId);
            r.setStartDateTime(start);
            r.setEndDateTime(end);
            r.setSelectedEquipments(equipmentsStr);
            r.setTotalPrice(totalPrice);

            boolean ok = reservationDao.create(r);
            if (ok) {
                // Send confirmation email
                tn.isimg.coworking.utils.EmailService emailService = new tn.isimg.coworking.utils.EmailService();
                // set the Room Name for the email
                
                if (room != null)
                    r.setRoomName(room.getName());
                emailService.sendReservationConfirmation(currentUser, r);

                request.setAttribute("successMessage",
                        "Réservation créée avec succès. Un email de confirmation a été envoyé.");
            } else {
                request.setAttribute("errorMessage",
                        "Impossible de créer la réservation : le créneau est déjà pris. " +
                                "Veuillez consulter <a href='" + request.getContextPath()
                                + "/calendar' style='text-decoration: underline; font-weight: bold;'>le calendrier des disponibilités</a>.");
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            request.setAttribute("errorMessage", "Données invalides. Veuillez vérifier le formulaire.");
        }

        request.getRequestDispatcher("/WEB-INF/views/reservation_new.jsp")
                .forward(request, response);
    }
}
