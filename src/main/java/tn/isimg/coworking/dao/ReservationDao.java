package tn.isimg.coworking.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import tn.isimg.coworking.model.Reservation;

public class ReservationDao {

    // Insertion d'une réservation (status CONFIRMED par défaut)
    private static final String INSERT_RES = "INSERT INTO reservations (user_id, room_id, start_datetime, end_datetime, status, selected_equipments, total_price) "
            +
            "VALUES (?, ?, ?, ?, 'CONFIRMED', ?, ?)";

    // Vérifier chevauchement pour la même salle
    private static final String CHECK_ROOM_CONFLICT = "SELECT COUNT(*) FROM reservations " +
            "WHERE room_id = ? " +
            "  AND status = 'CONFIRMED' " +
            "  AND ( " +
            "       (start_datetime < ? AND end_datetime > ?) " +
            "    OR (start_datetime >= ? AND start_datetime < ?) " +
            "    OR (end_datetime > ? AND end_datetime <= ?) " + "  )";

    // Vérifier qu'un utilisateur n'a pas déjà une salle sur ce créneau
    private static final String CHECK_USER_CONFLICT = "SELECT COUNT(*) FROM reservations " +
            "WHERE user_id = ? " +
            "  AND status = 'CONFIRMED' " +
            "  AND ( " +
            "       (start_datetime < ? AND end_datetime > ?) " +
            "    OR (start_datetime >= ? AND start_datetime < ?) " +
            "    OR (end_datetime > ? AND end_datetime <= ?) " +
            "  )";

    // Lister les réservations d'un utilisateur avec le nom de la salle
    private static final String SELECT_BY_USER = "SELECT r.id, r.user_id, r.room_id, r.start_datetime, r.end_datetime, r.status, r.selected_equipments, r.total_price, "
            +
            "       ro.name AS room_name " +
            "FROM reservations r " +
            "LEFT JOIN rooms ro ON r.room_id = ro.id " +
            "WHERE r.user_id = ? " +
            "ORDER BY r.id DESC";

