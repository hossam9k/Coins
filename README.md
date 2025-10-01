# 📱 Crypto Portfolio Tracker - KMP POC

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-blue.svg)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.9.0-brightgreen)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS%20%7C%20Desktop-lightgrey)](https://kotlinlang.org/docs/multiplatform.html)

A production-ready **Kotlin Multiplatform (KMP)** proof-of-concept demonstrating modern mobile development practices with **Clean Architecture**, **MVI pattern**, and **Compose Multiplatform**. Track your cryptocurrency portfolio across Android, iOS, and Desktop platforms with a single shared codebase.

## ✨ Features

### 🎯 Core Functionality
- 📊 **Real-time Crypto Tracking** - Live cryptocurrency prices and market data
- 💼 **Portfolio Management** - Buy/sell cryptocurrencies with balance tracking
- 📈 **Performance Analytics** - Track your gains/losses with visual charts
- 🔄 **Offline Support** - Local database with Room for offline access
- 🎨 **Material Design 3** - Modern UI with dynamic theming

### 🛠️ Technical Features
- 🔐 **Secure API Key Management** - Build-time injection with environment variants
- 🏗️ **Clean Architecture** - Separation of concerns with domain/data/presentation layers
- 🎭 **MVI Pattern** - Unidirectional data flow for predictable state management
- 🔄 **Type-safe Navigation** - Compose Navigation 2.8+ with serialization
- 💉 **Dependency Injection** - Koin for KMP
- 🧪 **Testable** - Unit tests with Turbine and AssertK

## 🏗️ Architecture

### 📐 Clean Architecture Layers

```
┌─────────────────────────────────────────────┐
│         Presentation Layer (UI)              │
│  • Compose Multiplatform UI                  │
│  • MVI ViewModels                            │
│  • Navigation & State Management             │
├─────────────────────────────────────────────┤
│          Domain Layer (Business)             │
│  • Use Cases / Interactors                   │
│  • Domain Models                             │
│  • Repository Interfaces                     │
├─────────────────────────────────────────────┤
│           Data Layer (Sources)               │
│  • Repository Implementations                │
│  • Remote Data Sources (Ktor)               │
│  • Local Data Sources (Room)                │
│  • DTOs & Mappers                            │
└─────────────────────────────────────────────┘
```

### 🎭 MVI Pattern

```kotlin
┌──────────────┐
│   UI State   │ ◄─────┐
└──────────────┘       │
       │               │
       │ renders       │ updates
       ▼               │
┌──────────────┐       │
│     View     │       │
└──────────────┘       │
       │               │
       │ sends         │
       ▼               │
┌──────────────┐       │
│   Intent     │       │
└──────────────┘       │
       │               │
       │ processes     │
       ▼               │
┌──────────────┐       │
│  ViewModel   │───────┘
└──────────────┘
```

## 🚀 Tech Stack

### Core
- **Kotlin Multiplatform** - Share code across platforms
- **Compose Multiplatform** - Declarative UI framework
- **Kotlin Coroutines** - Asynchronous programming

### Networking & Data
- **Ktor Client** - HTTP networking
- **Kotlinx Serialization** - JSON serialization
- **Room Database** - Local persistence with KMP support

### Architecture & DI
- **Koin** - Dependency injection for KMP
- **Lifecycle ViewModel** - State management
- **Navigation Compose** - Type-safe navigation

### UI & Design
- **Material Design 3** - Modern design system
- **Coil 3** - Image loading for Compose
- **Custom Design Tokens** - Consistent theming

### Build & Security
- **Gradle Build Variants** - Dev/Staging/Prod environments
- **ProGuard/R8** - Code obfuscation & shrinking
- **Detekt** - Static code analysis

## 📦 Project Structure

```
KMP_POC/
├── composeApp/                          # Main application module
│   ├── src/
│   │   ├── commonMain/                  # Shared code (all platforms)
│   │   │   └── kotlin/org/poc/app/
│   │   │       ├── core/                # Core utilities & config
│   │   │       │   ├── config/          # App configuration & BuildConfig
│   │   │       │   ├── data/            # Core data (database, network)
│   │   │       │   ├── domain/          # Core domain models
│   │   │       │   └── presentation/    # MVI base classes
│   │   │       ├── feature/             # Feature modules
│   │   │       │   ├── coins/           # Crypto list feature
│   │   │       │   ├── portfolio/       # Portfolio feature
│   │   │       │   └── trade/           # Buy/Sell feature
│   │   │       ├── navigation/          # App navigation
│   │   │       └── ui/                  # Design system
│   │   │           ├── components/      # Reusable UI components
│   │   │           ├── foundation/      # Colors, typography, spacing
│   │   │           └── theme/           # Theme configuration
│   │   ├── androidMain/                 # Android-specific code
│   │   ├── iosMain/                     # iOS-specific code
│   │   └── desktopMain/                 # Desktop-specific code
│   ├── build.gradle.kts                 # Build configuration
│   └── proguard-rules.pro              # ProGuard rules
├── config/
│   └── detekt/detekt.yml               # Code quality config
├── local.properties.example             # Template for API keys
├── API_KEYS_SETUP.md                   # API key security guide
├── BUILD_VARIANTS.md                   # Build variants documentation
└── README.md                           # This file
```

## 🛠️ Setup & Installation

### Prerequisites
- **JDK 17+** - Java Development Kit
- **Android Studio Ladybug+** - Latest stable version
- **Xcode 15+** (for iOS) - macOS only
- **Gradle 8.14+** - Build tool

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/yourusername/KMP_POC.git
cd KMP_POC
```

### 2️⃣ Configure API Keys

```bash
# Copy the example file
cp local.properties.example local.properties

# Edit and add your API keys
# Get free API keys from: https://coincap.io/
nano local.properties
```

**local.properties:**
```properties
# Development
DEV_COINCAP_API_KEY=your_dev_key_here
DEV_API_BASE_URL=https://api.coincap.io/v2

# Staging
STAGING_COINCAP_API_KEY=your_staging_key_here
STAGING_API_BASE_URL=https://api.coincap.io/v2

# Production
PROD_COINCAP_API_KEY=your_prod_key_here
PROD_API_BASE_URL=https://api.coincap.io/v2
```

📚 **Read more:** [API_KEYS_SETUP.md](./API_KEYS_SETUP.md)

### 3️⃣ Build & Run

#### Android
```bash
# Development build
./gradlew :composeApp:assembleDevDebug

# Install to device
./gradlew :composeApp:installDevDebug
```

#### iOS (macOS only)
```bash
# Build iOS framework
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

# Open Xcode project
open iosApp/iosApp.xcodeproj
```

#### Desktop
```bash
# Run desktop app
./gradlew :composeApp:run
```

📚 **Read more:** [BUILD_VARIANTS.md](./BUILD_VARIANTS.md)

## 🎯 Build Variants

The project supports three environment variants:

| Variant | App ID | Debug | Analytics | Use Case |
|---------|--------|-------|-----------|----------|
| **dev** | `org.poc.app.dev` | ✅ | ❌ | Local development |
| **staging** | `org.poc.app.staging` | ❌ | ✅ | QA testing |
| **prod** | `org.poc.app` | ❌ | ✅ | Production |

```bash
# Build different variants
./gradlew :composeApp:assembleDevDebug
./gradlew :composeApp:assembleStagingDebug
./gradlew :composeApp:assembleProdRelease
```

## 🧪 Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Android Instrumentation Tests
```bash
./gradlew :composeApp:connectedDevDebugAndroidTest
```

### Code Quality Analysis
```bash
# Run Detekt static analysis
./gradlew detekt
```

## 📱 Screenshots

*Coming soon...*

## 🔐 Security Features

- ✅ **No Hardcoded Secrets** - API keys in gitignored `local.properties`
- ✅ **Build-time Injection** - Keys embedded during compilation
- ✅ **ProGuard Obfuscation** - Code obfuscation in release builds
- ✅ **Environment Separation** - Different keys per environment
- ✅ **CI/CD Ready** - Environment variable support

## 📚 Documentation

- 📖 [API Keys Security Setup](./API_KEYS_SETUP.md)
- 📖 [Build Variants Guide](./BUILD_VARIANTS.md)
- 📖 [Architecture Documentation](./docs/ARCHITECTURE.md) *(coming soon)*
- 📖 [Contributing Guide](./CONTRIBUTING.md) *(coming soon)*

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [CoinCap API](https://coincap.io/) - Cryptocurrency data provider
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Material Design 3](https://m3.material.io/)

## 📞 Contact

**Your Name** - [@yourtwitter](https://twitter.com/yourtwitter)

Project Link: [https://github.com/yourusername/KMP_POC](https://github.com/yourusername/KMP_POC)

---

⭐ **Star this repo** if you found it helpful!

Made with ❤️ using Kotlin Multiplatform
