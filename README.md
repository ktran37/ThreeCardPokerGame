# Three Card Poker — Client-Server Application

A networked multiplayer Three Card Poker game built with Java, JavaFX, and Maven for **UIC CS 342 — Software Design (Project 3)**. A JavaFX server manages game logic and supports up to 8 simultaneous clients, each playing Three Card Poker against the dealer.

---

## Table of Contents

- [Game Rules](#game-rules)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [How to Build and Run](#how-to-build-and-run)
- [Project Structure](#project-structure)
- [Key Classes](#key-classes)
- [Features](#features)
- [Testing](#testing)

---

## Game Rules

1. **Bet** — Place an **Ante** bet (\$5–\$25, required) and an optional **Pair Plus** side bet (\$5–\$25 or \$0).
2. **Deal** — The server deals 3 cards to the player and 3 to the dealer. Player cards are shown; dealer cards remain face-down.
3. **Play or Fold:**
   - **Play** — Match the ante with a play bet (equal to the ante). Hands are then compared.
   - **Fold** — Forfeit ante and pair plus bets.
4. **Resolution:**
   - The dealer **qualifies** with Queen-high or better. If not, the play bet is returned and the ante pushes.
   - If the dealer qualifies, the higher hand wins. Ties return bets.
   - **Pair Plus** pays independently based on hand quality:

| Hand            | Payout |
|-----------------|--------|
| Straight Flush  | 40:1   |
| Three of a Kind | 30:1   |
| Straight        | 6:1    |
| Flush           | 3:1    |
| Pair            | 1:1    |

**Hand Rankings** (low → high): High Card, Pair, Flush, Straight, Three of a Kind, Straight Flush.

---

## Architecture

```
┌──────────────────┐        TCP/IP Sockets         ┌─────────────────────┐
│   Client (JavaFX)│  ←── ObjectInputStream ──────  │   Server (JavaFX)   │
│                  │  ──── ObjectOutputStream ────→  │                     │
│  WelcomeScreen   │        (PokerInfo objects)      │  ServerIntro (port) │
│  GamePlayScreen  │                                 │  ServerMain (log)   │
│  WinLoseScreen   │                                 │                     │
└──────────────────┘                                 └─────────────────────┘
       (up to 8 clients)                              (1 thread per client)
```

- **Protocol:** Java Object Serialization over TCP sockets. The `PokerInfo` object (`Serializable`) carries game actions, bets, card hands, results, and messages.
- **Threading:** One dedicated `ClientHandler` thread per connected client. Client-side network calls run on background threads with `Platform.runLater()` for UI updates.
- **Max clients:** 8 simultaneous connections.

---

## Technologies

| Technology | Details |
|---|---|
| Java | 11 |
| JavaFX | 19.0.2.1 (`javafx-controls`, `javafx-fxml`) |
| Maven | Build tool |
| JUnit 5 | `junit-jupiter 5.9.1` for unit testing |
| Networking | Java Sockets (`ServerSocket` / `Socket`) with `ObjectOutputStream` / `ObjectInputStream` |
| FXML + CSS | UI layouts and custom styling with theme support |

---

## How to Build and Run

### Prerequisites

- **Java 11+**
- **Maven 3.6+**

### Start the Server

```bash
cd server
mvn compile exec:java -Dexec.mainClass="ServerApp"
```

Enter a port on the intro screen (default **5555**) and click Start.

### Start a Client

```bash
cd client
mvn compile exec:java -Dexec.mainClass="ClientApp"
```

Enter the server IP (default `localhost`) and port (default `5555`), then click Connect.

### Run Tests

```bash
cd server
mvn test
```

---

## Project Structure

```
project3/
├── server/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   ├── ServerApp.java          # JavaFX entry point
│       │   │   ├── Server.java             # Socket listener, client manager
│       │   │   ├── ClientHandler.java       # Per-client game thread
│       │   │   ├── ThreeCardLogic.java      # Static game logic
│       │   │   ├── Card.java               # Card model
│       │   │   ├── Deck.java               # 52-card deck
│       │   │   ├── PokerInfo.java           # Serializable DTO
│       │   │   ├── ServerController.java    # Main server UI (game log)
│       │   │   └── ServerIntroController.java # Port entry screen
│       │   └── resources/                   # FXML, CSS, images
│       └── test/java/                       # Unit tests
│
├── client/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   ├── ClientApp.java           # JavaFX entry point
│       │   │   ├── Client.java              # Network wrapper
│       │   │   ├── GamePlayController.java  # Main game screen
│       │   │   ├── WelcomeController.java   # Connection screen
│       │   │   ├── WinLoseController.java   # Result dialog
│       │   │   ├── PokerInfo.java           # Serializable DTO
│       │   │   └── Card.java               # Card model
│       │   └── resources/                   # FXML, CSS, card images, themes
│       └── test/java/
│
└── README.md
```

---

## Key Classes

### Server

| Class | Role |
|---|---|
| `ServerApp` | JavaFX Application entry point. Launches intro screen. |
| `Server` | Listens on a `ServerSocket`, accepts connections, spawns `ClientHandler` threads. |
| `ClientHandler` | Per-client `Runnable`. Deals cards, evaluates bets via `ThreeCardLogic`, sends results. Handles `BET`, `PLAY`, `FOLD`, `NEW_GAME`, `NEW_HAND` actions. |
| `ThreeCardLogic` | Pure static game logic: hand evaluation, Pair Plus payouts, hand comparison, total winnings. |
| `Card` | Serializable card with suit (C/D/H/S) and value (2–14, Ace = 14). |
| `Deck` | 52-card deck with shuffle, deal, and auto-rebuild when exhausted. |
| `PokerInfo` | Serializable DTO exchanged between client and server. |

### Client

| Class | Role |
|---|---|
| `ClientApp` | JavaFX Application entry point. Loads the welcome screen. |
| `Client` | Network wrapper: connects via Socket, sends/receives `PokerInfo`. |
| `WelcomeController` | IP/port entry with retry logic (up to 3 attempts with backoff). |
| `GamePlayController` | Main game screen: bet validation, card display, play/fold, theme switching. |
| `WinLoseController` | Modal result dialog showing win/loss amount. |

---

## Features

- **Full GUI** with 3 client screens (Welcome → Gameplay → Win/Lose) and 2 server screens (Intro → Main Log)
- **52 card images** — Individual PNG card faces with a card-back for hidden dealer cards
- **4 swappable themes** via CSS class toggling
- **Connection retry** — Client retries up to 3 times with exponential backoff
- **Server game log** — Timestamped ListView showing all client connections, bets, and results
- **Multi-client support** — Up to 8 concurrent players, each with independent game state
- **Fresh Start** resets cumulative winnings; **New Hand** keeps the running total

---

## Testing

Tests are located in `server/src/test/java/`. **~59 unit tests** across 4 test classes:

| Test Class | Tests | Coverage |
|---|---|---|
| `CardTest` | 10 | Card construction, suit/value getters, `toString()` |
| `DeckTest` | 9 | Deck size, dealing, uniqueness, auto-rebuild, shuffle |
| `PokerInfoTest` | 10 | Default state, getters/setters for bets, hands, actions |
| `ThreeCardLogicTest` | ~30 | All hand types, Pair Plus payouts, hand comparison, dealer qualification, edge cases |
