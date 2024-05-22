package utils;
import models.Hotel;
import models.RoomsForDate;

import java.util.List;

public interface SeatLockProvider {

    void lockSeats(Hotel hotel, List<RoomsForDate> roomsForDates, String user);
    void unlockSeats(Hotel hotel, List<RoomsForDate> roomsForDates, String user);
    boolean validateLock(Hotel hotel, RoomsForDate roomsForDate, String user);

    List<RoomsForDate> getLockedRooms(Hotel hotel);
}
