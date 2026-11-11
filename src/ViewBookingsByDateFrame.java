import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ViewBookingsByDateFrame extends JFrame {

    private final JTextField dateField;
    private final DefaultTableModel tableModel;
    private final JButton searchButton;

    public ViewBookingsByDateFrame() {

        setTitle("SmartStay - View Bookings by Date");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel(
                "View Reservations by Check-in Date",
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel searchPanel = new JPanel(new FlowLayout());

        searchPanel.add(new JLabel("Check-in Date (YYYY-MM-DD):"));

        dateField = new JTextField(15);
        searchPanel.add(dateField);

        searchButton = new JButton("Search");
        searchPanel.add(searchButton);

        String[] columns = {
                "Booking ID",
                "Guest Name",
                "Room ID",
                "Check-in",
                "Check-out",
                "Guests",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable bookingsTable = new JTable(tableModel);
        bookingsTable.setAutoCreateRowSorter(true);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        searchButton.addActionListener(e -> {

            String dateText = dateField.getText().trim();

            if (dateText.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "Please enter a check-in date."
                );
                return;
            }

            try {
                java.time.LocalDate checkInDate =
                        java.time.LocalDate.parse(dateText);

                BookingDAO bookingDAO = new BookingDAO();

                java.util.List<Object[]> results =
                        bookingDAO.getBookingsByCheckInDate(checkInDate);

                tableModel.setRowCount(0);

                for (Object[] row : results) {
                    tableModel.addRow(row);
                }

                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            this, "No bookings found for this date."
                    );
                }

            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(
                        this, "Please enter a valid date (YYYY-MM-DD)."
                );
            }
        });



        setLayout(new BorderLayout(10, 10));
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(bookingsTable), BorderLayout.CENTER);
    }
}
