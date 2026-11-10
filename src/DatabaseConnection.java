
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://127.0.0.1:3306/smartstay_db";

    private static final String USER = "smartstay_user";

    private static final String PASSWORD =
            System.getenv("SMARTSTAY_DB_PASSWORD");

    public static Connection getConnection()
            throws SQLException {

        if (PASSWORD == null) {
            throw new SQLException(
                    "SMARTSTAY_DB_PASSWORD is not configured.");
        }

        return DriverManager.getConnection(
                URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("Database Connected Successfully!");
        } catch (SQLException e) {
            System.out.println("Database Connection Failed!");
            System.out.println(e.getMessage());
        }
    }
}
