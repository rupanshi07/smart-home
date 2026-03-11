import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SmartCamera extends SmartDevice implements EnergyMonitor {

    public enum Resolution { HD_720P, FULL_HD_1080P, UHD_4K }

    private Resolution resolution;
    private boolean    motionDetection;
    private boolean    isRecording;
    private boolean    nightVision;
    private int        motionAlerts;
    private double     storageUsedGB;
    private double     storageTotalGB;
    private final List<String> eventLog;

    public SmartCamera(String id, String name, String location,
                       Resolution resolution, double storageGB) {
        super(id, name, DeviceCategory.SECURITY, location);
        this.resolution      = resolution;
        this.motionDetection = true;
        this.isRecording     = false;
        this.nightVision     = true;
        this.motionAlerts    = 0;
        this.storageUsedGB   = 0;
        this.storageTotalGB  = storageGB;
        this.eventLog        = new ArrayList<>();
    }

    @Override public String getDeviceType() { return "Smart Camera"; }

    @Override
    public void displayDetails() {
        System.out.println("\n  ╔══════════════════════════════════════╗");
        System.out.println("  ║       📹 SMART CAMERA DETAILS         ║");
        System.out.println("  ╠══════════════════════════════════════╣");
        printSummary();
        System.out.println("  Resolution      : " + resolution);
        System.out.println("  Recording       : " + (isRecording ? "🔴 YES" : "No"));
        System.out.println("  Motion Detection: " + (motionDetection ? "✅ ON" : "OFF"));
        System.out.println("  Night Vision    : " + (nightVision ? "✅ ON" : "OFF"));
        System.out.printf ("  Storage Used    : %.1f / %.1f GB (%.0f%%)\n",
                storageUsedGB, storageTotalGB, (storageUsedGB / storageTotalGB) * 100);
        System.out.println("  Motion Alerts   : " + motionAlerts);
        System.out.println("  " + getEnergyReport());
        System.out.println("  ╚══════════════════════════════════════╝");
    }

    @Override
    public String getSpecificReport() {
        return String.format("Res: %s | Recording: %s | Motion: %s | Storage: %.1f/%.1fGB",
                resolution, isRecording ? "YES" : "NO",
                motionDetection ? "ON" : "OFF", storageUsedGB, storageTotalGB);
    }

    public void startRecording() {
        if (!isOn()) { System.out.println("  ⚠️  Camera must be ON to record."); return; }
        isRecording = true;
        status = DeviceStatus.RECORDING;
        updateTimestamp(); incrementOps();
        logEvent("Recording STARTED");
        System.out.println("  🔴 Recording started.");
    }

    public void stopRecording() {
        isRecording = false;
        status = DeviceStatus.IDLE;
        double recorded = Math.random() * 0.5;
        storageUsedGB = Math.min(storageTotalGB, storageUsedGB + recorded);
        updateTimestamp(); incrementOps();
        logEvent(String.format("Recording STOPPED (%.2f GB saved)", recorded));
        System.out.printf("  ⏹  Recording stopped. %.2f GB saved.\n", recorded);
    }

    public void triggerMotionAlert() {
        motionAlerts++;
        logEvent("MOTION DETECTED — Alert #" + motionAlerts);
        System.out.println("  🚨 Motion Alert #" + motionAlerts + " triggered!");
    }

    public void setResolution(Resolution r) {
        this.resolution = r;
        System.out.println("  📐 Resolution changed to: " + r);
    }

    public void setMotionDetection(boolean enabled) {
        this.motionDetection = enabled;
        System.out.println("  🎯 Motion detection: " + (enabled ? "ENABLED" : "DISABLED"));
    }

    public void setNightVision(boolean enabled) {
        this.nightVision = enabled;
        System.out.println("  🌙 Night vision: " + (enabled ? "ENABLED" : "DISABLED"));
    }

    public void clearStorage() {
        storageUsedGB = 0;
        System.out.println("  🗑️  Camera storage cleared.");
        logEvent("Storage cleared");
    }

    private void logEvent(String event) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        eventLog.add("[" + ts + "] " + event);
    }

    public void showEventLog() {
        System.out.println("  📋 Camera Event Log (" + eventLog.size() + " entries):");
        int from = Math.max(0, eventLog.size() - 8);
        for (int i = from; i < eventLog.size(); i++)
            System.out.println("    " + eventLog.get(i));
    }

    @Override
    public double getPowerConsumption() {
        if (!isOn()) return 0;
        double base;
        switch (resolution) {
            case HD_720P:       base = 5.0;  break;
            case FULL_HD_1080P: base = 8.0;  break;
            case UHD_4K:        base = 15.0; break;
            default:            base = 8.0;
        }
        return isRecording ? base * 1.5 : base;
    }

    @Override
    public double getEnergyUsedToday() { return getPowerConsumption() * 24 / 1000; }

    @Override
    public String getEnergyReport() {
        return String.format("Power: %.1fW | Daily estimate: %.3f kWh",
                getPowerConsumption(), getEnergyUsedToday());
    }

    public boolean isRecording()     { return isRecording; }
    public int    getMotionAlerts()  { return motionAlerts; }
    public double getStorageUsedGB() { return storageUsedGB; }
}
