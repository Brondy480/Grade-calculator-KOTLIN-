🛍️ CampusMarket — Student Marketplace App

SE 3242 – Android Application Development
Continuous Assessment Project | ICT University, Yaoundé, Cameroon
Author: Brondy Noumsi | Partner: Eskil16


📋 Table of Contents

Project Overview
Features
Screenshots
Architecture Diagram
Kotlin & OOP Concepts Used
Sensors Used
Code Documentation
Setup Instructions
User Guide
Project Structure
Team


🎯 Project Overview
CampusMarket is a full-stack Android marketplace app built with Kotlin + Jetpack Compose that allows ICT University students to buy and sell second-hand items on campus. The app features a complete authentication system, real-time chat between buyers and sellers, local data persistence with Room database, and sensor-based interactions.
This project demonstrates advanced Kotlin and Android development concepts including:

Full OOP design with data class, sealed class, interface, abstract class
Null safety throughout with ?., ?:, !!, let
MVVM architecture with ViewModel + StateFlow
Room database for local persistence
Jetpack Compose for declarative UI
Android sensor integration (Vibration + Accelerometer)


✨ Features
FeatureDescription🔐 AuthenticationLogin & Register with session persistence🏠 Home FeedBrowse all listings in a grid with category filters🔍 Search & FilterLive search with category and price filtering➕ Post ItemSell items with title, price, category, description📄 Item DetailFull item info with seller contact details💬 ChatReal-time messaging between buyer and seller📋 My ListingsManage your posted items, edit or delete👤 ProfileView and edit your profile, logout📳 VibrationHaptic feedback on send message and post item🔄 Shake to RefreshShake phone to refresh the home feed

📸 Screenshots

Add screenshots by running the app, taking screenshots and placing them in a screenshots/ folder

LoginHome FeedItem DetailAfficher l'imageAfficher l'imageAfficher l'image
Post ItemChatProfileAfficher l'imageAfficher l'imageAfficher l'image

📌 To add screenshots: create a screenshots/ folder in Project/, take screenshots from the emulator or phone, and update the paths above.


🏗️ Architecture Diagram
┌─────────────────────────────────────────────────────────────┐
│                        MainActivity                          │
│              (Entry point — wires all dependencies)          │
└───────────────────────────┬─────────────────────────────────┘
                            │
              ┌─────────────┴─────────────┐
              ▼                           ▼
┌─────────────────────┐     ┌─────────────────────────────┐
│   AuthViewModel     │     │      MarketViewModel         │
│                     │     │                              │
│  - login()          │     │  - loadAllItems()            │
│  - register()       │     │  - searchItems()             │
│  - logout()         │     │  - postItem()                │
│  - currentUser      │     │  - loadMessages()            │
│  - AuthState        │     │  - loadConversations()       │
│    (sealed class)   │     │  - MarketUiState             │
└──────────┬──────────┘     │    (sealed class)            │
           │                └────────────┬────────────────┘
           │                             │
           └──────────────┬──────────────┘
                          ▼
          ┌───────────────────────────────┐
          │    CampusMarketRepository     │
          │   (Single source of truth)    │
          └───────────────┬───────────────┘
                          │
          ┌───────────────┼───────────────┐
          ▼               ▼               ▼
    ┌──────────┐   ┌──────────┐   ┌──────────────┐
    │ ItemDao  │   │ UserDao  │   │ MessageDao   │
    └──────────┘   └──────────┘   └──────────────┘
          │               │               │
          └───────────────┼───────────────┘
                          ▼
          ┌───────────────────────────────┐
          │    CampusMarketDatabase       │
          │    (Room — local storage)     │
          └───────────────────────────────┘
MVVM Data Flow
User Action (tap, shake, type)
        │
        ▼
  Compose UI Screen
        │
        ▼
    ViewModel
   (business logic)
        │
        ▼
   Repository
  (single source)
        │
        ▼
   Room Database
  (local storage)
        │
        ▼
  StateFlow emits
        │
        ▼
  UI recomposes
Navigation Flow
Login / Register
      │
      ▼
  Home Feed ──────► Item Detail ──────► Chat
      │
      ├──► Search & Filter
      │
      ├──► Post Item
      │
      ├──► My Listings
      │
      ├──► Messages (Conversations)
      │
      └──► Profile ──────► Logout ──────► Login

