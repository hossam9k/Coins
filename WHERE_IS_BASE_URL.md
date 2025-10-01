# Where is the Base URL? 🔍

## The Complete Flow - Step by Step

### 1️⃣ Base URL is defined in ApiConfig
```kotlin
// ApiConfig.kt
object ApiConfig {
    enum class Environment(val baseUrl: String) {
        PRODUCTION("https://api.coinlore.net/api/"),  // ← BASE URL HERE!
        STAGING("https://staging.coinlore.net/api/")
    }

    val baseUrl = environment.baseUrl  // ← "https://api.coinlore.net/api/"
}
```

### 2️⃣ CoreModule passes base URL to NetworkFactory
```kotlin
// CoreModule.kt
single<HttpClient> {
    get<NetworkFactory>().getClient(
        baseUrl = ApiConfig.baseUrl  // ← PASSING BASE URL HERE!
    )
}
```

### 3️⃣ NetworkFactory creates client WITH base URL
```kotlin
// NetworkFactory.kt
private fun createClient(baseUrl: String) {  // ← RECEIVES BASE URL
    return HttpClient {
        defaultRequest {
            url {
                takeFrom(baseUrl)  // ← SETS BASE URL IN CLIENT!
            }
        }
    }
}
```

### 4️⃣ CoinsModule injects the configured client
```kotlin
// CoinsModule.kt
single<CoinsRemoteDataSource> {
    KtorCoinsRemoteDataSource(
        httpClient = get()  // ← Gets client WITH BASE URL already set!
    )
}
```

### 5️⃣ RemoteDataSource uses the client
```kotlin
// KtorCoinsRemoteDataSource.kt
class KtorCoinsRemoteDataSource(
    private val httpClient: HttpClient  // ← This client ALREADY has base URL!
) {
    suspend fun getListOfCoins() {
        httpClient.get {
            url {
                path(ApiConfig.Endpoints.ALL_COINS)  // Just adds "tickers/"
            }
        }
        // Final URL = base URL + path
        // = "https://api.coinlore.net/api/" + "tickers/"
        // = "https://api.coinlore.net/api/tickers/"
    }
}
```

## The Magic Happens Here:

When NetworkFactory creates the HttpClient:
```kotlin
HttpClient {
    defaultRequest {  // ← This runs for EVERY request!
        url {
            takeFrom("https://api.coinlore.net/api/")
        }
    }
}
```

This means EVERY request made with this client automatically starts with the base URL!

## Visual Flow:

```
ApiConfig.baseUrl
    ↓ "https://api.coinlore.net/api/"
CoreModule
    ↓ passes to
NetworkFactory.getClient(baseUrl)
    ↓ creates HttpClient with
defaultRequest { url { takeFrom(baseUrl) } }
    ↓ client injected to
KtorCoinsRemoteDataSource(httpClient)
    ↓ makes request
httpClient.get { path("tickers/") }
    ↓ results in
"https://api.coinlore.net/api/tickers/"
```

## Why You Didn't See It:

1. **It's in `defaultRequest`** - This configuration applies to ALL requests automatically
2. **Ktor hides it** - When you call `httpClient.get("endpoint")`, Ktor combines base URL + endpoint internally
3. **DI magic** - The client is configured once in CoreModule and injected everywhere

## Test It Yourself:

Add this debug line to see the full URL:
```kotlin
httpClient.get {
    url {
        path(ApiConfig.Endpoints.ALL_COINS)
    }
    println("Full URL: ${this.url}")  // Will print: https://api.coinlore.net/api/tickers/
}
```

The base URL IS there - it's just configured once and used everywhere automatically! 🎯