# 🏠 Smart Home Management System — Java

> A fully object-oriented Java application to manage and control smart home devices, built using core OOP principles: **Abstraction**, **Inheritance**, **Polymorphism**, **Encapsulation**, and **Interfaces**. Includes both a CLI and a Java Swing GUI dashboard.

---

## 📸 Features at a Glance

- 9 pre-configured smart devices across 4 categories
- Interactive **CLI** menu with full device control
- **Java Swing GUI** dashboard with live device cards and controls
- Energy monitoring with real-time watt consumption and cost estimation
- Schedule support for lights and thermostat
- PIN-protected smart locks with timestamped access logs
- Camera recording with motion alert history
- Speaker with playlist management and equalizer presets
- Bulk ON/OFF controls by category
- Activity log across the entire home

---

## 🗂️ Project Structure

```
SmartHome/
├── DeviceStatus.java          # Enum: ON, OFF, LOCKED, RECORDING, IDLE...
├── DeviceCategory.java        # Enum: LIGHTING, CLIMATE, SECURITY, ENTERTAINMENT
├── Controllable.java          # Interface: turnOn / turnOff / reset
├── EnergyMonitor.java         # Interface: getPowerConsumption / getEnergyUsedToday
├── Schedulable.java           # Interface: setSchedule / getSchedule
├── SmartDevice.java           # Abstract base class for all devices
├── SmartLight.java            # Dimmable RGB light with schedule support
├── SmartThermostat.java       # HVAC with HEATING/COOLING/AUTO/ECO modes
├── SmartLock.java             # PIN-protected lock with access log
├── SmartCamera.java           # Recording camera with motion detection
├── SmartSpeaker.java          # Playlist speaker with EQ presets
├── HomeManager.java           # Central controller — manages all devices
├── Main.java                  # CLI entry point
└── SmartHomeGUI.java          # Java Swing GUI dashboard
```

---

## 🎯 OOP Concepts Demonstrated

| Concept | Implementation |
|---|---|
| **Abstraction** | `SmartDevice` is abstract — forces every subclass to implement `getDeviceType()`, `displayDetails()`, and `getSpecificReport()` |
| **Inheritance** | `SmartLight`, `SmartThermostat`, `SmartCamera`, `SmartLock`, `SmartSpeaker` all extend `SmartDevice` and inherit common state and behaviour |
| **Polymorphism** | `HomeManager` stores all devices as `SmartDevice` references. Calls like `turnAllOn()` and `getStatusReport()` dispatch to each device's own override |
| **Encapsulation** | All fields are `private`; state is only accessed and modified through controlled getters and setters |
| **Interfaces** | `Controllable`, `EnergyMonitor`, and `Schedulable` — devices implement only the contracts they need (Interface Segregation) |
| **Enums** | `DeviceStatus`, `DeviceCategory`, `Mode`, `Resolution`, `EqualizerPreset` — type-safe named constants throughout |

---

## 🔌 Devices

| ID | Device | Category | Key Features |
|---|---|---|---|
| LT-001 | Living Room Light | Lighting | Dimmable 0–100%, RGB color, schedule |
| LT-002 | Kitchen Light | Lighting | Dimmable, cool white |
| LT-003 | Bedroom Light | Lighting | Warm white, schedule-ready |
| TH-001 | Main Thermostat | Climate | HEATING/COOLING/AUTO/ECO modes, fan speed |
| LK-001 | Front Door Lock | Security | PIN unlock, 3-attempt lockout, access log |
| LK-002 | Back Door Lock | Security | Same as above, independent PIN |
| CAM-001 | Front Camera | Security | 1080p, recording, motion alerts |
| CAM-002 | Back Camera | Security | 720p, storage tracking, event log |
| SP-001 | Living Room Speaker | Entertainment | Playlist, EQ presets, volume control |

---

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Download from: https://adoptium.net

### Verify Installation
```bash
java -version
javac -version
```

