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
            return true;
        }

        return false;
    }


    // Save a new booking safely using a database transaction
    public boolean addBooking(Booking booking) {

        if (booking == null ||
                booking.getGuest() == null ||
                booking.getRoom() == null ||
                booking.getCheckInDate() == null ||
                booking.getCheckOutDate() == null ||
                booking.getNumberOfGuests() <= 0 ||
                booking.getCheckInDate().isBefore(
                        LocalDate.now().plusDays(2)) ||
                !booking.getCheckOutDate().isAfter(
                        booking.getCheckInDate())) {
            return false;
        }

        String lockSql =
                "SELECT status FROM rooms " +
                        "WHERE room_id = ? FOR UPDATE";

        String overlapSql =
                "SELECT COUNT(*) FROM bookings " +
                        "WHERE room_id = ? " +
                        "AND booking_status = 'Confirmed' " +
                        "AND check_in < ? AND check_out > ?";

        String insertSql =
                "INSERT INTO bookings " +
                        "(guest_id, room_id, check_in, check_out, " +
                        "number_of_guests, booking_status) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                // Lock the room before checking availability
                try (PreparedStatement statement =
                             connection.prepareStatement(lockSql)) {

                    statement.setString(
                            1, booking.getRoom().getRoomId()
                    );

                    try (ResultSet rs = statement.executeQuery()) {

                        if (!rs.next() ||
                                !"Available".equalsIgnoreCase(
                                        rs.getString("status"))) {
                            connection.rollback();
                            return false;
                        }
                    }
                }

                // Check for overlapping confirmed bookings
                try (PreparedStatement statement =
                             connection.prepareStatement(overlapSql)) {

                    statement.setString(
                            1, booking.getRoom().getRoomId()
                    );
                    statement.setDate(
                            2, java.sql.Date.valueOf(
                                    booking.getCheckOutDate())
                    );
                    statement.setDate(
                            3, java.sql.Date.valueOf(
                                    booking.getCheckInDate())
                    );

                    try (ResultSet rs = statement.executeQuery()) {
                        if (!rs.next() || rs.getInt(1) > 0) {
                            connection.rollback();
                            return false;
                        }
                    }
                }

                // Insert booking while holding the room lock
                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     insertSql,
                                     Statement.RETURN_GENERATED_KEYS)) {

                    statement.setString(
                            1, booking.getGuest().getGuestId()
                    );
                    statement.setString(
                            2, booking.getRoom().getRoomId()
                    );
                    statement.setDate(
                            3, java.sql.Date.valueOf(
                                    booking.getCheckInDate())
                    );
                    statement.setDate(
                            4, java.sql.Date.valueOf(
                                    booking.getCheckOutDate())
                    );
                    statement.setInt(
                            5, booking.getNumberOfGuests()
                    );
                    statement.setString(6, "Confirmed");

                    if (statement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }

                    try (ResultSet keys =
                                 statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            connection.rollback();
                            return false;
                        }
                        booking.setBookingId(keys.getInt(1));
                    }
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                connection.rollback();
                System.out.println(
                        "Error saving booking: " + e.getMessage()
                );
                return false;

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println(
                    "Database error: " + e.getMessage()
            );
            return false;
        }
    }


    // Cancel a confirmed booking belonging to the logged-in guest
    public boolean cancelBooking(int bookingId, String guestId) {

        String sql = "UPDATE bookings " +
                "SET booking_status = 'Cancelled' " +
                "WHERE booking_id = ? " +
                "AND guest_id = ? " +
                "AND booking_status = 'Confirmed' " +
                "AND TIMESTAMP(check_in) > DATE_ADD(NOW(), INTERVAL 24 HOUR)";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, bookingId);
            statement.setString(2, guestId);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println(
                    "Error cancelling booking: " + e.getMessage()
            );
            return false;
        }
    }

    // Search all bookings by Booking ID or Guest Name (Admin)
    public List<Object[]> searchBookings(
            String searchType,
            String searchValue) {

        List<Object[]> results = new ArrayList<>();

        String sql;

        if ("Booking ID".equals(searchType)) {
            sql = "SELECT b.booking_id, g.full_name, " +
                    "b.room_id, b.check_in, b.check_out, " +
                    "b.number_of_guests, b.booking_status " +
                    "FROM bookings b " +
                    "JOIN guests g ON b.guest_id = g.guest_id " +
                    "WHERE b.booking_id = ?";
        } else if ("Guest Name".equals(searchType)) {
            sql = "SELECT b.booking_id, g.full_name, " +
                    "b.room_id, b.check_in, b.check_out, " +
                    "b.number_of_guests, b.booking_status " +
                    "FROM bookings b " +
                    "JOIN guests g ON b.guest_id = g.guest_id " +
                    "WHERE g.full_name LIKE ? " +
                    "ORDER BY b.check_in DESC";
        } else {
            return results;
        }

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            if ("Booking ID".equals(searchType)) {

                int bookingId =
                        Integer.parseInt(searchValue.trim());

                statement.setInt(1, bookingId);

            } else {
                statement.setString(
                        1, "%" + searchValue.trim() + "%"
                );
            }

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    Object[] row = {
                            resultSet.getInt("booking_id"),
                            resultSet.getString("full_name"),
                            resultSet.getString("room_id"),
                            resultSet.getDate("check_in"),
                            resultSet.getDate("check_out"),
                            resultSet.getInt("number_of_guests"),
                            resultSet.getString("booking_status")
                    };

                    results.add(row);
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid Booking ID.");

        } catch (SQLException e) {
            System.out.println(
                    "Error searching bookings: " + e.getMessage()
            );
        }

        return results;
    }

    // Search all bookings by check-in date (Admin)
    public List<Object[]> getBookingsByCheckInDate(
            LocalDate checkInDate) {

        List<Object[]> results = new ArrayList<>();

        String sql = "SELECT b.booking_id, g.full_name, " +
                "b.room_id, b.check_in, b.check_out, " +
                "b.number_of_guests, b.booking_status " +
                "FROM bookings b " +
                "JOIN guests g ON b.guest_id = g.guest_id " +
                "WHERE b.check_in = ? " +
                "ORDER BY b.booking_id";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setDate(
                    1, java.sql.Date.valueOf(checkInDate)
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    Object[] row = {
                            resultSet.getInt("booking_id"),
                            resultSet.getString("full_name"),
                            resultSet.getString("room_id"),
                            resultSet.getDate("check_in"),
                            resultSet.getDate("check_out"),
                            resultSet.getInt("number_of_guests"),
                            resultSet.getString("booking_status")
                    };

                    results.add(row);
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error loading bookings by date: " + e.getMessage()
            );
        }

        return results;
    }

    // Search bookings belonging only to the logged-in guest
    public List<Object[]> searchMyBookings(
            String guestId,
            String searchType,
            String searchValue) {

        List<Object[]> results = new ArrayList<>();

        String sql = "SELECT b.booking_id, g.full_name, " +
                "b.room_id, b.check_in, b.check_out, " +
                "b.number_of_guests, b.booking_status " +
                "FROM bookings b " +
                "JOIN guests g ON b.guest_id = g.guest_id " +
                "WHERE b.guest_id = ? ";

        if ("Booking ID".equals(searchType)) {
            sql += "AND b.booking_id = ?";
        } else if ("Guest Name".equals(searchType)) {
            sql += "AND g.full_name LIKE ?";
        } else {
            return results;
        }

        sql += " ORDER BY b.check_in DESC";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, guestId);

            if ("Booking ID".equals(searchType)) {

                int bookingId =
                        Integer.parseInt(searchValue.trim());

                statement.setInt(2, bookingId);

            } else {
                statement.setString(
                        2, "%" + searchValue.trim() + "%"
                );
            }

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    Object[] row = {
                            resultSet.getInt("booking_id"),
                            resultSet.getString("full_name"),
                            resultSet.getString("room_id"),
                            resultSet.getDate("check_in"),
                            resultSet.getDate("check_out"),
                            resultSet.getInt("number_of_guests"),
                            resultSet.getString("booking_status")
                    };

                    results.add(row);
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid Booking ID.");

        } catch (SQLException e) {
            System.out.println(
                    "Error searching guest bookings: " +
                            e.getMessage()
            );
        }

        return results;
    }


    // Modify a confirmed booking belonging to the guest
    public boolean updateBooking(
            int bookingId,
            String guestId,
            LocalDate newCheckIn,
            LocalDate newCheckOut,
            int newNumberOfGuests) {

        if (bookingId <= 0 ||
                guestId == null || guestId.isBlank() ||
                newCheckIn == null || newCheckOut == null ||
                newNumberOfGuests <= 0 ||
                newCheckIn.isBefore(LocalDate.now().plusDays(2)) ||
                !newCheckOut.isAfter(newCheckIn)) {
            return false;
        }

        String findBookingSql =
                "SELECT room_id FROM bookings " +
                        "WHERE booking_id = ? AND guest_id = ?";

        String lockRoomSql =
                "SELECT status FROM rooms " +
                        "WHERE room_id = ? FOR UPDATE";

        String lockBookingSql =
                "SELECT check_in, booking_status FROM bookings " +
                        "WHERE booking_id = ? AND guest_id = ? " +
                        "FOR UPDATE";

        String overlapSql =
                "SELECT COUNT(*) FROM bookings " +
                        "WHERE room_id = ? " +
                        "AND booking_id <> ? " +
                        "AND booking_status = 'Confirmed' " +
                        "AND check_in < ? AND check_out > ?";

        String updateSql =
                "UPDATE bookings SET check_in = ?, " +
                        "check_out = ?, number_of_guests = ? " +
                        "WHERE booking_id = ? AND guest_id = ? " +
                        "AND booking_status = 'Confirmed'";

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                String roomId;

                // Find the room belonging to this booking
                try (PreparedStatement statement =
                             connection.prepareStatement(findBookingSql)) {

                    statement.setInt(1, bookingId);
                    statement.setString(2, guestId);

                    try (ResultSet rs = statement.executeQuery()) {

                        if (!rs.next()) {
                            connection.rollback();
                            return false;
                        }

                        roomId = rs.getString("room_id");
                    }
                }

                // Lock the room first
                try (PreparedStatement statement =
                             connection.prepareStatement(lockRoomSql)) {

                    statement.setString(1, roomId);

                    try (ResultSet rs = statement.executeQuery()) {

                        if (!rs.next() ||
                                !"Available".equalsIgnoreCase(
                                        rs.getString("status"))) {

                            connection.rollback();
                            return false;
                        }
                    }
                }

                // Lock and verify the booking
                try (PreparedStatement statement =
                             connection.prepareStatement(lockBookingSql)) {

                    statement.setInt(1, bookingId);
                    statement.setString(2, guestId);

                    try (ResultSet rs = statement.executeQuery()) {

                        if (!rs.next() ||
                                !"Confirmed".equalsIgnoreCase(
                                        rs.getString("booking_status"))) {

                            connection.rollback();
                            return false;
                        }

                        LocalDate originalCheckIn =
                                rs.getDate("check_in").toLocalDate();

                        if (!originalCheckIn.atStartOfDay().isAfter(
                                java.time.LocalDateTime.now()
                                        .plusHours(24))) {

                            connection.rollback();
                            return false;
                        }
                    }
                }

                // Check overlapping bookings
                try (PreparedStatement statement =
                             connection.prepareStatement(overlapSql)) {

                    statement.setString(1, roomId);
                    statement.setInt(2, bookingId);
                    statement.setDate(
                            3, java.sql.Date.valueOf(newCheckOut)
                    );
                    statement.setDate(
                            4, java.sql.Date.valueOf(newCheckIn)
                    );

                    try (ResultSet rs = statement.executeQuery()) {

                        if (!rs.next() || rs.getInt(1) > 0) {
                            connection.rollback();
                            return false;
                        }
                    }
                }

                // Update booking details
                try (PreparedStatement statement =
                             connection.prepareStatement(updateSql)) {

                    statement.setDate(
                            1, java.sql.Date.valueOf(newCheckIn)
                    );
                    statement.setDate(
                            2, java.sql.Date.valueOf(newCheckOut)
                    );
                    statement.setInt(3, newNumberOfGuests);
                    statement.setInt(4, bookingId);
                    statement.setString(5, guestId);

                    if (statement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }

                connection.commit();
                return true;

            } catch (SQLException e) {

                connection.rollback();

                System.out.println(
                        "Error modifying booking: " + e.getMessage()
                );

                return false;

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Database error: " + e.getMessage()
            );

            return false;
        }
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
                    "Error loading guest bookings: " + e.getMessage()
            );
        }

        return bookings;
    }

}
