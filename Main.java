import java.util.Scanner;

public class Main {

    private static HomeManager home;
    private static Scanner sc = new Scanner(System.in);

    private static SmartLight      livingLight, kitchenLight, bedroomLight;
    private static SmartThermostat livingThermostat;
    private static SmartLock       frontDoor, backDoor;
    private static SmartCamera     frontCam, backCam;
    private static SmartSpeaker    livingSpeaker;

    public static void main(String[] args) {
        printBanner();
        home = new HomeManager("Green Valley Smart Home");
        setupDevices();

        System.out.println("\n  Home initialized with " + home.getDeviceCount() + " devices.");
        System.out.println("  Press ENTER to continue...");
        sc.nextLine();

        while (true) {
            showMainMenu();
            int choice = readInt("  Your choice: ");
            handleMainMenu(choice);
        }
    }

    private static void setupDevices() {
        System.out.println("\n  Initializing devices...");

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
        livingSpeaker.addToPlaylist("Chopin - Nocturne Op.9");

        home.addDevice(livingLight);
        home.addDevice(kitchenLight);
        home.addDevice(bedroomLight);
        home.addDevice(livingThermostat);
        home.addDevice(frontDoor);
        home.addDevice(backDoor);
        home.addDevice(frontCam);
        home.addDevice(backCam);
        home.addDevice(livingSpeaker);

        livingLight.turnOn();       livingLight.setBrightness(70);
        livingThermostat.turnOn();  livingThermostat.setTargetTemperature(22.0);
        frontCam.turnOn();          frontCam.startRecording();
        backCam.turnOn();
        livingSpeaker.turnOn();     livingSpeaker.play("Beethoven - Moonlight Sonata");

        livingLight.setSchedule("07:00", "23:00");
        livingThermostat.setSchedule("06:30", "22:30");
    }

    private static void showMainMenu() {
        System.out.println("\n" + "=".repeat(52));
        System.out.println("  SMART HOME MAIN MENU");
        System.out.println("=".repeat(52));
        System.out.println("  1.  Dashboard Overview");
        System.out.println("  2.  Lighting Controls");
        System.out.println("  3.  Climate Controls");
        System.out.println("  4.  Security - Locks");
        System.out.println("  5.  Security - Cameras");
        System.out.println("  6.  Entertainment");
        System.out.println("  7.  Energy Report");
        System.out.println("  8.  Schedules");
        System.out.println("  9.  Category Report");
        System.out.println("  10. Bulk Controls");
        System.out.println("  11. Activity Log");
        System.out.println("  0.  Exit");
        System.out.println("=".repeat(52));
    }

    private static void handleMainMenu(int c) {
        switch (c) {
            case 1:  home.showDashboard();       break;
            case 2:  lightingMenu();             break;
            case 3:  climateMenu();              break;
            case 4:  lockMenu();                 break;
            case 5:  cameraMenu();               break;
            case 6:  speakerMenu();              break;
            case 7:  home.showEnergyReport();    break;
            case 8:  home.showSchedules();       break;
            case 9:  home.showCategoryReport();  break;
            case 10: bulkMenu();                 break;
            case 11: home.showActivityLog();     break;
            case 0:  System.out.println("\n  Goodbye!"); System.exit(0); break;
            default: System.out.println("  Invalid choice.");
        }
    }

    private static void lightingMenu() {
        while (true) {
            System.out.println("\n  LIGHTING CONTROLS");
            System.out.println("  1. Living Room Light  [" + livingLight.getStatus() + "]");
            System.out.println("  2. Kitchen Light      [" + kitchenLight.getStatus() + "]");
            System.out.println("  3. Bedroom Light      [" + bedroomLight.getStatus() + "]");
            System.out.println("  0. Back");
            int ch = readInt("  Select: ");
            if (ch == 0) return;
            SmartLight light = null;
            if (ch == 1) light = livingLight;
            else if (ch == 2) light = kitchenLight;
            else if (ch == 3) light = bedroomLight;
            if (light != null) controlLight(light);
        }
    }

