package model;

import exception.InvalidParcelException;

public class Fragile extends Box {

    public static final double MAX_WEIGHT = 30.0;
    public static final double HANDLING_FEE = 100.0;
    public static final int MAX_NOTE_LENGTH = 100;

    private final String handlingNote;

    public Fragile(String trackingNumber, String receiverName, double weight,
                   int length, int width, int height, String handlingNote) throws InvalidParcelException {
        super(trackingNumber, receiverName, weight, length, width, height, MAX_WEIGHT);
        if (!isValidHandlingNote(handlingNote)) {
            throw new InvalidParcelException(
                    "Handling note cannot be empty and must be up to " + MAX_NOTE_LENGTH + " characters.");
        }
        this.handlingNote = normalizeName(handlingNote);
    }

    @Override
    public String getType() {
        return "Fragile";
    }

    @Override
    public double getMaxWeight() {
        return MAX_WEIGHT;
    }

    @Override
    public double getBaseFee() {
        return super.getBaseFee() + HANDLING_FEE;
    }

    public String getHandlingNote() {
        return handlingNote;
    }

    public static boolean isValidHandlingNote(String note) {
        String clean = normalizeName(note);
        return !clean.isEmpty() && clean.length() <= MAX_NOTE_LENGTH;
    }
}
