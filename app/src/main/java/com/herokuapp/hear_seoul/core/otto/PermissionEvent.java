package com.herokuapp.hear_seoul.core.otto;

public class PermissionEvent {
    private boolean permissionGranted;

    public PermissionEvent(boolean permissionGranted) {
        this.permissionGranted = permissionGranted;
    }

    public boolean isPermissionGranted() {
        return permissionGranted;
    }

    public void setPermissionGranted(boolean permissionGranted) {
        this.permissionGranted = permissionGranted;
    }
}
