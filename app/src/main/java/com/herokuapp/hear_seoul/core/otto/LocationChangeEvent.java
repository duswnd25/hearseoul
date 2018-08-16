package com.herokuapp.hear_seoul.core.otto;

import android.location.Location;

public class LocationChangeEvent {
    private Location location;

    public LocationChangeEvent(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
