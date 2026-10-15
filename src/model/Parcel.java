package model;

import exception.InvalidParcelException;
import exception.ParcelAlreadyDeliveredException;
import exception.SameDayNotAllowedException;
import strategy.DeliveryStrategy;

public abstract class Parcel {

    public static final String TRACKING_FORMAT = "ABC\\d{4}";
    public static final int MAX_NAME_LENGTH = 30;

    private final String trackingNumber;
    private final String receiverName;
    private final double weight;
    private DeliveryStrategy deliveryService;
    private ParcelStatus status;

    protected Parcel(String trackingNumber, String receiverName, double weight, double maxWeight)
            throws InvalidParcelException {
        if (!isValidTrackingNumber(trackingNumber)) {
            throw new InvalidParcelException(
                    "Invalid tracking number! Use ABC followed by 4 digits (e.g. ABC0001).");
        }
        if (!isValidReceiverName(receiverName)) {
            throw new InvalidParcelException(
                    "Receiver name must be 1 to " + MAX_NAME_LENGTH
                            + " characters and contain letters only (spaces, . ' - allowed).");
        }
        if (!isValidWeight(weight, maxWeight)) {
            throw new InvalidParcelException(
                    "Weight must be greater than 0 and up to " + formatKg(maxWeight) + ".");
        }
        this.trackingNumber = normalizeTrackingNumber(trackingNumber);
        this.receiverName = normalizeName(receiverName);
        this.weight = weight;
        this.status = ParcelStatus.PENDING;
    }

    public abstract String getType();

    public abstract double getMaxWeight();

    public abstract double getChargeableWeight();

    public abstract double getBaseFee();

    public double getDeliveryFee() {
        if (deliveryService == null) {
            throw new IllegalStateException("No delivery service selected yet.");
        }
        return deliveryService.computeFee(getBaseFee());
    }

    public void setDeliveryService(DeliveryStrategy newService)
            throws SameDayNotAllowedException, ParcelAlreadyDeliveredException {
        if (newService == null) {
            throw new IllegalArgumentException("Delivery service cannot be null.");
        }
        if (status == ParcelStatus.DELIVERED) {
            throw new ParcelAlreadyDeliveredException();
        }
        if (!newService.isAllowedFor(this)) {
            throw new SameDayNotAllowedException();
        }
        this.deliveryService = newService;
    }

    public void markDelivered() throws ParcelAlreadyDeliveredException {
        if (status == ParcelStatus.DELIVERED) {
            throw new ParcelAlreadyDeliveredException();
        }
        status = ParcelStatus.DELIVERED;
    }

    public boolean isDelivered() {
        return status == ParcelStatus.DELIVERED;
    }

    public boolean hasDeliveryService() {
        return deliveryService != null;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public double getWeight() {
        return weight;
    }

    public DeliveryStrategy getDeliveryService() {
        return deliveryService;
    }

    public ParcelStatus getStatus() {
        return status;
    }

    public static String normalizeTrackingNumber(String trackingNumber) {
        return trackingNumber == null ? "" : trackingNumber.trim().toUpperCase();
    }

    public static boolean isValidTrackingNumber(String trackingNumber) {
        return normalizeTrackingNumber(trackingNumber).matches(TRACKING_FORMAT);
    }

    public static String normalizeName(String name) {
        return name == null ? "" : name.trim().replaceAll("\\s+", " ");
    }

    public static boolean isValidReceiverName(String name) {
        String clean = normalizeName(name);
        return !clean.isEmpty()
                && clean.length() <= MAX_NAME_LENGTH
                && clean.matches("\\p{L}[\\p{L} .'-]*");
    }

    public static boolean isValidWeight(double weight, double maxWeight) {
        return weight > 0 && weight <= maxWeight;
    }

    public static String formatKg(double kg) {
        if (kg == Math.floor(kg)) {
            return String.format("%d kg", (long) kg);
        }
        return String.format("%.2f kg", kg);
    }
}
