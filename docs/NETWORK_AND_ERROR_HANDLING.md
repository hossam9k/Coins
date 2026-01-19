# Network Layer & Error Handling

> **Reference Architecture for Android/KMP Projects**

This document describes the network layer and comprehensive error handling system implemented in this project. It serves as a reference for future projects.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Why This Architecture?](#why-this-architecture)
3. [Error Hierarchy](#error-hierarchy)
4. [Network Layer Components](#network-layer-components)
5. [Error Handling Flow](#error-handling-flow)
6. [Usage Examples](#usage-examples)
7. [Customization Guide](#customization-guide)
8. [Benefits Summary](#benefits-summary)

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                        │
│  ┌─────────────┐    ┌──────────────┐    ┌──────────────────┐   │
│  │  ViewModel  │───▶│  Use Case    │───▶│  toUiText()      │   │
│  │             │    │              │    │  (error → string)│   │
│  └─────────────┘    └──────────────┘    └──────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         DOMAIN LAYER                             │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    Result<T, DataError>                  │   │
│  │  ┌─────────────────┐    ┌─────────────────────────────┐ │   │
│  │  │ Result.Success  │    │ Result.Error                │ │   │
│  │  │ (data: T)       │    │ (error: DataError)          │ │   │
│  │  └─────────────────┘    └─────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                          DATA LAYER                              │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                    Network Components                       │ │
│  │  ┌──────────────────┐  ┌──────────────────────────────┐   │ │
│  │  │ ExceptionHandler │  │ ResponseMapper               │   │ │
│  │  │ (network errors) │  │ (HTTP status → DataError)    │   │ │
│  │  └──────────────────┘  └──────────────────────────────┘   │ │
│  │  ┌──────────────────┐  ┌──────────────────────────────┐   │ │
│  │  │BusinessErrorHandler│ │ HttpClientExt (safeCall)    │   │ │
│  │  │ (API body errors)│  │ (composition layer)          │   │ │
│  │  └──────────────────┘  └──────────────────────────────┘   │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## Why This Architecture?

### Design Decisions & Benefits

#### 1. Single `safeCall` Function

We use **one function** for all API calls instead of multiple variants:

```kotlin
// ONE function handles everything
suspend inline fun <reified T> safeCall(
    errorParser: ApiErrorParser = DefaultApiErrorParser,
    execute: () -> HttpResponse,
): Result<T, DataError>
```

**Benefits:**
- **Simple** - One function to learn and use
- **Safe by default** - Always checks for HTTP errors AND business errors
- **No confusion** - Team doesn't need to decide which function to use
- **Consistent** - Same pattern everywhere in codebase

#### 2. Unified Return Type: `Result<T, DataError>`

All network calls return `Result<T, DataError>` (not `DataError.Remote`):

```kotlin
// Before (confusing - multiple return types)
suspend fun getCoins(): Result<List<Coin>, DataError.Remote>
suspend fun updateProfile(): Result<Profile, DataError>  // Different!

// After (simple - one return type)
suspend fun getCoins(): Result<List<Coin>, DataError>
suspend fun updateProfile(): Result<Profile, DataError>  // Same!
```

**Benefits:**
- **Uniform API** - All functions return same error type
- **Flexible** - Can return Remote, Local, or Business errors
- **Type-safe** - Compiler still enforces exhaustive handling

#### 3. Hybrid Error Pattern (Enum + Sealed Class)

| Error Type | Pattern | Why? |
|------------|---------|------|
| `DataError.Remote` | **Enum** | Simple HTTP/network errors, no extra data needed |
| `DataError.Local` | **Enum** | Simple device errors, no extra data needed |
| `DataError.Business` | **Sealed Class** | Needs to carry message, field errors, limits, etc. |

```kotlin
// Enum - simple, no extra data
DataError.Remote.UNAUTHORIZED  // Just the error type
DataError.Remote.NO_INTERNET   // Just the error type

// Sealed class - carries context
DataError.Business.MinimumAmountNotMet(
    message = "Minimum amount is $10",
    minimumAmount = "10.00"
)
```

#### 4. Single Responsibility Components

Each component has ONE job:

| Component | Responsibility |
|-----------|----------------|
| `ExceptionHandler` | Convert network exceptions to `DataError.Remote` |
| `ResponseMapper` | Convert HTTP status codes to `DataError.Remote` |
| `BusinessErrorHandler` | Parse business errors from response body |
| `HttpClientExt` | Compose all handlers into `safeCall` function |

#### 5. Map-Based Factory Pattern

Error code mapping uses `Map` for O(1) lookup:

```kotlin
object BusinessErrorCode {
    private val codeToErrorFactory: Map<String, (String?) -> DataError.Business> = mapOf(
        "PROFILE_INCOMPLETE" to { msg -> DataError.Business.ProfileIncomplete(msg) },
        "EMAIL_NOT_VERIFIED" to { msg -> DataError.Business.EmailNotVerified(msg) },
        // ... more mappings
    )

    fun fromCode(code: String, message: String? = null): DataError.Business {
        val factory = codeToErrorFactory[code.uppercase()]
        return factory?.invoke(message) ?: DataError.Business.Unknown(code, message)
    }
}
```

---

## Error Hierarchy

### DataError Sealed Interface

```kotlin
sealed interface DataError : Error {
    enum class Remote : DataError { ... }      // HTTP/network errors (enum)
    enum class Local : DataError { ... }       // Device errors (enum)
    sealed class Business(...) : DataError     // API business errors (sealed class)
}
```

### How to Check Error Type

```kotlin
when (val result = repository.getCoins()) {
    is Result.Success -> {
        // Use result.data
    }
    is Result.Error -> {
        when (result.error) {
            // Check by category
            is DataError.Remote -> handleNetworkError(result.error)
            is DataError.Local -> handleLocalError(result.error)
            is DataError.Business -> handleBusinessError(result.error)
        }

        // Or check specific errors
        when (result.error) {
            DataError.Remote.NO_INTERNET -> showNoInternetDialog()
            DataError.Remote.UNAUTHORIZED -> logout()
            is DataError.Business.SessionExpired -> logout()
            is DataError.Business.KycRequired -> navigateToKyc()
            else -> showGenericError(result.error.toUiText())
        }
    }
}
```

### 1. Remote Errors (Enum)

Simple HTTP status codes and network exceptions:

| Error | Description | Typical Cause |
|-------|-------------|---------------|
| `UNAUTHORIZED` | 401 - Not authenticated | Token expired |
| `FORBIDDEN` | 403 - Not authorized | Insufficient permissions |
| `BAD_REQUEST` | 400 - Invalid request | Malformed request |
| `NOT_FOUND` | 404 - Resource not found | Invalid ID |
| `CONFLICT` | 409 - Resource conflict | Duplicate entry |
| `PAYLOAD_TOO_LARGE` | 413 - Request too big | File too large |
| `TOO_MANY_REQUESTS` | 429 - Rate limited | Too many API calls |
| `SERVER_ERROR` | 500 - Server error | Backend bug |
| `SERVICE_UNAVAILABLE` | 503 - Service down | Maintenance |
| `NO_INTERNET` | Network unavailable | WiFi/cellular off |
| `REQUEST_TIMEOUT` | Request timed out | Slow network |
| `SSL_ERROR` | TLS/Certificate error | Certificate issue |
| `CONNECTION_REFUSED` | Server refused | Server down |
| `SERIALIZATION` | JSON parse failed | API response changed |
| `UNKNOWN` | Unexpected error | Catch-all |

### 2. Local Errors (Enum)

| Error | Description |
|-------|-------------|
| `DISK_FULL` | Device storage is full |
| `NOT_FOUND` | Local resource not found |
| `INSUFFICIENT_FUNDS` | Not enough balance |
| `UNKNOWN` | Unexpected local error |

### 3. Business Errors (Sealed Class)

API returns HTTP 200 but error in response body:

```json
{
  "success": false,
  "errorCode": "MINIMUM_AMOUNT_NOT_MET",
  "message": "Minimum purchase amount is $10"
}
```

| Error Type | Codes | Extra Data |
|------------|-------|------------|
| `ProfileIncomplete` | PROFILE_INCOMPLETE | `requiredFields: List<String>?` |
| `EmailNotVerified` | EMAIL_NOT_VERIFIED | `message` |
| `PhoneNotVerified` | PHONE_NOT_VERIFIED | `message` |
| `KycRequired` | KYC_REQUIRED | `message` |
| `AccountSuspended` | ACCOUNT_SUSPENDED | `reason: String?` |
| `MinimumAmountNotMet` | MINIMUM_AMOUNT_NOT_MET | `minimumAmount: String?` |
| `MaximumAmountExceeded` | MAXIMUM_AMOUNT_EXCEEDED | `maximumAmount: String?` |
| `DailyLimitReached` | DAILY_LIMIT_REACHED | `limit: String?` |
| `FeatureDisabled` | FEATURE_DISABLED | `featureName: String?` |
| `ResourceUnavailable` | RESOURCE_UNAVAILABLE | `resourceId: String?` |
| `ValidationFailed` | VALIDATION_FAILED | `fieldErrors: Map<String, String>?` |
| `DuplicateEntry` | DUPLICATE_ENTRY | `field: String?` |
| `SessionExpired` | SESSION_EXPIRED | `message` |
| `ReAuthRequired` | REAUTH_REQUIRED | `message` |
| `Unknown` | Any unrecognized code | `code: String`, `message` |

---

## Network Layer Components

### 1. HttpClientExt - The Main API

Two functions for all network calls:

```kotlin
// Primary function - use for all standard API calls
suspend inline fun <reified T> safeCall(
    errorParser: ApiErrorParser = DefaultApiErrorParser,
    execute: () -> HttpResponse,
): Result<T, DataError>

// For APIs using ApiResponse<T> wrapper format
suspend inline fun <reified T> safeApiCall(
    execute: () -> HttpResponse,
): Result<T, DataError>
```

| Function | Use Case |
|----------|----------|
| `safeCall` | Standard API calls (returns data directly) |
| `safeApiCall` | APIs that wrap response in `{ "success": true, "data": {...} }` |

### 2. ExceptionHandler

Converts network exceptions to typed errors:

| Exception | Error |
|-----------|-------|
| `CancellationException` | Rethrown (structured concurrency) |
| `SocketTimeoutException` | `REQUEST_TIMEOUT` |
| `HttpRequestTimeoutException` | `REQUEST_TIMEOUT` |
| `ConnectTimeoutException` | `REQUEST_TIMEOUT` |
| `UnresolvedAddressException` | `NO_INTERNET` |
| `IOException` with "ssl" | `SSL_ERROR` |
| `IOException` with "refused" | `CONNECTION_REFUSED` |
| `IOException` (other) | `NO_INTERNET` |
| Other exceptions | `UNKNOWN` |

### 3. ResponseMapper

Maps HTTP status codes:

```kotlin
object ResponseMapper {
    fun isSuccess(statusCode: Int): Boolean = statusCode in 200..299
    fun mapStatusCodeToError(statusCode: Int): DataError.Remote
}
```

### 4. BusinessErrorHandler

Parses business errors from response body:

```kotlin
object BusinessErrorHandler {
    fun parseBusinessError(
        body: String,
        parser: ApiErrorParser = DefaultApiErrorParser,
    ): DataError.Business?
}
```

---

## Error Handling Flow

```
┌──────────────────┐
│   HTTP Request   │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐     ┌─────────────────────────┐
│ Network Exception│────▶│ DataError.Remote        │
│ (ExceptionHandler)     │ (NO_INTERNET, TIMEOUT)  │
└──────────────────┘     └─────────────────────────┘
         │ No exception
         ▼
┌──────────────────┐     ┌─────────────────────────┐
│ HTTP Status Code │────▶│ DataError.Remote        │
│ (ResponseMapper) │     │ (UNAUTHORIZED, NOT_FOUND│
│ 4xx / 5xx        │     │  SERVER_ERROR)          │
└──────────────────┘     └─────────────────────────┘
         │ 2xx
         ▼
┌──────────────────┐     ┌─────────────────────────┐
│ Business Error   │────▶│ DataError.Business      │
│(BusinessErrorHandler)  │ (ProfileIncomplete,     │
│ (success: false) │     │  KycRequired)           │
└──────────────────┘     └─────────────────────────┘
         │ No error
         ▼
┌──────────────────┐
│  Result.Success  │
│  (parsed data)   │
└──────────────────┘
```

---

## Usage Examples

### Data Source

```kotlin
class KtorCoinsRemoteDataSource(
    private val client: HttpClient
) : CoinsRemoteDataSource {

    override suspend fun getCoins(): Result<CoinsResponseDto, DataError> =
        safeCall {
            client.get("coins")
        }

    override suspend fun getCoinById(id: String): Result<CoinDto, DataError> =
        safeCall {
            client.get("coins/$id")
        }
}
```

### Repository

```kotlin
class CoinsRepositoryImpl(
    private val remoteDataSource: CoinsRemoteDataSource
) : CoinsRepository {

    override suspend fun getCoins(): Result<List<CoinModel>, DataError> =
        remoteDataSource.getCoins().map { response ->
            response.data.coins.map { it.toCoinModel() }
        }
}
```

### Use Case

```kotlin
class GetCoinsListUseCase(
    private val repository: CoinsRepository
) : NoParamUseCase<Result<List<CoinModel>, DataError>> {

    override suspend operator fun invoke(): Result<List<CoinModel>, DataError> =
        repository.getCoins()
}
```

### ViewModel

```kotlin
class CoinsViewModel(
    private val getCoinsUseCase: GetCoinsListUseCase
) : ViewModel() {

    fun loadCoins() {
        viewModelScope.launch {
            _state.value = CoinsState.Loading

            when (val result = getCoinsUseCase()) {
                is Result.Success -> {
                    _state.value = CoinsState.Success(result.data)
                }
                is Result.Error -> {
                    // Convert to user-friendly message
                    val message = result.error.toUiText()
                    _state.value = CoinsState.Error(message)

                    // Handle specific errors
                    when (result.error) {
                        DataError.Remote.NO_INTERNET -> showRetryOption()
                        DataError.Remote.UNAUTHORIZED -> logout()
                        is DataError.Business.SessionExpired -> logout()
                        is DataError.Business.KycRequired -> navigateToKyc()
                        else -> { /* Show generic error */ }
                    }
                }
            }
        }
    }
}
```

### Custom Error Parser

```kotlin
object MyApiErrorParser : ApiErrorParser {
    @Serializable
    data class MyErrorResponse(
        val status: Int,
        val errorCode: String? = null,
        val msg: String? = null
    )

    override fun parse(body: String): BusinessErrorInfo? {
        return try {
            val response = Json.decodeFromString<MyErrorResponse>(body)
            BusinessErrorInfo(
                isError = response.status != 0,
                code = response.errorCode,
                message = response.msg
            )
        } catch (e: Exception) {
            null
        }
    }
}

// Usage
suspend fun getProfile(): Result<ProfileDto, DataError> =
    safeCall(errorParser = MyApiErrorParser) {
        client.get("profile")
    }
```

---

## Customization Guide

### Adding New Business Errors

1. **Add to `DataError.Business`:**
```kotlin
data class InsufficientLevel(
    override val message: String? = null,
    val requiredLevel: Int? = null
) : Business(message)
```

2. **Add String Resource:**
```xml
<string name="error_insufficient_level">You need level %d to access this.</string>
```

3. **Update `DataErrorToString`:**
```kotlin
is DataError.Business.InsufficientLevel -> Res.string.error_insufficient_level
```

4. **Update `BusinessErrorCode`:**
```kotlin
"INSUFFICIENT_LEVEL" to { msg -> DataError.Business.InsufficientLevel(msg) },
```

---

## Benefits Summary

| Benefit | Description |
|---------|-------------|
| **Simple API** | One `safeCall` function for all network calls |
| **No Confusion** | Team doesn't need to choose between multiple functions |
| **Type Safety** | Exhaustive `when` expressions catch missing cases |
| **Flexible** | Can return any error type (Remote, Local, Business) |
| **Separation of Concerns** | Each component has ONE job |
| **Easy Testing** | Components can be unit tested in isolation |
| **Extensibility** | Map-based mapping, pluggable parsers |
| **User-Friendly** | Automatic mapping to localized strings |
| **Maintainable** | No hardcoded strings, single source of truth |

---

## File Reference

| File | Purpose |
|------|---------|
| `DataError.kt` | Error type definitions + `HttpErrorCode` + `BusinessErrorCode` |
| `Result.kt` | Result wrapper with utilities |
| `ExceptionHandler.kt` | Network exception handling |
| `ResponseMapper.kt` | HTTP status code mapping |
| `BusinessErrorHandler.kt` | Business error parsing + `ApiErrorParser` |
| `HttpClientExt.kt` | `safeCall` and `safeApiCall` functions |
| `ApiResponse.kt` | API response DTOs |
| `NetworkErrorLogger.kt` | Error logging utilities |
| `DataErrorToString.kt` | Error to UI string mapping |
| `strings.xml` | User-facing error messages |

---

## Best Practices

1. **Use `safeCall` for all API calls** - Never make raw HTTP calls
2. **Return `Result<T, DataError>`** - Unified error type everywhere
3. **Handle specific errors** - `SessionExpired` → logout, `KycRequired` → KYC flow
4. **Use `toUiText()`** - Always convert errors to localized strings
5. **Log errors** - Use `logNetworkError()` for debugging
6. **Use exhaustive `when`** - Let compiler catch missing cases

---

*Reference: [Chirp by Philipp Lackner](https://github.com/philipplackner/Chirp)*
