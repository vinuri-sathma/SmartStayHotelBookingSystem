public class Room {

    private String roomId;
    private String roomType;
    private String bedType;
    private double dailyRate;
    private String status;

    public Room(String roomId, String roomType, String bedType,
                double dailyRate, String status) {

        this.roomId = roomId;
        this.roomType = roomType;
        this.bedType = bedType;
        this.dailyRate = dailyRate;
        this.status = status;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getBedType() {
        return bedType;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {

        if (status.equals("Available") ||
                status.equals("Reserved") ||
                status.equals("Under Maintenance")) {

            this.status = status;

        } else {
            System.out.println("Invalid room status!");
        }
    }
}