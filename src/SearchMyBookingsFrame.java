import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SearchMyBookingsFrame extends JFrame {

    private final Guest guest;
    private final JComboBox<String> searchTypeCombo;
    private final JTextField searchField;
    private final JButton searchButton;
    private final DefaultTableModel tableModel;

    public SearchMyBookingsFrame(Guest guest) {

        this.guest = guest;

        setTitle("SmartStay - Search My Bookings");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel(
                "Search My Bookings",
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel searchPanel = new JPanel(
                new GridLayout(3, 2, 10, 15)
        );

        searchPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 40, 20, 40)
        );

        searchTypeCombo = new JComboBox<>(
                new String[]{"Booking ID", "Guest Name"}
        );

        searchField = new JTextField();
        searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Search By:"));
        searchPanel.add(searchTypeCombo);

        searchPanel.add(new JLabel("Enter Value:"));
        searchPanel.add(searchField);

        searchPanel.add(new JLabel(""));
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
                    bookingDAO.searchMyBookings(
                            guest.getGuestId(),
                            searchType,
                            searchValue
                    );

            tableModel.setRowCount(0);

            for (Object[] row : results) {
                tableModel.addRow(row);
            }

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "No matching bookings found."
                );
            }
        });


        setLayout(new BorderLayout(10, 10));
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(bookingsTable), BorderLayout.CENTER);
    }
}
