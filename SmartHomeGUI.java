import javax.swing.*;
import javax.swing.border.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * SmartHomeGUI — Java Swing Dashboard for Smart Home Management System
 * Dark industrial theme with live device cards, status indicators, and controls.
 * Integrates directly with existing SmartDevice class hierarchy.
 */
public class SmartHomeGUI extends JFrame {

    // ── Palette ────────────────────────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(10, 12, 18);
    private static final Color BG_PANEL     = new Color(18, 22, 32);
    private static final Color BG_CARD      = new Color(24, 29, 42);
    private static final Color BG_CARD_HOV  = new Color(30, 36, 52);
    private static final Color ACCENT_BLUE  = new Color(56, 139, 253);
    private static final Color ACCENT_GREEN = new Color(35, 197, 94);
    private static final Color ACCENT_RED   = new Color(248, 81, 73);
    private static final Color ACCENT_AMBER = new Color(255, 176, 0);
    private static final Color ACCENT_PURPLE= new Color(139, 92, 246);
    private static final Color TEXT_PRIMARY = new Color(230, 237, 243);
    private static final Color TEXT_SECONDARY=new Color(139, 148, 158);
    private static final Color DIVIDER      = new Color(33, 38, 55);

    // ── State ──────────────────────────────────────────────────────────────────
    private HomeManager home;
    private JPanel      cardsPanel;
    private JLabel      clockLabel;
    private JLabel      activeLbl, totalLbl, powerLbl;
    private Timer       refreshTimer;
    private final Map<String, DeviceCardPanel> cardMap = new LinkedHashMap<>();

    // ── Devices ───────────────────────────────────────────────────────────────
    private SmartLight      livingLight, kitchenLight, bedroomLight;
    private SmartThermostat livingThermostat;
    private SmartLock       frontDoor, backDoor;
    private SmartCamera     frontCam, backCam;
    private SmartSpeaker    livingSpeaker;

    // ══════════════════════════════════════════════════════════════════════════
    public SmartHomeGUI() {
        super("Smart Home Control Panel");
        setupHome();
        buildUI();
        startClock();
        setVisible(true);
    }

    // ── Device initialisation ─────────────────────────────────────────────────
    private void setupHome() {
        home = new HomeManager("Green Valley Smart Home");

        livingLight  = new SmartLight("LT-001", "Living Room Light", "Living Room", true, "Warm White");
        kitchenLight = new SmartLight("LT-002", "Kitchen Light",     "Kitchen",     true, "Cool White");
        bedroomLight = new SmartLight("LT-003", "Bedroom Light",     "Bedroom",     true, "Warm White");

        livingThermostat = new SmartThermostat("TH-001", "Main Thermostat", "Living Room", 24.5);

        frontDoor = new SmartLock("LK-001", "Front Door Lock", "Entrance",  "1234");
        backDoor  = new SmartLock("LK-002", "Back Door Lock",  "Back Yard", "5678");

        frontCam = new SmartCamera("CAM-001", "Front Camera", "Driveway",
                SmartCamera.Resolution.FULL_HD_1080P, 64.0);
        backCam  = new SmartCamera("CAM-002", "Back Camera",  "Garden",
                SmartCamera.Resolution.HD_720P, 32.0);

        livingSpeaker = new SmartSpeaker("SP-001", "Living Room Speaker", "Living Room");
        livingSpeaker.addToPlaylist("Beethoven - Moonlight Sonata");
        livingSpeaker.addToPlaylist("Mozart - Symphony No. 40");

        home.addDevice(livingLight);
        home.addDevice(kitchenLight);
        home.addDevice(bedroomLight);
        home.addDevice(livingThermostat);
        home.addDevice(frontDoor);
        home.addDevice(backDoor);
        home.addDevice(frontCam);
        home.addDevice(backCam);
        home.addDevice(livingSpeaker);

        // Initial states
        livingLight.turnOn();
        livingLight.setBrightness(70);
        livingThermostat.turnOn();
        livingThermostat.setTargetTemperature(22.0);
        frontCam.turnOn();
        frontCam.startRecording();
        backCam.turnOn();
        livingSpeaker.turnOn();
        livingSpeaker.play("Beethoven - Moonlight Sonata");
    }

