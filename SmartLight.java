public class SmartLight extends SmartDevice implements EnergyMonitor, Schedulable {

    private int brightness;
    private String color;
    private boolean dimmable;
    private String scheduleOn;
    private String scheduleOff;
    private boolean scheduleActive;
    private double hoursOnToday;

    private static final double WATTS_PER_BRIGHTNESS = 0.1;

    public SmartLight(String id, String name, String location,
                      boolean dimmable, String color) {
        super(id, name, DeviceCategory.LIGHTING, location);
        this.brightness     = 100;
        this.color          = color;
        this.dimmable       = dimmable;
        this.scheduleActive = false;
        this.hoursOnToday   = 0;
    }

    @Override public String getDeviceType() { return "Smart Light"; }

    @Override
    public void displayDetails() {
        System.out.println("\n  ╔══════════════════════════════════════╗");
        System.out.println("  ║        💡 SMART LIGHT DETAILS        ║");
        System.out.println("  ╠══════════════════════════════════════╣");
        printSummary();
        System.out.printf ("  Brightness: %d%%\n", brightness);
        System.out.println("  Color     : " + color);
        System.out.println("  Dimmable  : " + (dimmable ? "Yes" : "No"));
        System.out.println("  Schedule  : " + getSchedule());
        System.out.println("  " + getEnergyReport());
        System.out.println("  ╚══════════════════════════════════════╝");
    }

    @Override
    public String getSpecificReport() {
        return String.format("Brightness: %d%% | Color: %s | %s",
                brightness, color, getEnergyReport());
    }

    public void setBrightness(int level) {
        if (!isOn()) { System.out.println("  ⚠️  Turn the light ON first."); return; }
        if (!dimmable && level != 100) {
            System.out.println("  ⚠️  This light is not dimmable."); return;
        }
        this.brightness = Math.max(0, Math.min(100, level));
        updateTimestamp(); incrementOps();
        System.out.printf("  💡 Brightness set to %d%%\n", brightness);
    }

    public void setColor(String color) {
        if (!isOn()) { System.out.println("  ⚠️  Turn the light ON first."); return; }
        this.color = color;
        updateTimestamp(); incrementOps();
        System.out.println("  🎨 Color changed to: " + color);
    }

    @Override
    public void turnOn() {
        super.turnOn();
        this.brightness = 100;
    }

    @Override
    public void turnOff() {
        super.turnOff();
        this.brightness = 0;
    }

    @Override
    public double getPowerConsumption() {
        return isOn() ? brightness * WATTS_PER_BRIGHTNESS : 0;
    }

    @Override
    public double getEnergyUsedToday() {
        return Math.round((getPowerConsumption() * hoursOnToday / 1000) * 1000.0) / 1000.0;
    }

    @Override
    public String getEnergyReport() {
        return String.format("Power: %.1fW | Energy today: %.3f kWh",
                getPowerConsumption(), getEnergyUsedToday());
    }

    @Override
    public void setSchedule(String onTime, String offTime) {
        this.scheduleOn     = onTime;
        this.scheduleOff    = offTime;
        this.scheduleActive = true;
        System.out.printf("  📅 Schedule set: ON at %s, OFF at %s\n", onTime, offTime);
    }

    @Override
    public String getSchedule() {
        return scheduleActive
                ? "ON @ " + scheduleOn + " | OFF @ " + scheduleOff
                : "No schedule set";
    }

    @Override public boolean isScheduleActive() { return scheduleActive; }

    public int getBrightness()          { return brightness; }
    public String getColor()            { return color; }
    public boolean isDimmable()         { return dimmable; }
    public void addHoursOn(double h)    { this.hoursOnToday += h; }
}
