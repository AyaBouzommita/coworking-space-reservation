package tn.isimg.coworking.servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import tn.isimg.coworking.dao.RoomDao;
import tn.isimg.coworking.model.Room;
import tn.isimg.coworking.model.User;

@MultipartConfig
@WebServlet("/admin/rooms")
public class AdminRoomServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private RoomDao roomDao;

    @Override
    public void init() throws ServletException {
        this.roomDao = new RoomDao();
    }

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        switch (action) {
            case "new":
                request.setAttribute("mode", "create");
                request.getRequestDispatcher("/WEB-INF/views/admin_room_form.jsp")
                        .forward(request, response);
                break;
            case "edit":
                String idParam = request.getParameter("id");
                try {
                    int id = Integer.parseInt(idParam);
                    Room room = roomDao.findById(id);
                    request.setAttribute("room", room);
                    request.setAttribute("mode", "edit");
                    request.getRequestDispatcher("/WEB-INF/views/admin_room_form.jsp")
                            .forward(request, response);
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                }
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            String idParam = request.getParameter("id");
            try {
                int id = Integer.parseInt(idParam);
                roomDao.delete(id);
            } catch (NumberFormatException e) {
                // ignore
            }
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        // create or edit
        String idParam = request.getParameter("id");
        String name = request.getParameter("name");
        String type = request.getParameter("type");
        String capacityParam = request.getParameter("capacity");
        String equipment = request.getParameter("equipment");
        String zone = request.getParameter("zone");
        String priceParam = request.getParameter("pricePerHour");

        int capacity = 0;
        try {
            capacity = Integer.parseInt(capacityParam);
        } catch (NumberFormatException e) {
            capacity = 0;
        }

        double pricePerHour = 0.0;
        try {
            if (priceParam != null) {
                pricePerHour = Double.parseDouble(priceParam);
            }
        } catch (NumberFormatException e) {
            pricePerHour = 0.0;
        }

        Room room = new Room();
        room.setName(name);
        room.setType(type);
        room.setCapacity(capacity);
        room.setEquipment(equipment);
        room.setZone(zone);
        room.setPricePerHour(pricePerHour);

        int roomId = -1;

        if ("create".equals(action)) {
            roomId = roomDao.create(room);
        } else if ("edit".equals(action)) {
            try {
                roomId = Integer.parseInt(idParam);
                room.setId(roomId); // Ensure object has ID
                boolean ok = roomDao.update(room);
                if (!ok)
                    roomId = -1;
            } catch (NumberFormatException e) {
                roomId = -1;
            }
        }

        // Handle File Upload
        if (roomId != -1) {
            try {
                Part filePart = request.getPart("image");
                if (filePart != null && filePart.getSize() > 0) {
                    // Use a standardized filename: room_{id}.png
                    String fileName = "room_" + roomId + ".png";

                    // Path to src/main/webapp/images/rooms dans le serveur
                    String uploadPath = getServletContext().getRealPath("") + java.io.File.separator + "images"
                            + java.io.File.separator + "rooms";

                    java.io.File uploadDir = new java.io.File(uploadPath);
                    if (!uploadDir.exists())
                        uploadDir.mkdirs();

                    filePart.write(uploadPath + java.io.File.separator + fileName);

                    
                }
            } catch (Exception e) {
                // Log upload error but don't fail the request
                e.printStackTrace();
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }
}
