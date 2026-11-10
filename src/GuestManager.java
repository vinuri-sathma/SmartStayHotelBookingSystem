import java.util.ArrayList;

public class GuestManager {

    private ArrayList<Guest> guests;
    private final GuestDAO guestDAO;

    public GuestManager() {

        guestDAO = new GuestDAO();

        // Load registered guests from MySQL
        guests = guestDAO.getAllGuests();
    }

    // Register Guest with a securely hashed password
    public boolean registerGuest(
            Guest guest, String passwordHash) {

        if (guest == null || passwordHash == null
                || passwordHash.isBlank()) {
            return false;
        }

        // Check duplicate Guest ID
        for (Guest existingGuest : guests) {

            if (existingGuest.getGuestId()
                    .equalsIgnoreCase(guest.getGuestId())) {

                System.out.println(
                        "Guest ID already exists!"
                );
                return false;
            }
        }

        // Save guest and password hash to MySQL
        if (!guestDAO.addGuest(guest, passwordHash)) {

            System.out.println(
                    "Guest could not be saved to database!"
            );
            return false;
        }

        // Add to ArrayList after successful save
        guests.add(guest);

        System.out.println(
                "Guest registered successfully!"
        );

        return true;
    }


    // Find Registered Guest
    public Guest findGuest(String guestId) {

        if (guestId == null) {
            return null;
        }

        for (Guest guest : guests) {

            if (guest.getGuestId()
                    .equalsIgnoreCase(guestId)) {

                return guest;
            }
        }

        return null;
    }
}
