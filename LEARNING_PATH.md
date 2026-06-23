# Android Interview Preparation — SportsConnect Learning Path

This guide is written for someone who knows basic Jetpack Compose UI components and wants to
understand everything used in this app at a level deep enough to discuss it confidently in
interviews. Topics are ordered so that each one builds on the last. Do not skip ahead.

---

## Stage 1 — Kotlin You Must Know Cold

Before touching any Android framework, you need to be fluent in the Kotlin features this app
uses everywhere. Interviewers test these directly.

### 1.1 Sealed Classes and When Expressions
- What sealed classes are and why they exist
- How `Resource<T>` (Success, Error, Loading) works as a result wrapper
- Exhaustive `when` — why the compiler forces you to handle every branch
- Difference between sealed class and enum

**Why this matters here:** Every API call in the app returns `Resource<T>`. You need to
understand why this pattern exists over throwing exceptions or returning nulls.

### 1.2 Data Classes
- What the compiler generates: `equals()`, `hashCode()`, `toString()`, `copy()`
- Structural equality vs reference equality
- Why `copy()` is used for immutable state updates in ViewModels

### 1.3 Generics and Type Parameters
- How `Resource<T>` is generic
- Upper bounds (`<T : Any>`)
- Why type erasure matters at runtime

### 1.4 Scope Functions
- `let`, `run`, `also`, `apply`, `with` — when to use each
- The difference between `it` and `this` inside scope functions
- How `?.let { }` replaces null checks throughout the app

### 1.5 Extension Functions and Properties
- How to write them, where they live
- Why they don't break encapsulation
- How `toDomain()` in the repository layer is an extension on DTO classes

### 1.6 Higher-Order Functions and Lambdas
- Passing functions as parameters (every `onClick`, `onBlock`, `onProfileClick`)
- Trailing lambda syntax
- Function types: `() -> Unit`, `(Long, String) -> Unit`
- `inline` functions and why Compose uses them

### 1.7 Object, Companion Object, Singleton
- `object` declaration = singleton in Kotlin
- `companion object` for static-like members
- How `BlockEventBus` is a singleton managed by Hilt

### 1.8 `operator fun invoke()`
- Kotlin lets you call an object like a function if you define `operator fun invoke()`
- Every use case in this app uses this pattern:
  ```kotlin
  class GetGigByIdUseCase @Inject constructor(...) {
      suspend operator fun invoke(gigId: Long): Resource<Gig> { ... }
  }
  // Called as: getGigByIdUseCase(gigId) instead of getGigByIdUseCase.invoke(gigId)
  ```
- Why this makes use cases read like functions at the call site

**Key interview question:** "Why do use cases use `operator fun invoke()`?"

### 1.9 `Result<T>` vs Custom `Resource<T>`
- Kotlin stdlib provides `Result<T>` with `success` and `failure`
- `Result<T>` has no `Loading` state — suited for one-shot operations like `reportMessage()`
- Custom `Resource<T>` adds a `Loading` state — suited for UI-facing flows that show spinners
- This app uses both: `Resource<T>` for all repository calls, `Result<T>` for the report
  message feature where loading is handled separately

---

## Stage 2 — Coroutines and Flow

This is the most important stage. The entire async layer of this app — API calls, DataStore
reads, real-time events — runs on coroutines and Flow. Interviewers ask about this constantly.

### 2.1 Coroutines Fundamentals
- What a coroutine is (lightweight thread, not an OS thread)
- `suspend` keyword — what it means, where it can be called
- `CoroutineScope` and why you need one
- `launch` vs `async` / `await`
- `Dispatchers.IO`, `Dispatchers.Main`, `Dispatchers.Default` — when to use each
- `withContext` — switching dispatchers mid-coroutine

**Key interview question:** "What is the difference between `launch` and `async`?"

### 2.2 Coroutine Scopes in Android
- `viewModelScope` — why it's tied to ViewModel lifecycle
- What happens to a running coroutine when the ViewModel is cleared
- Why you should never use `GlobalScope` in production

### 2.3 `Job` vs `SupervisorJob`
- `Job` — if one child coroutine fails, it cancels all siblings and the parent
- `SupervisorJob` — a child failure is isolated; other children keep running
- When to use `SupervisorJob`: long-lived scopes in Singleton classes where one failure
  (e.g. a failed message insert) should not kill the entire WebSocket listener
- In this app: `StompWebSocketManager` and `ChatRepositoryImpl` both create their own
  `CoroutineScope(SupervisorJob() + Dispatchers.IO)` because they are Singletons that
  outlive any ViewModel

**Key interview question:** "What is the difference between Job and SupervisorJob?"

### 2.4 Custom CoroutineScope Outside ViewModel
- ViewModels get `viewModelScope` for free. Singletons do not.
- Pattern: `private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)`
- You are responsible for cancelling this scope when it is no longer needed
- This scope lives as long as the Singleton lives (app lifetime)
- Why this is necessary: `StompWebSocketManager` needs to launch coroutines to read the
  token and connect, but it has no lifecycle owner

