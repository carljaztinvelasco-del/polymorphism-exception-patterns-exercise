package exception;

public class SameDayNotAllowedException extends ParcelException {
    private static final long serialVersionUID = 1L;

    public SameDayNotAllowedException() {
        super("Same Day delivery is only for parcels up to 10 kg!");
    }
}
