public enum DeviceCategory {
    LIGHTING("💡 Lighting"),
    CLIMATE("🌡️  Climate"),
    SECURITY("🔐 Security"),
    ENTERTAINMENT("🎵 Entertainment"),
    APPLIANCE("🏠 Appliance");

    private final String display;

    DeviceCategory(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
