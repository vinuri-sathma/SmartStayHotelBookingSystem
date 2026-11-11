import javax.swing.*;
import java.awt.*;

public class CancelBookingFrame extends JFrame {

    public CancelBookingFrame(Guest guest, RoomManager roomManager) {

        setTitle("SmartStay - Cancel Booking");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel(
                "Cancel My Booking",
                SwingConstants.CENTER
        );

        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 15));
        panel.setBorder(
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        );

        JTextField bookingIdField = new JTextField();
        JButton cancelButton = new JButton("Cancel Booking");

        panel.add(new JLabel("Booking ID:"));
        panel.add(bookingIdField);
        panel.add(new JLabel(""));
        panel.add(cancelButton);

        cancelButton.addActionListener(e -> {

            String input = bookingIdField.getText().trim();

            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "Please enter a Booking ID."
                );
                return;
            }

            int bookingId;

            try {
                bookingId = Integer.parseInt(input);

                if (bookingId <= 0) {
                    throw new NumberFormatException();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this, "Please enter a valid Booking ID."
                );
                return;
            }

            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to cancel Booking #" + bookingId + "?",
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }

            BookingDAO bookingDAO = new BookingDAO();

            boolean cancelled = bookingDAO.cancelBooking(
                    bookingId,
                    guest.getGuestId()
            );

            if (cancelled) {
                JOptionPane.showMessageDialog(
                        this, "Booking cancelled successfully!"
                );
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Unable to cancel booking.\n" +
                                "Please check the Booking ID, booking status,\n" +
                                "and 24-hour cancellation restriction."
                );
            }
        });


        add(titleLabel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }
}
