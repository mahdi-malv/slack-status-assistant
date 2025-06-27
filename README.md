# Slack status assistant

> **Why is this created?**
> 
> At the moment I live 20 mins away from my office.
> I want to be able to set my slack status to "Working from home" when I'm in the street,
> so when I'm walking, I can set my status to help my coworkers see how close I am.
> 
> In the future, there could be some location detection and automatic status change. Who knows :)?

|Quick actions|Location-wise|
|--|--|
|<img src="https://github.com/mahdi-malv/slack-status-assistant/assets/21319971/a05eb55d-048f-43bf-8a2a-69e44e002024" width="200" />|<img src="https://github.com/mahdi-malv/slack-status-assistant/assets/21319971/60571bec-f67a-474e-9da7-a906333342c0" width="200" />|


The project doesn't intend to follow any "clear architecture" patterns or ...,
instead I wanted to try new things and have some fun with functional approach in the project.  


**Features**:
- Multiple commands for different possible situations
- Console log

## How to use
- Clone this repo 
- Set your own slack token in your `local.properties` file
   ```yml
   userToken=YOUR_SLACK_TOKEN
   ```
- Run the app

## Future TODO
- [ ] Add snooze functionality
- [ ] Add set presence functionality
- [ ] Load user token in runtime
- [x] Update future automated setter tasks

## Codebase Overview

This Android application is built using Kotlin and leverages modern Android development practices. It allows users to quickly update their Slack status either through predefined commands, custom inputs, or automatically based on their location relative to a pre-configured office.

The core components of the application are:

*   **Application Class (`SlackAssistantApp.kt`):** Initializes the application and sets up Hilt for dependency injection.
*   **Main Activity (`MainActivity.kt`):** Serves as the entry point for the UI, built with Jetpack Compose. It handles navigation between the home screen and the location settings screen. It also displays the current Slack status in the top app bar.
*   **Slack API Interaction (`api/`):**
    *   `SlackAPIClient.kt`: A Ktor-based HTTP client responsible for all communication with the Slack API (e.g., setting status, fetching user info). It's configured in `AppModule.kt`.
    *   API data models are located in `api/model/`.
*   **Location Features (`features/location/`):**
    *   `RequestService.kt`: A foreground Android Service that periodically fetches the user's location using the `FusedLocationProviderClient`.
    *   It calculates the distance to a saved office location and updates the Slack status accordingly (e.g., "Commuting, Xm away").
    *   It uses local broadcasts to communicate its state and location updates within the app.
*   **Dependency Injection (`di/AppModule.kt`):** Hilt is used for managing dependencies throughout the application. `AppModule.kt` provides instances of key classes like the Ktor `HttpClient`, `SharedPreferences`, and `FusedLocationProviderClient`.
*   **UI (`ui/`):**
    *   Built with Jetpack Compose.
    *   `screens/`: Contains composable functions for different app screens (e.g., `HomeScreen.kt`, `LocationScreen.kt`). ViewModels (like `HomeViewModel.kt`) are used to manage UI state and logic.
    *   `components/`: Holds reusable UI elements used across different screens.
    *   `Command.kt`: Defines the structure for quick and custom status commands.
*   **Persistence (`persistance/LocalStorage.kt`):** Uses `SharedPreferences` for storing simple data like the office location or user preferences.
*   **Utilities (`utils/`):** Contains helper classes for various tasks like logging, permission handling, Slack emoji definitions, and text manipulation.

The project mentions an intent to explore functional programming approaches, which can be seen in the use of immutable states, side-effect handling in ViewModels, and potentially in other areas of the codebase.

## Project Structure

Here's a high-level overview of the important directories within the `app/src/main/java/dk/malv/slack/assistant/` path:

*   **`MainActivity.kt`**: The main entry point of the application's UI.
*   **`SlackAssistantApp.kt`**: The main application class.
*   **`api/`**: Contains code related to Slack API communication.
    *   `client/`: Holds the Ktor HTTP client (`SlackAPIClient.kt`).
    *   `model/`: Data classes for API requests and responses.
*   **`di/`**: Dependency injection modules (using Hilt).
*   **`features/`**: Houses feature-specific logic.
    *   `location/`: Code for location tracking and automatic status updates (`RequestService.kt`, `DistanceController.kt`).
*   **`persistance/`**: For data storage (e.g., `LocalStorage.kt` using SharedPreferences).
*   **`receiver/`**: Components for internal app communication (e.g., `LocalBroadcast.kt`).
*   **`ui/`**: Contains all UI-related code, built with Jetpack Compose.
    *   `components/`: Reusable UI building blocks.
    *   `screens/`: Composable functions representing different application screens and their ViewModels.
    *   `theme/`: Styling and theming for the app.
*   **`utils/`**: Utility classes and extension functions.
