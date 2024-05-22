package dao;

import enums.RoomType;
import models.Hotel;
import models.RoomsForDate;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HotelDao {
    private final Map<Integer, Hotel> hotelMap;
    private Map<Integer, Map<LocalDate, Map<RoomType, Integer>>> availableRooms;
    private Map<Integer, Map<LocalDate, Map<RoomType, Integer>>> bookedRooms;

    private static HotelDao instance = null;
    private HotelDao() {
        this.hotelMap = new HashMap<>();
        this.availableRooms = new HashMap<>();
        this.bookedRooms = new HashMap<>();
    }

    public static HotelDao getInstance(){
        if(instance == null){
            instance = new HotelDao();
        }
        return instance;
    }

    public Map<Integer, Hotel> getHotelMap() {
        return hotelMap;
    }

    public Map<Integer, Map<LocalDate, Map<RoomType, Integer>>> getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(Map<Integer, Map<LocalDate, Map<RoomType, Integer>>> availableRooms) {
        this.availableRooms = availableRooms;
    }

    public Map<Integer, Map<LocalDate, Map<RoomType, Integer>>> getBookedRooms() {
        return bookedRooms;
    }

    public void setBookedRooms(Map<Integer, Map<LocalDate, Map<RoomType, Integer>>> bookedRooms) {
        this.bookedRooms = bookedRooms;
    }
}