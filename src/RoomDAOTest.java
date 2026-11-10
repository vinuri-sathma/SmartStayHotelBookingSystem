public class RoomDAOTest {

    public static void main(String[] args) {

        RoomDAO roomDAO = new RoomDAO();

        Room room = new Room(
                "R101",
                "Standard",
                "Double",
                6000.00,
                "Available"
        );

        boolean result = roomDAO.addRoom(room);

        System.out.println("Room Saved: " + result);
    }
}
