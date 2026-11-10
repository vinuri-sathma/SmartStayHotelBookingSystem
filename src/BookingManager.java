import java.time.LocalDate;

public class BookingManager {

    private final BookingDAO bookingDAO;

    public BookingManager() {
        bookingDAO = new BookingDAO();
    }

    public boolean isRoomAvailable(
            Room room,
            LocalDate checkIn,
            LocalDate checkOut) {

        if (room == null || checkIn == null || checkOut == null) {
            return false;
        }

        // Check room status
        if (!room.getStatus().equalsIgnoreCase("Available")) {
            return false;
        }

        // Check booking dates
        if (checkIn.isBefore(LocalDate.now().plusDays(2))) {
            return false;
        }

        if (!checkOut.isAfter(checkIn)) {
            return false;
        }

        // Check overlapping bookings in MySQL
        return !bookingDAO.hasOverlappingBooking(
                room.getRoomId(),
                checkIn,
                checkOut
        );
    }

    // Create a new booking
    public boolean createBooking(Booking booking) {

        if (booking == null ||
                booking.getGuest() == null ||
                booking.getRoom() == null) {
            return false;
        }

        // Validate number of guests
        if (booking.getNumberOfGuests() <= 0) {
            return false;
        }

        // Check room availability before saving
        if (!isRoomAvailable(
                booking.getRoom(),
                booking.getCheckInDate(),
                booking.getCheckOutDate())) {
            return false;
        }

        // Save booking to MySQL
        return bookingDAO.addBooking(booking);
    }
}
