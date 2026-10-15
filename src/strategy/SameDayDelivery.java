package strategy;

import model.Parcel;

public class SameDayDelivery implements DeliveryStrategy {

    public static final double MULTIPLIER = 2.0;
    public static final double SURCHARGE = 100.0;
    public static final double MAX_CHARGEABLE_WEIGHT = 10.0;

    @Override
    public String getName() {
        return "Same Day";
    }

    @Override
    public double computeFee(double baseFee) {
        return (baseFee * MULTIPLIER) + SURCHARGE;
    }

    @Override
    public boolean isAllowedFor(Parcel parcel) {
        return parcel != null && parcel.getChargeableWeight() <= MAX_CHARGEABLE_WEIGHT;
    }
}
