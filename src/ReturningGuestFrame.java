import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ReturningGuestFrame extends JFrame {

    public ReturningGuestFrame(RoomManager roomManager) {

        setTitle("SmartStay - Returning Guest Login");
        setSize(450, 280);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        );

        JTextField guestIdField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");

        panel.add(new JLabel("NIC / Passport Number:"));
        panel.add(guestIdField);

        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        panel.add(new JLabel(""));
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(e -> {

            String guestId = guestIdField.getText().trim();
            char[] password = passwordField.getPassword();

            try {
                if (guestId.isEmpty() || password.length == 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Please enter Guest ID and Password."
                    );
                    return;
                }

                GuestDAO guestDAO = new GuestDAO();

                Guest guest = guestDAO.authenticateGuest(
                        guestId, password
                );

                if (guest != null) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Login successful!"
                    );

                    GuestDashboard dashboard =
                            new GuestDashboard(guest, roomManager);

                    dashboard.setVisible(true);
                    dispose();

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Invalid Guest ID or Password."
                    );
                }

            } finally {
                Arrays.fill(password, '\0');
                passwordField.setText("");
            }
        });
    }
}
