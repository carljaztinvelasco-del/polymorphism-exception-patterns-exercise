package strategy;

public class StandardDelivery implements DeliveryStrategy {

    @Override
    public String getName() {
        return "Standard";
    }

    @Override
    public double computeFee(double baseFee) {
        return baseFee;
    }
}
