
public class Guest {

    private String guestId;
    private String fullName;
    private String contactNumber;
    private String email;

    public Guest(String guestId, String fullName,
                 String contactNumber, String email) {

        this.guestId = guestId;
        this.fullName = fullName;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public String getGuestId() {
        return guestId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }
}
