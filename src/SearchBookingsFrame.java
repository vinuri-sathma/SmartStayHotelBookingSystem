import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

public class SearchBookingsFrame extends JFrame {

    public SearchBookingsFrame() {

        setTitle("SmartStay - Search Bookings");
        setSize(550, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel(
                "Search Guest Bookings",
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        );

        JComboBox<String> searchTypeCombo = new JComboBox<>(
                new String[]{"Booking ID", "Guest Name"}
        );

        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");

        panel.add(new JLabel("Search By:"));
        panel.add(searchTypeCombo);

        panel.add(new JLabel("Enter Value:"));
        panel.add(searchField);

        panel.add(new JLabel(""));
        panel.add(searchButton);

        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(panel, BorderLayout.NORTH);

        String[] columns = {
                "Booking ID",
                "Guest Name",
                "Room ID",
                "Check-in",
                "Check-out",
                "Guests",
                "Status"
        };

        DefaultTableModel tableModel = new DefaultTableModel(
                columns, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable resultsTable = new JTable(tableModel);
        resultsTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        add(scrollPane, BorderLayout.CENTER);

        setSize(850, 500);

        searchButton.addActionListener(e -> {

            String searchType =
                    (String) searchTypeCombo.getSelectedItem();

            String searchValue = searchField.getText().trim();

            if (searchValue.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "Please enter a search value."
                );
                return;
            }

            if ("Booking ID".equals(searchType)) {
                try {
                    if (Integer.parseInt(searchValue) <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            this, "Please enter a valid Booking ID."
                    );
                    return;
                }
            }

            BookingDAO bookingDAO = new BookingDAO();

            java.util.List<Object[]> results =
                    bookingDAO.searchBookings(searchType, searchValue);

            tableModel.setRowCount(0);

            for (Object[] row : results) {
                tableModel.addRow(row);
            }

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "No bookings found."
                );
            }
        });

    }
}
