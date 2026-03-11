import java.util.*;
import java.util.stream.Collectors;

public class HomeManager {

    private final String homeName;
    private final Map<String, SmartDevice> devices;
    private final List<String> activityLog;

    public HomeManager(String homeName) {
        this.homeName    = homeName;
        this.devices     = new LinkedHashMap<>();
        this.activityLog = new ArrayList<>();
        log("Home Manager initialized for: " + homeName);
    }

    public void addDevice(SmartDevice device) {
        devices.put(device.getDeviceId(), device);
        log("Device added: " + device.getDeviceName() + " [" + device.getDeviceId() + "]");
        System.out.println("  ✅ Registered: " + device.getDeviceName());
    }

    public boolean removeDevice(String deviceId) {
        SmartDevice d = devices.remove(deviceId);
        if (d != null) {
            log("Device removed: " + d.getDeviceName());
            System.out.println("  🗑️  Removed: " + d.getDeviceName());
            return true;
        }
        System.out.println("  ⚠️  Device not found: " + deviceId);
        return false;
    }

    public SmartDevice getDevice(String id) { return devices.get(id); }

    public void turnAllOn() {
        System.out.println("\n  ⚡ Turning ALL devices ON...");
        devices.values().forEach(d -> { d.turnOn(); log(d.getDeviceName() + " -> ON"); });
    }

    public void turnAllOff() {
        System.out.println("\n  🌙 Turning ALL devices OFF...");
        devices.values().forEach(d -> { d.turnOff(); log(d.getDeviceName() + " -> OFF"); });
    }

    public void turnCategoryOn(DeviceCategory cat) {
        System.out.println("\n  ⚡ Turning ON all " + cat + " devices...");
        getByCategory(cat).forEach(d -> { d.turnOn(); log(d.getDeviceName() + " -> ON"); });
    }

    public void turnCategoryOff(DeviceCategory cat) {
        System.out.println("\n  ❌ Turning OFF all " + cat + " devices...");
        getByCategory(cat).forEach(d -> { d.turnOff(); log(d.getDeviceName() + " -> OFF"); });
    }

    public void showDashboard() {
        System.out.println("\n" + "=".repeat(72));
        System.out.println("  🏠  SMART HOME DASHBOARD — " + homeName.toUpperCase());
        System.out.println("=".repeat(72));
        System.out.printf("  Total Devices: %-5d  Active: %-5d  Inactive: %-5d\n",
                devices.size(), countActive(), devices.size() - countActive());
        System.out.printf("  Total Power Now : %.1f W\n", getTotalPower());
        System.out.println("-".repeat(72));
        System.out.printf("  %-24s %-20s %-16s %-10s\n",
                "Device Name", "Type", "Status", "Location");
        System.out.println("-".repeat(72));
        devices.values().forEach(d -> System.out.println(d.getStatusReport()));
        System.out.println("=".repeat(72));
    }

    public void showEnergyReport() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ⚡  ENERGY CONSUMPTION REPORT");
        System.out.println("=".repeat(60));
        double total = 0;
        for (SmartDevice d : devices.values()) {
            if (d instanceof EnergyMonitor) {
                EnergyMonitor em = (EnergyMonitor) d;
                double w = em.getPowerConsumption();
                total += w;
                System.out.printf("  %-22s  %6.1f W  |  %.3f kWh/day\n",
                        d.getDeviceName(), w, em.getEnergyUsedToday());
            }
        }
        System.out.println("-".repeat(60));
        System.out.printf("  %-22s  %6.1f W\n", "TOTAL", total);
        System.out.printf("  Estimated monthly cost: $%.2f (at $0.12/kWh)\n",
                total * 24 * 30 / 1000 * 0.12);
        System.out.println("=".repeat(60));
    }

    public void showSchedules() {
        System.out.println("\n  📅 SCHEDULED DEVICES:");
        boolean any = false;
        for (SmartDevice d : devices.values()) {
            if (d instanceof Schedulable) {
                Schedulable s = (Schedulable) d;
                if (s.isScheduleActive()) {
                    System.out.printf("  %-22s  %s\n", d.getDeviceName(), s.getSchedule());
                    any = true;
                }
            }
        }
        if (!any) System.out.println("  No active schedules.");
    }

    public void showActivityLog() {
        System.out.println("\n  📋 ACTIVITY LOG (last 15 entries):");
        int from = Math.max(0, activityLog.size() - 15);
        for (int i = from; i < activityLog.size(); i++)
            System.out.println("  " + activityLog.get(i));
    }

    public void showCategoryReport() {
        System.out.println("\n  📊 DEVICES BY CATEGORY:");
        for (DeviceCategory cat : DeviceCategory.values()) {
            List<SmartDevice> list = getByCategory(cat);
            if (!list.isEmpty()) {
                System.out.printf("  %s (%d)\n", cat, list.size());
                list.forEach(d -> System.out.printf("    * %-20s [%s]\n",
                        d.getDeviceName(), d.getStatus()));
            }
        }
    }

    private List<SmartDevice> getByCategory(DeviceCategory cat) {
        return devices.values().stream()
                .filter(d -> d.getCategory() == cat)
                .collect(Collectors.toList());
    }

    private long countActive() {
        return devices.values().stream().filter(SmartDevice::isOn).count();
    }

    private double getTotalPower() {
        return devices.values().stream()
                .filter(d -> d instanceof EnergyMonitor)
                .mapToDouble(d -> ((EnergyMonitor) d).getPowerConsumption())
                .sum();
    }

    private void log(String msg) {
        activityLog.add(String.format("[%s] %s",
                java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")), msg));
    }

    public Map<String, SmartDevice> getDevices() { return Collections.unmodifiableMap(devices); }
    public String getHomeName()                   { return homeName; }
    public int    getDeviceCount()                { return devices.size(); }
}
