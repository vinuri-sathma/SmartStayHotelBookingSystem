import java.util.ArrayList;

public class RoomManager {

    private ArrayList<Room> rooms;

    public RoomManager() {
        rooms = new ArrayList<>();
    }
    public boolean addRoom(Room room) {

        for (Room existingRoom : rooms) {
            if (existingRoom.getRoomId().equals(room.getRoomId())) {
                System.out.println("Room ID already exists!");
                return false;
            }
        }

        rooms.add(room);
        System.out.println("Room added successfully!");
        return true;
    }

    public ArrayList<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

}