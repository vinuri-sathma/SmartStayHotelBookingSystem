import javax.swing.*;
import java.awt.*;

public class UpdateRoomFrame extends JFrame {

    public UpdateRoomFrame(RoomManager roomManager) {

        setTitle("SmartStay - Update Room");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(
                new GridLayout(6, 2, 10, 15)
        );
        panel.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

        // Room ID dropdown
        JComboBox<String> roomIdBox = new JComboBox<>();

        for (Room room : roomManager.getAllRooms()) {
            roomIdBox.addItem(room.getRoomId());
        }

        JComboBox<String> roomTypeBox = new JComboBox<>(
                new String[]{
                        "Standard", "Deluxe",
                        "Suite", "Family Suite"
                }
        );

        JComboBox<String> bedTypeBox = new JComboBox<>(
                new String[]{
                        "Single", "Double", "Queen", "King"
                }
        );

        JTextField dailyRateField = new JTextField();
        dailyRateField.setEditable(false);

        JComboBox<String> statusBox = new JComboBox<>(
                new String[]{
                        "Available", "Reserved",
                        "Under Maintenance"
                }
        );

        // Load selected Room details
        Runnable loadRoomDetails = () -> {

            String selectedRoomId =
                    (String) roomIdBox.getSelectedItem();

            if (selectedRoomId == null) {
                return;
            }

            for (Room room : roomManager.getAllRooms()) {

                if (room.getRoomId()
                        .equalsIgnoreCase(selectedRoomId)) {

                    roomTypeBox.setSelectedItem(
                            room.getRoomType()
                    );

                    bedTypeBox.setSelectedItem(
                            room.getBedType()
                    );

                    dailyRateField.setText(
                            String.valueOf(room.getDailyRate())
                    );

                    statusBox.setSelectedItem(
                            room.getStatus()
                    );

                    break;
                }
            }
        };

        // Auto-fill details when Room ID changes
        roomIdBox.addActionListener(e ->
                loadRoomDetails.run()
        );

        // Auto-update Daily Rate when Room Type changes
        roomTypeBox.addActionListener(e -> {

            String type =
                    (String) roomTypeBox.getSelectedItem();

            if ("Standard".equals(type)) {
                dailyRateField.setText("6000");

            } else if ("Deluxe".equals(type)) {
                dailyRateField.setText("9000");

            } else if ("Suite".equals(type)) {
                dailyRateField.setText("14000");

            } else if ("Family Suite".equals(type)) {
                dailyRateField.setText("18000");
            }
        });

        // Form fields
        panel.add(new JLabel("Room ID:"));
        panel.add(roomIdBox);

        panel.add(new JLabel("Room Type:"));
        panel.add(roomTypeBox);

        panel.add(new JLabel("Bed Type:"));
        panel.add(bedTypeBox);

        panel.add(new JLabel("Daily Rate (LKR):"));
        panel.add(dailyRateField);

        panel.add(new JLabel("Status:"));
        panel.add(statusBox);

        JButton updateButton = new JButton("Update Room");

        panel.add(new JLabel(""));
        panel.add(updateButton);

        add(panel);

        // Load first Room automatically
        loadRoomDetails.run();

        if (roomIdBox.getItemCount() == 0) {
            updateButton.setEnabled(false);
            JOptionPane.showMessageDialog(
                    this,
                    "No rooms available to update."
            );
        }

        // Update Room
        updateButton.addActionListener(e -> {

            String roomId =
                    (String) roomIdBox.getSelectedItem();

            if (roomId == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a Room ID."
                );
                return;
            }

            boolean updated = roomManager.updateRoom(
                    roomId,
                    (String) roomTypeBox.getSelectedItem(),
                    (String) bedTypeBox.getSelectedItem(),
                    Double.parseDouble(
                            dailyRateField.getText()
                    ),
                    (String) statusBox.getSelectedItem()
            );

            if (updated) {
                JOptionPane.showMessageDialog(
                        this,
                        "Room updated successfully!"
                );
                dispose();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Room could not be updated. Check the database connection."
                );
            }
        });
    }
}