### Clone the Repository
```bash
git clone https://github.com/your-username/smart-home-java.git
cd smart-home-java
```

### Compile
```bash
mkdir out
javac -d out *.java
```

### Run CLI
```bash
java -cp out Main
```

### Run GUI Dashboard
```bash
java -cp out SmartHomeGUI
```

> **Windows PowerShell users:** Use `javac -d out *.java` (PowerShell supports wildcards directly in the working directory).

---

## 🖥️ GUI Dashboard

The Swing dashboard (`SmartHomeGUI.java`) provides a visual control panel:

- **Device cards** — each device shown with icon, live status dot, location, and real-time state info
- **Toggle button** — turn any device ON/OFF (or Lock/Unlock) directly from the card
- **Controls button** — opens a popup with device-specific sliders, dropdowns, and actions
- **Stats bar** — live active count and total watt consumption, auto-refreshes every 2 seconds
- **Bulk controls** — "All ON" / "All OFF" buttons in the header
- **Live clock** — displayed in the header

---

## 📋 CLI Menu Overview

```
SMART HOME MAIN MENU
1.  Dashboard Overview
2.  Lighting Controls
3.  Climate Controls
4.  Security - Locks
5.  Security - Cameras
6.  Entertainment
7.  Energy Report
8.  Schedules
9.  Category Report
10. Bulk Controls
11. Activity Log
0.  Exit
```

---

## ⚡ Energy Monitoring

Every device implementing `EnergyMonitor` reports:
- Current power draw (Watts)
- Energy used today (kWh)
- Estimated monthly cost at $0.12/kWh

Example energy report output:
```
Living Room Light       70.0 W  |  0.000 kWh/day
Main Thermostat        320.0 W  |  0.00 kWh/day
Front Camera            12.0 W  |  0.288 kWh/day
Living Room Speaker     23.0 W  |  0.000 kWh/day
─────────────────────────────────────────────
TOTAL                  425.0 W
Estimated monthly cost: $3.67
```

---

## 🏗️ Class Hierarchy

```
SmartDevice  (abstract)
│   implements Controllable
├── SmartLight        implements EnergyMonitor, Schedulable
├── SmartThermostat   implements EnergyMonitor, Schedulable
├── SmartLock
├── SmartCamera       implements EnergyMonitor
└── SmartSpeaker      implements EnergyMonitor

HomeManager
└── manages Map<String, SmartDevice>
    └── polymorphic dispatch to all device types
```

---

## 🛡️ Smart Lock Security

- PIN required to unlock (minimum 4 digits)
- After **3 failed attempts**, the device is locked out
- Every event is timestamped and stored in an access log
- Auto-lock configurable (default: 5 minutes)
- PIN can be changed with old PIN verification

---

## 📷 Smart Camera Features

- Resolutions: `HD_720P`, `FULL_HD_1080P`, `UHD_4K`
- Start/stop recording with automatic storage tracking
- Motion detection alerts with count history
- Timestamped event log
- Night vision toggle

---

## 🎵 Smart Speaker Features

- Playlist management (add, clear, next, previous)
- Equalizer presets: `FLAT`, `BASS_BOOST`, `TREBLE_BOOST`, `VOCAL`, `ROCK`, `CLASSICAL`
- Volume control (0–100%)
- Mute / unmute
- Energy tracking based on volume and playback state

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/new-device`
3. Commit your changes: `git commit -m "Add SmartFridge device"`
4. Push to the branch: `git push origin feature/new-device`
5. Open a Pull Request

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👩‍💻 Author

Built as a Java OOP learning project demonstrating real-world application of object-oriented design principles.

---

> **Tip:** To add a new device, extend `SmartDevice`, implement whichever interfaces apply (`EnergyMonitor`, `Schedulable`), register it in `HomeManager`, and add a card in `SmartHomeGUI`. The polymorphic architecture means no other files need to change.
