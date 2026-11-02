
import javax.swing.*;
import java.awt.*;

public class RemoveRoomFrame extends JFrame {

    public RemoveRoomFrame(RoomManager roomManager) {

        setTitle("SmartStay - Remove Room");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 15));
        panel.setBorder(
                BorderFactory.createEmptyBorder(30, 25, 30, 25)
        );

        JTextField roomIdField = new JTextField();
        JButton removeButton = new JButton("Remove Room");

        panel.add(new JLabel("Room ID:"));
        panel.add(roomIdField);
        panel.add(new JLabel(""));
        panel.add(removeButton);

        add(panel);

        removeButton.addActionListener(e -> {

            String roomId = roomIdField.getText().trim();

            if (roomId.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a Room ID.");
                return;
            }

            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to remove Room " + roomId + "?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {

                boolean removed = roomManager.removeRoom(roomId);

                if (removed) {
                    JOptionPane.showMessageDialog(this,
                            "Room removed successfully!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Room ID not found!");
                }
            }
        });
    }
}