    private static final String SELECT_ALL = "SELECT r.id, r.user_id, r.room_id, r.start_datetime, r.end_datetime, r.status, r.selected_equipments, r.total_price, "
            +
            "       ro.name AS room_name " +
            "FROM reservations r " +
            "LEFT JOIN rooms ro ON r.room_id = ro.id " +
            "ORDER BY r.start_datetime DESC";

    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Reservation r = new Reservation();
                r.setId(rs.getInt("id"));
                r.setUserId(rs.getInt("user_id"));
                r.setRoomId(rs.getInt("room_id"));
                r.setRoomName(rs.getString("room_name")); // Added mapping
                r.setStartDateTime(rs.getTimestamp("start_datetime").toLocalDateTime());
                r.setEndDateTime(rs.getTimestamp("end_datetime").toLocalDateTime());
                r.setStatus(rs.getString("status"));
                r.setSelectedEquipments(rs.getString("selected_equipments"));
                r.setTotalPrice(rs.getDouble("total_price"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Récupérer une réservation par son ID
    public Reservation findById(int reservationId) {
        String SELECT_BY_ID = "SELECT r.id, r.user_id, r.room_id, r.start_datetime, r.end_datetime, r.status, " +
                "       ro.name AS room_name " +
                "FROM reservations r " +
                "LEFT JOIN rooms ro ON r.room_id = ro.id " +
                "WHERE r.id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setInt(1, reservationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Reservation r = new Reservation();
                    r.setId(rs.getInt("id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setRoomId(rs.getInt("room_id"));
                    r.setRoomName(rs.getString("room_name"));
                    r.setStartDateTime(rs.getTimestamp("start_datetime").toLocalDateTime());
                    r.setEndDateTime(rs.getTimestamp("end_datetime").toLocalDateTime());
                    r.setStatus(rs.getString("status"));
                    return r;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Annuler une réservation (pour user ou admin)
    private static final String CANCEL_RES = "UPDATE reservations SET status = 'CANCELLED' " +
            "WHERE id = ?";

    public boolean hasRoomConflict(int roomId,
            LocalDateTime start,
            LocalDateTime end) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(CHECK_ROOM_CONFLICT)) {

            Timestamp tsStart = Timestamp.valueOf(start);
            Timestamp tsEnd = Timestamp.valueOf(end);

            ps.setInt(1, roomId);
            ps.setTimestamp(2, tsEnd);
            ps.setTimestamp(3, tsStart);
            ps.setTimestamp(4, tsStart);
            ps.setTimestamp(5, tsEnd);
            ps.setTimestamp(6, tsStart);
            ps.setTimestamp(7, tsEnd);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true; // en cas d'erreur, on bloque par sécurité
    }

    public boolean hasUserConflict(int userId,
            LocalDateTime start,
            LocalDateTime end) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(CHECK_USER_CONFLICT)) {

            Timestamp tsStart = Timestamp.valueOf(start);
            Timestamp tsEnd = Timestamp.valueOf(end);

            ps.setInt(1, userId);
            ps.setTimestamp(2, tsEnd);
            ps.setTimestamp(3, tsStart);
            ps.setTimestamp(4, tsStart);
            ps.setTimestamp(5, tsEnd);
            ps.setTimestamp(6, tsStart);
            ps.setTimestamp(7, tsEnd);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Crée une réservation si les règles métier sont respectées :
     * - pas de chevauchement sur la même salle
     * - pas de double réservation pour l'utilisateur sur ce créneau
     * Retourne true si insertion OK, false sinon.
     */
    public boolean create(Reservation r) {
        LocalDateTime start = r.getStartDateTime();
        LocalDateTime end = r.getEndDateTime();

        if (start == null || end == null || !end.isAfter(start)) {
            return false; // règle: end > start
        }

        // règle: pas de chevauchement sur la même salle
        if (hasRoomConflict(r.getRoomId(), start, end)) {
            return false;
        }

        // règle: l'utilisateur ne peut réserver qu'une salle à la fois
        if (hasUserConflict(r.getUserId(), start, end)) {
            return false;
        }

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(INSERT_RES)) {

            ps.setInt(1, r.getUserId());
            ps.setInt(2, r.getRoomId());
            ps.setTimestamp(3, Timestamp.valueOf(start));
            ps.setTimestamp(4, Timestamp.valueOf(end));
            ps.setString(5, r.getSelectedEquipments());
            ps.setDouble(6, r.getTotalPrice());

            int rows = ps.executeUpdate();
            return rows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Reservation> findByUser(int userId) {
        List<Reservation> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SELECT_BY_USER)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reservation r = new Reservation();
                    r.setId(rs.getInt("id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setRoomId(rs.getInt("room_id"));
                    r.setRoomName(rs.getString("room_name")); // Populate room name
                    r.setStartDateTime(rs.getTimestamp("start_datetime").toLocalDateTime());
                    r.setEndDateTime(rs.getTimestamp("end_datetime").toLocalDateTime());
                    r.setStatus(rs.getString("status"));
                    r.setSelectedEquipments(rs.getString("selected_equipments"));
                    r.setTotalPrice(rs.getDouble("total_price"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean cancel(int reservationId) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(CANCEL_RES)) {

            ps.setInt(1, reservationId);
            int rows = ps.executeUpdate();
            return rows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public tn.isimg.coworking.model.ReservationStatistics getStatistics(String period) {
        tn.isimg.coworking.model.ReservationStatistics stats = new tn.isimg.coworking.model.ReservationStatistics();
        stats.setCurrentPeriod(period);

        // Date filter condition
        String dateFilter = "";
        if ("week".equals(period)) {
            dateFilter = " WHERE start_datetime >= DATE_SUB(NOW(), INTERVAL 7 DAY) ";
        } else if ("month".equals(period)) {
            dateFilter = " WHERE start_datetime >= DATE_SUB(NOW(), INTERVAL 30 DAY) ";
        } else if ("year".equals(period)) {
            dateFilter = " WHERE start_datetime >= DATE_SUB(NOW(), INTERVAL 1 YEAR) ";
        }

        // 1. Basic Counts
        String STATS_QUERY = "SELECT " +
                "COUNT(*) as total, " +
                "SUM(CASE WHEN status = 'CONFIRMED' THEN 1 ELSE 0 END) as confirmed, " +
                "SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelled " +
                "FROM reservations" + dateFilter;

        // 2. Top Room
        String TOP_ROOM_QUERY = "SELECT ro.name " +
                "FROM reservations r " +
                "JOIN rooms ro ON r.room_id = ro.id " +
                dateFilter.replace("WHERE", "AND").replace("start_datetime", "r.start_datetime") +
                "GROUP BY ro.name " +
                "ORDER BY COUNT(*) DESC " +
                "LIMIT 1";
        // Fix for WHERE/AND if filter is empty (though here filter logic is additive to
        // JOIN)
        if (dateFilter.isEmpty()) {
            TOP_ROOM_QUERY = TOP_ROOM_QUERY.replace("AND", "WHERE"); // Quick fix if no period, but period has default.
        } else {
            TOP_ROOM_QUERY = "SELECT ro.name " +
                    "FROM reservations r " +
                    "JOIN rooms ro ON r.room_id = ro.id " +
                    dateFilter +
                    " GROUP BY ro.name " +
                    "ORDER BY COUNT(*) DESC " +
                    "LIMIT 1";
        }

        // 3. Trends (Daily or Monthly)
        String TRENDS_QUERY = "";
        if ("year".equals(period)) {
            TRENDS_QUERY = "SELECT DATE_FORMAT(start_datetime, '%Y-%m') as time_slot, COUNT(*) as count " +
                    "FROM reservations " + dateFilter +
                    "GROUP BY time_slot ORDER BY time_slot";
        } else {
            TRENDS_QUERY = "SELECT DATE(start_datetime) as time_slot, COUNT(*) as count " +
                    "FROM reservations " + dateFilter +
                    "GROUP BY time_slot ORDER BY time_slot";
        }

        // 4. Peak Hours (0-23)
        String PEAK_HOURS_QUERY = "SELECT HOUR(start_datetime) as res_hour, COUNT(*) as count " +
                "FROM reservations " + dateFilter +
                "GROUP BY res_hour ORDER BY res_hour";

        // 5. Top Clients
        String TOP_CLIENTS_QUERY = "SELECT u.id, u.full_name, COUNT(*) as count, SUM(r.total_price) as spent " +
                "FROM reservations r " +
                "JOIN users u ON r.user_id = u.id " +
                (dateFilter.isEmpty() ? " WHERE " : dateFilter + " AND ") + " r.status='CONFIRMED' " +
                "GROUP BY u.id, u.full_name " +
                "ORDER BY count DESC " +
                "LIMIT 5";

        try (Connection conn = DBConnection.getConnection()) {

            // 1. Basic Stats
            try (PreparedStatement ps = conn.prepareStatement(STATS_QUERY);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalReservations(rs.getInt("total"));
                    stats.setConfirmedReservations(rs.getInt("confirmed"));
                    stats.setCancelledReservations(rs.getInt("cancelled"));
                }
            }

            // 2. Top Room
            try (PreparedStatement ps = conn.prepareStatement(TOP_ROOM_QUERY);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setTopRoomName(rs.getString(1));
                } else {
                    stats.setTopRoomName("Aucune");
                }
            }

            // 3. Trends
            java.util.Map<String, Integer> trends = new java.util.LinkedHashMap<>();
            try (PreparedStatement ps = conn.prepareStatement(TRENDS_QUERY);
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    trends.put(rs.getString("time_slot"), rs.getInt("count"));
                }
            }
            stats.setTrendData(trends);

            // 4. Peak Hours
            java.util.Map<Integer, Integer> peaks = new java.util.TreeMap<>();
            try (PreparedStatement ps = conn.prepareStatement(PEAK_HOURS_QUERY);
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    peaks.put(rs.getInt("res_hour"), rs.getInt("count"));
                }
            }
            stats.setPeakHours(peaks);

            // 5. Top Clients
            java.util.List<tn.isimg.coworking.model.UserStats> clients = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(TOP_CLIENTS_QUERY);
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clients.add(new tn.isimg.coworking.model.UserStats(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getInt("count"),
                            rs.getDouble("spent")));
                }
            }
            stats.setTopClients(clients);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }
}
