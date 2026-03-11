import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SmartLock extends SmartDevice {

    private boolean isLocked;
    private String  accessPin;
    private int     failedAttempts;
    private boolean autoLockEnabled;
    private int     autoLockMinutes;
    private final List<String> accessLog;

    private static final int MAX_FAILED = 3;

    public SmartLock(String id, String name, String location, String pin) {
        super(id, name, DeviceCategory.SECURITY, location);
        this.isLocked        = true;
        this.accessPin       = pin;
        this.failedAttempts  = 0;
        this.autoLockEnabled = true;
        this.autoLockMinutes = 5;
        this.accessLog       = new ArrayList<>();
        this.status          = DeviceStatus.LOCKED;
        logEvent("System", "Device initialized — locked");
    }

    @Override public String getDeviceType() { return "Smart Lock"; }

    @Override
    public void displayDetails() {
        System.out.println("\n  ╔══════════════════════════════════════╗");
        System.out.println("  ║       🔐 SMART LOCK DETAILS           ║");
        System.out.println("  ╠══════════════════════════════════════╣");
        printSummary();
        System.out.println("  State       : " + (isLocked ? "🔒 LOCKED" : "🔓 UNLOCKED"));
        System.out.println("  Auto-Lock   : " + (autoLockEnabled ? autoLockMinutes + " min" : "Disabled"));
        System.out.println("  Bad Attempts: " + failedAttempts + "/" + MAX_FAILED);
        System.out.println("  Log Entries : " + accessLog.size());
        System.out.println("  Recent Logs :");
        int from = Math.max(0, accessLog.size() - 5);
        for (int i = from; i < accessLog.size(); i++)
            System.out.println("    " + accessLog.get(i));
        System.out.println("  ╚══════════════════════════════════════╝");
    }

    @Override
    public String getSpecificReport() {
        return String.format("State: %s | Auto-lock: %s | Attempts: %d",
                isLocked ? "LOCKED" : "UNLOCKED",
                autoLockEnabled ? autoLockMinutes + "min" : "off",
                failedAttempts);
    }

    public boolean unlock(String pin) {
        if (failedAttempts >= MAX_FAILED) {
            System.out.println("  🚨 Device LOCKED due to too many failed attempts!");
            logEvent("SYSTEM", "Unlock blocked — max attempts reached");
            return false;
        }
        if (!pin.equals(accessPin)) {
            failedAttempts++;
            System.out.println("  ❌ Incorrect PIN. Attempt " + failedAttempts + "/" + MAX_FAILED);
            logEvent("USER", "Failed unlock attempt (" + failedAttempts + ")");
            return false;
        }
        isLocked       = false;
        failedAttempts = 0;
        status         = DeviceStatus.UNLOCKED;
        updateTimestamp(); incrementOps();
        System.out.println("  🔓 Door UNLOCKED successfully.");
        logEvent("USER", "Unlocked successfully");
        return true;
    }

    public void lock() {
        isLocked = true;
        status   = DeviceStatus.LOCKED;
        updateTimestamp(); incrementOps();
        System.out.println("  🔒 Door LOCKED.");
        logEvent("USER", "Locked");
    }

    public boolean changePin(String oldPin, String newPin) {
        if (!oldPin.equals(accessPin)) {
            System.out.println("  ❌ Incorrect current PIN."); return false;
        }
        if (newPin.length() < 4) {
            System.out.println("  ⚠️  PIN must be at least 4 digits."); return false;
        }
        this.accessPin = newPin;
        System.out.println("  ✅ PIN changed successfully.");
        logEvent("USER", "PIN changed");
        return true;
    }

    @Override
    public void turnOn()  { System.out.println("  ℹ️  Use lock() / unlock() for this device."); }
    @Override
    public void turnOff() { System.out.println("  ℹ️  Use lock() / unlock() for this device."); }

    public void setAutoLock(boolean enabled, int minutes) {
        this.autoLockEnabled = enabled;
        this.autoLockMinutes = minutes;
        System.out.println("  ⚙️  Auto-lock " + (enabled ? "enabled (" + minutes + " min)" : "disabled"));
    }

    private void logEvent(String actor, String event) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        accessLog.add(String.format("[%s] [%s] %s", ts, actor, event));
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
        System.out.println("  ✅ Failed attempts counter reset.");
    }

    public boolean isLocked()         { return isLocked; }
    public int getFailedAttempts()    { return failedAttempts; }
    public List<String> getAccessLog(){ return new ArrayList<>(accessLog); }
}
