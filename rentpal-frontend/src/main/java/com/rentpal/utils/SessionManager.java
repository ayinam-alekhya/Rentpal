package com.rentpal.utils;

import com.rentpal.dto.OwnerDTO;
import com.rentpal.dto.TenantDTO;

public class SessionManager {
    private static SessionManager instance;
    private OwnerDTO currentOwner;
    private TenantDTO currentTenant;
    private boolean isOwner;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentOwner(OwnerDTO owner) {
        this.currentOwner = owner;
        this.currentTenant = null;
        this.isOwner = true;
    }

    public void setCurrentTenant(TenantDTO tenant) {
        this.currentTenant = tenant;
        this.currentOwner = null;
        this.isOwner = false;
    }

    public OwnerDTO getCurrentOwner() {
        return currentOwner;
    }

    public TenantDTO getCurrentTenant() {
        return currentTenant;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public boolean isTenant() {
        return !isOwner;
    }

    public void clearSession() {
        this.currentOwner = null;
        this.currentTenant = null;
        this.isOwner = false;
    }
}