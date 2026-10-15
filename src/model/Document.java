package model;

import exception.InvalidParcelException;

public class Document extends Parcel {

    public static final double MAX_WEIGHT = 0.5;
    public static final int MIN_PAGES = 1;
    public static final int MAX_PAGES = 100;
    public static final double BASE_FEE = 50.0;

    private final int numberOfPages;

    public Document(String trackingNumber, String receiverName, double weight, int numberOfPages)
            throws InvalidParcelException {
        super(trackingNumber, receiverName, weight, MAX_WEIGHT);
        if (!isValidPages(numberOfPages)) {
            throw new InvalidParcelException(
                    "Number of pages must be between " + MIN_PAGES + " and " + MAX_PAGES + ".");
        }
        this.numberOfPages = numberOfPages;
    }

    @Override
    public String getType() {
        return "Document";
    }

    @Override
    public double getMaxWeight() {
        return MAX_WEIGHT;
    }

    @Override
    public double getChargeableWeight() {
        return getWeight();
    }

    @Override
    public double getBaseFee() {
        return BASE_FEE;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public static boolean isValidPages(int pages) {
        return pages >= MIN_PAGES && pages <= MAX_PAGES;
    }
}
