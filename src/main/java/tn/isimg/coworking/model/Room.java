package tn.isimg.coworking.model;

import java.io.Serializable;

public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    // MEETING, PHONE_BOX, TRAINING, DESK, VISIO
    private String type;
    private int capacity;
    // ex: "TV,Whiteboard,Video"
    private String equipment;
    // SILENT ou CASUAL
    private String zone;
    private double pricePerHour;

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public Room() {
        // constructeur sans argument obligatoire pour un JavaBean
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

}
