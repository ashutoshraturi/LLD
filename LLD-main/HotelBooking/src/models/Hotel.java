package models;

import java.util.ArrayList;
import java.util.List;

public class Hotel {
    private int hotelId;
    private List<Room> rooms;
    private List<String> amenities;

    public Hotel(int hotelId, List<Room> rooms) {
        this.hotelId = hotelId;
        this.rooms = rooms;
        amenities = new ArrayList<>();
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }
}
