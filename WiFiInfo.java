package com.example.wi_fid;

public class WiFiInfo {
    private final String SSID;
    private final String BSSID;
    private boolean status = false;

    WiFiInfo(String SSID, String BSSID) {
        this.SSID = SSID.toUpperCase();
        this.BSSID = BSSID;
    }

    public String getBSSID() { return this.BSSID; }
    public String getSSID() { return this.SSID; }
    public boolean getStatus() { return this.status; }

    public void setStatus(boolean newStatus) { this.status = newStatus; }
}
