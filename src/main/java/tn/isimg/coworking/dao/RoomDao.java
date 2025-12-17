package tn.isimg.coworking.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tn.isimg.coworking.model.Room;

public class RoomDao {

    private static final String SELECT_ALL = "SELECT id, name, type, capacity, equipment, zone, price_per_hour FROM rooms ORDER BY id";

    private static final String SELECT_BY_ID = "SELECT id, name, type, capacity, equipment, zone, price_per_hour FROM rooms WHERE id = ?";

    private static final String INSERT_ROOM = "INSERT INTO rooms (name, type, capacity, equipment, zone, price_per_hour) "
            +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_ROOM = "UPDATE rooms SET name = ?, type = ?, capacity = ?, equipment = ?, zone = ?, price_per_hour = ? "
            +
            "WHERE id = ?";

    private static final String DELETE_ROOM = "DELETE FROM rooms WHERE id = ?";

    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setName(rs.getString("name"));
                room.setType(rs.getString("type"));
                room.setCapacity(rs.getInt("capacity"));
                room.setEquipment(rs.getString("equipment"));
                room.setZone(rs.getString("zone"));
                room.setPricePerHour(rs.getDouble("price_per_hour"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    public Room findById(int id) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setName(rs.getString("name"));
                    room.setType(rs.getString("type"));
                    room.setCapacity(rs.getInt("capacity"));
                    room.setEquipment(rs.getString("equipment"));
                    room.setZone(rs.getString("zone"));
                    room.setPricePerHour(rs.getDouble("price_per_hour"));
                    return room;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int create(Room room) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(INSERT_ROOM, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, room.getName());
            ps.setString(2, room.getType());
            ps.setInt(3, room.getCapacity());
            ps.setString(4, room.getEquipment());
            ps.setString(5, room.getZone());
            ps.setDouble(6, room.getPricePerHour());

            int rows = ps.executeUpdate();
            if (rows == 1) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean update(Room room) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(UPDATE_ROOM)) {

            ps.setString(1, room.getName());
            ps.setString(2, room.getType());
            ps.setInt(3, room.getCapacity());
            ps.setString(4, room.getEquipment());
            ps.setString(5, room.getZone());
            ps.setDouble(6, room.getPricePerHour());
            ps.setInt(7, room.getId());

            int rows = ps.executeUpdate();
            return rows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(DELETE_ROOM)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
