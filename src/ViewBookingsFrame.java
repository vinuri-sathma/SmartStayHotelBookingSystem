import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewBookingsFrame extends JFrame {

    public ViewBookingsFrame(
            Guest guest,
            RoomManager roomManager) {

        setTitle("SmartStay - My Bookings");
        setSize(850, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columns = {
                "Booking ID",
                "Room ID",
                "Room Type",
                "Check-in",
                "Check-out",
                "Guests",
                "Status"
        };

        DefaultTableModel tableModel =
                new DefaultTableModel(columns, 0) {

                    @Override
                    public boolean isCellEditable(
                            int row, int column) {
                        return false;
                    }
                };

        JTable bookingTable = new JTable(tableModel);
        bookingTable.setRowHeight(25);

        BookingDAO bookingDAO = new BookingDAO();

        List<Booking> bookings =
                bookingDAO.getBookingsByGuestId(
                        guest, roomManager
                );

        for (Booking booking : bookings) {

            Object[] row = {
                    booking.getBookingId(),
                    booking.getRoom().getRoomId(),
                    booking.getRoom().getRoomType(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getNumberOfGuests(),
                    booking.getBookingStatus()
            };

            tableModel.addRow(row);
        }

        JLabel titleLabel = new JLabel(
                "My Booking History",
                SwingConstants.CENTER
        );

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        add(titleLabel, BorderLayout.NORTH);
        add(new JScrollPane(bookingTable),
                BorderLayout.CENTER);
    }
}