### 2.5 Exception Handling
- `try/catch` inside coroutines
- `CoroutineExceptionHandler`
- Why exceptions in `launch` crash but exceptions in `async` are deferred to `await()`

### 2.6 `runBlocking`
- Bridges blocking code into coroutines — blocks the current thread until the coroutine completes
- Almost always wrong to use in production Android code
- The one legitimate use in this app: reading the auth token synchronously inside
  `WebSocketListener.onMessage()` which is a callback on OkHttp's thread, not a coroutine
- Never call `runBlocking` on the main thread — it will cause ANR

### 2.7 Flow Basics
- What `Flow` is — a cold, asynchronous stream
- Cold means: nothing happens until someone calls `collect`
- `flow { emit(...) }` builder
- Terminal operators: `collect`, `first`, `toList`
- Intermediate operators: `map`, `filter`, `onEach`, `distinctUntilChanged`

### 2.8 Hot vs Cold Streams
- **Cold Flow**: starts producing values only when collected; each collector gets its own stream
  — e.g. Room DAO `Flow`, DataStore `Flow`, Retrofit (wrapped in flow)
- **Hot Flow**: produces values regardless of collectors; all collectors share the same stream
  — e.g. `StateFlow`, `SharedFlow`, `stateIn`, `shareIn`
- `StateFlow` is always hot — it always has a current value
- `SharedFlow` is always hot — emissions happen regardless of whether anyone is listening

**Key interview question:** "What is the difference between a hot and cold Flow?"

### 2.9 StateFlow and SharedFlow
- `StateFlow` — always has a value, replays last value to new collectors (used for UI state)
- `SharedFlow` — no initial value, configurable replay (used for one-shot events like navigation)
- `MutableStateFlow` vs `MutableSharedFlow`
- `asStateFlow()` / `asSharedFlow()` — why you expose read-only versions
- `update { }` — atomic, thread-safe state update (important: regular assignment is not atomic)
- `extraBufferCapacity` — extra buffer slots so `tryEmit` succeeds even with no active collector.
  Used in `BlockEventBus` and `StompWebSocketManager` so events are never dropped.

**Key interview question:** "Why use StateFlow over LiveData?"

### 2.10 `collectLatest` vs `collect`
- `collect` — processes every emission, even if a new one arrives mid-processing
- `collectLatest` — cancels the current block when a new emission arrives
- When each is appropriate (DataStore subscriptions use `collectLatest`)

### 2.11 `launchIn` and `onEach`
- `.onEach { }.launchIn(scope)` is equivalent to `scope.launch { flow.onEach { }.collect() }`
- More idiomatic when chaining operators before collecting
- Used in `ChatViewModel` for observing messages:
  ```kotlin
  observeMessagesUseCase(gigId)
      .onEach { messages -> _state.update { ... } }
      .launchIn(viewModelScope)
  ```
- The returned `Job` can be cancelled if needed

### 2.12 `tryEmit` vs `emit`
- `emit` is a suspend function — it suspends until there is buffer space
- `tryEmit` is non-suspending — returns `true` if emitted, `false` if buffer is full
- Use `tryEmit` when you are in a non-coroutine context (e.g. a callback like `onMessage`)
- Used in `StompWebSocketManager.onMessage()` because OkHttp calls it on its own thread,
  not inside a coroutine

### 2.13 `distinctUntilChanged()`
- Flow operator that suppresses re-emission if the new value equals the previous value
- Used in `ChatRepositoryImpl.observeMessages()` — Room emits on every write to the database,
  even if the messages for the current chat group did not change
- Prevents the UI from recomposing for unrelated database writes

### 2.14 `stateIn()` — Converting Cold Flow to StateFlow
- Problem: a cold `Flow` from DataStore or the network cannot be directly used as `StateFlow`
- `flow.stateIn(scope, started, initialValue)` converts any cold Flow into a hot `StateFlow`
- Used in `MainViewModel` to turn `authPreferences.accessToken` (a DataStore Flow) into a
  `StateFlow<Boolean>` that Compose can observe as stable state
- `SharingStarted.Eagerly` — starts collecting immediately, even before any subscriber
- `SharingStarted.Lazily` — starts on first subscriber, never stops
- `SharingStarted.WhileSubscribed(5000)` — starts on first subscriber, stops 5s after last
  subscriber disappears (best for ViewModels — saves resources when screen is in background)

**Key interview question:** "What is the difference between `stateIn` and `shareIn`? What does `SharingStarted.WhileSubscribed` do?"

