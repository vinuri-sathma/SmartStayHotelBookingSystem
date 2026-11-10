import java.time.temporal.ChronoUnit;

public class BookingBillCalculator {

    private static final double ADVANCE_PAYMENT = 10000.0;

    // Calculate number of nights
    public static long calculateNights(Booking booking) {
        return ChronoUnit.DAYS.between(
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );
    }

    // Get room capacity based on room type
    public static int getRoomCapacity(String roomType) {

        switch (roomType) {
            case "Standard":
            case "Deluxe":
                return 2;

            case "Suite":
                return 3;

            case "Family Suite":
                return 4;

            default:
                throw new IllegalArgumentException(
                        "Invalid room type"
                );
        }
    }

    // Extra guest charge per guest per night
    public static double getExtraGuestRate(String roomType) {

        switch (roomType) {
            case "Standard":
                return 1000;

            case "Deluxe":
                return 1500;

            case "Suite":
                return 2000;

            case "Family Suite":
                return 2500;

            default:
                throw new IllegalArgumentException(
                        "Invalid room type"
                );
        }
    }

    // Tax rate based on room type
    public static double getTaxRate(String roomType) {

        switch (roomType) {
            case "Standard":
                return 0.10;

            case "Deluxe":
                return 0.12;

            case "Suite":
            case "Family Suite":
                return 0.15;

            default:
                throw new IllegalArgumentException(
                        "Invalid room type"
                );
        }
    }

    // Calculate base room charge
    public static double calculateBaseCharge(Booking booking) {

        return booking.getRoom().getDailyRate()
                * calculateNights(booking);
    }

    // Calculate extra guest charges
    public static double calculateExtraGuestCharge(
            Booking booking) {

        String roomType = booking.getRoom().getRoomType();

        int capacity = getRoomCapacity(roomType);

        int extraGuests = Math.max(
                0,
                booking.getNumberOfGuests() - capacity
        );

        return extraGuests
                * getExtraGuestRate(roomType)
                * calculateNights(booking);
    }

    // Calculate 12% discount for 5+ nights
    public static double calculateDiscount(Booking booking) {

        if (calculateNights(booking) >= 5) {
            return calculateBaseCharge(booking) * 0.12;
        }

        return 0;
    }

    // Calculate tax
    public static double calculateTax(Booking booking) {

        String roomType = booking.getRoom().getRoomType();

        double taxableAmount =
                calculateBaseCharge(booking)
                        + calculateExtraGuestCharge(booking)
                        - calculateDiscount(booking);

        return taxableAmount * getTaxRate(roomType);
    }

    // Calculate total bill before advance deduction
    public static double calculateTotal(Booking booking) {

        if (calculateNights(booking) <= 0) {
            return 0;
        }

        return calculateBaseCharge(booking)
                + calculateExtraGuestCharge(booking)
                - calculateDiscount(booking)
                + calculateTax(booking);
    }

    // Calculate remaining balance after advance
    public static double calculateRemainingBalance(
            Booking booking) {

        return calculateTotal(booking) - ADVANCE_PAYMENT;
    }

    public static double getAdvancePayment() {
        return ADVANCE_PAYMENT;
    }

    // Complimentary breakfast
    public static boolean hasComplimentaryBreakfast(
            String roomType) {

        return !roomType.equals("Standard");
    }
}
