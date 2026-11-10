import javax.swing.*;
import java.awt.*;

public class AddRoomFrame extends JFrame {

    private RoomManager roomManager;

    public AddRoomFrame(RoomManager roomManager) {

        this.roomManager = roomManager;

        setTitle("SmartStay - Add Room");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );
        panel.setLayout(new GridLayout(5, 2, 10, 10));

        // Room ID
        panel.add(new JLabel("Room ID:"));
        JTextField roomIdField = new JTextField();
        panel.add(roomIdField);

        // Room Type
        panel.add(new JLabel("Room Type:"));
        String[] roomTypes = {
                "Standard", "Deluxe", "Suite", "Family Suite"
        };
        JComboBox<String> roomTypeBox =
                new JComboBox<>(roomTypes);
        panel.add(roomTypeBox);

        // Daily Rate
        panel.add(new JLabel("Daily Rate (LKR):"));
        JTextField dailyRateField = new JTextField("6000");
        dailyRateField.setEditable(false);
        panel.add(dailyRateField);

        // Automatically update Daily Rate
        roomTypeBox.addActionListener(e -> {

            String selectedType =
                    (String) roomTypeBox.getSelectedItem();

            if ("Standard".equals(selectedType)) {
                dailyRateField.setText("6000");

            } else if ("Deluxe".equals(selectedType)) {
                dailyRateField.setText("9000");

            } else if ("Suite".equals(selectedType)) {
                dailyRateField.setText("14000");

            } else if ("Family Suite".equals(selectedType)) {
                dailyRateField.setText("18000");
            }
        });

        // Bed Type
        panel.add(new JLabel("Bed Type:"));
        String[] bedTypes = {
                "Single", "Double", "Queen", "King"
        };
        JComboBox<String> bedTypeBox =
                new JComboBox<>(bedTypes);
        panel.add(bedTypeBox);

        // Status
        panel.add(new JLabel("Status:"));
        String[] statuses = {
                "Available", "Reserved", "Under Maintenance"
        };
        JComboBox<String> statusBox =
                new JComboBox<>(statuses);
        panel.add(statusBox);

        // Save Button
        JButton saveButton = new JButton("Save Room");

        JPanel buttonPanel =
                new JPanel(new FlowLayout(FlowLayout.CENTER));

        buttonPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 0, 15, 0)
        );

        saveButton.setPreferredSize(
                new Dimension(150, 35)
        );
        buttonPanel.add(saveButton);

        // Save Room Action
        saveButton.addActionListener(e -> {

            String roomId = roomIdField.getText().trim();

            String roomType =
                    (String) roomTypeBox.getSelectedItem();

            String bedType =
                    (String) bedTypeBox.getSelectedItem();

            String status =
                    (String) statusBox.getSelectedItem();

            if (roomId.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a Room ID."
                );
                return;
            }

            double dailyRate =
                    Double.parseDouble(
                            dailyRateField.getText()
                    );

            Room room = new Room(
                    roomId,
                    roomType,
                    bedType,
                    dailyRate,
                    status
            );

            boolean added = roomManager.addRoom(room);

            if (added) {
                JOptionPane.showMessageDialog(
                        this,
                        "Room added successfully!"
                );
                dispose();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Room could not be added. Check the Room ID or database connection."
                );
            }
        });

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
