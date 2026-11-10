import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final RoomManager roomManager = new RoomManager();
    private final GuestManager guestManager = new GuestManager();


    public MainFrame() {

        setTitle("SmartStay Hotel Booking System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel(
                "Welcome to SmartStay Hotel Booking System",
                SwingConstants.CENTER
        );

        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));

        JButton adminButton = new JButton("Administrator");
        adminButton.addActionListener(e -> {
            AdminDashboard dashboard = new AdminDashboard(roomManager);
            dashboard.setVisible(true);
        });
        JButton guestButton = new JButton("Guest");
        JButton returningGuestButton = new JButton("Returning Guest");

        guestButton.addActionListener(e -> {
            GuestRegistrationFrame registrationFrame =
                    new GuestRegistrationFrame(guestManager, roomManager);
            registrationFrame.setVisible(true);
        });


        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 15));

        adminButton.setPreferredSize(new Dimension(180, 45));
        guestButton.setPreferredSize(new Dimension(180, 45));
        returningGuestButton.setPreferredSize(new Dimension(180, 45));

        // Open Returning Guest Login window
        returningGuestButton.addActionListener(e -> {

            ReturningGuestFrame loginFrame =
                    new ReturningGuestFrame(roomManager);

            loginFrame.setVisible(true);
        });

        buttonPanel.add(adminButton);
        buttonPanel.add(guestButton);
        buttonPanel.add(returningGuestButton);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(180, 165));

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(welcomeLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalGlue());

        setLayout(new BorderLayout());
        add(centerPanel, BorderLayout.CENTER);


    }

}