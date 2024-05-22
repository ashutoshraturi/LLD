package models;

import enums.PaymentStatus;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class Booking {
    private int bookingId;
    private int userId;
    private Hotel hotel;
    private List<RoomsForDate> roomsBooked;
    private LocalDate bookingDate;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String amount;

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    private PaymentStatus paymentStatus;

    public Booking(int bookingId, int userId, Hotel hotel, List<RoomsForDate> roomsBooked,
                   LocalDate bookingDate, LocalDate fromDate, LocalDate toDate, String amount) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.hotel = hotel;
        this.roomsBooked = roomsBooked;
        this.bookingDate = bookingDate;
        this.amount = amount;
        this.fromDate = fromDate;
        this.toDate = toDate;
        paymentStatus = PaymentStatus.PENDING;
    }
}
