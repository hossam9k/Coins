# Network Architecture Flow

## How It All Connects 🔗

```
┌──────────────────────────────────────────────────────────────┐
│                         ApiConfig                             │
│  • Environment (PRODUCTION/STAGING/DEV)                       │
│  • Base URLs                                                  │
│  • Endpoint paths (/coins, /user/profile, etc)               │
└────────────────┬─────────────────────────────────────────────┘
                 │ Provides base URL & endpoints
                 ▼
┌──────────────────────────────────────────────────────────────┐
│                      NetworkFactory                           │
│  • Creates HTTP clients                                       │
│  • Configures timeout, retry, logging                         │
│  • Caches clients for reuse                                   │
└────────────────┬─────────────────────────────────────────────┘
                 │ Creates configured client
                 ▼
┌──────────────────────────────────────────────────────────────┐
│                    CoreModule (DI)                            │
│  • single { NetworkFactory() }                                │
│  • single<HttpClient> {                                       │
│      get<NetworkFactory>().getClient(ApiConfig.baseUrl)       │
│    }                                                           │
└────────────────┬─────────────────────────────────────────────┘
                 │ Provides HttpClient via DI
                 ▼
┌──────────────────────────────────────────────────────────────┐
│                  Feature Modules (DI)                         │
│                                                                │
│  CoinsModule:                                                 │
│  single { KtorCoinsRemoteDataSource(httpClient = get()) }     │
│                                                                │
│  ProfileModule:                                               │
│  single { ProfileRemoteDataSource(httpClient = get()) }       │
└────────────────┬─────────────────────────────────────────────┘
                 │ Injects HttpClient
                 ▼
┌──────────────────────────────────────────────────────────────┐
│                     RemoteDataSource                          │
│                                                                │
│  class KtorCoinsRemoteDataSource(httpClient: HttpClient) {    │
│      suspend fun getCoins() {                                 │
│          httpClient.get(ApiConfig.Endpoints.ALL_COINS)        │
│      }                                                         │
│  }                                                             │
└───────────────────────────────────────────────────────────────┘
```

## Current Implementation in Your Project

### 1️⃣ **ApiConfig** defines everything
```kotlin
// core/data/network/ApiConfig.kt
object ApiConfig {
    val baseUrl = "https://api.coinlore.net/api/"

    object Endpoints {
        const val ALL_COINS = "tickers/"
        const val COIN_DETAILS = "ticker"
    }
}
```

### 2️⃣ **NetworkFactory** creates clients
```kotlin
// core/data/network/NetworkFactory.kt
class NetworkFactory {
    fun getClient(baseUrl: String): HttpClient {
        return HttpClient {
            // All configuration here
            defaultRequest { url(baseUrl) }
            install(ContentNegotiation) { json() }
            install(HttpTimeout) { ... }
        }
    }
}
```

### 3️⃣ **CoreModule** wires it up
```kotlin
// di/CoreModule.kt
val coreModule = module {
    single { NetworkFactory() }
    single<HttpClient> {
        get<NetworkFactory>().getClient(ApiConfig.baseUrl)
    }
}
```

### 4️⃣ **CoinsModule** uses it
```kotlin
// di/CoinsModule.kt
val coinsModule = module {
    single<CoinsRemoteDataSource> {
        KtorCoinsRemoteDataSource(
            httpClient = get()  // ← Gets from CoreModule!
        )
    }
}
```

### 5️⃣ **RemoteDataSource** makes API calls
```kotlin
// feature/coins/data/KtorCoinsRemoteDataSource.kt
class KtorCoinsRemoteDataSource(
    private val httpClient: HttpClient  // ← Injected
) {
    suspend fun getListOfCoins() {
        return httpClient.get(ApiConfig.Endpoints.ALL_COINS)
        // ↑ Just the endpoint, base URL already configured!
    }
}
```

## The Magic ✨

When you call `httpClient.get(ApiConfig.Endpoints.ALL_COINS)`:
1. The client already has base URL: `https://api.coinlore.net/api/`
2. It appends the endpoint: `tickers/`
3. Final URL: `https://api.coinlore.net/api/tickers/`
4. All with retry, timeout, and logging already configured!

## Adding a New Feature

Want to add user authentication? Just:

1. **Add endpoints**:
```kotlin
// In ApiConfig.kt
const val LOGIN = "auth/login"
```

2. **Create data source**:
```kotlin
class AuthRemoteDataSource(
    private val httpClient: HttpClient  // Get from DI
) {
    suspend fun login(email: String, password: String) {
        httpClient.post(ApiConfig.Endpoints.LOGIN) {
            setBody(LoginRequest(email, password))
        }
    }
}
```

3. **Wire in DI**:
```kotlin
val authModule = module {
    single { AuthRemoteDataSource(httpClient = get()) }
}
```

That's it! No need to configure HTTP client again! 🎉

## Why This Architecture is Great

1. **DRY (Don't Repeat Yourself)**: Configure once, use everywhere
2. **Easy Testing**: Mock NetworkFactory or HttpClient
3. **Environment Switching**: Change one line to switch ALL APIs
4. **Maintainable**: All network config in one place
5. **Scalable**: Easy to add new features/services