    // ── UI Construction ───────────────────────────────────────────────────────
    private void buildUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 780);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setBackground(BG_DARK);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);

        root.add(buildHeader(),  BorderLayout.NORTH);
        root.add(buildStats(),   BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PANEL);
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, DIVIDER),
            new EmptyBorder(18, 28, 18, 28)
        ));

        // Left: title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        JLabel icon  = makeLabel("⌂", 26, ACCENT_BLUE, Font.PLAIN);
        JLabel title = makeLabel("  Smart Home Control", 20, TEXT_PRIMARY, Font.BOLD);
        JLabel sub   = makeLabel("  —  Green Valley", 14, TEXT_SECONDARY, Font.PLAIN);
        left.add(icon); left.add(title); left.add(sub);

        // Right: clock + all-on/off
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        clockLabel = makeLabel("", 13, TEXT_SECONDARY, Font.PLAIN);

        JButton allOn  = roundButton("All ON",  ACCENT_GREEN);
        JButton allOff = roundButton("All OFF", ACCENT_RED);

        allOn.addActionListener(e  -> { home.turnAllOn();  refreshCards(); });
        allOff.addActionListener(e -> { home.turnAllOff(); refreshCards(); });

        right.add(clockLabel); right.add(allOn); right.add(allOff);

        header.add(left,  BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildStats() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 0));
        wrapper.setBackground(BG_DARK);

        // Stat strip
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        stats.setBackground(BG_PANEL);
        stats.setBorder(new EmptyBorder(14, 28, 14, 28));

        totalLbl  = makeLabel("Devices: " + home.getDeviceCount(), 13, TEXT_SECONDARY, Font.PLAIN);
        activeLbl = makeLabel("", 13, ACCENT_GREEN,   Font.BOLD);
        powerLbl  = makeLabel("", 13, ACCENT_AMBER,   Font.PLAIN);

        stats.add(totalLbl);
        stats.add(makeLabel("   ·   ", 13, DIVIDER, Font.PLAIN));
        stats.add(activeLbl);
        stats.add(makeLabel("   ·   ", 13, DIVIDER, Font.PLAIN));
        stats.add(powerLbl);

        // Cards grid
        cardsPanel = new JPanel(new GridLayout(0, 3, 16, 16));
        cardsPanel.setBackground(BG_DARK);
        cardsPanel.setBorder(new EmptyBorder(20, 24, 24, 24));

        buildCards();
        updateStats();

        JScrollPane scroll = new JScrollPane(cardsPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        wrapper.add(stats,  BorderLayout.NORTH);
        wrapper.add(scroll, BorderLayout.CENTER);

        // Auto-refresh every 2 s
        refreshTimer = new Timer(2000, e -> { refreshCards(); updateStats(); });
        refreshTimer.start();

        return wrapper;
    }

    private void buildCards() {
        cardsPanel.removeAll();
        cardMap.clear();

        Object[][] devices = {
            { "LT-001", livingLight,      "💡", "Lighting",     ACCENT_AMBER  },
            { "LT-002", kitchenLight,     "💡", "Lighting",     ACCENT_AMBER  },
            { "LT-003", bedroomLight,     "💡", "Lighting",     ACCENT_AMBER  },
            { "TH-001", livingThermostat, "🌡", "Climate",      ACCENT_BLUE   },
            { "LK-001", frontDoor,        "🔐", "Security",     ACCENT_PURPLE },
            { "LK-002", backDoor,         "🔐", "Security",     ACCENT_PURPLE },
            { "CAM-001",frontCam,         "📹", "Security",     ACCENT_RED    },
            { "CAM-002",backCam,          "📹", "Security",     ACCENT_RED    },
            { "SP-001", livingSpeaker,    "🔊", "Entertainment",ACCENT_GREEN  },
        };

        for (Object[] row : devices) {
            String        id     = (String)        row[0];
            SmartDevice   dev    = (SmartDevice)   row[1];
            String        icon   = (String)        row[2];
            String        cat    = (String)        row[3];
            Color         accent = (Color)         row[4];

            DeviceCardPanel card = new DeviceCardPanel(id, dev, icon, cat, accent);
            cardMap.put(id, card);
            cardsPanel.add(card);
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private void refreshCards() {
        cardMap.values().forEach(DeviceCardPanel::refresh);
        updateStats();
    }

    private void updateStats() {
        long active = home.getDevices().values().stream().filter(SmartDevice::isOn).count();
        activeLbl.setText("Active: " + active + " / " + home.getDeviceCount());

        double power = home.getDevices().values().stream()
                .filter(d -> d instanceof EnergyMonitor)
                .mapToDouble(d -> ((EnergyMonitor) d).getPowerConsumption())
                .sum();
        powerLbl.setText(String.format("Total Power: %.0f W", power));
    }

    private void startClock() {
        Timer t = new Timer(1000, e -> {
            clockLabel.setText(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("EEE, dd MMM  HH:mm:ss")));
        });
        t.start();
        t.getActionListeners()[0].actionPerformed(null);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Device Card
    // ══════════════════════════════════════════════════════════════════════════
    class DeviceCardPanel extends JPanel {

        private final String        id;
        private final SmartDevice   device;
        private final String        iconText;
        private final Color         accent;

        private JLabel  statusDot;
        private JLabel  statusLbl;
        private JLabel  detailLbl;
        private JButton toggleBtn;
        private JButton controlBtn;
        private boolean hovered = false;

        DeviceCardPanel(String id, SmartDevice device, String icon, String cat, Color accent) {
            this.id       = id;
            this.device   = device;
            this.iconText = icon;
            this.accent   = accent;

            setLayout(new BorderLayout(0, 12));
            setBackground(BG_CARD);
            setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(DIVIDER, 1, true),
                new EmptyBorder(18, 18, 18, 18)
            ));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    hovered = true; setBackground(BG_CARD_HOV); repaint();
                }
                public void mouseExited(MouseEvent e) {
                    hovered = false; setBackground(BG_CARD);    repaint();
                }
            });

            build();
            refresh();
        }

        private void build() {
            // ── Top row: icon + category + status dot ─────────────────────────
            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);

            JPanel topLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            topLeft.setOpaque(false);
            JLabel ico = makeLabel(iconText, 22, accent, Font.PLAIN);
            JLabel cat = makeLabel(device.getCategory().toString().replace("💡 ", "")
                    .replace("🌡️  ", "").replace("🔐 ", "").replace("🎵 ", "").replace("🏠 ", ""),
                    11, TEXT_SECONDARY, Font.PLAIN);
            topLeft.add(ico); topLeft.add(cat);

            statusDot = makeLabel("●", 14, ACCENT_GREEN, Font.PLAIN);
            top.add(topLeft, BorderLayout.WEST);
            top.add(statusDot, BorderLayout.EAST);

            // ── Name ─────────────────────────────────────────────────────────
            JLabel nameLbl = makeLabel(device.getDeviceName(), 15, TEXT_PRIMARY, Font.BOLD);
            nameLbl.setBorder(new EmptyBorder(4, 0, 0, 0));

            // ── Location ─────────────────────────────────────────────────────
            JLabel locLbl = makeLabel("📍 " + device.getLocation(), 11, TEXT_SECONDARY, Font.PLAIN);

            // ── Status ───────────────────────────────────────────────────────
            statusLbl = makeLabel("", 12, ACCENT_GREEN, Font.BOLD);

            // ── Detail line ───────────────────────────────────────────────────
            detailLbl = makeLabel("", 11, TEXT_SECONDARY, Font.PLAIN);

            // ── Buttons ───────────────────────────────────────────────────────
            toggleBtn  = roundButton("Turn ON", ACCENT_GREEN);
            controlBtn = roundButton("Controls ▸", ACCENT_BLUE);
            controlBtn.setFont(controlBtn.getFont().deriveFont(11f));

            toggleBtn.addActionListener(e -> handleToggle());
            controlBtn.addActionListener(e -> openControls());

            JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
            btnRow.setOpaque(false);
            btnRow.add(toggleBtn);
            btnRow.add(controlBtn);

            // ── Info stack ────────────────────────────────────────────────────
            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setOpaque(false);
            info.add(nameLbl);
            info.add(Box.createVerticalStrut(3));
            info.add(locLbl);
            info.add(Box.createVerticalStrut(8));
            info.add(statusLbl);
            info.add(Box.createVerticalStrut(3));
            info.add(detailLbl);

            add(top,    BorderLayout.NORTH);
            add(info,   BorderLayout.CENTER);
            add(btnRow, BorderLayout.SOUTH);
        }

        void refresh() {
            boolean on   = device.isOn();
            String  stat = device.getStatus().toString();

            // Status dot colour
            if (on) statusDot.setForeground(ACCENT_GREEN);
            else     statusDot.setForeground(new Color(60, 70, 90));

            statusLbl.setText(stat);
            statusLbl.setForeground(on ? ACCENT_GREEN : TEXT_SECONDARY);

            detailLbl.setText(getDetail());

            toggleBtn.setText(on ? "Turn OFF" : "Turn ON");
            toggleBtn.setBackground(on ? new Color(60, 30, 30) : new Color(25, 55, 35));
            toggleBtn.setForeground(on ? ACCENT_RED : ACCENT_GREEN);

            repaint();
        }

        private String getDetail() {
            if (device instanceof SmartLight) {
                SmartLight l = (SmartLight) device;
                return device.isOn()
                        ? "Brightness: " + l.getBrightness() + "%  Color: " + l.getColor()
                        : "Standby";
            }
            if (device instanceof SmartThermostat) {
                SmartThermostat t = (SmartThermostat) device;
                return String.format("%.1f°C → %.1f°C  Mode: %s",
                        t.getCurrentTemp(), t.getTargetTemp(), t.getMode());
            }
            if (device instanceof SmartLock) {
                SmartLock l = (SmartLock) device;
                return l.isLocked() ? "🔒 Secured" : "🔓 Open";
            }
            if (device instanceof SmartCamera) {
                SmartCamera c = (SmartCamera) device;
                return c.isRecording() ? "🔴 Recording" : "Idle";
            }
            if (device instanceof SmartSpeaker) {
                SmartSpeaker s = (SmartSpeaker) device;
                return s.isPlaying() ? "▶ " + s.getCurrentTrack() : "Stopped";
            }
            return "";
        }

        private void handleToggle() {
            if (device instanceof SmartLock) {
                SmartLock lock = (SmartLock) device;
                if (lock.isLocked()) {
                    String pin = JOptionPane.showInputDialog(this, "Enter PIN to unlock:", "Unlock", JOptionPane.PLAIN_MESSAGE);
                    if (pin != null) lock.unlock(pin);
                } else {
                    lock.lock();
                }
            } else {
                if (device.isOn()) device.turnOff(); else device.turnOn();
            }
            refresh();
            updateStats();
        }

        private void openControls() {
            if (device instanceof SmartLight)      showLightDialog((SmartLight) device);
            else if (device instanceof SmartThermostat) showThermoDialog((SmartThermostat) device);
            else if (device instanceof SmartLock)  showLockDialog((SmartLock) device);
            else if (device instanceof SmartCamera) showCameraDialog((SmartCamera) device);
            else if (device instanceof SmartSpeaker) showSpeakerDialog((SmartSpeaker) device);
            refresh();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Control Dialogs
    // ══════════════════════════════════════════════════════════════════════════

    private void showLightDialog(SmartLight light) {
        JDialog d = styledDialog("💡 " + light.getDeviceName(), 340, 280);
        JPanel  p = dialogPanel();

        p.add(infoRow("Status",   light.getStatus().toString()));
        p.add(infoRow("Color",    light.getColor()));
        p.add(Box.createVerticalStrut(12));

        JLabel bLabel = makeLabel("Brightness: " + light.getBrightness() + "%", 12, TEXT_SECONDARY, Font.PLAIN);
        JSlider slider = new JSlider(0, 100, light.getBrightness());
        styleSlider(slider);
        slider.addChangeListener(e -> {
            if (!slider.getValueIsAdjusting()) {
                if (!light.isOn()) light.turnOn();
                light.setBrightness(slider.getValue());
                bLabel.setText("Brightness: " + slider.getValue() + "%");
            }
        });

        String[] colors = {"Warm White", "Cool White", "Red", "Green", "Blue", "Purple", "Yellow"};
        JComboBox<String> colorBox = styledCombo(colors);
        colorBox.setSelectedItem(light.getColor());
        colorBox.addActionListener(e -> {
            if (light.isOn()) light.setColor((String) colorBox.getSelectedItem());
        });

        p.add(bLabel);
        p.add(Box.createVerticalStrut(4));
        p.add(slider);
        p.add(Box.createVerticalStrut(12));
        p.add(makeLabel("Color:", 12, TEXT_SECONDARY, Font.PLAIN));
        p.add(Box.createVerticalStrut(4));
        p.add(colorBox);

        d.add(p);
        d.setVisible(true);
    }

    private void showThermoDialog(SmartThermostat t) {
        JDialog d = styledDialog("🌡 " + t.getDeviceName(), 340, 300);
        JPanel  p = dialogPanel();

        p.add(infoRow("Status",       t.getStatus().toString()));
        p.add(infoRow("Current Temp", String.format("%.1f°C", t.getCurrentTemp())));
        p.add(infoRow("Mode",         t.getMode().toString()));
        p.add(Box.createVerticalStrut(12));

        JLabel tLabel = makeLabel("Target: " + (int) t.getTargetTemp() + "°C", 12, TEXT_SECONDARY, Font.PLAIN);
        JSlider tSlider = new JSlider(10, 35, (int) t.getTargetTemp());
        styleSlider(tSlider);
        tSlider.addChangeListener(e -> {
            if (!tSlider.getValueIsAdjusting()) {
                t.setTargetTemperature(tSlider.getValue());
                tLabel.setText("Target: " + tSlider.getValue() + "°C");
            }
        });

        String[] modes = {"HEATING", "COOLING", "AUTO", "FAN_ONLY", "ECO"};
        JComboBox<String> modeBox = styledCombo(modes);
        modeBox.setSelectedItem(t.getMode().toString());
        modeBox.addActionListener(e -> {
            String sel = (String) modeBox.getSelectedItem();
            t.setMode(SmartThermostat.Mode.valueOf(sel));
        });

        p.add(tLabel);
        p.add(Box.createVerticalStrut(4));
        p.add(tSlider);
        p.add(Box.createVerticalStrut(12));
        p.add(makeLabel("Mode:", 12, TEXT_SECONDARY, Font.PLAIN));
        p.add(Box.createVerticalStrut(4));
        p.add(modeBox);

        d.add(p);
        d.setVisible(true);
    }

    private void showLockDialog(SmartLock lock) {
        JDialog d = styledDialog("🔐 " + lock.getDeviceName(), 340, 260);
        JPanel  p = dialogPanel();

        p.add(infoRow("State",    lock.isLocked() ? "LOCKED" : "UNLOCKED"));
        p.add(infoRow("Attempts", lock.getFailedAttempts() + " / 3"));
        p.add(Box.createVerticalStrut(16));

        JButton unlockBtn = roundButton("Unlock with PIN", ACCENT_GREEN);
        JButton lockBtn   = roundButton("Lock Now",        ACCENT_RED);
        JButton resetBtn  = roundButton("Reset Attempts",  ACCENT_BLUE);

        unlockBtn.addActionListener(e -> {
            String pin = JOptionPane.showInputDialog(d, "Enter PIN:", "Unlock", JOptionPane.PLAIN_MESSAGE);
            if (pin != null) { lock.unlock(pin); d.dispose(); }
        });
        lockBtn.addActionListener(e   -> { lock.lock(); d.dispose(); });
        resetBtn.addActionListener(e  -> { lock.resetFailedAttempts(); d.dispose(); });

        p.add(unlockBtn);
        p.add(Box.createVerticalStrut(8));
        p.add(lockBtn);
        p.add(Box.createVerticalStrut(8));
        p.add(resetBtn);

        d.add(p);
        d.setVisible(true);
    }

    private void showCameraDialog(SmartCamera cam) {
        JDialog d = styledDialog("📹 " + cam.getDeviceName(), 340, 260);
        JPanel  p = dialogPanel();

        p.add(infoRow("Status",    cam.getStatus().toString()));
        p.add(infoRow("Recording", cam.isRecording() ? "YES" : "NO"));
        p.add(infoRow("Alerts",    cam.getMotionAlerts() + " motion alerts"));
        p.add(Box.createVerticalStrut(16));

        JButton recBtn   = roundButton(cam.isRecording() ? "Stop Recording" : "Start Recording",
                cam.isRecording() ? ACCENT_RED : ACCENT_GREEN);
        JButton alertBtn = roundButton("Simulate Motion Alert", ACCENT_AMBER);
        JButton clrBtn   = roundButton("Clear Storage",         ACCENT_BLUE);

        recBtn.addActionListener(e -> {
            if (cam.isRecording()) cam.stopRecording(); else { cam.turnOn(); cam.startRecording(); }
            d.dispose();
        });
        alertBtn.addActionListener(e -> { cam.triggerMotionAlert(); d.dispose(); });
        clrBtn.addActionListener(e   -> { cam.clearStorage();        d.dispose(); });

        p.add(recBtn);
        p.add(Box.createVerticalStrut(8));
        p.add(alertBtn);
        p.add(Box.createVerticalStrut(8));
        p.add(clrBtn);

        d.add(p);
        d.setVisible(true);
    }

    private void showSpeakerDialog(SmartSpeaker sp) {
        JDialog d = styledDialog("🔊 " + sp.getDeviceName(), 360, 300);
        JPanel  p = dialogPanel();

        p.add(infoRow("Now Playing", sp.getCurrentTrack()));
        p.add(Box.createVerticalStrut(12));

        JLabel vLabel = makeLabel("Volume: " + sp.getVolume() + "%", 12, TEXT_SECONDARY, Font.PLAIN);
        JSlider vol = new JSlider(0, 100, sp.getVolume());
        styleSlider(vol);
        vol.addChangeListener(e -> {
            if (!vol.getValueIsAdjusting()) {
                sp.setVolume(vol.getValue());
                vLabel.setText("Volume: " + vol.getValue() + "%");
            }
        });

        JPanel playRow = new JPanel(new GridLayout(1, 3, 8, 0));
        playRow.setOpaque(false);
        JButton prev = roundButton("⏮ Prev", ACCENT_BLUE);
        JButton stop = roundButton("⏹ Stop", ACCENT_RED);
        JButton next = roundButton("Next ⏭", ACCENT_BLUE);

        prev.addActionListener(e -> sp.playPrevious());
        stop.addActionListener(e -> sp.stop());
        next.addActionListener(e -> sp.playNext());
        playRow.add(prev); playRow.add(stop); playRow.add(next);

        p.add(vLabel);
        p.add(Box.createVerticalStrut(4));
        p.add(vol);
        p.add(Box.createVerticalStrut(12));
        p.add(playRow);

        d.add(p);
        d.setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Dialog helpers
    // ══════════════════════════════════════════════════════════════════════════

    private JDialog styledDialog(String title, int w, int h) {
        JDialog d = new JDialog(this, title, true);
        d.setSize(w, h);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(BG_PANEL);
        d.setResizable(false);
        return d;
    }

    private JPanel dialogPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_PANEL);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));
        return p;
    }

    private JPanel infoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(4, 0, 4, 0));
        row.add(makeLabel(label + ":", 12, TEXT_SECONDARY, Font.PLAIN), BorderLayout.WEST);
        row.add(makeLabel(value,       12, TEXT_PRIMARY,   Font.BOLD),  BorderLayout.EAST);
        return row;
    }

    private void styleSlider(JSlider s) {
        s.setOpaque(false);
        s.setForeground(ACCENT_BLUE);
        s.setBackground(BG_PANEL);
        s.setPaintTicks(true);
        s.setMajorTickSpacing(25);
    }

    @SuppressWarnings("unchecked")
    private <T> JComboBox<T> styledCombo(T[] items) {
        JComboBox<T> box = new JComboBox<>(items);
        box.setBackground(BG_CARD);
        box.setForeground(TEXT_PRIMARY);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        return box;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Generic UI helpers
    // ══════════════════════════════════════════════════════════════════════════

    private static JLabel makeLabel(String text, int size, Color color, int style) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", style, size));
        l.setForeground(color);
        return l;
    }

    private static JButton roundButton(String text, Color fg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        b.setForeground(fg);
        b.setBackground(new Color(fg.getRed()/6, fg.getGreen()/6, fg.getBlue()/6, 200));
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(7, 14, 7, 14));
        return b;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Entry point
    // ══════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(SmartHomeGUI::new);
    }
}
