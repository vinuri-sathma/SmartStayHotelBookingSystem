import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class SearchRoomsFrame extends JFrame {

    public SearchRoomsFrame(RoomManager roomManager) {

        setTitle("SmartStay - Search Available Rooms");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Filter Panel
        JPanel filterPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 15)
        );

        JComboBox<String> roomTypeBox = new JComboBox<>(
                new String[]{
                        "All Types",
                        "Standard",
                        "Deluxe",
                        "Suite",
                        "Family Suite"
                }
        );

        JTextField checkInField = new JTextField(10);
        JTextField checkOutField = new JTextField(10);

        checkInField.setToolTipText("YYYY-MM-DD");
        checkOutField.setToolTipText("YYYY-MM-DD");

        JButton searchButton = new JButton("Search Rooms");

        filterPanel.add(new JLabel("Room Type:"));
        filterPanel.add(roomTypeBox);

        filterPanel.add(new JLabel("Check-in:"));
        filterPanel.add(checkInField);

        filterPanel.add(new JLabel("Check-out:"));
        filterPanel.add(checkOutField);

        filterPanel.add(searchButton);

        add(filterPanel, BorderLayout.NORTH);

        // Room Table
        String[] columns = {
                "Room ID",
                "Room Type",
                "Bed Type",
                "Daily Rate (LKR)",
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

        JTable roomTable = new JTable(tableModel);

        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(tableModel);

        roomTable.setRowSorter(sorter);
        roomTable.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(roomTable);

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 20, 20, 20
                )
        );

        add(scrollPane, BorderLayout.CENTER);

        // Room Type Filter
        roomTypeBox.addActionListener(e -> {

            String selectedType =
                    (String) roomTypeBox.getSelectedItem();

            if ("All Types".equals(selectedType)) {
                sorter.setRowFilter(null);

            } else {
                sorter.setRowFilter(
                        RowFilter.regexFilter(
                                "^" + Pattern.quote(selectedType) + "$",
                                1
                        )
                );
            }
        });

        // Search Button - Date Validation and Availability
        searchButton.addActionListener(e -> {

            // Clear old search results
            tableModel.setRowCount(0);

            String checkInText =
                    checkInField.getText().trim();

            String checkOutText =
                    checkOutField.getText().trim();

            if (checkInText.isEmpty() ||
                    checkOutText.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter Check-in and Check-out dates."
                );
                return;
            }

            try {

                LocalDate checkIn =
                        LocalDate.parse(checkInText);

                LocalDate checkOut =
                        LocalDate.parse(checkOutText);

                // Check-in must be at least 2 days ahead
                if (checkIn.isBefore(
                        LocalDate.now().plusDays(2))) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Check-in must be at least 2 days from today."
                    );
                    return;
                }

                // Check-out must be after Check-in
                if (!checkOut.isAfter(checkIn)) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Check-out must be after Check-in."
                    );
                    return;
                }

                // Check room availability using MySQL
                BookingDAO bookingDAO = new BookingDAO();

                String selectedType =
                        (String) roomTypeBox.getSelectedItem();

                int availableCount = 0;

                for (Room room : roomManager.getAllRooms()) {

                    // Only include Available rooms
                    if (!room.getStatus()
                            .equalsIgnoreCase("Available")) {
                        continue;
                    }

                    // Filter by selected Room Type
                    if (!"All Types".equals(selectedType)
                            && !room.getRoomType()
                            .equals(selectedType)) {
                        continue;
                    }

                    // Check overlapping bookings
                    if (bookingDAO.hasOverlappingBooking(
                            room.getRoomId(),
                            checkIn,
                            checkOut)) {
                        continue;
                    }

                    // Add available room to table
                    tableModel.addRow(new Object[]{
                            room.getRoomId(),
                            room.getRoomType(),
                            room.getBedType(),
                            room.getDailyRate(),
                            room.getStatus()
                    });

                    availableCount++;
                }

                if (availableCount == 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "No available rooms found for the selected dates."
                    );
                }

            } catch (DateTimeParseException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid date format. Use YYYY-MM-DD."
                );
            }
        });
    }
}
