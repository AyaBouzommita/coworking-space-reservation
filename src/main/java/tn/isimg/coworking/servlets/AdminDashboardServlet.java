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
import tn.isimg.coworking.dao.UserDao;
import tn.isimg.coworking.model.Reservation;
import tn.isimg.coworking.model.Room;
import tn.isimg.coworking.model.User;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private RoomDao roomDao;
    private UserDao userDao;
    private ReservationDao reservationDao;

    @Override
    public void init() throws ServletException {
        this.roomDao = new RoomDao();
        this.userDao = new UserDao();
        this.reservationDao = new ReservationDao();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<Room> rooms = roomDao.findAll();
        List<User> users = userDao.findAll();
        List<Reservation> reservations = reservationDao.findAll();

        request.setAttribute("rooms", rooms);
        request.setAttribute("users", users);
        request.setAttribute("reservations", reservations);

        request.getRequestDispatcher("/WEB-INF/views/admin_dashboard.jsp")
               .forward(request, response);
    }
}
