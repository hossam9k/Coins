# Network Architecture Usage Guide

## Building a New Feature: User Profile

Here's how to use the NetworkFactory and ApiConfig when building a new feature:

### 1. Add Endpoints to ApiConfig
```kotlin
// core/data/network/ApiConfig.kt
object Endpoints {
    // Existing endpoints...

    // Add your new feature endpoints
    const val USER_PROFILE = "user/profile"
    const val UPDATE_PROFILE = "user/profile/update"
    const val UPLOAD_AVATAR = "user/avatar"
}
```

### 2. Create DTOs (Data Transfer Objects)
```kotlin
// feature/profile/data/dto/ProfileDto.kt
@Serializable
data class ProfileDto(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?
)
```

### 3. Create Remote Data Source
```kotlin
// feature/profile/data/ProfileRemoteDataSource.kt
class ProfileRemoteDataSource(
    private val httpClient: HttpClient  // Injected from DI
) {
    suspend fun getProfile(): Result<ProfileDto, DataError.Remote> {
        return safeCall {
            // Just use the endpoint constant - base URL is already configured!
            httpClient.get(ApiConfig.Endpoints.USER_PROFILE)
        }
    }

    suspend fun updateProfile(name: String): Result<ProfileDto, DataError.Remote> {
        return safeCall {
            httpClient.put(ApiConfig.Endpoints.UPDATE_PROFILE) {
                setBody(UpdateProfileRequest(name))
            }
        }
    }
}
```

### 4. Create Repository
```kotlin
// feature/profile/domain/ProfileRepository.kt
interface ProfileRepository {
    suspend fun getProfile(): Result<Profile, DataError>
    suspend fun updateProfile(name: String): Result<Profile, DataError>
}

// feature/profile/data/ProfileRepositoryImpl.kt
class ProfileRepositoryImpl(
    private val remoteDataSource: ProfileRemoteDataSource,
    private val mapper: ProfileMapper
) : ProfileRepository {
    override suspend fun getProfile(): Result<Profile, DataError> {
        return remoteDataSource.getProfile().map { dto ->
            mapper.toDomain(dto)
        }
    }
}
```

### 5. Set Up Dependency Injection
```kotlin
// di/ProfileModule.kt
val profileModule = module {
    // Data layer
    single { ProfileRemoteDataSource(httpClient = get()) }
    single { ProfileMapper() }
    single<ProfileRepository> {
        ProfileRepositoryImpl(
            remoteDataSource = get(),
            mapper = get()
        )
    }

    // Domain layer
    factory { GetProfileUseCase(repository = get()) }
    factory { UpdateProfileUseCase(repository = get()) }

    // Presentation layer
    viewModelOf(::ProfileViewModel)
}
```

### 6. Use in ViewModel
```kotlin
// feature/profile/presentation/ProfileViewModel.kt
class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    fun loadProfile() {
        viewModelScope.launch {
            getProfileUseCase().fold(
                onSuccess = { profile ->
                    // Update UI state
                },
                onError = { error ->
                    // Handle error
                }
            )
        }
    }
}
```

## Advanced Scenarios

### Multiple Base URLs (Microservices)
```kotlin
val authModule = module {
    // Different service = different base URL
    single<HttpClient>("authClient") {
        get<NetworkFactory>().getClient(
            baseUrl = "https://auth-service.myapp.com/"
        )
    }

    single {
        AuthRemoteDataSource(
            httpClient = get(named("authClient"))
        )
    }
}
```

### Custom Headers for Specific Service
```kotlin
class PaymentRemoteDataSource(
    private val networkFactory: NetworkFactory,
    private val apiKey: String
) {
    private val client = networkFactory.getClient().config {
        defaultRequest {
            header("X-API-Key", apiKey)
            header("X-Client-Version", "1.0.0")
        }
    }
}
```

### Environment Switching
```kotlin
// Just change this one line to switch ALL APIs to different environment
ApiConfig.environment = ApiConfig.Environment.STAGING

// Or use build flavors
val environment = when {
    BuildConfig.DEBUG -> Environment.DEVELOPMENT
    BuildConfig.STAGING -> Environment.STAGING
    else -> Environment.PRODUCTION
}
```

## Benefits of This Architecture

1. **No Hardcoded URLs**: Services only know about endpoint paths
2. **Easy Environment Switching**: Change one line to switch all APIs
3. **Centralized Configuration**: All network config in one place
4. **Testable**: Easy to mock NetworkFactory for tests
5. **Flexible**: Different clients for different services if needed
6. **Type-Safe**: Using constants prevents typos

## Testing

```kotlin
class ProfileRemoteDataSourceTest {
    private val mockClient = MockHttpClient()
    private val dataSource = ProfileRemoteDataSource(mockClient)

    @Test
    fun `get profile returns success`() = runTest {
        // Mock response
        mockClient.onGet(ApiConfig.Endpoints.USER_PROFILE) {
            respond("""{"id": "1", "name": "John"}""")
        }

        // Test
        val result = dataSource.getProfile()
        assertTrue(result.isSuccess)
    }
}
```

This architecture scales from simple apps to complex multi-service architectures!