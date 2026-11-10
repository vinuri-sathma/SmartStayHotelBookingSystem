import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class BookingFrame extends JFrame {

    public BookingFrame(RoomManager roomManager,
                        BookingManager bookingManager,
                        Guest guest) {

        setTitle("SmartStay - Book a Room");
        setSize(500, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 15));
        panel.setBorder(
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        );

        JComboBox<String> roomBox = new JComboBox<>();

        // Load available rooms
        for (Room room : roomManager.getAllRooms()) {
            if (room.getStatus().equalsIgnoreCase("Available")) {
                roomBox.addItem(room.getRoomId());
            }
        }

        JTextField checkInField = new JTextField();
        JTextField checkOutField = new JTextField();
        JTextField guestsField = new JTextField();

        JButton confirmButton = new JButton("Confirm Booking");

        panel.add(new JLabel("Guest Name:"));
        panel.add(new JLabel(guest.getFullName()));

        panel.add(new JLabel("Room ID:"));
        panel.add(roomBox);

        panel.add(new JLabel("Check-in (YYYY-MM-DD):"));
        panel.add(checkInField);

        panel.add(new JLabel("Check-out (YYYY-MM-DD):"));
        panel.add(checkOutField);

        panel.add(new JLabel("Number of Guests:"));
        panel.add(guestsField);

        panel.add(new JLabel(""));
        panel.add(confirmButton);

        add(panel);

        // Confirm Booking
        confirmButton.addActionListener(e -> {

            String selectedRoomId =
                    (String) roomBox.getSelectedItem();

            if (selectedRoomId == null) {
                JOptionPane.showMessageDialog(
                        this, "No room selected."
                );
                return;
            }

            try {
                LocalDate checkIn = LocalDate.parse(
                        checkInField.getText().trim()
                );

                LocalDate checkOut = LocalDate.parse(
                        checkOutField.getText().trim()
                );

                int numberOfGuests = Integer.parseInt(
                        guestsField.getText().trim()
                );

                if (numberOfGuests <= 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Number of guests must be greater than zero."
                    );
                    return;
                }

                Room selectedRoom = null;

                for (Room room : roomManager.getAllRooms()) {
                    if (room.getRoomId().equals(selectedRoomId)) {
                        selectedRoom = room;
                        break;
                    }
                }

                if (selectedRoom == null) {
                    JOptionPane.showMessageDialog(
                            this, "Selected room not found."
                    );
                    return;
                }

                Booking booking = new Booking(
                        guest,
                        selectedRoom,
                        checkIn,
                        checkOut,
                        numberOfGuests
                );

                // Validate availability before bill preview
                if (!bookingManager.isRoomAvailable(
                        selectedRoom, checkIn, checkOut)) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Room is not available for the selected dates."
                    );
                    return;
                }

                // Calculate bill
                long nights =
                        BookingBillCalculator.calculateNights(booking);

                double baseCharge =
                        BookingBillCalculator.calculateBaseCharge(booking);

                double extraCharge =
                        BookingBillCalculator.calculateExtraGuestCharge(booking);

                double discount =
                        BookingBillCalculator.calculateDiscount(booking);

                double tax =
                        BookingBillCalculator.calculateTax(booking);

                double total =
                        BookingBillCalculator.calculateTotal(booking);

                double advance =
                        BookingBillCalculator.getAdvancePayment();

                double balance =
                        BookingBillCalculator.calculateRemainingBalance(booking);

                // Build bill preview
                String bill = String.format(
                        "Guest: %s%n" +
                                "Room ID: %s%n" +
                                "Room Type: %s%n" +
                                "Bed Type: %s%n" +
                                "Daily Rate: LKR %,.2f%n" +
                                "Check-in: %s%n" +
                                "Check-out: %s%n" +
                                "Nights: %d%n" +
                                "Guests: %d%n" +
                                "Complimentary Breakfast: %s%n%n" +
                                "Base Charge: LKR %,.2f%n" +
                                "Extra Guest Charges: LKR %,.2f%n" +
                                "Discount: LKR %,.2f%n" +
                                "Tax: LKR %,.2f%n" +
                                "Total Bill: LKR %,.2f%n" +
                                "Refundable Advance: LKR %,.2f%n" +
                                "Remaining Balance: LKR %,.2f",
                        guest.getFullName(),
                        selectedRoom.getRoomId(),
                        selectedRoom.getRoomType(),
                        selectedRoom.getBedType(),
                        selectedRoom.getDailyRate(),
                        checkIn,
                        checkOut,
                        nights,
                        numberOfGuests,
                        BookingBillCalculator.hasComplimentaryBreakfast(
                                selectedRoom.getRoomType()
                        ) ? "Yes" : "No",
                        baseCharge,
                        extraCharge,
                        discount,
                        tax,
                        total,
                        advance,
                        balance
                );

                JTextArea previewArea = new JTextArea(bill);
                previewArea.setEditable(false);
                previewArea.setFont(
                        new Font(Font.MONOSPACED, Font.PLAIN, 13)
                );

                JScrollPane previewScroll =
                        new JScrollPane(previewArea);

                previewScroll.setPreferredSize(
                        new Dimension(450, 350)
                );

                int choice = JOptionPane.showConfirmDialog(
                        this,
                        previewScroll,
                        "Booking Bill Preview",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.INFORMATION_MESSAGE
                );

                if (choice != JOptionPane.OK_OPTION) {
                    return;
                }

                // Revalidate and save booking to MySQL
                boolean saved =
                        bookingManager.createBooking(booking);

                if (saved) {

                    String invoice =
                            "SMARTSTAY HOTEL - BOOKING INVOICE\n\n" +
                                    "Booking ID: " + booking.getBookingId() +
                                    "\n\n" + bill;

                    JTextArea invoiceArea =
                            new JTextArea(invoice);

                    invoiceArea.setEditable(false);
                    invoiceArea.setFont(
                            new Font(Font.MONOSPACED, Font.PLAIN, 13)
                    );

                    JScrollPane invoiceScroll =
                            new JScrollPane(invoiceArea);

                    invoiceScroll.setPreferredSize(
                            new Dimension(450, 380)
                    );

                    JOptionPane.showMessageDialog(
                            this,
                            invoiceScroll,
                            "Booking Confirmed - Invoice",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    dispose();

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Booking failed. Please check room availability and dates."
                    );
                }

            } catch (DateTimeParseException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid date format. Use YYYY-MM-DD."
                );

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a valid number of guests."
                );

            } catch (IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Booking error: " + ex.getMessage()
                );
            }
        });
    }
}
