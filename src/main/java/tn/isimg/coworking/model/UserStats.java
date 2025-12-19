package tn.isimg.coworking.model;

public class UserStats {
    private int userId;
    private String fullName;
    private int reservationCount;
    private double totalSpent;

    public UserStats() {
    }

    public UserStats(int userId, String fullName, int reservationCount, double totalSpent) {
        this.userId = userId;
        this.fullName = fullName;
        this.reservationCount = reservationCount;
        this.totalSpent = totalSpent;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getReservationCount() {
        return reservationCount;
    }

    public void setReservationCount(int reservationCount) {
        this.reservationCount = reservationCount;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }
}
