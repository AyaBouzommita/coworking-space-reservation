package tn.isimg.coworking.model;

public class ReservationStatistics {
    private int totalReservations;
    private int confirmedReservations;
    private int cancelledReservations;
    private String topRoomName;

    // New fields for advanced statistics
    private java.util.Map<String, Integer> trendData;
    private java.util.Map<Integer, Integer> peakHours;
    private java.util.List<UserStats> topClients;
    private String currentPeriod;

    public ReservationStatistics() {
    }

    public ReservationStatistics(int totalReservations, int confirmedReservations, int cancelledReservations,
            String topRoomName) {
        this.totalReservations = totalReservations;
        this.confirmedReservations = confirmedReservations;
        this.cancelledReservations = cancelledReservations;
        this.topRoomName = topRoomName;
    }

    public int getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(int totalReservations) {
        this.totalReservations = totalReservations;
    }

    public int getConfirmedReservations() {
        return confirmedReservations;
    }

    public void setConfirmedReservations(int confirmedReservations) {
        this.confirmedReservations = confirmedReservations;
    }

    public int getCancelledReservations() {
        return cancelledReservations;
    }

    public void setCancelledReservations(int cancelledReservations) {
        this.cancelledReservations = cancelledReservations;
    }

    public String getTopRoomName() {
        return topRoomName;
    }

    public void setTopRoomName(String topRoomName) {
        this.topRoomName = topRoomName;
    }

    public java.util.Map<String, Integer> getTrendData() {
        return trendData;
    }

    public void setTrendData(java.util.Map<String, Integer> trendData) {
        this.trendData = trendData;
    }

    public java.util.Map<Integer, Integer> getPeakHours() {
        return peakHours;
    }

    public void setPeakHours(java.util.Map<Integer, Integer> peakHours) {
        this.peakHours = peakHours;
    }

    public java.util.List<UserStats> getTopClients() {
        return topClients;
    }

    public void setTopClients(java.util.List<UserStats> topClients) {
        this.topClients = topClients;
    }

    public String getCurrentPeriod() {
        return currentPeriod;
    }

    public void setCurrentPeriod(String currentPeriod) {
        this.currentPeriod = currentPeriod;
    }
}
