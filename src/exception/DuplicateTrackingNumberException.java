package exception;

public class DuplicateTrackingNumberException extends ParcelException {
    private static final long serialVersionUID = 1L;

    public DuplicateTrackingNumberException(String trackingNumber) {
        super("Tracking number " + trackingNumber + " already exists!");
    }
}
