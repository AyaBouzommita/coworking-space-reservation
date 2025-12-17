package tn.isimg.coworking.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private int roomId;
    private String roomName; // Nom de la salle (pour affichage)
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String status; // PENDING, CONFIRMED, CANCELLED
    private String selectedEquipments;
    private double totalPrice; // Store the calculated total price

    public String getSelectedEquipments() {
        return selectedEquipments;
    }

    public void setSelectedEquipments(String selectedEquipments) {
        this.selectedEquipments = selectedEquipments;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Reservation() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
