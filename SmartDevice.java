import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class SmartDevice implements Controllable {

    private final String deviceId;
    private String deviceName;
    private final DeviceCategory category;
    protected DeviceStatus status;
    private String location;
    private LocalDateTime lastUpdated;
    private int operationCount;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SmartDevice(String deviceId, String deviceName,
                       DeviceCategory category, String location) {
        this.deviceId       = deviceId;
        this.deviceName     = deviceName;
        this.category       = category;
        this.location       = location;
        this.status         = DeviceStatus.OFF;
        this.lastUpdated    = LocalDateTime.now();
        this.operationCount = 0;
    }

    public abstract String getDeviceType();
    public abstract void displayDetails();
    public abstract String getSpecificReport();

    @Override
    public void turnOn() {
        this.status      = DeviceStatus.ON;
        this.lastUpdated = LocalDateTime.now();
        this.operationCount++;
        System.out.println("  ✅ " + deviceName + " turned ON.");
    }

    @Override
    public void turnOff() {
        this.status      = DeviceStatus.OFF;
        this.lastUpdated = LocalDateTime.now();
        this.operationCount++;
        System.out.println("  ❌ " + deviceName + " turned OFF.");
    }

    @Override
    public void reset() {
        this.status         = DeviceStatus.OFF;
        this.lastUpdated    = LocalDateTime.now();
        this.operationCount = 0;
        System.out.println("  🔄 " + deviceName + " has been reset.");
    }

    @Override
    public String getStatusReport() {
        return String.format(
            "  %-22s │ %-18s │ %-14s │ %-12s",
            deviceName, getDeviceType(), status, location
        );
    }

    public void printSummary() {
        System.out.println("  Device   : " + deviceName + " [" + deviceId + "]");
        System.out.println("  Type     : " + getDeviceType());
        System.out.println("  Category : " + category);
        System.out.println("  Location : " + location);
        System.out.println("  Status   : " + status);
        System.out.println("  Updated  : " + lastUpdated.format(FORMATTER));
        System.out.println("  Ops Count: " + operationCount);
    }

    public String getDeviceId()           { return deviceId; }
    public String getDeviceName()         { return deviceName; }
    public DeviceCategory getCategory()   { return category; }
    public DeviceStatus getStatus()       { return status; }
    public String getLocation()           { return location; }
    public int getOperationCount()        { return operationCount; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }

    public void setDeviceName(String name) { this.deviceName = name; }
    public void setLocation(String loc)    { this.location = loc; }

    protected void incrementOps()    { this.operationCount++; }
    protected void updateTimestamp() { this.lastUpdated = LocalDateTime.now(); }

    public boolean isOn()  { return status == DeviceStatus.ON; }
    public boolean isOff() { return status == DeviceStatus.OFF; }
}
