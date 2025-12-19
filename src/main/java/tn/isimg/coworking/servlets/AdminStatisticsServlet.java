package tn.isimg.coworking.servlets;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tn.isimg.coworking.dao.ReservationDao;
import tn.isimg.coworking.model.ReservationStatistics;
import tn.isimg.coworking.model.User;

@WebServlet("/admin/statistics")
public class AdminStatisticsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ReservationDao reservationDao;

    @Override
    public void init() throws ServletException {
        this.reservationDao = new ReservationDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String period = request.getParameter("period");
        if (period == null || period.trim().isEmpty()) {
            period = "week"; // Default period
        }

        ReservationStatistics stats = reservationDao.getStatistics(period);
        request.setAttribute("stats", stats);
        request.setAttribute("currentPeriod", period);

        request.getRequestDispatcher("/WEB-INF/views/admin_statistics.jsp")
                .forward(request, response);
    }
}
