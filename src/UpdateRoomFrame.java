
import javax.swing.*;
import java.awt.*;

public class UpdateRoomFrame extends JFrame {

    public UpdateRoomFrame(RoomManager roomManager) {

        setTitle("SmartStay - Update Room");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JTextField roomIdField = new JTextField();

        JComboBox<String> roomTypeBox = new JComboBox<>(
                new String[]{"Standard", "Deluxe", "Suite", "Family Suite"}
        );

        JComboBox<String> bedTypeBox = new JComboBox<>(
                new String[]{"Single", "Double", "Queen", "King"}
        );

        JTextField dailyRateField = new JTextField("6000");
        dailyRateField.setEditable(false);

        JComboBox<String> statusBox = new JComboBox<>(
                new String[]{"Available", "Reserved", "Under Maintenance"}
        );

        roomTypeBox.addActionListener(e -> {
            String type = (String) roomTypeBox.getSelectedItem();

            if ("Standard".equals(type)) {
                dailyRateField.setText("6000");
            } else if ("Deluxe".equals(type)) {
                dailyRateField.setText("9000");
            } else if ("Suite".equals(type)) {
                dailyRateField.setText("14000");
            } else {
                dailyRateField.setText("18000");
            }
        });

        JButton updateButton = new JButton("Update Room");

        panel.add(new JLabel("Room ID:"));
        panel.add(roomIdField);

        panel.add(new JLabel("Room Type:"));
        panel.add(roomTypeBox);

        panel.add(new JLabel("Bed Type:"));
        panel.add(bedTypeBox);

        panel.add(new JLabel("Daily Rate (LKR):"));
        panel.add(dailyRateField);

        panel.add(new JLabel("Status:"));
        panel.add(statusBox);

        panel.add(new JLabel(""));
        panel.add(updateButton);

        add(panel);

        updateButton.addActionListener(e -> {

            String roomId = roomIdField.getText().trim();

            if (roomId.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a Room ID.");
                return;
            }

            boolean updated = roomManager.updateRoom(
                    roomId,
                    (String) roomTypeBox.getSelectedItem(),
                    (String) bedTypeBox.getSelectedItem(),
                    Double.parseDouble(dailyRateField.getText()),
                    (String) statusBox.getSelectedItem()
            );

            if (updated) {
                JOptionPane.showMessageDialog(this,
                        "Room updated successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Room ID not found!");
            }
        });
    }
}
