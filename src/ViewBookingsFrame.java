import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
        List<Booking> bookings = new ArrayList<>(
                bookingDAO.getBookingsByGuestId(
                        guest, roomManager
                )
        );

        // Display bookings in the table
        Runnable showAllBookings = () -> {

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
        };

        showAllBookings.run();

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

        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");
        JButton modifyButton = new JButton("Modify Booking");

        searchPanel.add(dateLabel);
        searchPanel.add(dateField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);
        searchPanel.add(modifyButton);

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

        // Display all bookings again
        showAllButton.addActionListener(e -> {

            dateField.setText("");
            showAllBookings.run();
        });

        // Modify selected booking
        modifyButton.addActionListener(e -> {

            int selectedRow = bookingTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a booking to modify."
                );
                return;
            }

            int modelRow =
                    bookingTable.convertRowIndexToModel(selectedRow);

            int bookingId = (int)
                    tableModel.getValueAt(modelRow, 0);

            Booking selectedBooking = null;

            for (Booking booking : bookings) {

                if (booking.getBookingId() == bookingId) {
                    selectedBooking = booking;
                    break;
                }
            }

            if (selectedBooking == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Booking not found."
                );
                return;
            }

            if (!selectedBooking.getBookingStatus()
                    .equalsIgnoreCase("Confirmed")) {

                JOptionPane.showMessageDialog(
                        this,
                        "Only confirmed bookings can be modified."
                );
                return;
            }

            // Create modification form
            JTextField checkInField = new JTextField(
                    selectedBooking.getCheckInDate().toString()
            );

            JTextField checkOutField = new JTextField(
                    selectedBooking.getCheckOutDate().toString()
            );

            JTextField guestsField = new JTextField(
                    String.valueOf(
                            selectedBooking.getNumberOfGuests()
                    )
            );

            JPanel modifyPanel = new JPanel(
                    new GridLayout(3, 2, 10, 10)
            );

            modifyPanel.add(new JLabel("New Check-in:"));
            modifyPanel.add(checkInField);

            modifyPanel.add(new JLabel("New Check-out:"));
            modifyPanel.add(checkOutField);

            modifyPanel.add(new JLabel("Number of Guests:"));
            modifyPanel.add(guestsField);

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    modifyPanel,
                    "Modify Booking",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (choice != JOptionPane.OK_OPTION) {
                return;
            }

            try {

                LocalDate newCheckIn =
                        LocalDate.parse(
                                checkInField.getText().trim()
                        );

                LocalDate newCheckOut =
                        LocalDate.parse(
                                checkOutField.getText().trim()
                        );

                int newGuests = Integer.parseInt(
                        guestsField.getText().trim()
                );

                if (newGuests <= 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Number of guests must be greater than zero."
                    );
                    return;
                }

                boolean updated = bookingDAO.updateBooking(
                        bookingId,
                        guest.getGuestId(),
                        newCheckIn,
                        newCheckOut,
                        newGuests
                );

                if (updated) {

                    // Reload updated bookings from MySQL
                    bookings.clear();

                    bookings.addAll(
                            bookingDAO.getBookingsByGuestId(
                                    guest, roomManager
                            )
                    );

                    dateField.setText("");
                    showAllBookings.run();

                    JOptionPane.showMessageDialog(
                            this,
                            "Booking updated successfully!"
                    );

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Booking could not be updated. " +
                                    "Please check the dates, availability " +
                                    "and modification deadline."
                    );
                }

            } catch (DateTimeParseException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter valid dates (YYYY-MM-DD)."
                );

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a valid number of guests."
                );
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
