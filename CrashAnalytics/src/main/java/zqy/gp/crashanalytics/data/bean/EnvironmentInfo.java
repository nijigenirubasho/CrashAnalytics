package zqy.gp.crashanalytics.data.bean;

import androidx.annotation.Keep;

@Keep
public class EnvironmentInfo {

    private String manufacturer;
    private String model;
    private int sdkInt;
    private String fingerprint;
    private long availableMemory;
    private long availableStorage;
    private int orientation;

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getSdkInt() {
        return sdkInt;
    }

    public void setSdkInt(int sdkInt) {
        this.sdkInt = sdkInt;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public long getAvailableMemory() {
        return availableMemory;
    }

    public void setAvailableMemory(long availableMemory) {
        this.availableMemory = availableMemory;
    }

    public long getAvailableStorage() {
        return availableStorage;
    }

    public void setAvailableStorage(long availableStorage) {
        this.availableStorage = availableStorage;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
