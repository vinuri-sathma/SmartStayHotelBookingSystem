import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GuestDAO {

    // Save Guest and password hash to MySQL
    public boolean addGuest(Guest guest, String passwordHash) {

        if (guest == null || passwordHash == null
                || passwordHash.isBlank()) {
            return false;
        }

        String sql = "INSERT INTO guests " +
                "(guest_id, full_name, contact_number, " +
                "email, password_hash) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, guest.getGuestId());
            statement.setString(2, guest.getFullName());
            statement.setString(3, guest.getContactNumber());
            statement.setString(4, guest.getEmail());
            statement.setString(5, passwordHash);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Error saving guest: " + e.getMessage()
            );
            return false;
        }
    }


    // Load all registered Guests from MySQL
    public ArrayList<Guest> getAllGuests() {

        ArrayList<Guest> guests = new ArrayList<>();

        String sql = "SELECT * FROM guests";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                Guest guest = new Guest(
                        resultSet.getString("guest_id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("contact_number"),
                        resultSet.getString("email")
                );

                guests.add(guest);
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error loading guests: " + e.getMessage()
            );
        }

        return guests;
    }


    // Find a registered guest using Guest ID
    public Guest findGuestById(String guestId) {

        String sql = "SELECT guest_id, full_name, " +
                "contact_number, email FROM guests " +
                "WHERE guest_id = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, guestId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return new Guest(
                            resultSet.getString("guest_id"),
                            resultSet.getString("full_name"),
                            resultSet.getString("contact_number"),
                            resultSet.getString("email")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error finding guest: " + e.getMessage()
            );
        }

        return null;
    }


    // Verify returning guest credentials
    public Guest authenticateGuest(
            String guestId, char[] password) {

        if (guestId == null || guestId.isBlank()
                || password == null || password.length == 0) {
            return null;
        }

        String sql = "SELECT guest_id, full_name, " +
                "contact_number, email, password_hash " +
                "FROM guests WHERE guest_id = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, guestId.trim());

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    String storedHash =
                            resultSet.getString("password_hash");

                    if (PasswordUtil.verifyPassword(
                            password, storedHash)) {

                        return new Guest(
                                resultSet.getString("guest_id"),
                                resultSet.getString("full_name"),
                                resultSet.getString("contact_number"),
                                resultSet.getString("email")
                        );
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error authenticating guest: " +
                            e.getMessage()
            );
        }

        return null;
    }

}
