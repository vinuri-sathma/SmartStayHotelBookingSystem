
import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private final RoomManager roomManager;

    public AdminDashboard(RoomManager roomManager) {

        this.roomManager = roomManager;

        setTitle("SmartStay - Administrator Dashboard");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 1, 0, 15));

        JButton addRoomButton = new JButton("Add Room");
        JButton updateRoomButton = new JButton("Update Room");
        JButton removeRoomButton = new JButton("Remove Room");
        JButton viewRoomsButton = new JButton("View Rooms");

        // Add Room
        addRoomButton.addActionListener(e -> {
            AddRoomFrame addRoomFrame = new AddRoomFrame(roomManager);
            addRoomFrame.setVisible(true);
        });

        // Update Room
        updateRoomButton.addActionListener(e -> {
            UpdateRoomFrame updateRoomFrame = new UpdateRoomFrame(roomManager);
            updateRoomFrame.setVisible(true);
        });

        // Remove Room
        removeRoomButton.addActionListener(e -> {
            RemoveRoomFrame removeRoomFrame = new RemoveRoomFrame(roomManager);
            removeRoomFrame.setVisible(true);
        });

        // View Rooms
        viewRoomsButton.addActionListener(e -> {
            ViewRoomsFrame viewRoomsFrame = new ViewRoomsFrame(roomManager);
            viewRoomsFrame.setVisible(true);
        });

        panel.add(addRoomButton);
        panel.add(updateRoomButton);
        panel.add(removeRoomButton);
        panel.add(viewRoomsButton);

        panel.setPreferredSize(new Dimension(200, 220));

        setLayout(new GridBagLayout());
        add(panel);
    }
}
