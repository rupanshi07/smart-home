import java.util.ArrayList;
import java.util.List;

public class SmartSpeaker extends SmartDevice implements EnergyMonitor {

    public enum EqualizerPreset { FLAT, BASS_BOOST, TREBLE_BOOST, VOCAL, ROCK, CLASSICAL }

    private int             volume;
    private boolean         isMuted;
    private String          currentTrack;
    private boolean         isPlaying;
    private EqualizerPreset equalizer;
    private final List<String> playlist;
    private int             playlistIndex;
    private double          hoursPlayedToday;

    public SmartSpeaker(String id, String name, String location) {
        super(id, name, DeviceCategory.ENTERTAINMENT, location);
        this.volume           = 40;
        this.isMuted          = false;
        this.currentTrack     = "None";
        this.isPlaying        = false;
        this.equalizer        = EqualizerPreset.FLAT;
        this.playlist         = new ArrayList<>();
        this.playlistIndex    = 0;
        this.hoursPlayedToday = 0;
    }

    @Override public String getDeviceType() { return "Smart Speaker"; }

    @Override
    public void displayDetails() {
        System.out.println("\n  ╔══════════════════════════════════════╗");
        System.out.println("  ║      🔊 SMART SPEAKER DETAILS         ║");
        System.out.println("  ╠══════════════════════════════════════╣");
        printSummary();
        System.out.printf ("  Volume    : %d%% %s\n", volume, isMuted ? "[MUTED]" : "");
        System.out.println("  Now Playing: " + (isPlaying ? "▶ " + currentTrack : "Stopped"));
        System.out.println("  Equalizer : " + equalizer);
        System.out.println("  Playlist  : " + playlist.size() + " track(s)");
        System.out.println("  " + getEnergyReport());
        System.out.println("  ╚══════════════════════════════════════╝");
    }

    @Override
    public String getSpecificReport() {
        return String.format("Vol: %d%% | %s | EQ: %s | Playlist: %d tracks",
                volume, isPlaying ? "▶ " + currentTrack : "Stopped",
                equalizer, playlist.size());
    }

    public void play(String track) {
        if (!isOn()) { System.out.println("  ⚠️  Turn speaker ON first."); return; }
        currentTrack = track;
        isPlaying    = true;
        updateTimestamp(); incrementOps();
        System.out.println("  ▶  Now playing: " + track);
    }

    public void playNext() {
        if (playlist.isEmpty()) { System.out.println("  ℹ️  Playlist is empty."); return; }
        playlistIndex = (playlistIndex + 1) % playlist.size();
        play(playlist.get(playlistIndex));
    }

    public void playPrevious() {
        if (playlist.isEmpty()) { System.out.println("  ℹ️  Playlist is empty."); return; }
        playlistIndex = (playlistIndex - 1 + playlist.size()) % playlist.size();
        play(playlist.get(playlistIndex));
    }

    public void stop() {
        isPlaying    = false;
        currentTrack = "None";
        System.out.println("  ⏹  Playback stopped.");
    }

    public void setVolume(int v) {
        if (!isOn()) { System.out.println("  ⚠️  Turn speaker ON first."); return; }
        this.volume = Math.max(0, Math.min(100, v));
        if (isMuted) isMuted = false;
        updateTimestamp(); incrementOps();
        System.out.printf("  🔊 Volume set to %d%%\n", volume);
    }

    public void mute()   { isMuted = true;  System.out.println("  🔇 Speaker muted.");   }
    public void unmute() { isMuted = false; System.out.println("  🔊 Speaker unmuted."); }

    public void addToPlaylist(String track) {
        playlist.add(track);
        System.out.println("  ➕ Added to playlist: " + track);
    }

    public void clearPlaylist() {
        playlist.clear(); playlistIndex = 0;
        System.out.println("  🗑️  Playlist cleared.");
    }

    public void showPlaylist() {
        if (playlist.isEmpty()) { System.out.println("  ℹ️  Playlist is empty."); return; }
        System.out.println("  📋 Playlist (" + playlist.size() + " tracks):");
        for (int i = 0; i < playlist.size(); i++)
            System.out.printf("    %s %d. %s\n", i == playlistIndex ? "▶" : " ", i + 1, playlist.get(i));
    }

    public void setEqualizer(EqualizerPreset preset) {
        this.equalizer = preset;
        System.out.println("  🎛️  Equalizer set to: " + preset);
    }

    @Override
    public double getPowerConsumption() {
        if (!isOn()) return 0;
        return isPlaying ? 15 + (volume * 0.2) : 3.0;
    }

    @Override
    public double getEnergyUsedToday() {
        return Math.round((getPowerConsumption() * hoursPlayedToday / 1000) * 1000.0) / 1000.0;
    }

    @Override
    public String getEnergyReport() {
        return String.format("Power: %.1fW | Energy today: %.3f kWh",
                getPowerConsumption(), getEnergyUsedToday());
    }

    public void addPlayHours(double h)  { this.hoursPlayedToday += h; }
    public int    getVolume()           { return volume; }
    public boolean isPlaying()          { return isPlaying; }
    public String getCurrentTrack()     { return currentTrack; }
}
