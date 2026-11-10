import javax.swing.*;
import java.awt.*;

public class GuestDashboard extends JFrame {

    public GuestDashboard(Guest guest, RoomManager roomManager) {

        setTitle("SmartStay - Guest Dashboard");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel(
                "Welcome, " + guest.getFullName(),
                SwingConstants.CENTER
        );

        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel buttonPanel = new JPanel(
                new GridLayout(4, 1, 0, 15)
        );

        JButton searchRoomsButton =
                new JButton("Search Available Rooms");

        JButton bookRoomButton =
                new JButton("Book a Room");

        JButton viewBookingsButton =
                new JButton("View My Bookings");

        JButton cancelBookingButton =
                new JButton("Cancel Booking");

        // Open Search Available Rooms window
        searchRoomsButton.addActionListener(e -> {

            SearchRoomsFrame searchRoomsFrame =
                    new SearchRoomsFrame(roomManager);

            searchRoomsFrame.setVisible(true);
        });

        // Open Book a Room window
        bookRoomButton.addActionListener(e -> {

            BookingManager bookingManager =
                    new BookingManager();

            BookingFrame bookingFrame =
                    new BookingFrame(
                            roomManager,
                            bookingManager,
                            guest
                    );

            bookingFrame.setVisible(true);
        });


        // Open View My Bookings window
        viewBookingsButton.addActionListener(e -> {

            ViewBookingsFrame viewBookingsFrame =
                    new ViewBookingsFrame(
                            guest,
                            roomManager
                    );

            viewBookingsFrame.setVisible(true);
        });

        buttonPanel.add(searchRoomsButton);
        buttonPanel.add(bookRoomButton);
        buttonPanel.add(viewBookingsButton);
        buttonPanel.add(cancelBookingButton);

        buttonPanel.setPreferredSize(new Dimension(220, 220));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(buttonPanel);

        setLayout(new BorderLayout(10, 10));
        add(welcomeLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
}
