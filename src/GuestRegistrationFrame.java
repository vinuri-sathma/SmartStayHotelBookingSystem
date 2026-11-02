
import javax.swing.*;
import java.awt.*;

public class GuestRegistrationFrame extends JFrame {

    public GuestRegistrationFrame(GuestManager guestManager) {

        setTitle("SmartStay - Guest Registration");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 15));
        panel.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

        JTextField guestIdField = new JTextField();

        JComboBox<String> documentTypeBox = new JComboBox<>(
                new String[]{"NIC", "Passport"}
        );

        JTextField fullNameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField emailField = new JTextField();

        JButton registerButton = new JButton("Register");

        panel.add(new JLabel("Document Type:"));
        panel.add(documentTypeBox);

        panel.add(new JLabel("NIC / Passport Number:"));
        panel.add(guestIdField);

        panel.add(new JLabel("Full Name:"));
        panel.add(fullNameField);

        panel.add(new JLabel("Contact Number:"));
        panel.add(contactField);

        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        panel.add(new JLabel(""));
        panel.add(registerButton);

        add(panel);

        registerButton.addActionListener(e -> {

            String guestId = guestIdField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String contact = contactField.getText().trim();
            String email = emailField.getText().trim();

            if (guestId.isEmpty() || fullName.isEmpty()
                    || contact.isEmpty() || email.isEmpty()) {

                JOptionPane.showMessageDialog(this,
                        "Please fill in all fields.");
                return;
            }

            if (!contact.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this,
                        "Contact number must contain exactly 10 digits.");
                return;
            }

            if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid email address.");
                return;
            }

            String documentType = (String) documentTypeBox.getSelectedItem();

            if ("NIC".equals(documentType)) {

                if (!guestId.matches("(\\d{9}[VvXx]|\\d{12})")) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter a valid Sri Lankan NIC number.");
                    return;
                }

            } else if ("Passport".equals(documentType)) {

                if (!guestId.matches("[A-Za-z0-9]{6,20}")) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter a valid passport number.");
                    return;
                }
            }

            Guest guest = new Guest(
                    guestId, fullName, contact, email
            );

            boolean registered = guestManager.registerGuest(guest);

            if (registered) {
                JOptionPane.showMessageDialog(this,
                        "Registration successful!");

                // Open Guest Dashboard
                GuestDashboard dashboard = new GuestDashboard(guest);
                dashboard.setVisible(true);

                dispose();

            } else {
                JOptionPane.showMessageDialog(this,
                        "Guest ID already registered!");
            }
        });
    }
}
