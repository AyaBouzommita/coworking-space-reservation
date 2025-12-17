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
            "       (start_datetime < ? AND end_datetime > ?) " + // chevauchement strict
            "    OR (start_datetime >= ? AND start_datetime < ?) " + // début dans l'intervalle
            "    OR (end_datetime > ? AND end_datetime <= ?) " + // fin dans l'intervalle
            "  )";

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
}
