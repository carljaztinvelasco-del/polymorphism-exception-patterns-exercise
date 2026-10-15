package main;

import exception.DuplicateTrackingNumberException;
import exception.InvalidParcelException;
import exception.ParcelAlreadyDeliveredException;
import exception.ParcelException;
import exception.ParcelNotFoundException;
import exception.SameDayNotAllowedException;
import java.util.List;
import java.util.Scanner;
import model.Box;
import model.Document;
import model.Fragile;
import model.Parcel;
import service.ParcelManager;
import service.ParcelOperations;
import strategy.DeliveryStrategy;
import strategy.ExpressDelivery;
import strategy.SameDayDelivery;
import strategy.StandardDelivery;
import validation.InputValidator;

public class Main {

    private static final ParcelOperations manager = ParcelManager.getInstance();
    private static InputValidator input;

    private static final int[] COLUMN_WIDTHS = {10, 9, 30, 9, 9, 14, 9};
    private static final String[] HEADERS = {
        "Tracking", "Type", "Receiver", "Chg. Wt.", "Service", "Delivery Fee", "Status"
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        input = new InputValidator(scanner);

        System.out.println("=========================================");
        System.out.println("        PARCEL DELIVERY SYSTEM");
        System.out.println("=========================================");

        boolean running = true;
        while (running) {
            showMenu();
            int choice = input.readIntInRange("Input choice: ", 1, 5,
                    "Invalid choice! Please input a number from 1 to 5.");
            System.out.println();

            switch (choice) {
                case 1:
                    addParcel();
                    break;
                case 2:
                    viewAllParcels();
                    break;
                case 3:
                    changeDeliveryService();
                    break;
                case 4:
                    markAsDelivered();
                    break;
                case 5:
                    System.out.println("Thank you for using the Parcel Delivery System. Goodbye!");
                    running = false;
                    break;
            }
        }
        scanner.close();
    }

    private static void showMenu() {
        System.out.println();
        System.out.println("Menu");
        System.out.println("1 - Add Parcel");
        System.out.println("2 - View All Parcels");
        System.out.println("3 - Change Delivery Service");
        System.out.println("4 - Mark as Delivered");
        System.out.println("5 - Exit");
    }

