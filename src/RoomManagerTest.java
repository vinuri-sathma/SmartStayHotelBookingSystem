public class RoomManagerTest {

    public static void main(String[] args) {

        RoomManager manager = new RoomManager();

        Room room1 = new Room(
                "R001", "Standard", "Single", 6000.0, "Available"
        );

        Room room2 = new Room(
                "R001", "Deluxe", "Double", 9000.0, "Available"
        );

        manager.addRoom(room1);
        manager.addRoom(room2);

        System.out.println("\nAll Rooms:");

        for (Room room : manager.getAllRooms()) {
            System.out.println(
                    room.getRoomId() + " - " +
                            room.getRoomType() + " - " +
                            room.getStatus()
            );
        }
    }
}