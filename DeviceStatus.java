public enum DeviceStatus {
    ON("✅ ON"),
    OFF("❌ OFF"),
    STANDBY("💤 STANDBY"),
    ERROR("⚠️  ERROR"),
    LOCKED("🔒 LOCKED"),
    UNLOCKED("🔓 UNLOCKED"),
    RECORDING("🔴 RECORDING"),
    IDLE("⏸️  IDLE");

    private final String display;

    DeviceStatus(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
