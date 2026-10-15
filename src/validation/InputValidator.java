package validation;

import java.util.Scanner;
import model.Parcel;

public class InputValidator {

    private final Scanner scanner;

    public InputValidator(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.out.println("No more input. Exiting program...");
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    public int readIntInRange(String prompt, int min, int max, String rangeMessage) {
        while (true) {
            String text = readLine(prompt);
            if (text.isEmpty()) {
                System.out.println("Input cannot be empty.");
            } else if (!text.matches("\\d{1,9}")) {
                System.out.println("Invalid input! Please input a whole number only.");
            } else {
                int value = Integer.parseInt(text);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println(rangeMessage);
            }
        }
    }

    public double readWeight(String prompt, double maxWeight) {
        while (true) {
            String text = readLine(prompt);
            if (text.isEmpty()) {
                System.out.println("Weight cannot be empty.");
            } else if (!text.matches("\\d{1,7}(\\.\\d{1,2})?")) {
                System.out.println("Invalid weight! Input a number with up to 2 decimal places (e.g. 1.25).");
            } else {
                double value = Double.parseDouble(text);
                if (Parcel.isValidWeight(value, maxWeight)) {
                    return value;
                }
                System.out.println("Weight must be greater than 0 and up to "
                        + Parcel.formatKg(maxWeight) + " for this parcel type.");
            }
        }
    }

    public String readNonEmpty(String prompt, int maxLength, String fieldName) {
        while (true) {
            String text = Parcel.normalizeName(readLine(prompt));
            if (text.isEmpty()) {
                System.out.println(fieldName + " cannot be empty.");
            } else if (text.length() > maxLength) {
                System.out.println(fieldName + " must be up to " + maxLength + " characters only.");
            } else {
                return text;
            }
        }
    }
}
