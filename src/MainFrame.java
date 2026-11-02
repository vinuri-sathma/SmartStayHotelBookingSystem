import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

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
            AdminDashboard dashboard = new AdminDashboard();
            dashboard.setVisible(true);
        });
        JButton guestButton = new JButton("Guest");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(adminButton);
        buttonPanel.add(guestButton);

        setLayout(new BorderLayout());

        add(welcomeLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

    }

}