import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RoomDAO {

    // Add Room to MySQL Database
    public boolean addRoom(Room room) {

        String sql = "INSERT INTO rooms " +
                "(room_id, room_type, bed_type, daily_rate, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, room.getRoomId());
            statement.setString(2, room.getRoomType());
            statement.setString(3, room.getBedType());
            statement.setDouble(4, room.getDailyRate());
            statement.setString(5, room.getStatus());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Error saving room: " + e.getMessage());
            return false;
        }
    }

    // Load All Rooms from MySQL Database
    public ArrayList<Room> getAllRooms() {

        ArrayList<Room> rooms = new ArrayList<>();

        String sql = "SELECT * FROM rooms";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                Room room = new Room(
                        resultSet.getString("room_id"),
                        resultSet.getString("room_type"),
                        resultSet.getString("bed_type"),
                        resultSet.getDouble("daily_rate"),
                        resultSet.getString("status")
                );

                rooms.add(room);
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error loading rooms: " + e.getMessage());
        }

        return rooms;
    }

    // Update Existing Room in MySQL Database
    public boolean updateRoom(Room room) {

        String sql = "UPDATE rooms SET " +
                "room_type = ?, " +
                "bed_type = ?, " +
                "daily_rate = ?, " +
                "status = ? " +
                "WHERE room_id = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, room.getRoomType());
            statement.setString(2, room.getBedType());
            statement.setDouble(3, room.getDailyRate());
            statement.setString(4, room.getStatus());
            statement.setString(5, room.getRoomId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Error updating room: " + e.getMessage());
            return false;
        }
    }

    // Delete Room from MySQL Database
    public boolean deleteRoom(String roomId) {

        String sql = "DELETE FROM rooms WHERE room_id = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, roomId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Error deleting room: " + e.getMessage());
            return false;
        }
    }
}
