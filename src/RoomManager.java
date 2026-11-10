import java.util.ArrayList;

public class RoomManager {

    private ArrayList<Room> rooms;
    private final RoomDAO roomDAO;

    public RoomManager() {
        roomDAO = new RoomDAO();
        rooms = roomDAO.getAllRooms();
    }

    // Add Room to MySQL Database
    public boolean addRoom(Room room) {

        if (room == null) {
            return false;
        }

        // Check duplicate Room ID
        for (Room existingRoom : rooms) {
            if (existingRoom.getRoomId()
                    .equalsIgnoreCase(room.getRoomId())) {

                System.out.println("Room ID already exists!");
                return false;
            }
        }

        // Save to database first
        if (!roomDAO.addRoom(room)) {
            System.out.println(
                    "Room could not be saved to database!");
            return false;
        }

        rooms.add(room);

        System.out.println("Room added successfully!");
        return true;
    }

    // View All Rooms
    public ArrayList<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    // Update Room Details in MySQL
    public boolean updateRoom(String roomId, String roomType,
                              String bedType, double dailyRate,
                              String status) {

        for (Room room : rooms) {

            if (room.getRoomId().equalsIgnoreCase(roomId)) {

                // Create updated Room object
                Room updatedRoom = new Room(
                        room.getRoomId(),
                        roomType,
                        bedType,
                        dailyRate,
                        status
                );

                // Update database first
                if (!roomDAO.updateRoom(updatedRoom)) {
                    System.out.println(
                            "Room could not be updated in database!");
                    return false;
                }

                // Update ArrayList after database success
                room.setRoomType(roomType);
                room.setBedType(bedType);
                room.setDailyRate(dailyRate);
                room.setStatus(status);

                System.out.println("Room updated successfully!");
                return true;
            }
        }

        return false;
    }

    // Remove Room from MySQL
    public boolean removeRoom(String roomId) {

        for (Room room : rooms) {

            if (room.getRoomId().equalsIgnoreCase(roomId)) {

                // Delete from database first
                if (!roomDAO.deleteRoom(room.getRoomId())) {
                    System.out.println(
                            "Room could not be deleted from database!");
                    return false;
                }

                // Remove from ArrayList after database success
                rooms.remove(room);

                System.out.println("Room removed successfully!");
                return true;
            }
        }

        return false;
    }
}
