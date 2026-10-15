package strategy;

public class ExpressDelivery implements DeliveryStrategy {

    public static final double MULTIPLIER = 1.5;

    @Override
    public String getName() {
        return "Express";
    }

    @Override
    public double computeFee(double baseFee) {
        return baseFee * MULTIPLIER;
    }
}
