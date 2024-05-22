package dao;

import models.Booking;

import java.util.HashMap;
import java.util.Map;

public class BookingDao {
    private final Map<Integer, Booking> bookingIdToBookingMap;
    private final Map<Integer, Booking> userIdToBookingMap;
    private static BookingDao instance = null;

    private BookingDao() {
        this.bookingIdToBookingMap = new HashMap<>();
        this.userIdToBookingMap = new HashMap<>();
    }

    public static BookingDao getInstance(){
        if(instance == null){
            instance = new BookingDao();
        }
        return instance;
    }

    public Map<Integer, Booking> getBookingIdToBookingMap() {
        return bookingIdToBookingMap;
    }

    public Map<Integer, Booking> getUserIdToBookingMap() {
        return userIdToBookingMap;
    }
}
