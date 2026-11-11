import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ViewBookingsFrame extends JFrame {

    public ViewBookingsFrame(
            Guest guest,
            RoomManager roomManager) {

        setTitle("SmartStay - My Bookings");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columns = {
                "Booking ID",
                "Room ID",
                "Room Type",
                "Check-in",
                "Check-out",
                "Guests",
                "Status"
        };

        DefaultTableModel tableModel =
                new DefaultTableModel(columns, 0) {

                    @Override
                    public boolean isCellEditable(
                            int row, int column) {
                        return false;
                    }
                };

        JTable bookingTable = new JTable(tableModel);
        bookingTable.setRowHeight(25);
        bookingTable.setAutoCreateRowSorter(true);

        BookingDAO bookingDAO = new BookingDAO();

        // Load bookings belonging to the logged-in guest
        List<Booking> bookings =
                bookingDAO.getBookingsByGuestId(
                        guest, roomManager
                );

        // Display all bookings initially
        for (Booking booking : bookings) {

            Object[] row = {
                    booking.getBookingId(),
                    booking.getRoom().getRoomId(),
                    booking.getRoom().getRoomType(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getNumberOfGuests(),
                    booking.getBookingStatus()
            };

            tableModel.addRow(row);
        }

        JLabel titleLabel = new JLabel(
                "My Booking History",
                SwingConstants.CENTER
        );

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        // Date search controls
        JPanel searchPanel = new JPanel(new FlowLayout());

        JLabel dateLabel =
                new JLabel("Check-in Date (YYYY-MM-DD):");

        JTextField dateField = new JTextField(12);

        JButton searchButton =
                new JButton("Search");

        JButton showAllButton =
                new JButton("Show All");

        searchPanel.add(dateLabel);
        searchPanel.add(dateField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        // Search bookings by check-in date
        searchButton.addActionListener(e -> {

            String dateText = dateField.getText().trim();

            if (dateText.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a check-in date."
                );
                return;
            }

            try {
                LocalDate searchDate =
                        LocalDate.parse(dateText);

                tableModel.setRowCount(0);

                for (Booking booking : bookings) {

                    if (booking.getCheckInDate()
                            .equals(searchDate)) {

                        Object[] row = {
                                booking.getBookingId(),
                                booking.getRoom().getRoomId(),
                                booking.getRoom().getRoomType(),
                                booking.getCheckInDate(),
                                booking.getCheckOutDate(),
                                booking.getNumberOfGuests(),
                                booking.getBookingStatus()
                        };

                        tableModel.addRow(row);
                    }
                }

                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "No bookings found for this date."
                    );
                }

            } catch (DateTimeParseException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a valid date (YYYY-MM-DD)."
                );
            }
        });

        // Display all guest bookings again
        showAllButton.addActionListener(e -> {

            dateField.setText("");
            tableModel.setRowCount(0);

            for (Booking booking : bookings) {

                Object[] row = {
                        booking.getBookingId(),
                        booking.getRoom().getRoomId(),
                        booking.getRoom().getRoomType(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getNumberOfGuests(),
                        booking.getBookingStatus()
                };

                tableModel.addRow(row);
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        setLayout(new BorderLayout(10, 10));

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(bookingTable),
                BorderLayout.CENTER);
    }
}