🧠 Kotlin & OOP Concepts Used
1. data class — Model classes
kotlin// Auto-generates equals(), hashCode(), toString(), copy()
data class Item(
    val id: Int = 0,
    val title: String,
    val price: Double,
    val category: String,
    val description: String?,   // ✅ nullable
    val sellerId: Int,
    val imageUri: String?,      // ✅ nullable
    val isSold: Boolean = false
)

data class User(
    val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val phone: String?,         // ✅ nullable
    val avatarUri: String?      // ✅ nullable
)
2. sealed class — UI State management
kotlin// ✅ Exhaustive when — no else needed
sealed class MarketUiState {
    object Idle    : MarketUiState()
    object Loading : MarketUiState()
    data class Success(val items: List<Item>) : MarketUiState()
    data class Error(val message: String)     : MarketUiState()
}

// Used in Compose UI
when (val state = uiState) {
    is MarketUiState.Loading -> CircularProgressIndicator()
    is MarketUiState.Success -> ItemGrid(state.items)
    is MarketUiState.Error   -> ErrorText(state.message)
    is MarketUiState.Idle    -> { }
}
3. sealed class — Navigation routes
kotlinsealed class Screen(val route: String) {
    object Home       : Screen("home")
    object Login      : Screen("login")
    object ItemDetail : Screen("item_detail/{itemId}") {
        fun createRoute(itemId: Int) = "item_detail/$itemId"
    }
}
4. sealed class — Categories
kotlinsealed class Category(val label: String) {
    object Books       : Category("Books")
    object Electronics : Category("Electronics")
    object Clothing    : Category("Clothing")
    // ...
    companion object {
        fun fromLabel(label: String): Category = when (label) {
            "Books" -> Books
            else    -> Other
        }
    }
}
5. Nullable types & Safe calls ?.
kotlin// ✅ Safe call: only access if not null
val seller = repository.getUserById(item.sellerId)
seller?.let { user ->
    Text(user.displayName)
}

// ✅ Chain of safe calls
val city = person?.address?.city
6. Elvis operator ?:
kotlin// ✅ Fallback if null
val displayName = user?.name ?: "Anonymous"
val contact     = phone ?: email ?: "No contact info"
val description = input.ifBlank { null }  // store null if blank
7. Interface — DAOs
kotlin// ✅ Room DAOs are interfaces — Room generates the implementation
@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY postedAt DESC")
    fun getAllItems(): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item): Long
}
8. when expression
kotlin// ✅ when as expression — returns a value
fun getCategoryEmoji(category: String): String = when (category) {
    "Books"       -> "📚"
    "Electronics" -> "💻"
    "Clothing"    -> "👕"
    else          -> "📦"
}
9. companion object — Singleton Database
kotlincompanion object {
    @Volatile
    private var INSTANCE: CampusMarketDatabase? = null

    fun getInstance(context: Context): CampusMarketDatabase {
        return INSTANCE ?: synchronized(this) {
            // ✅ Elvis: create only if doesn't exist
            Room.databaseBuilder(...).build().also { INSTANCE = it }
        }
    }
}

📡 Sensors Used
1. 📳 Vibration Sensor
Purpose: Provides haptic feedback for key user actions
ActionVibration PatternSend messageShort buzz (50ms)Post itemMedium buzz (100ms)Receive messageDouble pulse (80ms + 80ms)Invalid inputError pattern (3 short bursts)
kotlinclass VibrationManager(context: Context) {
    fun vibrateOnMessageSent()    { vibrate(50L) }
    fun vibrateOnItemPosted()     { vibrate(100L) }
    fun vibrateOnMessageReceived(){ vibratePattern(longArrayOf(0, 80, 100, 80)) }
}
How to test: Send a message in Chat screen — phone vibrates on send.

2. 🔄 Accelerometer Sensor — Shake to Refresh
Purpose: Detects phone shake to refresh the home feed
kotlinclass ShakeDetector(context: Context, private val onShake: () -> Unit) {
    private val SHAKE_THRESHOLD = 12f  // minimum force

    // Detects shake when acceleration > threshold
    // Fires onShake() callback with 1 second cooldown
}
How to test on emulator:

Open Extended Controls (Ctrl+Shift+X)
Go to Virtual Sensors
Move X slider rapidly → "🔄 Refreshed by shake!" banner appears

How to test on real phone: Physically shake the phone while on Home screen.

