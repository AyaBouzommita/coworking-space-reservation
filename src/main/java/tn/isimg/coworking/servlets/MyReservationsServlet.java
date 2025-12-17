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
import tn.isimg.coworking.model.Reservation;
import tn.isimg.coworking.model.User;

@WebServlet("/reservations/mine")
public class MyReservationsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ReservationDao reservationDao;

    @Override
    public void init() throws ServletException {
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

        List<Reservation> reservations = reservationDao.findByUser(currentUser.getId());
        request.setAttribute("reservations", reservations);

        request.getRequestDispatcher("/WEB-INF/views/reservations_mine.jsp")
               .forward(request, response);
    }
}
