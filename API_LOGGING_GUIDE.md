# API Logging Configuration Guide

## 🔧 How to Enable/Disable API Logging

### Quick Toggle (Current Setup)

API logging is controlled by the `AppConfig` in `CoreModule.kt`:

```kotlin
// di/CoreModule.kt
single<AppConfig> {
    DevAppConfig()    // ✅ Logging ENABLED (isDebug = true)
    // or
    DefaultAppConfig() // ❌ Logging DISABLED (isDebug = false)
}
```

## 📊 Logging Levels

In `NetworkFactory.kt`, you can adjust the logging detail:

```kotlin
install(Logging) {
    logger = Logger.DEFAULT
    level = LogLevel.ALL      // Shows everything
    // Options:
    // LogLevel.ALL     - Headers, body, full request/response
    // LogLevel.HEADERS - Only headers
    // LogLevel.BODY    - Only body
    // LogLevel.INFO    - Basic info
    // LogLevel.NONE    - No logging
}
```

## 📱 Platform-Specific Logging

### Android
Logs appear in **Logcat** with tag `Ktor`
```
Filter: Ktor
```

### iOS
Logs appear in **Xcode Console**
```
Filter: Ktor
```

### Desktop
Logs appear in **System.out** (IDE console)

## 🎯 What You'll See When Logging is Enabled

```
REQUEST: https://api.coinlore.net/api/tickers/
METHOD: HttpMethod(value=GET)
HEADERS
-> Accept: application/json
-> Content-Type: application/json

RESPONSE: 200 OK
BODY
{
  "data": [
    {
      "id": "90",
      "symbol": "BTC",
      "name": "Bitcoin",
      "price_usd": "45000.00"
    }
  ]
}
TIME: 245ms
```

## 🔐 Security Features

The logging configuration automatically:
- ✅ Hides sensitive headers (Authorization, X-Api-Key)
- ✅ Only logs API calls (filters by host)
- ✅ Disabled in production (when using ProdAppConfig)

## 🚀 Best Practices

### Development
```kotlin
single<AppConfig> { DevAppConfig() }  // Full logging
```

### Staging
```kotlin
single<AppConfig> {
    DefaultAppConfig(isDebug = true)  // Basic logging
}
```

### Production
```kotlin
single<AppConfig> {
    ProdAppConfig(apiKey = "your-key")  // No logging
}
```

## 🐛 Troubleshooting

### Not Seeing Logs?

1. Check `CoreModule.kt` - using `DevAppConfig()`?
2. Check `NetworkFactory` - `enableLogging = true`?
3. Run the app and make a network call
4. Check the correct console for your platform

### Too Much Logging?

Change level in `NetworkFactory.kt`:
```kotlin
level = LogLevel.INFO  // Less verbose
```

### Want Custom Logging?

Create custom logger:
```kotlin
logger = object : Logger {
    override fun log(message: String) {
        println("🌐 API: $message")
    }
}
```

## 📝 Current Configuration

✅ **Logging is NOW ENABLED** because:
- Using `DevAppConfig()` in CoreModule
- `isDebug = true` in DevAppConfig
- `LogLevel.ALL` in NetworkFactory

You should see all API calls in your console! 🎉