    private static void controlLight(SmartLight light) {
        light.displayDetails();
        System.out.println("  1.Turn ON  2.Turn OFF  3.Brightness  4.Color  5.Schedule  0.Back");
        int ch = readInt("  Action: ");
        switch (ch) {
            case 1: light.turnOn(); break;
            case 2: light.turnOff(); break;
            case 3: int b = readInt("  Brightness (0-100): "); light.setBrightness(b); break;
            case 4: String col = readStr("  Color name: "); light.setColor(col); break;
            case 5:
                String on  = readStr("  ON time (HH:MM): ");
                String off = readStr("  OFF time (HH:MM): ");
                light.setSchedule(on, off); break;
        }
    }

    private static void climateMenu() {
        while (true) {
            livingThermostat.displayDetails();
            System.out.println("  1.Turn ON  2.Turn OFF  3.Set Temp  4.Set Mode  5.Fan Speed  0.Back");
            int ch = readInt("  Action: ");
            switch (ch) {
                case 1: livingThermostat.turnOn(); break;
                case 2: livingThermostat.turnOff(); break;
                case 3:
                    double t = readDouble("  Target temp (10-35 C): ");
                    livingThermostat.setTargetTemperature(t); break;
                case 4:
                    System.out.println("  1.HEATING  2.COOLING  3.AUTO  4.FAN_ONLY  5.ECO");
                    int m = readInt("  Mode: ");
                    SmartThermostat.Mode mode = SmartThermostat.Mode.AUTO;
                    if (m == 1) mode = SmartThermostat.Mode.HEATING;
                    else if (m == 2) mode = SmartThermostat.Mode.COOLING;
                    else if (m == 4) mode = SmartThermostat.Mode.FAN_ONLY;
                    else if (m == 5) mode = SmartThermostat.Mode.ECO;
                    livingThermostat.setMode(mode); break;
                case 5:
                    int f = readInt("  Fan speed (1-5): ");
                    livingThermostat.setFanSpeed(f); break;
                case 0: return;
            }
        }
    }

    private static void lockMenu() {
        while (true) {
            System.out.println("\n  SECURITY - LOCKS");
            System.out.println("  1. Front Door  [" + (frontDoor.isLocked() ? "LOCKED" : "UNLOCKED") + "]");
            System.out.println("  2. Back Door   [" + (backDoor.isLocked()  ? "LOCKED" : "UNLOCKED") + "]");
            System.out.println("  0. Back");
            int ch = readInt("  Select: ");
            if (ch == 0) return;
            SmartLock lock = (ch == 1) ? frontDoor : (ch == 2) ? backDoor : null;
            if (lock != null) controlLock(lock);
        }
    }

    private static void controlLock(SmartLock lock) {
        lock.displayDetails();
        System.out.println("  1.Unlock  2.Lock  3.Change PIN  4.Reset Attempts  0.Back");
        int ch = readInt("  Action: ");
        switch (ch) {
            case 1: String pin = readStr("  Enter PIN: "); lock.unlock(pin); break;
            case 2: lock.lock(); break;
            case 3:
                String op = readStr("  Current PIN: ");
                String np = readStr("  New PIN: ");
                lock.changePin(op, np); break;
            case 4: lock.resetFailedAttempts(); break;
        }
    }

    private static void cameraMenu() {
        while (true) {
            System.out.println("\n  SECURITY - CAMERAS");
            System.out.println("  1. Front Camera  [" + frontCam.getStatus() + "]");
            System.out.println("  2. Back Camera   [" + backCam.getStatus() + "]");
            System.out.println("  0. Back");
            int ch = readInt("  Select: ");
            if (ch == 0) return;
            SmartCamera cam = (ch == 1) ? frontCam : (ch == 2) ? backCam : null;
            if (cam != null) controlCamera(cam);
        }
    }

