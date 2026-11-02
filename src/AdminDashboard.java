
import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private final RoomManager roomManager = new RoomManager();

    public AdminDashboard() {

        setTitle("SmartStay - Administrator Dashboard");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 1, 0, 15));

        JButton addRoomButton = new JButton("Add Room");
        JButton updateRoomButton = new JButton("Update Room");
        JButton removeRoomButton = new JButton("Remove Room");
        JButton viewRoomsButton = new JButton("View Rooms");

        // Open the Add Room form
        addRoomButton.addActionListener(e -> {
            AddRoomFrame addRoomFrame = new AddRoomFrame(roomManager);
            addRoomFrame.setVisible(true);
        });

        panel.add(addRoomButton);
        panel.add(updateRoomButton);
        panel.add(removeRoomButton);
        panel.add(viewRoomsButton);

        // Set button panel size
        panel.setPreferredSize(new Dimension(200, 220));

        // Center the buttons in the window
        setLayout(new GridBagLayout());
        add(panel);
    }
}
