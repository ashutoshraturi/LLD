package models;

import enums.RoomType;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomLock {
    private RoomType roomType;
    private Hotel hotel;
    private AtomicInteger timeoutInSeconds;
    private Date lockTime;
    private String lockedBy;

    public RoomLock(RoomType roomType, Hotel hotel, AtomicInteger timeoutInSeconds, Date lockTime, String lockedBy) {
        this.roomType = roomType;
        this.hotel = hotel;
        this.timeoutInSeconds = timeoutInSeconds;
        this.lockTime = lockTime;
        this.lockedBy = lockedBy;
    }

    public boolean isLockExpired() {
        final Instant lockInstant = lockTime.toInstant().plusSeconds(timeoutInSeconds.get());
        final Instant currentInstant = new Date().toInstant();
        return lockInstant.isBefore(currentInstant);
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public AtomicInteger getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public void setTimeoutInSeconds(AtomicInteger timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }
}