    private static void controlCamera(SmartCamera cam) {
        cam.displayDetails();
        System.out.println("  1.Turn ON  2.Turn OFF  3.Start Rec  4.Stop Rec  5.Motion Alert  6.Log  7.Clear  0.Back");
        int ch = readInt("  Action: ");
        switch (ch) {
            case 1: cam.turnOn(); break;
            case 2: cam.turnOff(); break;
            case 3: cam.startRecording(); break;
            case 4: cam.stopRecording(); break;
            case 5: cam.triggerMotionAlert(); break;
            case 6: cam.showEventLog(); break;
            case 7: cam.clearStorage(); break;
        }
    }

    private static void speakerMenu() {
        while (true) {
            livingSpeaker.displayDetails();
            System.out.println("  1.ON  2.OFF  3.Play  4.Next  5.Prev  6.Stop  7.Volume  8.Mute  9.Playlist  10.EQ  0.Back");
            int ch = readInt("  Action: ");
            switch (ch) {
                case 1:  livingSpeaker.turnOn(); break;
                case 2:  livingSpeaker.turnOff(); break;
                case 3:  String t = readStr("  Track name: "); livingSpeaker.play(t); break;
                case 4:  livingSpeaker.playNext(); break;
                case 5:  livingSpeaker.playPrevious(); break;
                case 6:  livingSpeaker.stop(); break;
                case 7:  int v = readInt("  Volume (0-100): "); livingSpeaker.setVolume(v); break;
                case 8:  if (livingSpeaker.getVolume() > 0) livingSpeaker.mute(); else livingSpeaker.unmute(); break;
                case 9:  livingSpeaker.showPlaylist(); break;
                case 10:
                    System.out.println("  1.FLAT  2.BASS_BOOST  3.TREBLE_BOOST  4.VOCAL  5.ROCK  6.CLASSICAL");
                    int eq = readInt("  Preset: ");
                    SmartSpeaker.EqualizerPreset p = SmartSpeaker.EqualizerPreset.FLAT;
                    if (eq == 2) p = SmartSpeaker.EqualizerPreset.BASS_BOOST;
                    else if (eq == 3) p = SmartSpeaker.EqualizerPreset.TREBLE_BOOST;
                    else if (eq == 4) p = SmartSpeaker.EqualizerPreset.VOCAL;
                    else if (eq == 5) p = SmartSpeaker.EqualizerPreset.ROCK;
                    else if (eq == 6) p = SmartSpeaker.EqualizerPreset.CLASSICAL;
                    livingSpeaker.setEqualizer(p); break;
                case 0: return;
            }
        }
    }

    private static void bulkMenu() {
        System.out.println("\n  BULK CONTROLS");
        System.out.println("  1. Turn ALL ON");
        System.out.println("  2. Turn ALL OFF");
        System.out.println("  3. All LIGHTS ON");
        System.out.println("  4. All LIGHTS OFF");
        System.out.println("  5. All SECURITY ON");
        System.out.println("  6. All SECURITY OFF");
        System.out.println("  0. Back");
        int ch = readInt("  Action: ");
        switch (ch) {
            case 1: home.turnAllOn(); break;
            case 2: home.turnAllOff(); break;
            case 3: home.turnCategoryOn(DeviceCategory.LIGHTING); break;
            case 4: home.turnCategoryOff(DeviceCategory.LIGHTING); break;
            case 5: home.turnCategoryOn(DeviceCategory.SECURITY); break;
            case 6: home.turnCategoryOff(DeviceCategory.SECURITY); break;
        }
    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    private static double readDouble(String prompt) {
        System.out.print(prompt);
        try { return Double.parseDouble(sc.nextLine().trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private static String readStr(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static void printBanner() {
        System.out.println("============================================================");
        System.out.println("         SMART HOME MANAGEMENT SYSTEM");
        System.out.println("============================================================");
        System.out.println("   Built with Java OOP:");
        System.out.println("   * Abstraction   * Inheritance");
        System.out.println("   * Polymorphism  * Encapsulation");
        System.out.println("   * Interfaces    * Enums");
        System.out.println("============================================================");
    }
}
