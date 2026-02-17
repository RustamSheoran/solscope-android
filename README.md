# SolScope 🔭

**Advanced Solana Wallet Analyzer & Risk Intelligence Tool**

SolScope is a native Android application designed to provide transparency into the Solana ecosystem. It bypasses traditional heavy SDKs in favor of a raw, lightweight JSON-RPC implementation to communicate directly with the Solana Mainnet.

The app features a custom-built **Glassmorphic** design system, a rule-based risk engine, and a robust MVVM architecture optimized for performance and maintainability.

---

## 🏗️ Technical Architecture

SolScope follows the principles of **Clean Architecture** and **MVVM (Model-View-ViewModel)** to separate concerns and ensure testability.

### Layers

1.  **Presentation Layer (UI)**
    - **Jetpack Compose**: 100% Kotlin-based declarative UI.
    - **State Management**: Uses `StateFlow` and `collectAsStateWithLifecycle` to reactively update the UI based on `ResultState` and `WatchlistState`.
    - **Custom Design System**: Implements `GlassCard`, `GlassButton`, and neon-accented components to achieve a "Cyber/Glass" aesthetic involving blur effects, gradients, and transparency.

2.  **Domain Layer (Business Logic)**
    - **Risk Engine**: A pure Kotlin logic module that ingests raw blockchain data (transaction history, account age, balance) and applies heuristic rules to generate a safety score (0-100).
    - **Models**: Immutable data classes defining the core entities (`RiskScore`, `TokenAccount`, `Transaction`).

3.  **Data Layer (Infrastructure)**
    - **Direct RPC Client**: A custom-built networking layer using **OkHttp** and **Kotlin Serialization**. It constructs raw JSON-RPC 2.0 requests (`getAccountInfo`, `getSignaturesForAddress`, `getTokenAccountsByOwner`) and parses responses directly, avoiding the overhead of the massive web3.js/solana4j libraries.
    - **Repository Pattern**: Mediates data fetching, error handling (mapping HTTP/RPC errors to domain `ErrorType`), and caching strategies.
    - **Persistence**: Uses **DataStore** and **Room** (planned) for persisting the local Watchlist.

---

## 🚀 Key Features & Implementation Details

### ⚡ Raw JSON-RPC Implementation

Instead of relying on third-party APIs (which can be rate-limited or shutdown) or heavy SDKs, SolScope speaks the native language of Solana nodes.

- **Batching**: Capable of batching multiple RPC calls to reduce network round-trips.
- **Parsing**: Efficiently parses complex nested JSON structures for Token Accounts and Parsed Instructions.

### 🛡️ Algorithmic Risk Scoring

The core value proposition is the **Risk Score**, calculated via a weighted heuristic engine:

- **Account Age**: Older accounts (genesis or >1 year) receive safety bonuses.
- **Balance Analysis**: Dust wallets (<0.01 SOL) are flagged for potential spam/burner status.
- **Interaction History**: High transaction volume with reputable programs increases trust; interaction with known malicious contracts (future feature) flags danger.
- **Asset Diversity**: Wallets holding a diverse portfolio of reputable tokens score higher than those holding only a single unknown mint.

### 👁️ Watchlist System

- **Local Persistence**: Stores watched wallets securely on the device.
- **Real-time Updates**: Background refreshing of balances and asset values.
- **Asset Aggregation**: detailed breakdown of SOL vs. SPL Token holdings.

---

## 🎨 Design System: "Glass & Neon"

SolScope features a bespoke UI toolkit built on top of Material 3.

- **GlassCard**: A surface composable with low alpha background, blur backdrop (Android 12+), and a thin, high-contrast gradient border to simulate glass edges.
- **Neon Typography**: Uses custom fonts (orbitron/inter families) with glowing shadows for headers and key metrics.
- **Fluid Motion**: extensive usage of `AnimatedVisibility`, `Crossfade`, and `animateFloatAsState` for smooth state transitions (e.g., Risk Score circle filling up, lists cascading in).

---

## 📂 Project Structure

```
com.example.solscope
├── data
│   ├── rpc                 # Low-level JSON-RPC client & Request/Response models
│   ├── repository          # Repo implementations (WatchlistRepo, AnalyzeRepo)
│   └── mapper              # DTO to Domain model mappers
├── domain
│   ├── model               # RiskScore, WalletSnapshot, ErrorType (Pure Kotlin)
│   └── usecase             # (Optional) Specific business logic flows
├── presentation
│   ├── home                # Landing screen, Search/Scan input
│   ├── result              # Analysis Dashboard (Risk Gauge, Asset List, Txn Log)
│   ├── watchlist           # Saved Wallets Grid, Add/Edit Dialogs
│   ├── components          # Reusable Glass UI (GlassCard, AssetDetailSheet)
│   ├── theme               # Color (CyberColors), Type, Shapes
│   └── util                # Extension functions (Address truncation, formatting)
└── SolScopeApplication.kt  # Hilt/Koin (DI) setup point
```

---

## 🛠️ Stack & Dependencies

- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose (BOM 2024.02+)
- **Async**: Kotlin Coroutines & Flow
- **Networking**: OkHttp 4.12
- **Serialization**: Kotlinx Serialization (JSON)
- **Navigation**: Jetpack Compose Navigation
- **Lifecycle**: ViewModel, Lifecycle-Runtime-Compose

---

## 🔧 Build Instructions

1.  **Prerequisites**:
    - Android Studio Iguana or later.
    - JDK 17.
    - Android SDK API 26 (Min) to 34 (Target).

2.  **Clone & Sync**:

    ```bash
    git clone https://github.com/RustamSheoran/solscope-android.git
    cd solscope-android
    # Open in Android Studio and let Gradle sync
    ```

3.  **Build**:

    ```bash
    ./gradlew assembleDebug
    ```

4.  **Install**:
    ```bash
    adb install -r app/build/outputs/apk/debug/app-debug.apk
    ```

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.
