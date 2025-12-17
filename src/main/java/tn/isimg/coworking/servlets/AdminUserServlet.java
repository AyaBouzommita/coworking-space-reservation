package tn.isimg.coworking.servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tn.isimg.coworking.dao.UserDao;
import tn.isimg.coworking.model.User;

@WebServlet("/admin/users")
public class AdminUserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        this.userDao = new UserDao();
    }

    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null) ? (User) session.getAttribute("currentUser") : null;
    }

    private boolean isAdmin(HttpServletRequest request) {
        User u = getCurrentUser(request);
        return u != null && "ADMIN".equals(u.getRole());
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User current = getCurrentUser(request);

        String action = request.getParameter("action");
        String idParam = request.getParameter("id");

        try {
            int id = Integer.parseInt(idParam);

            // On évite de supprimer / modifier son propre rôle
            if (current != null && current.getId() == id) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                return;
            }

            if ("delete".equals(action)) {
                userDao.delete(id);
            } else if ("toggleRole".equals(action)) {
                String currentRole = request.getParameter("currentRole");
                String newRole = "USER".equals(currentRole) ? "ADMIN" : "USER";
                userDao.updateRole(id, newRole);
            }
        } catch (NumberFormatException e) {
            // ignore
        }

        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }
}
