public class SmartThermostat extends SmartDevice implements EnergyMonitor, Schedulable {

    public enum Mode { HEATING, COOLING, AUTO, FAN_ONLY, ECO }

    private double targetTemp;
    private double currentTemp;
    private Mode   mode;
    private int    fanSpeed;
    private String scheduleOn;
    private String scheduleOff;
    private boolean scheduleActive;
    private double hoursRunToday;

    public SmartThermostat(String id, String name, String location, double initialTemp) {
        super(id, name, DeviceCategory.CLIMATE, location);
        this.currentTemp    = initialTemp;
        this.targetTemp     = 22.0;
        this.mode           = Mode.AUTO;
        this.fanSpeed       = 2;
        this.scheduleActive = false;
        this.hoursRunToday  = 0;
    }

    @Override public String getDeviceType() { return "Smart Thermostat"; }

    @Override
    public void displayDetails() {
        System.out.println("\n  ╔══════════════════════════════════════╗");
        System.out.println("  ║     🌡️  SMART THERMOSTAT DETAILS      ║");
        System.out.println("  ╠══════════════════════════════════════╣");
        printSummary();
        System.out.printf ("  Current Temp : %.1f°C\n", currentTemp);
        System.out.printf ("  Target Temp  : %.1f°C\n", targetTemp);
        System.out.println("  Mode         : " + mode);
        System.out.println("  Fan Speed    : " + fanSpeed + "/5");
        System.out.println("  Schedule     : " + getSchedule());
        System.out.println("  " + getEnergyReport());
        System.out.println("  ╚══════════════════════════════════════╝");
    }

    @Override
    public String getSpecificReport() {
        return String.format("Temp: %.1f°C -> %.1f°C | Mode: %s | Fan: %d",
                currentTemp, targetTemp, mode, fanSpeed);
    }

    public void setTargetTemperature(double temp) {
        if (temp < 10 || temp > 35) {
            System.out.println("  ⚠️  Temperature must be between 10°C and 35°C."); return;
        }
        this.targetTemp = temp;
        updateTimestamp(); incrementOps();
        System.out.printf("  🌡️  Target temperature set to %.1f°C\n", temp);
        assessHeatingCooling();
    }

    public void setMode(Mode newMode) {
        this.mode = newMode;
        updateTimestamp(); incrementOps();
        System.out.println("  ⚙️  Mode changed to: " + mode);
    }

    public void setFanSpeed(int speed) {
        if (speed < 1 || speed > 5) {
            System.out.println("  ⚠️  Fan speed must be 1-5."); return;
        }
        this.fanSpeed = speed;
        updateTimestamp(); incrementOps();
        System.out.println("  💨 Fan speed set to: " + fanSpeed);
    }

    private void assessHeatingCooling() {
        if (targetTemp > currentTemp && mode != Mode.COOLING)
            System.out.println("  🔥 Heating activated.");
        else if (targetTemp < currentTemp && mode != Mode.HEATING)
            System.out.println("  ❄️  Cooling activated.");
        else
            System.out.println("  ✅ Temperature is at target.");
    }

    public void updateCurrentTemp(double temp) {
        this.currentTemp = temp;
        System.out.printf("  📡 Room temperature updated to %.1f°C\n", temp);
    }

    @Override
    public double getPowerConsumption() {
        if (!isOn()) return 0;
        double base = (mode == Mode.HEATING) ? 2000 : (mode == Mode.COOLING) ? 1500 : 800;
        return base * (fanSpeed / 5.0);
    }

    @Override
    public double getEnergyUsedToday() {
        return Math.round((getPowerConsumption() * hoursRunToday / 1000) * 100.0) / 100.0;
    }

    @Override
    public String getEnergyReport() {
        return String.format("Power: %.0fW | Energy today: %.2f kWh",
                getPowerConsumption(), getEnergyUsedToday());
    }

    @Override
    public void setSchedule(String onTime, String offTime) {
        this.scheduleOn = onTime; this.scheduleOff = offTime; this.scheduleActive = true;
        System.out.printf("  📅 Schedule set: ON at %s, OFF at %s\n", onTime, offTime);
    }

    @Override
    public String getSchedule() {
        return scheduleActive ? "ON @ " + scheduleOn + " | OFF @ " + scheduleOff : "No schedule";
    }

    @Override public boolean isScheduleActive() { return scheduleActive; }

    public double getTargetTemp()       { return targetTemp; }
    public double getCurrentTemp()      { return currentTemp; }
    public Mode   getMode()             { return mode; }
    public int    getFanSpeed()         { return fanSpeed; }
    public void   addRunHours(double h) { this.hoursRunToday += h; }
}
