
import java.util.ArrayList;

public class GuestManager {

    private ArrayList<Guest> guests = new ArrayList<>();

    public boolean registerGuest(Guest guest) {

        for (Guest existingGuest : guests) {

            if (existingGuest.getGuestId()
                    .equalsIgnoreCase(guest.getGuestId())) {

                return false;
            }
        }

        guests.add(guest);
        return true;
    }

    public Guest findGuest(String guestId) {

        for (Guest guest : guests) {

            if (guest.getGuestId().equalsIgnoreCase(guestId)) {
                return guest;
            }
        }

        return null;
    }
}