    private static void addParcel() {
        System.out.println("Select Parcel Type");
        System.out.println("1 - Document");
        System.out.println("2 - Box");
        System.out.println("3 - Fragile");
        System.out.println("0 - Back");
        int type = input.readIntInRange("Input parcel type: ", 0, 3,
                "Invalid parcel type! Please input 0 to 3.");
        if (type == 0) {
            return;
        }
        System.out.println();

        String trackingNumber = readNewTrackingNumber();
        String receiverName = readReceiverName();

        Parcel parcel;
        try {
            switch (type) {
                case 1:
                    parcel = buildDocument(trackingNumber, receiverName);
                    break;
                case 2:
                    parcel = buildBox(trackingNumber, receiverName);
                    break;
                default:
                    parcel = buildFragile(trackingNumber, receiverName);
                    break;
            }
        } catch (InvalidParcelException e) {
            System.out.println(e.getMessage());
            return;
        }

        while (true) {
            System.out.println();
            DeliveryStrategy service = selectDeliveryService();
            try {
                parcel.setDeliveryService(service);
                break;
            } catch (SameDayNotAllowedException e) {
                System.out.println(e.getMessage());
            } catch (ParcelAlreadyDeliveredException e) {
                System.out.println(e.getMessage());
                return;
            }
        }

        System.out.println();
        try {
            manager.addParcel(parcel);
            System.out.println("Parcel added successfully! Delivery fee: " + peso(parcel.getDeliveryFee()));
        } catch (InvalidParcelException | DuplicateTrackingNumberException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String readNewTrackingNumber() {
        while (true) {
            String text = input.readLine("Input Tracking Number (e.g. ABC0001): ");
            if (text.isEmpty()) {
                System.out.println("Tracking number cannot be empty.");
            } else if (!Parcel.isValidTrackingNumber(text)) {
                System.out.println("Invalid tracking number! Use ABC followed by 4 digits (e.g. ABC0001).");
            } else if (manager.isTrackingNumberTaken(text)) {
                System.out.println("Tracking number " + Parcel.normalizeTrackingNumber(text)
                        + " already exists! Please input a different one.");
            } else {
                return Parcel.normalizeTrackingNumber(text);
            }
        }
    }

    private static String readReceiverName() {
        while (true) {
            String name = Parcel.normalizeName(input.readLine("Input Receiver Name: "));
            if (name.isEmpty()) {
                System.out.println("Receiver name cannot be empty.");
            } else if (name.length() > Parcel.MAX_NAME_LENGTH) {
                System.out.println("Receiver name must be up to " + Parcel.MAX_NAME_LENGTH + " characters only.");
            } else if (!Parcel.isValidReceiverName(name)) {
                System.out.println("Receiver name must start with a letter and contain letters only "
                        + "(spaces, . ' - allowed).");
            } else {
                return name;
            }
        }
    }

    private static Parcel buildDocument(String trackingNumber, String receiverName)
            throws InvalidParcelException {
        double weight = input.readWeight("Input Weight (kg): ", Document.MAX_WEIGHT);
        int pages = input.readIntInRange("Input Number of Pages: ",
                Document.MIN_PAGES, Document.MAX_PAGES,
                "Number of pages must be between " + Document.MIN_PAGES + " and " + Document.MAX_PAGES + ".");
        return new Document(trackingNumber, receiverName, weight, pages);
    }

    private static Parcel buildBox(String trackingNumber, String receiverName)
            throws InvalidParcelException {
        double weight = input.readWeight("Input Weight (kg): ", Box.MAX_WEIGHT);
        int length = readDimension("Length");
        int width = readDimension("Width");
        int height = readDimension("Height");
        return new Box(trackingNumber, receiverName, weight, length, width, height);
    }

    private static Parcel buildFragile(String trackingNumber, String receiverName)
            throws InvalidParcelException {
        double weight = input.readWeight("Input Weight (kg): ", Fragile.MAX_WEIGHT);
        int length = readDimension("Length");
        int width = readDimension("Width");
        int height = readDimension("Height");
        String note = input.readNonEmpty("Input Handling Note: ", Fragile.MAX_NOTE_LENGTH, "Handling note");
        return new Fragile(trackingNumber, receiverName, weight, length, width, height, note);
    }

    private static int readDimension(String label) {
        return input.readIntInRange("Input " + label + " (cm): ", Box.MIN_DIMENSION, Box.MAX_DIMENSION,
                label + " must be a whole number from " + Box.MIN_DIMENSION + " to " + Box.MAX_DIMENSION + " cm.");
    }

    private static DeliveryStrategy selectDeliveryService() {
        System.out.println("Select Delivery Service");
        System.out.println("1 - Standard");
        System.out.println("2 - Express");
        System.out.println("3 - Same Day");
        int choice = input.readIntInRange("Input delivery service: ", 1, 3,
                "Invalid delivery service! Please input 1 to 3.");
        switch (choice) {
            case 1:
                return new StandardDelivery();
            case 2:
                return new ExpressDelivery();
            default:
                return new SameDayDelivery();
        }
    }

    private static void viewAllParcels() {
        if (!manager.hasParcels()) {
            System.out.println("No parcels to display. Add a parcel first.");
            return;
        }

        List<Parcel> parcels = manager.getAllParcels();
        System.out.println("Parcel List:");
        printBorder();
        printRow(HEADERS);
        printBorder();
        for (Parcel p : parcels) {
            printRow(new String[] {
                p.getTrackingNumber(),
                p.getType(),
                p.getReceiverName(),
                Parcel.formatKg(p.getChargeableWeight()),
                p.getDeliveryService().getName(),
                peso(p.getDeliveryFee()),
                p.getStatus().toString()
            });
        }
        printBorder();
        System.out.println("Total parcels: " + parcels.size());
    }

    private static void printBorder() {
        StringBuilder line = new StringBuilder("+");
        for (int width : COLUMN_WIDTHS) {
            line.append("-".repeat(width + 2)).append("+");
        }
        System.out.println(line);
    }

    private static void printRow(String[] cells) {
        StringBuilder row = new StringBuilder("|");
        for (int i = 0; i < cells.length; i++) {
            String format = (i == 5) ? " %" + COLUMN_WIDTHS[i] + "s |" : " %-" + COLUMN_WIDTHS[i] + "s |";
            row.append(String.format(format, cells[i]));
        }
        System.out.println(row);
    }

    private static void changeDeliveryService() {
        if (!manager.hasParcels()) {
            System.out.println("No parcels yet. Add a parcel first.");
            return;
        }
        if (!manager.hasPendingParcels()) {
            System.out.println("All parcels are already delivered. Nothing to change.");
            return;
        }

        String trackingNumber = readExistingTrackingNumber();

        Parcel parcel;
        try {
            parcel = manager.findPendingParcel(trackingNumber);
        } catch (ParcelNotFoundException e) {
            System.out.println("Parcel not found!");
            return;
        } catch (ParcelAlreadyDeliveredException e) {
            System.out.println("Parcel already delivered!");
            return;
        }

        System.out.println("Current service: " + parcel.getDeliveryService().getName());
        System.out.println();
        DeliveryStrategy newService = selectDeliveryService();

        System.out.println();
        try {
            double newFee = manager.changeDeliveryService(trackingNumber, newService);
            System.out.println("Delivery service changed! New fee: " + peso(newFee));
        } catch (ParcelException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void markAsDelivered() {
        if (!manager.hasParcels()) {
            System.out.println("No parcels yet. Add a parcel first.");
            return;
        }
        if (!manager.hasPendingParcels()) {
            System.out.println("All parcels are already delivered.");
            return;
        }

        String trackingNumber = readExistingTrackingNumber();
        try {
            manager.markAsDelivered(trackingNumber);
            System.out.println("Parcel delivered successfully!");
        } catch (ParcelNotFoundException e) {
            System.out.println("Parcel not found!");
        } catch (ParcelAlreadyDeliveredException e) {
            System.out.println("Parcel was already delivered.");
        }
    }

    private static String readExistingTrackingNumber() {
        while (true) {
            String text = input.readLine("Input Tracking Number: ");
            if (!text.isEmpty()) {
                return text;
            }
            System.out.println("Tracking number cannot be empty.");
        }
    }

    private static String peso(double amount) {
        return String.format("Php %,.2f", amount);
    }
}
