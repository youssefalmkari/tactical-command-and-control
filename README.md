# Tactical Command & Control (C2) Mobile Platform

A mission-critical Android application for real-time command and control of autonomous drone operations. Built with a multi-module Clean Architecture approach, the platform provides operators with live telemetry monitoring, direct drone command authority, and mission planning capabilities — all through a tactical dark-themed interface designed for ruggedized field tablets.

---

## Table of Contents

- [Overview](#overview)
- [Screenshots & Feature Walkthrough](#screenshots--feature-walkthrough)
- [Architecture](#architecture)
- [Module Structure](#module-structure)
- [Tech Stack](#tech-stack)
- [Core Features](#core-features)
- [Security](#security)
- [Communication Protocol](#communication-protocol)
- [Build & Run](#build--run)
- [Testing](#testing)

---

## Overview

The Tactical C2 platform serves as the ground-station interface for managing a fleet of autonomous drones. Operators can monitor live telemetry feeds, issue flight commands with MAVLink protocol encoding, plan multi-waypoint missions, and maintain situational awareness across all connected assets — even in degraded network conditions thanks to the offline-first data architecture.

**Key design goals:**

- **Reliability first** — Encrypted local database, MQTT QoS-tiered messaging, and offline command queuing ensure the system functions in contested or disconnected environments.
- **Operator safety** — Biometric authentication, role-based access control, and a dedicated emergency stop command protect against unauthorized or accidental actions.
- **Real-time awareness** — 1 Hz telemetry updates, color-coded status indicators, and a split-panel dashboard keep operators informed at a glance.

---

## Screenshots & Feature Walkthrough

### Live Operations Dashboard

The primary screen presents a split-panel command center:

- **Left panel (40%)** — Drone fleet roster with connection status, battery level, and current flight mode. Each card is tappable to navigate directly into the control screen for that drone.
- **Right panel (60%)** — Aggregated telemetry feed showing position (lat/lon/alt), attitude (roll/pitch/yaw), battery state, and data freshness timestamps across all connected drones.

### Drone Control Screen

Individual drone command interface with:

- **Drone selector panel** — Switch between drones without returning to Live Ops. The drone clicked in Live Ops is automatically pre-selected.
- **Telemetry card** — Real-time position, attitude, battery voltage/current/temperature, and flight mode for the selected drone.
- **Command grid** — ARM, DISARM, TAKEOFF, LAND, RETURN TO LAUNCH, and EMERGENCY STOP buttons with live command status feedback (Sending, Acknowledged, Rejected, Timeout, Queued).

### Mission Planning

Mission management screen for creating and organizing multi-waypoint flight plans:

- Create missions with name and description
- View mission cards showing status, waypoint count, and assigned drones
- Mission status lifecycle: Draft → Planned → Uploaded → Executing → Paused → Completed / Aborted
- Delete missions with confirmation

---

## Architecture

The application follows **Clean Architecture** with strict dependency inversion and **MVVM** for the presentation layer.

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│         Feature Modules (Compose + ViewModels)          │
├─────────────────────────────────────────────────────────┤
│                      Domain Layer                       │
│       Models · Repository Interfaces · Use Cases        │
│                    (Pure Kotlin / JVM)                   │
├─────────────────────────────────────────────────────────┤
│                       Data Layer                        │
│    Repository Impls · Mappers · Background Workers      │
├──────────────────────┬──────────────────────────────────┤
│   Database (Room +   │   Network (MQTT + MAVLink)       │
│     SQLCipher)       │                                  │
└──────────────────────┴──────────────────────────────────┘
```

**Dependency rule:** Dependencies point inward only. Feature modules depend on the domain layer, never on each other. The domain layer has zero Android dependencies — it is a pure Kotlin/JVM module.

**Data flow:**

```
User Action → Composable → ViewModel → UseCase → Repository → DataSource (DB / MQTT)
                                                                    │
UI Recomposition ← StateFlow ← ViewModel ← Flow ← Repository ◄────┘
```

---

## Module Structure

```
tactical-command-and-control/
├── app/                           Main application, navigation, DI wiring, demo simulator
├── build-logic/
│   └── convention/                Gradle convention plugins
├── core/
│   ├── domain/                    Pure Kotlin: models, repository interfaces, use cases
│   ├── data/                      Repository implementations, mappers, workers
│   ├── database/                  Room DB, DAOs, entities (SQLCipher encrypted)
│   ├── network/                   MQTT client, MAVLink encoder/parser, TLS config
│   ├── common/                    Utilities, extensions, constants, security helpers
│   └── ui/                        Shared Compose components, tactical theme, design system
├── feature/
│   ├── mission-planning/          Mission CRUD screens + ViewModel
│   ├── live-ops/                  Real-time fleet monitoring dashboard + ViewModel
│   └── drone-control/             Individual drone command interface + ViewModel
└── gradle/
    └── libs.versions.toml         Version catalog
```

**10 modules total** — each with a single responsibility, wired together through 6 custom Gradle convention plugins that eliminate per-module boilerplate.

| Convention Plugin | Purpose |
|---|---|
| `c2.android.application` | App module setup (compileSdk 36, minSdk 29, Java 11) |
| `c2.android.library` | Library module setup |
| `c2.android.compose` | Compose BOM, Material 3, icons, foundation |
| `c2.android.hilt` | Hilt + KSP annotation processing |
| `c2.android.feature` | Composite: library + compose + hilt + core deps |
| `c2.jvm.library` | Pure Kotlin/JVM modules (domain layer) |

---

## Tech Stack

| Category | Technology |
|---|---|
| **Language** | Kotlin 2.2 |
| **UI** | Jetpack Compose (BOM 2026.01) + Material 3 |
| **Architecture** | MVVM + Clean Architecture + Multi-Module |
| **DI** | Hilt 2.59 + KSP |
| **Navigation** | Compose Navigation 2.9 with type-safe arguments |
| **Database** | Room 2.8 + SQLCipher 4.6 (AES-256 encryption at rest) |
| **Networking** | HiveMQ MQTT 5 Client (async, TLS/mTLS) |
| **Protocol** | MAVLink 2 via dronefleet/mavlink (COMMAND_LONG, telemetry) |
| **Background Work** | WorkManager (mission sync, telemetry cleanup) |
| **Async** | Kotlin Coroutines 1.10 + Flow (StateFlow, callbackFlow) |
| **Security** | Biometric auth, RBAC, MAVLink HMAC-SHA256 signing |
| **Build** | Gradle 9.1, AGP 9.0, convention plugins, version catalog |
| **Testing** | JUnit 4, Turbine (Flow), Coroutines-test, Espresso |

---

## Core Features

### Real-Time Telemetry

- 1 Hz position, attitude, and battery updates per drone
- Color-coded data freshness indicators (fresh vs. stale)
- Aggregated fleet-wide telemetry view in Live Ops
- Per-drone detailed telemetry in Drone Control

### Flight Commands

Commands are encoded as MAVLink 2 `COMMAND_LONG` messages and transmitted over MQTT:

| Command | MAVLink | Description |
|---|---|---|
| ARM | `MAV_CMD_COMPONENT_ARM_DISARM(1)` | Arm motors for flight |
| DISARM | `MAV_CMD_COMPONENT_ARM_DISARM(0)` | Disarm motors |
| TAKEOFF | `MAV_CMD_NAV_TAKEOFF` | Vertical takeoff to specified altitude |
| LAND | `MAV_CMD_NAV_LAND` | Land at current position |
| RTL | `MAV_CMD_NAV_RETURN_TO_LAUNCH` | Return to launch point |
| E-STOP | `MAV_CMD_COMPONENT_ARM_DISARM(0, 21196)` | Force disarm (emergency) |
| GO TO | `MAV_CMD_DO_REPOSITION` | Navigate to coordinates |
| START MISSION | `MAV_CMD_MISSION_START` | Begin uploaded mission |

Each command produces a `CommandResult`: Acknowledged, Rejected (with reason), Timeout, or Queued (offline fallback).

### Mission Planning

- Create, edit, and delete multi-waypoint missions
- Waypoint actions: Takeoff, Navigate, Loiter, Land, and more
- Assign multiple drones to a single mission
- Mission validation: requires waypoints and assigned drones before execution
- Background sync to remote mission server via WorkManager

### Offline-First Architecture

All data flows through the local Room database first:

1. Telemetry arrives via MQTT → persisted to Room → emitted as Flow to UI
2. Commands submitted by operator → sent via MQTT (QoS 2) → ACK awaited → fallback to local application if MQTT is unavailable
3. Missions synced in background via `MissionSyncWorker` with retry logic (up to 5 attempts)
4. Stale telemetry automatically cleaned up after 24 hours by `TelemetryCleanupWorker`

### Demo Simulator (Debug Builds)

A built-in simulator seeds the database with 4 drones and 3 missions for development and demonstration:

| Drone | State | Flight Pattern |
|---|---|---|
| Alpha-1 | Flying | Circular |
| Bravo-2 | Armed | Stationary |
| Charlie-3 | Idle | Stationary |
| Delta-4 | Returning | Linear return |

The simulator runs a 1 Hz loop updating positions, attitudes, and battery levels, and responds to commands issued through the UI (e.g., arming a drone updates its status).

---

## Security

### Authentication

- **Biometric gate** — Face or fingerprint authentication is required before accessing the application. The app presents an authentication screen on launch and does not render the main interface until the operator is verified.

### Data Protection

- **SQLCipher encryption** — The Room database is encrypted at rest with AES-256 via SQLCipher. The passphrase is managed by `EncryptedDatabaseFactory`.
- **TLS / mTLS** — The MQTT client supports TLS 1.2/1.3 with optional mutual TLS (client certificates) for authenticated broker connections.
- **MAVLink signing** — Commands can be signed with HMAC-SHA256 using a 32-byte key derived via PBKDF2, with monotonically increasing timestamps to prevent replay attacks.

### Role-Based Access Control

A four-tier permission model restricts operator capabilities:

| Role | Permissions |
|---|---|
| **Observer** | View telemetry, view missions |
| **Pilot** | + Send flight commands, emergency stop |
| **Mission Commander** | + Create/edit/delete missions, assign drones |
| **Admin** | + Manage operators, configure system |

---

## Communication Protocol

### MQTT Topic Structure

```
c2/drones/{droneId}/telemetry       QoS 0 (at most once)
c2/drones/{droneId}/commands        QoS 2 (exactly once)
c2/drones/{droneId}/command_ack     QoS 1 (at least once)
c2/missions/{missionId}/plan        QoS 2 (exactly once)
c2/missions/{missionId}/status      QoS 1 (at least once)
```

QoS levels are chosen deliberately: telemetry tolerates loss (fresh data replaces stale), commands require exactly-once delivery, and acknowledgments use at-least-once for reliability without the overhead of QoS 2.

### MAVLink 2 Integration

The network layer encodes and decodes MAVLink 2 binary frames using the [dronefleet/mavlink](https://github.com/dronefleet/mavlink) library. Key message types:

- **Outbound**: `COMMAND_LONG` (ID 76) for all flight commands
- **Inbound**: `HEARTBEAT` (ID 0), `GLOBAL_POSITION_INT` (ID 33), `ATTITUDE` (ID 30), `BATTERY_STATUS` (ID 147)

The GCS identifies itself as system ID 255, component ID 190 — standard MAVLink conventions for ground control stations.

---

## Build & Run

### Prerequisites

- Android Studio Narwhal (2025.1+) or later
- JDK 11+
- Android SDK with API 36

### Build

```bash
./gradlew assembleDebug
```

### Install on device/emulator

```bash
./gradlew installDebug
```

The debug build includes the demo simulator — 4 simulated drones and 3 sample missions will be available immediately.

### Release build

```bash
./gradlew assembleRelease
```

Release builds enable R8 minification and resource shrinking via ProGuard rules.

---

## Testing

```bash
# Unit tests (domain models, mappers, MAVLink signing, utilities)
./gradlew test

# Instrumented tests (Room DAOs, database operations)
./gradlew connectedAndroidTest
```

### Test coverage

| Area | Tests |
|---|---|
| Domain models | Operator role hierarchy, permission inheritance |
| Data mappers | Drone, Mission, Telemetry entity ↔ domain mapping |
| Utilities | Coordinate calculations, date/time formatting |
| Security | MAVLink signer key derivation, HMAC verification, timestamp monotonicity |
| Database | Drone DAO CRUD, Telemetry DAO insertion and cleanup |

---

## Project Configuration

| Property | Value |
|---|---|
| Min SDK | 29 (Android 10) |
| Target / Compile SDK | 36 |
| Kotlin | 2.2.0 |
| Gradle | 9.1.0 |
| AGP | 9.0.0 |
| JVM Target | 11 |
| Orientation | Landscape (locked) |
