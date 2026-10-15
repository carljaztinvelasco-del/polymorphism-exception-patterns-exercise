package model;

import exception.InvalidParcelException;

public class Box extends Parcel {

    public static final double MAX_WEIGHT = 50.0;
    public static final int MIN_DIMENSION = 1;
    public static final int MAX_DIMENSION = 150;
    public static final int VOLUMETRIC_DIVISOR = 3500;
    public static final double RATE_PER_KG = 40.0;

    private final int length;
    private final int width;
    private final int height;

    public Box(String trackingNumber, String receiverName, double weight,
               int length, int width, int height) throws InvalidParcelException {
        this(trackingNumber, receiverName, weight, length, width, height, MAX_WEIGHT);
    }

    protected Box(String trackingNumber, String receiverName, double weight,
                  int length, int width, int height, double maxWeight) throws InvalidParcelException {
        super(trackingNumber, receiverName, weight, maxWeight);
        if (!isValidDimension(length) || !isValidDimension(width) || !isValidDimension(height)) {
            throw new InvalidParcelException(
                    "Length, width and height must be whole numbers from "
                            + MIN_DIMENSION + " to " + MAX_DIMENSION + " cm.");
        }
        this.length = length;
        this.width = width;
        this.height = height;
    }

    @Override
    public String getType() {
        return "Box";
    }

    @Override
    public double getMaxWeight() {
        return MAX_WEIGHT;
    }

    @Override
    public double getChargeableWeight() {
        long volumetricRoundedUp = (getVolume() + VOLUMETRIC_DIVISOR - 1) / VOLUMETRIC_DIVISOR;
        long actualRoundedUp = (long) Math.ceil(getWeight());
        return Math.max(volumetricRoundedUp, actualRoundedUp);
    }

    @Override
    public double getBaseFee() {
        return RATE_PER_KG * getChargeableWeight();
    }

    public long getVolume() {
        return (long) length * width * height;
    }

    public double getVolumetricWeight() {
        return (double) getVolume() / VOLUMETRIC_DIVISOR;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static boolean isValidDimension(int cm) {
        return cm >= MIN_DIMENSION && cm <= MAX_DIMENSION;
    }
}