### 2.15 `combine()` — Merging Multiple Flows
- `combine(flow1, flow2, flow3) { a, b, c -> result }` — emits whenever ANY input flow emits
- Used in `HomeViewModel` to react to any change in sport query, near-me toggle, or radius:
  ```kotlin
  combine(_sportQuery, _nearMe, _radiusKm) { sport, near, radius ->
      Triple(sport, near, radius)
  }.collectLatest { ... }
  ```
- All input flows must have emitted at least one value before `combine` emits anything
- Contrast with `zip` (waits for all flows to emit, pairs them one-to-one)

### 2.16 `debounce()` — Rate Limiting Emissions
- Suppresses emissions until a specified time has passed without a new emission
- Used for search: if the user types quickly, only the final value after 400ms of inactivity
  is passed downstream — prevents an API call on every keystroke
- Requires `@OptIn(FlowPreview::class)` because `debounce` is still experimental API
- `@OptIn` is how you opt into APIs marked `@Experimental` without suppressing all warnings

### 2.17 Job Cancellation for In-Flight Requests
- Problem: user changes the search filter while the previous API call is still in flight —
  the stale result could overwrite the newer one
- Solution: store the `Job` returned by `viewModelScope.launch { }` and cancel it before
  starting a new one:
  ```kotlin
  private var loadGigJob: Job? = null

  fun loadGigs(...) {
      loadGigJob?.cancel()
      loadGigJob = viewModelScope.launch { ... }
  }
  ```
- `.cancel()` on a coroutine Job is cooperative — the coroutine must reach a suspension
  point (like a network call) before it actually stops

### 2.18 Flow in DataStore
- How DataStore exposes data as `Flow<T>`
- Why you read DataStore with `.first()` for one-shot reads
- Why you `collectLatest` for reactive subscriptions

---

## Stage 3 — ViewModel and UI State

### 3.1 ViewModel Fundamentals
- What ViewModel is and what problem it solves (survives configuration changes)
- Why you never pass `Context` directly into a ViewModel
- `SavedStateHandle` — persisting state across process death, how route arguments are read from it

### 3.2 Unidirectional Data Flow (UDF)
- The pattern: State flows down, events flow up
- ViewModel holds state, UI observes state, UI sends events to ViewModel
- Why this makes UI predictable and testable

### 3.3 UI State Design
- Modeling state as a single data class (e.g. `GigDetailState`)
- Computed properties inside state classes (e.g. `isOwner`, `buttonState`)
- Why you avoid multiple separate `StateFlow`s for related state

### 3.4 One-Shot Events with SharedFlow
- The problem: navigation and snackbars should not be re-triggered on recomposition
- Why `StateFlow` is wrong for events (replays on resubscription)
- Pattern: `MutableSharedFlow<UiEvent>` in ViewModel, `collectLatest` in `LaunchedEffect`
- Sealed classes for UI events (e.g. `GigDetailUiEvent`)

**Key interview question:** "How do you handle navigation from a ViewModel?"

---

## Stage 4 — Jetpack Compose Beyond the Basics

You know UI components. Now you need to understand how Compose thinks.

### 4.1 Recomposition
- What triggers recomposition
- Why `@Composable` functions must be side-effect free
- Stability and `@Stable` — why unstable types cause excess recompositions
- `remember` — surviving recomposition
- `rememberSaveable` — surviving recomposition AND configuration changes

