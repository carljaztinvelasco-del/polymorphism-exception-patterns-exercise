package service;

import exception.DuplicateTrackingNumberException;
import exception.InvalidParcelException;
import exception.ParcelAlreadyDeliveredException;
import exception.ParcelNotFoundException;
import exception.SameDayNotAllowedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.Parcel;
import strategy.DeliveryStrategy;

public class ParcelManager implements ParcelOperations {

    private static ParcelManager instance;

    private final List<Parcel> parcels;

    private ParcelManager() {
        parcels = new ArrayList<>();
    }

    public static synchronized ParcelManager getInstance() {
        if (instance == null) {
            instance = new ParcelManager();
        }
        return instance;
    }

    @Override
    public void addParcel(Parcel parcel) throws InvalidParcelException, DuplicateTrackingNumberException {
        if (parcel == null) {
            throw new InvalidParcelException("Parcel cannot be empty.");
        }
        if (!parcel.hasDeliveryService()) {
            throw new InvalidParcelException("Please select a delivery service first.");
        }
        if (isTrackingNumberTaken(parcel.getTrackingNumber())) {
            throw new DuplicateTrackingNumberException(parcel.getTrackingNumber());
        }
        parcels.add(parcel);
    }

    @Override
    public Parcel findParcel(String trackingNumber) throws ParcelNotFoundException {
        String target = Parcel.normalizeTrackingNumber(trackingNumber);
        for (Parcel parcel : parcels) {
            if (parcel.getTrackingNumber().equals(target)) {
                return parcel;
            }
        }
        throw new ParcelNotFoundException();
    }

    @Override
    public Parcel findPendingParcel(String trackingNumber)
            throws ParcelNotFoundException, ParcelAlreadyDeliveredException {
        Parcel parcel = findParcel(trackingNumber);
        if (parcel.isDelivered()) {
            throw new ParcelAlreadyDeliveredException();
        }
        return parcel;
    }

    @Override
    public double changeDeliveryService(String trackingNumber, DeliveryStrategy newService)
            throws ParcelNotFoundException, ParcelAlreadyDeliveredException,
                   SameDayNotAllowedException, InvalidParcelException {
        Parcel parcel = findPendingParcel(trackingNumber);
        if (newService == null) {
            throw new InvalidParcelException("Please select a delivery service.");
        }
        if (parcel.getDeliveryService().getClass() == newService.getClass()) {
            throw new InvalidParcelException(
                    "Parcel is already using " + newService.getName() + " delivery!");
        }
        parcel.setDeliveryService(newService);
        return parcel.getDeliveryFee();
    }

    @Override
    public void markAsDelivered(String trackingNumber)
            throws ParcelNotFoundException, ParcelAlreadyDeliveredException {
        findParcel(trackingNumber).markDelivered();
    }

    @Override
    public List<Parcel> getAllParcels() {
        return Collections.unmodifiableList(new ArrayList<>(parcels));
    }

    @Override
    public boolean isTrackingNumberTaken(String trackingNumber) {
        try {
            findParcel(trackingNumber);
            return true;
        } catch (ParcelNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean hasParcels() {
        return !parcels.isEmpty();
    }

    @Override
    public boolean hasPendingParcels() {
        for (Parcel parcel : parcels) {
            if (!parcel.isDelivered()) {
                return true;
            }
        }
        return false;
    }
}