📖 Code Documentation
Data Layer
FileResponsibilityItem.ktItem entity + Category sealed classUser.ktUser entity with displayName + contactInfo computed propertiesMessage.ktMessage entity with formattedTime() helperDaos.ktItemDao, UserDao, MessageDao — all DB queriesCampusMarketDatabase.ktRoom DB singleton, version 2, with migrationCampusMarketRepository.ktSingle source of truth for all dataSessionManager.ktSharedPreferences — keeps user logged inVibrationManager.ktHaptic feedback for user actionsShakeDetector.ktAccelerometer-based shake detection
ViewModel Layer
FileResponsibilityMarketViewModel.ktItems, search, chat, listings logicAuthViewModel.ktLogin, register, logout, sessionUiState.ktMarketUiState, ItemDetailUiState, ChatUiState sealed classes
UI Layer
FileScreenAuthScreens.ktLogin + Register screensHomeScreen.ktHome feed with shake-to-refreshItemDetailScreen.ktItem detail + contact sellerOtherScreens.ktPostItem, Search, MyListings, ChatProfileScreen.ktUser profile + logoutNavigation.ktNavHost + bottom navigation + ConversationsScreen

⚙️ Setup Instructions
Prerequisites
ToolVersionAndroid StudioHedgehog / Iguana or laterKotlin2.0.0Min SDKAPI 24 (Android 7.0)Target SDKAPI 35JDK11Gradle8.8AGP8.7.0-alpha02
Dependencies Added
kotlin// Room — local database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
Steps to Run
1. Clone the repository
bashgit clone https://github.com/Brondy480/Grade-calculator-KOTLIN-.git
cd Grade-calculator-KOTLIN-/Project/CampusMarket
2. Open in Android Studio
File → Open → navigate to Project/CampusMarket
3. Sync Gradle
Click "Sync Now" in the yellow banner
Wait for BUILD SUCCESSFUL
4. Run the app
Connect a device or start an emulator (API 30 recommended)
Press ▶️ Run (Shift+F10)
5. Demo accounts
Email:    alice@ictuniversity.cm  |  Password: password123
Email:    bob@ictuniversity.cm    |  Password: password123

📱 User Guide
Getting Started
Register a new account:

Open the app → tap "Register"
Enter your name, email, and password
Phone number is optional
Tap "Create Account" → you're logged in!

Login with existing account:

Enter your email and password
Tap "Login"
Use demo accounts to test: alice@ictuniversity.cm / password123


Buying
Browse items:

Home screen shows all available listings in a grid
Tap a category tab to filter by type
Tap the 🔍 icon to search by keyword

View item details:

Tap any item card to see full details
See price, description, category and seller info
Tap "Contact Seller" to open a chat

Chat with seller:

Type your message and tap Send
Phone vibrates on each sent message
Check all your conversations in the Messages tab


Selling
Post an item:

Tap "Post" in the bottom navigation
Enter title, price, category
Description is optional
Tap "Post Item" — phone vibrates on success!

Manage your listings:

Tap "My Items" in the bottom navigation
See all items you've posted
Tap "View" to see details
Tap 🗑️ to delete a listing


Sensor Features
FeatureHow to use📳 VibrationAutomatic — feel it when sending messages or posting items🔄 Shake to refreshShake your phone on the Home screen to reload listings

📁 Project Structure
Project/
├── README.md                          ← this file
├── screenshots/                       ← add your screenshots here
└── CampusMarket/
    └── app/src/main/java/
        └── com/example/campusmarket/
            ├── MainActivity.kt
            ├── data/
            │   ├── model/
            │   │   ├── Item.kt
            │   │   ├── User.kt
            │   │   └── Message.kt
            │   ├── dao/
            │   │   └── Daos.kt
            │   ├── database/
            │   │   └── CampusMarketDatabase.kt
            │   ├── repository/
            │   │   └── CampusMarketRepository.kt
            │   ├── SessionManager.kt
            │   ├── VibrationManager.kt
            │   └── ShakeDetector.kt
            ├── viewmodel/
            │   ├── MarketViewModel.kt
            │   ├── AuthViewModel.kt
            │   └── UiState.kt
            └── ui/
                ├── screens/
                │   ├── AuthScreens.kt
                │   ├── HomeScreen.kt
                │   ├── ItemDetailScreen.kt
                │   ├── OtherScreens.kt
                │   ├── ProfileScreen.kt
                │   └── Navigation.kt
                └── theme/
                    └── Theme.kt

👥 Team
MemberRoleRepositoryBrondy NoumsiKotlin / AndroidGrade-calculator-KOTLIN-Eskil16Dart / Flutter(partner repo link)

📄 License
This project is submitted as part of SE 3242 – Android Application Development at ICT University, Cameroon.
