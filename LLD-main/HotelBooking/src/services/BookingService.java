package services;

import dao.BookingDao;
import dao.HotelDao;
import enums.PaymentStatus;
import enums.RoomType;
import models.*;
import utils.SeatLockProvider;

import java.time.LocalDate;
import java.util.*;

public class BookingService {
    HotelDao hotelDao = HotelDao.getInstance();
    Map<Integer, Map<LocalDate, Map<RoomType, Integer>>> availableRooms = hotelDao.getAvailableRooms();
    BookingDao bookingDao = BookingDao.getInstance();
    PaymentService paymentService;
    private final SeatLockProvider seatLockProvider;

    public BookingService(SeatLockProvider seatLockProvider) {
        this.seatLockProvider = seatLockProvider;
    }

    public synchronized boolean createBooking(Integer bookingId, User user, Hotel hotel, List<RoomsForDate> rooms,
                                              LocalDate fromDate, LocalDate toDate, Double amount){
        Map<Integer, Map<LocalDate, Map<RoomType, Integer>>> bookedRooms = hotelDao.getBookedRooms();
        Map<LocalDate, Map<RoomType, Integer>> availableRoomsForDateMap = availableRooms.get(hotel.getHotelId());
        Map<LocalDate, Map<RoomType, Integer>> bookedRoomsForDateMap = bookedRooms.get(hotel.getHotelId());
        Map<LocalDate, Map<RoomType, Integer>> tempBooking = new HashMap<>();
        for (LocalDate date = fromDate; date.isBefore(toDate); date = date.plusDays(1)) {
            Map<RoomType, Integer> availableRoomTypeIntegerMap = availableRoomsForDateMap.get(fromDate);
            Map<RoomType, Integer> bookedRoomTypeIntegerMap = bookedRoomsForDateMap.get(fromDate);
            seatLockProvider.lockSeats(hotel, rooms, String.valueOf(user.getUserId()));
            for(RoomsForDate roomsForDate : rooms){
                Integer availableRoomsCount = availableRoomTypeIntegerMap.get(roomsForDate.getRoomType());
                Integer bookedRoomsCount = bookedRoomTypeIntegerMap.get(roomsForDate.getRoomType());
                if(roomsForDate.getRoomsCount() + bookedRoomsCount > availableRoomsCount){
                    seatLockProvider.unlockSeats(hotel, rooms, String.valueOf(user.getUserId()));
                    return false;
                }
                bookedRoomsCount += roomsForDate.getRoomsCount();
                Map<RoomType, Integer> tempRoomTypeBooking = new HashMap<>();
                tempRoomTypeBooking.put(roomsForDate.getRoomType(), bookedRoomsCount);
                tempBooking.put(fromDate, tempRoomTypeBooking);
            }
        }
        Booking booking = new Booking(bookingId, user.getUserId(), hotel, rooms,
                LocalDate.now(), fromDate, toDate, String.valueOf(amount));
        Boolean paid = paymentService.makePayment(user, amount);
        if(paid){
            booking.setPaymentStatus(PaymentStatus.SUCCESSFUL);
            bookingDao.getBookingIdToBookingMap().put(bookingId, booking);
            bookingDao.getUserIdToBookingMap().put(user.getUserId(), booking);
            hotelDao.getBookedRooms().put(hotel.getHotelId(), tempBooking);
        }else{
            booking.setPaymentStatus(PaymentStatus.FAILED);
            //retry mechanism
            return false;
        }
        return true;
    }
}
