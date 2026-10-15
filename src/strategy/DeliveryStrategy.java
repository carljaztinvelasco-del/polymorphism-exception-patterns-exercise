package strategy;

import model.Parcel;

public interface DeliveryStrategy {

    String getName();

    double computeFee(double baseFee);

    default boolean isAllowedFor(Parcel parcel) {
        return true;
    }
}
