import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;


public class BookingDAO {

    // Check if a room has overlapping bookings
    public boolean hasOverlappingBooking(
            String roomId,
            LocalDate checkIn,
            LocalDate checkOut) {

        String sql = "SELECT COUNT(*) FROM bookings " +
                "WHERE room_id = ? " +
                "AND booking_status = 'Confirmed' " +
                "AND check_in < ? " +
                "AND check_out > ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, roomId);
            statement.setDate(
                    2, java.sql.Date.valueOf(checkOut)
            );
            statement.setDate(
                    3, java.sql.Date.valueOf(checkIn)
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error checking booking availability: "
                            + e.getMessage()
            );

            // Prevent booking when availability
            // cannot be verified
            return true;
        }

        return false;
    }

    // Save a new booking to MySQL
    public boolean addBooking(Booking booking) {

        String sql = "INSERT INTO bookings " +
                "(guest_id, room_id, check_in, check_out, " +
                "number_of_guests, booking_status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setString(
                    1, booking.getGuest().getGuestId()
            );

            statement.setString(
                    2, booking.getRoom().getRoomId()
            );

            statement.setDate(
                    3,
                    java.sql.Date.valueOf(
                            booking.getCheckInDate()
                    )
            );

            statement.setDate(
                    4,
                    java.sql.Date.valueOf(
                            booking.getCheckOutDate()
                    )
            );

            statement.setInt(
                    5, booking.getNumberOfGuests()
            );

            statement.setString(
                    6, booking.getBookingStatus()
            );

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {

                try (ResultSet generatedKeys =
                             statement.getGeneratedKeys()) {

                    if (generatedKeys.next()) {
                        booking.setBookingId(
                                generatedKeys.getInt(1)
                        );
                    }
                }

                return true;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error saving booking: " + e.getMessage()
            );
        }

        return false;
    }

    // Retrieve bookings belonging to a specific guest
    public List<Booking> getBookingsByGuestId(
            Guest guest,
            RoomManager roomManager) {

        List<Booking> bookings = new ArrayList<>();

        String sql = "SELECT * FROM bookings " +
                "WHERE guest_id = ? " +
                "ORDER BY check_in DESC";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, guest.getGuestId());

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    String roomId =
                            resultSet.getString("room_id");

                    Room selectedRoom = null;

                    for (Room room : roomManager.getAllRooms()) {
                        if (room.getRoomId().equals(roomId)) {
                            selectedRoom = room;
                            break;
                        }
                    }

                    if (selectedRoom == null) {
                        System.out.println(
                                "Room not found: " + roomId
                        );
                        continue;
                    }

                    Booking booking = new Booking(
                            guest,
                            selectedRoom,
                            resultSet.getDate("check_in").toLocalDate(),
                            resultSet.getDate("check_out").toLocalDate(),
                            resultSet.getInt("number_of_guests")
                    );

                    booking.setBookingId(
                            resultSet.getInt("booking_id")
                    );

                    booking.setBookingStatus(
                            resultSet.getString("booking_status")
                    );

                    bookings.add(booking);
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error loading guest bookings: " +
                            e.getMessage()
            );
        }

        return bookings;
    }

}
