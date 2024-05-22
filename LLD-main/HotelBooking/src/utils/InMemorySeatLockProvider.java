package utils;

import enums.RoomType;
import models.Hotel;
import models.RoomLock;
import models.RoomsForDate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.common.collect.ImmutableList;

public class InMemorySeatLockProvider implements SeatLockProvider {

    private final AtomicInteger lockTimeout; // Bonus feature.
    private final ConcurrentHashMap<Hotel, Map<RoomType, RoomLock>> locks;

    public InMemorySeatLockProvider(AtomicInteger lockTimeout) {
        this.locks = new ConcurrentHashMap<>();
        this.lockTimeout = lockTimeout;
    }

    public void lockSeats(Hotel hotel, List<RoomsForDate> roomsForDates, String user) {
        for (RoomsForDate room : roomsForDates) {
            if (isSeatLocked(hotel, room.getRoomType())) {
                throw new RuntimeException();
            }
        }

        for (RoomsForDate seat : roomsForDates) {
            lockSeat(hotel, seat.getRoomType(), user, lockTimeout);
        }
    }

    @Override
    public void unlockSeats(final Hotel show, final List<RoomsForDate> seats, final String user) {
        for (RoomsForDate seat: seats) {
            if (validateLock(show, seat.getRoomType(), user)) {
                unlockSeat(show, seat.getRoomType());
            }
        }
    }

    public boolean validateLock(final Hotel show, final RoomType seat, final String user) {
        return isSeatLocked(show, seat) && locks.get(show).get(seat).getLockedBy().equals(user);
    }

    public List<RoomType> getLockedSeats(final Hotel show) {
        if (!locks.containsKey(show)) {
            return ImmutableList.of();
        }
        final List<RoomType> lockedSeats = new ArrayList<>();

        for (RoomType seat : locks.get(show).keySet()) {
            if (isSeatLocked(show, seat)) {
                lockedSeats.add(seat);
            }
        }
        return lockedSeats;
    }

    private void unlockSeat(final Hotel show, final RoomType seat) {
        if (!locks.containsKey(show)) {
            return;
        }
        locks.get(show).remove(seat);
    }

    private void lockSeat(final Hotel hotel, final RoomType roomType, final String user, final AtomicInteger timeoutInSeconds) {
        if (!locks.containsKey(hotel)) {
            locks.put(hotel, new HashMap<>());
        }
        final RoomLock lock = new RoomLock(roomType, hotel, timeoutInSeconds, new Date(), user);
        locks.get(hotel).put(roomType, lock);
    }

    private boolean isSeatLocked(final Hotel show, final RoomType seat) {
        return locks.containsKey(show) && locks.get(show).containsKey(seat) && !locks.get(show).get(seat).isLockExpired();
    }
}