### 4.2 State in Compose
- `mutableStateOf` — observable state inside a composable
- State hoisting — moving state up so composables are stateless and reusable
- `collectAsStateWithLifecycle` — the correct way to collect Flow in Compose (lifecycle-aware)
- Why `collectAsState()` is less safe (doesn't respect lifecycle)

### 4.3 Side Effects
These are one of the most interviewed Compose topics.

- `LaunchedEffect(key)` — runs a coroutine when key changes, cancels on recomposition
- `DisposableEffect(key)` — for cleanup (e.g. MapLibre lifecycle observer in `GigMiniMap`)
- `SideEffect` — runs after every successful recomposition
- `derivedStateOf` — derive state from other state without unnecessary recomposition
- `rememberCoroutineScope` — get a scope tied to the composition for button click coroutines

**Key interview question:** "What is the difference between LaunchedEffect and rememberCoroutineScope?"

### 4.4 Compose Navigation
- `NavHostController`, `NavHost`, `composable(route)`
- Passing arguments via routes (type-safe: `{userId}` with `NavType.LongType`)
- `popBackStack()` vs navigating to a new destination
- Clearing back stack on logout: `popUpTo(0) { inclusive = true }`

### 4.5 Lists
- `LazyColumn`, `LazyRow` — only composing visible items
- `key` parameter — why it matters for animations and state preservation
- `items()`, `item()`, `itemsIndexed()`

### 4.6 `BackHandler`
- Compose API for intercepting the system back button / back gesture
- `BackHandler(enabled = true) { /* your action */ }` — when enabled, your lambda runs
  instead of the default back behavior
- Used in `MainScreen` to navigate directly to the Home tab when the user presses back
  from the Gigs or Profile tab, rather than exiting the app
- `enabled` parameter lets you conditionally activate it — if `false`, the default back
  behavior is restored

### 4.7 Observing Current Navigation Destination
- `navController.currentBackStackEntryAsState()` — returns a `State<NavBackStackEntry?>` that
  recomposes whenever the current destination changes
- Used in `MainScreen` to decide whether to show the bottom bar and FAB:
  ```kotlin
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route
  val showBottomBar = currentRoute in listOf(Screen.Home.route, ...)
  ```
- This is the correct pattern — never manually track current screen with your own StateFlow

### 4.8 Animations
- `AnimatedVisibility` with `enter`/`exit`
- `animateColorAsState`, `animateFloatAsState`
- `Crossfade`
- `SwipeToDismissBox` — used for accept/reject swipe on join requests

### 4.7 `AndroidView`
- The bridge between classic Android Views and Compose
- Use when a Compose equivalent does not exist (MapLibre has no Composable wrapper)
- `factory` block runs once to create the View
- `update` block runs on recomposition to sync state to the View
- You are responsible for the View's lifecycle inside `DisposableEffect`

---

## Stage 5 — Clean Architecture

This is what separates junior from mid-level Android developers in interviews.

### 5.1 Why Architecture Matters
- The cost of no architecture (God Activity, untestable code)
- What "separation of concerns" means in practice

### 5.2 The Three Layers
- **Presentation** — ViewModels, UI State, Composables. Knows nothing about network or database.
- **Domain** — Use cases, repository interfaces, domain models. Pure Kotlin, no Android imports.
- **Data** — Repository implementations, DTOs, API clients, local storage. Implements domain interfaces.

### 5.3 The Dependency Rule
- Dependencies only point inward: Data → Domain ← Presentation
- Domain never imports from Data or Presentation
- Why this allows you to swap implementations (e.g. replace Retrofit with another client)

### 5.4 Repository Pattern
- Repository interface lives in Domain (e.g. `UserRepository`)
- Implementation lives in Data (e.g. `UserRepositoryImpl`)
- ViewModel depends on the interface, not the implementation
- Why this makes testing possible (you can mock the interface)

### 5.5 Use Cases (Interactors)
- Single-responsibility: one use case = one action
- Always use `operator fun invoke()` so the use case is called like a function
- When use cases are worth it vs when they're just boilerplate
- A non-suspend use case (e.g. `ObserveMessageUseCase`) can return a `Flow` directly —
  the caller collects it in their own scope

### 5.6 Domain Models vs DTOs
- DTO (Data Transfer Object) — matches the API JSON structure
- Domain model — matches your app's needs, decoupled from the API
- The `toDomain()` mapping function — why it lives in the Data layer
- Why you never pass a DTO directly to your ViewModel

---

## Stage 6 — Dependency Injection with Hilt

### 6.1 Why Dependency Injection
- The problem: manually constructing objects with many dependencies is brittle
- DI inverts control — you declare what you need, the framework provides it

### 6.2 The Application Class
- Every Android app has an `Application` class that lives for the entire app lifetime
- You subclass it, annotate it with `@HiltAndroidApp`, and declare it in `AndroidManifest.xml`
- Global one-time initialization goes here (e.g. `MapLibre.getInstance(...)`)
- Do not put business logic here — it is only for SDK/library initialization

### 6.3 Hilt Fundamentals
- `@HiltAndroidApp` on Application class
- `@AndroidEntryPoint` on Activity/Fragment (not needed in Compose-only apps using hiltViewModel)
- `@HiltViewModel` + `@Inject constructor` on ViewModels
- `@Inject constructor` on repositories and other classes

### 6.4 Context Qualifiers
- Hilt can inject two kinds of `Context`: Application context and Activity context
- `@ApplicationContext` — the Application-scoped context, safe to store in a Singleton
- `@ActivityContext` — the Activity-scoped context, must never be stored in a Singleton
  (it would leak the Activity)
- `AuthPreferences` is a `@Singleton` that needs `Context` to access DataStore — it uses
  `@ApplicationContext` so no Activity is ever leaked
- Rule: if you need context in anything scoped to Application or ViewModel, always use
  `@ApplicationContext`

### 6.5 Modules
- `@Module` + `@InstallIn` — where the bindings live
- `@InstallIn(SingletonComponent::class)` — bindings live as long as the Application
- `@Provides` — for classes you don't own (Retrofit, OkHttp, DataStore)
- `@Binds` — for binding an interface to its implementation (e.g. `UserRepository` → `UserRepositoryImpl`)

### 6.6 Scopes
- `@Singleton` — one instance for the entire app lifetime
- `@ViewModelScoped` — one instance per ViewModel
- Why scope matters: misusing it creates memory leaks or incorrect state sharing
- `StompWebSocketManager` is `@Singleton` because only one WebSocket connection should exist

### 6.7 Multiple Retrofit Instances
- This app has two Retrofit instances: one for the main backend, one for Nominatim (geocoding)
- Each is provided via a separate `@Provides` method with a `@Named` qualifier to distinguish them
- This is the correct Hilt pattern when you need two clients pointing at different base URLs

### 6.8 Injecting into Compose
- `hiltViewModel()` — gets the ViewModel from Hilt inside a composable
- How Hilt integrates with Compose Navigation (each destination gets its own ViewModel scope)

---

## Stage 7 — Networking

### 7.1 HTTP Fundamentals
- Request methods: GET, POST, PUT, DELETE
- Status codes: 200, 201, 204, 400, 401, 403, 404, 409, 500
- Headers: `Authorization: Bearer <token>`, `Content-Type`
- What a request body and response body are

### 7.2 Retrofit
- Defining API interfaces with annotations: `@GET`, `@POST`, `@Path`, `@Query`, `@Body`
- `suspend` functions in Retrofit — how it integrates with coroutines
- `Response<T>` vs direct `T` return — when you need access to status codes
- Gson converter — automatic JSON to Kotlin data class deserialization
- `@SerializedName` — mapping a JSON field name to a differently-named Kotlin property

### 7.3 OkHttp — Interceptors
- `Interceptor` — runs on every request and response, regardless of status code
- How an `AuthInterceptor` attaches the JWT token to every request by reading from DataStore
- Auth endpoints (`/api/auth/`) are skipped so the login request itself isn't blocked
- `HttpLoggingInterceptor` — logs full request/response bodies in debug builds only
- Timeouts: connect, read, write — set to 30s in this app
- OkHttp's `WebSocket` and `WebSocketListener` — covered in Stage 9

### 7.4 OkHttp — Authenticator (Token Refresh)
- `Authenticator` is completely different from `Interceptor`
- `Interceptor` runs on every request. `Authenticator` runs only when the server returns a 401.
- `Authenticator.authenticate(route, response)` — OkHttp calls this automatically on 401;
  return a new `Request` with a fresh token to retry, or return `null` to give up
- This enables **silent token refresh**: the user never sees a login screen mid-session
- The full silent refresh flow in this app:
  1. Request is made with the current access token
  2. Server returns 401 (token expired)
  3. OkHttp calls `TokenAuthenticator.authenticate()`
  4. Authenticator reads the refresh token from DataStore
  5. Calls `/api/auth/refresh` to get a new access token
  6. Saves the new tokens to DataStore
  7. Returns the original request rebuilt with the new access token
  8. OkHttp retries the original request — user never knew anything happened
  9. If refresh also fails (refresh token expired) → clear all auth data → user is logged out

### 7.5 Thread Safety During Token Refresh — `ReentrantLock`
- Problem: multiple requests can expire simultaneously, causing multiple concurrent 401s and
  multiple simultaneous refresh attempts — which wastes calls and can corrupt stored tokens
- Solution: `ReentrantLock` ensures only one thread executes the refresh at a time
- After acquiring the lock, re-read the stored token — if another thread already refreshed it,
  just retry with the new token without calling the refresh endpoint again
- `lock.isHeldByCurrentThread` — detects recursive calls (e.g. if the refresh endpoint itself
  returns 401) and bails out to prevent a deadlock
- Always release the lock in a `finally` block so it is never held permanently on exception

### 7.6 Breaking Circular Dependencies with `Lazy<T>`
- Circular dependency: `OkHttpClient` needs `TokenAuthenticator`, `TokenAuthenticator` needs
  `SportsApi`, `SportsApi` needs `OkHttpClient` — a cycle Hilt cannot resolve
- Solution: inject `Lazy<SportsApi>` instead of `SportsApi` directly
- `Lazy<T>` from Dagger/Hilt defers construction — the `SportsApi` instance is not created
  until `api.get()` is called, by which time `OkHttpClient` already exists
- `api.get()` — retrieves the lazily-initialized instance

### 7.4 Error Handling
- `HttpException` — thrown by Retrofit for non-2xx responses
- `e.code()` — extracting the HTTP status code
- Why 403 specifically means "blocked" in this app vs generic errors
- Network errors (`IOException`) vs HTTP errors (`HttpException`) — handling both

### 7.7 Authentication Flow — End to End
- JWT (JSON Web Token) — what it is, what it contains (header.payload.signature)
- Access token (short-lived, e.g. 15 min) vs refresh token (long-lived, e.g. 7 days)
- Storing both tokens in DataStore (not SharedPreferences, not memory)
- Attaching access token to requests via `AuthInterceptor`
- 401 response → `TokenAuthenticator` silently refreshes and retries (see 7.4)
- Refresh also fails → clear DataStore → `MainViewModel` detects `accessToken == null`
  → navigates to Login automatically

### 7.8 Third-Party Public APIs (Nominatim)
- Not all APIs require an auth key — Nominatim (OpenStreetMap geocoding) is free and open
- **Forward geocoding**: text address → `(lat, lng)` coordinates — used for location search
- **Reverse geocoding**: `(lat, lng)` → human-readable address
- Nominatim requires a separate Retrofit instance with base URL `https://nominatim.openstreetmap.org/`
- The `NominatimApi` interface uses `@GET("search")` and `@GET("reverse")` with `@Query` params
- API response is a `List<NominatimResultDto>` containing `lat`, `lon`, `display_name`

---

## Stage 8 — Local Persistence

### 8.1 DataStore Preferences — Why Over SharedPreferences
- SharedPreferences is synchronous and can cause ANR on the main thread
- DataStore is asynchronous, built on coroutines and Flow
- DataStore is type-safe with Preferences keys
- Use DataStore for small primitive data: tokens, user ID, blocked user IDs

### 8.2 Preferences DataStore
- `preferencesDataStore` delegate
- `stringPreferencesKey`, `longSetPreferencesKey`, etc.
- Reading: `dataStore.data.map { preferences -> preferences[KEY] }`
- Writing: `dataStore.edit { preferences -> preferences[KEY] = value }`
- `.first()` for one-shot reads inside coroutines

### 8.3 Room Database — Why
- DataStore is for small primitive data. For structured data (chat messages), you need Room.
- Room is a type-safe ORM (Object Relational Mapper) on top of SQLite
- In this app: all chat messages are cached in Room so the user sees history instantly on open,
  before the network request completes

### 8.4 Room — Entities
- `@Entity("table_name")` — marks a data class as a database table
- `@PrimaryKey` — the unique identifier for each row
- Each field in the data class becomes a column
- Example from this app: `ChatMessageEntity` with fields `id`, `groupId`, `senderId`, etc.

### 8.5 Room — DAOs
- DAO (Data Access Object) — an interface that defines database operations
- `@Dao` annotation
- `@Insert(onConflict = OnConflictStrategy.IGNORE)` — insert a row, ignore if duplicate
- `@Query("SELECT * FROM ...")` — raw SQL query
- `@Delete`
- DAO methods can be `suspend` (one-shot) or return `Flow<T>` (reactive, re-emits on change)

### 8.6 Room — Reactive Queries with Flow
- When a DAO method returns `Flow<List<T>>`, Room automatically re-emits whenever the
  table changes
- This is how chat messages update in real time: new message arrives via WebSocket,
  is inserted into Room, Room emits to the Flow, ViewModel updates state, UI recomposes
- This is the correct pattern — the WebSocket and the UI are decoupled via Room

### 8.7 Room — Database Class
- Abstract class extending `RoomDatabase`
- `@Database(entities = [...], version = N, exportSchema = false)`
- Declares abstract DAO accessor functions
- Instantiated once via Hilt as a Singleton using `Room.databaseBuilder(...).build()`

### 8.8 Room — Versioning and Migrations
- Every time you change a table schema, you must increment the `version` number
- Without a migration, the app will crash on upgrade
- `fallbackToDestructiveMigration()` — drops and recreates all tables (data lost); only
  acceptable during development, never in production after users have data
- Proper migrations use `Migration(fromVersion, toVersion)` with ALTER TABLE SQL

### 8.9 What to Store Where
- Auth tokens, user ID, username → **DataStore** (small, primitive, no structure)
- Blocked user IDs → **DataStore** (a Set<Long>)
- Chat messages → **Room** (structured, queryable, reactive)
- Large binary data → **File system**, never Room or DataStore

### 8.10 Backup Exclusion
- Why auth tokens must be excluded from Android backup
- `data_extraction_rules.xml` (API 31+) and `backup_rules.xml` (older)
- Security implication of getting the exclusion path wrong

---

## Stage 9 — Real-Time Communication

### 9.1 WebSocket Fundamentals
- HTTP vs WebSocket — persistent bidirectional connection
- The handshake — starts as HTTP, upgrades to WS
- `ws://` vs `wss://` (secure WebSocket)
- When to use WebSocket vs polling

### 9.2 OkHttp WebSocket API
- `OkHttpClient.newWebSocket(request, listener)` — opens the connection
- `WebSocketListener` — a class you extend with callbacks:
  - `onOpen` — connection established
  - `onMessage(text: String)` — text frame received from server
  - `onFailure` — connection dropped or error
  - `onClosing` — server initiated close
- `WebSocket.send(text)` — send a text frame to the server
- `WebSocket.close(code, reason)` — close the connection gracefully
- These callbacks run on OkHttp's internal thread, NOT the main thread and NOT a coroutine

### 9.3 STOMP Protocol
- STOMP (Simple Text Oriented Messaging Protocol) sits on top of WebSocket
- Adds messaging semantics: destinations, subscriptions, acknowledgements
- Each STOMP frame is a text string with a command, headers, and body, terminated by ` `
- **CONNECT frame** — authenticate and establish a STOMP session:
  ```
  CONNECT
  accept-version:1.2
  host:example.com
  Authorization:Bearer <token>

   
  ```
- **CONNECTED frame** — server response confirming the session
- **SUBSCRIBE frame** — subscribe to a topic to receive messages:
  ```
  SUBSCRIBE
  id:sub-0
  destination:/topic/chat/42
  ack:auto

   
  ```
- **SEND frame** — send a message to a destination:
  ```
  SEND
  destination:/app/chat/42/send
  content-type:application/json

  {"content":"hello"}
   
  ```
- **MESSAGE frame** — what the server sends back when a message is published to your subscription

### 9.4 The Full Chat Data Flow in This App
1. `ChatViewModel.init` calls `connectChatUseCase(gigId)`
2. `StompWebSocketManager.connect()` opens OkHttp WebSocket and sends a CONNECT frame
3. On CONNECTED response, sends a SUBSCRIBE frame for `/topic/chat/{gigId}`
4. When a user sends a message, `StompWebSocketManager.sendMessage()` sends a SEND frame
5. Server broadcasts the message — `onMessage()` fires with a MESSAGE frame
6. The frame body is parsed with Gson into a `ChatMessage`
7. `_messageFlow.tryEmit(message)` puts it on the SharedFlow
8. `ChatRepositoryImpl` collects the SharedFlow and inserts the message into Room via DAO
9. Room's reactive Flow re-emits the updated list
10. `ObserveMessageUseCase` returns this Flow to the ViewModel
11. ViewModel filters out blocked users and updates `_state`
12. UI recomposes

### 9.5 Lifecycle Management
- Connecting in `init`, disconnecting in `onCleared()`
- What happens if the connection drops — reconnection strategy
- Why you filter messages by `senderId` to hide blocked users

---

## Stage 10 — Location and Maps

### 10.1 Android Location
- `FusedLocationProviderClient` — Google's battery-efficient location API
- Runtime permissions: `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`
- `ActivityResultLauncher` — the correct modern way to request permissions
- One-shot location vs continuous updates

### 10.2 Runtime Permissions in Compose
- `rememberLauncherForActivityResult` with `RequestPermission` contract
- Checking if permission is already granted before requesting
- Handling permanent denial (redirect to settings)

### 10.3 MapLibre
- Open-source alternative to Google Maps (no API key cost)
- `MapView` — an Android View, not a Composable (requires `AndroidView` wrapper)
- `AndroidView` — embedding classic Views inside Compose (covered in Stage 4.7)
- Managing MapView lifecycle manually with `DisposableEffect` (onCreate, onStart, onResume,
  onPause, onStop, onDestroy)
- Adding markers, setting camera position
- Initializing MapLibre in `Application.onCreate()` — must happen before any `MapView` is created

### 10.4 Nominatim Geocoding (covered in Stage 7.6)
- Used alongside MapLibre: user types a location name, Nominatim returns coordinates,
  MapLibre moves the camera to those coordinates and places a marker

---

## Stage 11 — Android Build System

### 11.1 Gradle with Kotlin DSL
- `build.gradle.kts` vs `build.gradle` (Groovy)
- `defaultConfig` — applicationId, minSdk, targetSdk, versionCode, versionName
- `buildTypes` — debug vs release
- `buildConfigField` — injecting constants (BASE_URL, WS_BASE_URL) at build time
- `BuildConfig.DEBUG` — a boolean that is `true` in debug builds, `false` in release;
  used throughout the app for conditional logging: `if (BuildConfig.DEBUG) Log.d(...)`
  so logs never appear in the version users install

### 11.2 ProGuard / R8
- What minification does — renames classes/methods to short names, removes unused code
- What shrinking does — removes unused resources
- Why you need ProGuard rules: Gson, Retrofit, and Hilt rely on reflection and will break without them
- `-keep` rules — telling R8 not to touch specific classes
- Why DTOs and domain models must be kept (Gson deserializes by field name)

### 11.3 Release Signing
- Why APKs must be signed to be installed or published
- Keystore file — what it contains, why losing it means you can never update your app on the store
- `signingConfigs` in Gradle
- Why signing credentials must never be committed to git (`local.properties` pattern)

### 11.4 App Bundle vs APK
- APK — single file, installs directly
- AAB (Android App Bundle) — Play Store splits it into smaller APKs per device configuration
- Play Store requires AAB for new apps

### 11.5 Splash Screen API
- `androidx.core:core-splashscreen` — the Jetpack implementation of the Android 12+ splash screen
- Configured via a theme in `res/values/themes.xml`
- Applied in `Activity.onCreate()` before `super.onCreate()`
- Allows you to show your app icon with a branded background while the app initializes

---

## Stage 12 — Patterns Used Throughout This App

These are recurring patterns worth knowing by name for interviews.

### 12.1 Event Bus
- A shared `SharedFlow` that any ViewModel can emit to or collect from
- Used here for cross-ViewModel block events (`BlockEventBus`)
- Trade-off: decouples ViewModels but makes data flow harder to trace

### 12.2 Local-First Write
- Write to local storage immediately, sync with server in the background
- User sees instant feedback; the server call is best-effort
- Used when blocking a user: DataStore is updated first, then `syncBlockedIds()` is called

### 12.3 Resource Wrapper
- `sealed class Resource<T> { Success, Error, Loading }`
- Forces every call site to handle all three states
- Avoids returning null or throwing exceptions across layer boundaries

### 12.4 Computed State Properties
- State data class with properties derived from its own fields
- e.g. `val isOwner get() = gig?.isOwner ?: false` inside `GigDetailState`
- Keeps the ViewModel's `update {}` calls simpler

### 12.5 Offline-First Chat (Room + WebSocket)
- Messages arrive via WebSocket and are immediately written to Room
- The UI observes Room, not the WebSocket directly
- This means the UI is always showing Room's state — consistent and testable
- Network and UI are fully decoupled; if WebSocket drops, messages already received are still visible

---

## Stage 13 — Interview Preparation

### Questions you should be able to answer from this app

**Architecture**
- Explain Clean Architecture and why the dependency rule matters
- What is the Repository pattern and why is the interface in the Domain layer?
- When would you use a Use Case vs calling the repository directly from the ViewModel?
- Why do use cases use `operator fun invoke()`?

**Coroutines & Flow**
- What is the difference between `StateFlow` and `SharedFlow`?
- What is the difference between a hot and cold stream?
- When do you use `collectLatest` vs `collect`?
- What happens if an exception is thrown inside `viewModelScope.launch`?
- What is `SupervisorJob` and when do you need it?
- What is the difference between `tryEmit` and `emit`?
- How does `.onEach { }.launchIn(scope)` differ from `scope.launch { flow.collect { } }`?

**Compose**
- What triggers recomposition and how do you prevent unnecessary recompositions?
- What is the difference between `LaunchedEffect` and `rememberCoroutineScope`?
- How do you handle one-shot events (navigation, snackbars) from a ViewModel?
- What is state hoisting and why does it matter?
- How do you embed a non-Composable View (like a map) inside Compose?

**Coroutines & Flow (continued)**
- What is `stateIn()` and what does `SharingStarted.WhileSubscribed` do?
- How do you use `combine()` and when does it emit?
- What does `debounce()` do and why is it useful for search?
- How do you cancel an in-flight coroutine when a new request arrives?

**Networking**
- How do you attach an auth token to every Retrofit request?
- What is the difference between an OkHttp `Interceptor` and an `Authenticator`?
- How does silent token refresh work with OkHttp's `Authenticator`?
- How do you prevent multiple concurrent token refreshes from corrupting stored tokens?
- What is `Lazy<T>` in Hilt and why is it used in `TokenAuthenticator`?
- How do you distinguish a 403 from a 404 in Retrofit?
- What is the difference between `HttpException` and `IOException`?
- How does STOMP work over WebSocket?
- Why is `tryEmit` used inside `WebSocketListener.onMessage()`?

**Room**
- How do you get real-time updates from Room?
- What is `OnConflictStrategy.IGNORE` and when would you use it?
- What happens if you change the database schema without a migration?
- When would you use Room vs DataStore?

**General Android**
- What is the ViewModel and what problem does it solve?
- Why should you not store auth tokens in SharedPreferences?
- What does ProGuard do and why do you need rules for Gson?
- Why does losing your keystore mean you cannot update your Play Store app?
- What is `BuildConfig.DEBUG` and why is it important?

---

## Recommended Resources (in order)

1. **Kotlin** — Official Kotlin documentation (kotlinlang.org), specifically coroutines and Flow
2. **Coroutines** — Roman Elizarov's talks on YouTube, "Kotlin Flows in practice" (Google I/O)
3. **Compose** — Official Compose pathway on developer.android.com
4. **Architecture** — "Guide to app architecture" on developer.android.com
5. **Hilt** — Official Hilt documentation, "Dependency Injection on Android" (Google I/O)
6. **Room** — Official Room documentation on developer.android.com
7. **This codebase** — Read it layer by layer. Start with one feature end-to-end: pick the chat
   feature and trace it from the `ChatScreen` composable → `ChatViewModel` → `ConnectChatUseCase`
   → `ChatRepositoryImpl` → `StompWebSocketManager` → OkHttp WebSocket → STOMP frame → Room DAO
   → Flow back to the ViewModel → UI recomposition. That single trace covers coroutines, Flow,
   Clean Architecture, Room, WebSocket, STOMP, and Compose side effects all at once.
