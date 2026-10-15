package service;

import exception.DuplicateTrackingNumberException;
import exception.InvalidParcelException;
import exception.ParcelAlreadyDeliveredException;
import exception.ParcelNotFoundException;
import exception.SameDayNotAllowedException;
import java.util.List;
import model.Parcel;
import strategy.DeliveryStrategy;

public interface ParcelOperations {

    void addParcel(Parcel parcel) throws InvalidParcelException, DuplicateTrackingNumberException;

    Parcel findParcel(String trackingNumber) throws ParcelNotFoundException;

    Parcel findPendingParcel(String trackingNumber)
            throws ParcelNotFoundException, ParcelAlreadyDeliveredException;

    double changeDeliveryService(String trackingNumber, DeliveryStrategy newService)
            throws ParcelNotFoundException, ParcelAlreadyDeliveredException,
                   SameDayNotAllowedException, InvalidParcelException;

    void markAsDelivered(String trackingNumber)
            throws ParcelNotFoundException, ParcelAlreadyDeliveredException;

    List<Parcel> getAllParcels();

    boolean isTrackingNumberTaken(String trackingNumber);

    boolean hasParcels();

    boolean hasPendingParcels();
}
