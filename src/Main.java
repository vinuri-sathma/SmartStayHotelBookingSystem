public class Main {
    public static void main(String[] args) {

        Room room1 = new Room(
                "R001",
                "Standard",
                "Single",
                6000.0,
                "Available"
        );

        System.out.println("Room ID: " + room1.getRoomId());
        System.out.println("Initial Status: " + room1.getStatus());

        room1.setStatus("Reserved");
        System.out.println("Updated Status: " + room1.getStatus());

        room1.setStatus("Invalid");
        System.out.println("Final Status: " + room1.getStatus());
    }
}