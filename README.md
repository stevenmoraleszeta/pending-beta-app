# Mis Pedidos - Order Management Android App

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.10-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API%2028%2B-brightgreen.svg?style=flat&logo=android)](https://developer.android.com)
[![Firebase](https://img.shields.io/badge/Firebase-32.7.0-orange.svg?style=flat&logo=firebase)](https://firebase.google.com)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg?style=flat)](LICENSE)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-28-green.svg?style=flat)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-34-green.svg?style=flat)](https://developer.android.com)

A modern Android application built with Kotlin for managing orders, clients, and delivery tracking. The app provides a comprehensive solution for order management with Firebase integration for authentication and cloud data storage.

## ✨ Introduction

**Mis Pedidos** (My Orders) is a full-featured order management application designed to help businesses track and manage their orders efficiently. The app enables users to create, update, and monitor orders with detailed information including client data, delivery dates, payment tracking, and order status management.

### Key Features

- 🔐 **Firebase Authentication** - Secure user authentication with email and password
- 📦 **Order Management** - Create, edit, and delete orders with comprehensive details
- 👥 **Client Management** - Store and manage client information
- 📅 **Date Tracking** - Track order dates and delivery dates with date pickers
- 💰 **Payment Tracking** - Monitor total amounts, paid amounts, and pending payments
- ✅ **Status Management** - Mark orders as active, pending, or completed
- 💬 **WhatsApp Integration** - Quick contact with clients via WhatsApp
- 👤 **User Profiles** - Manage user information and company details
- 🔄 **Real-time Sync** - Automatic data synchronization with Firebase Firestore
- 📱 **Modern UI** - Material Design components with View Binding

## 🚀 Technologies Used

### Core Technologies

- **Kotlin** - Primary programming language
- **Android SDK** - Native Android development
- **Gradle (Kotlin DSL)** - Build system and dependency management

### Libraries & Frameworks

- **Firebase Authentication** - User authentication and session management
- **Firebase Firestore** - Cloud database for real-time data storage
- **Material Design Components** - Modern UI components
- **AndroidX Libraries** - Core Android extension libraries
  - `androidx.core:core-ktx` - Kotlin extensions for Android
  - `androidx.appcompat:appcompat` - Backward compatibility
  - `androidx.constraintlayout` - Flexible layout system
  - `androidx.lifecycle` - Lifecycle-aware components
  - `androidx.recyclerview` - Efficient list rendering

### Development Tools

- **View Binding** - Type-safe view references
- **Gradle Plugin** - Android build tools
- **ProGuard** - Code obfuscation and optimization

## ⚙️ Installation

### Prerequisites

- **Android Studio** - Hedgehog (2023.1.1) or later
- **JDK** - Java Development Kit 8 or higher
- **Android SDK** - API Level 28 (Android 9.0) or higher
- **Firebase Project** - Active Firebase project with Firestore and Authentication enabled
- **Google Services** - `google-services.json` file from Firebase Console

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/pending-beta-app.git
   cd pending-beta-app
   ```

2. **Firebase Configuration**
   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Enable **Authentication** with Email/Password provider
   - Enable **Cloud Firestore** database
   - Download `google-services.json` from Firebase Console
   - Place `google-services.json` in the `app/` directory

3. **Configure Firebase Rules**

   Set up Firestore security rules to allow authenticated users to read/write their own data:

   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /orders/{orderId} {
         allow read, write: if request.auth != null && 
           resource.data.creator_mail == request.auth.token.email;
       }
       match /users/{userId} {
         allow read, write: if request.auth != null && 
           request.auth.token.email == userId;
       }
     }
   }
   ```

4. **Build the Project**
   - Open the project in Android Studio
   - Sync Gradle files
   - Build the project (Build > Make Project)
   - Run on an emulator or physical device

5. **Run the Application**
   - Click Run button or press `Shift + F10`
   - Select a device or emulator
   - The app will install and launch automatically

### Environment Variables

For local development, you may need to configure the following:

- **Firebase Configuration**: `google-services.json` (already included in `app/` directory)
- **Build Configuration**: Check `gradle.properties` for build settings
- **API Keys**: All keys are managed through Firebase Console and `google-services.json`

> **Note**: The `google-services.json` file contains sensitive configuration. Never commit this file if it contains production keys. Use different Firebase projects for development and production.

## 🧩 Project Structure

```
pending-beta-app/
├── app/
│   ├── build.gradle.kts          # App-level build configuration
│   ├── google-services.json      # Firebase configuration
│   ├── proguard-rules.pro        # ProGuard rules
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/innnova/pendingbetaapp/
│       │   │   ├── AuthActivity.kt        # Authentication screen
│       │   │   ├── MainActivity.kt        # Main orders list screen
│       │   │   ├── OrderActivity.kt       # Order creation/editing screen
│       │   │   ├── UserActivity.kt        # User profile screen
│       │   │   └── CustomAdapter.kt       # RecyclerView adapter
│       │   └── res/
│       │       ├── layout/                # XML layouts
│       │       ├── values/                # Strings, colors, themes
│       │       └── drawable/              # Icons and images
│       ├── test/                          # Unit tests
│       └── androidTest/                   # Instrumented tests
├── build.gradle.kts              # Project-level build configuration
├── settings.gradle.kts           # Project settings
├── gradle.properties             # Gradle properties
└── gradlew                       # Gradle wrapper
```

### Key Components

- **AuthActivity** - Handles user authentication (login/signup)
- **MainActivity** - Displays list of orders with filtering options
- **OrderActivity** - Create and edit orders with full details
- **UserActivity** - Manage user profile and company information
- **CustomAdapter** - RecyclerView adapter for displaying orders


## 📱 Usage

### Getting Started

1. **Sign Up / Login**
   - Launch the app
   - Create a new account or login with existing credentials
   - The app uses Firebase Authentication for secure access

2. **Create an Order**
   - Tap the "+" button on the main screen
   - Fill in order details:
     - Order name
     - Product information
     - Client name and contact
     - Order date and delivery date
     - Total amount, paid amount
     - Additional details
   - Tap "Save" to create the order

3. **Manage Orders**
   - View all orders on the main screen
   - Filter by status (All Orders / Pending Orders)
   - Tap an order to view/edit details
   - Tap the check button to toggle order status
   - Use the menu to delete orders

4. **Contact Clients**
   - Open an order
   - Tap the WhatsApp button to contact the client
   - The app will open WhatsApp with the client's phone number

5. **User Profile**
   - Tap on your email/name on the main screen
   - Update your name and company information
   - Save changes to update your profile

## 🚢 Deployment

### Build Release APK

1. **Generate Signed APK**

   ```bash
   ./gradlew assembleRelease
   ```

   The APK will be located at `app/build/outputs/apk/release/app-release.apk`

2. **Generate App Bundle (AAB)**

   ```bash
   ./gradlew bundleRelease
   ```

   The AAB will be located at `app/build/outputs/bundle/release/app-release.aab`

### Deployment Options

#### Google Play Store

- Create a developer account at [Google Play Console](https://play.google.com/console)
- Prepare app assets (screenshots, icons, descriptions)
- Upload the AAB file
- Complete store listing and publish

#### Firebase App Distribution

- Set up Firebase App Distribution in Firebase Console
- Upload APK/AAB for testing
- Distribute to testers via email

#### Internal Distribution

- Use enterprise distribution methods
- Deploy via MDM (Mobile Device Management) solutions
- Share APK directly (not recommended for production)

### Environment Setup

- **Development**: Use Firebase development project
- **Staging**: Use Firebase staging project with test data
- **Production**: Use Firebase production project with optimized settings

### Best Practices

- Enable ProGuard/R8 for release builds
- Configure proper Firebase security rules
- Use different Firebase projects for different environments
- Implement crash reporting (Firebase Crashlytics)
- Add analytics tracking (Firebase Analytics)
- Set up CI/CD pipeline for automated builds


## 📜 License

This project is proprietary software. All rights reserved.

**Copyright (c) 2024 Steven Morales Fallas**

Redistribution, modification, reproduction, sublicensing, or any form of transaction (including commercial, educational, or promotional use) involving this repository, its source code, or derived works is strictly prohibited without the explicit and personal written authorization of the Lead Developer, Steven Morales Fallas.

Unauthorized commercial use, resale, or licensing of this repository or its contents is strictly forbidden and will be subject to applicable legal action.

For licensing inquiries, please contact: [Your Contact Information]

## 👨‍💻 Author

**Steven Morales Fallas**

- Full Stack Developer
- Specialized in Android development with Kotlin
- Firebase and cloud services integration


## 🤝 Contributing

This is a proprietary project. Contributions are not accepted without prior written authorization from the project owner.

## 📞 Support

For support, feature requests, or inquiries, please contact the project maintainer.

## 🔒 Security

- All sensitive data is stored securely in Firebase
- Authentication is handled through Firebase Authentication
- Firestore security rules ensure data isolation between users
- Never commit `google-services.json` with production keys to public repositories

## 📝 Changelog

### Version 1.0 (Current)
- Initial release
- Firebase Authentication integration
- Order management functionality
- Client management
- Payment tracking
- WhatsApp integration
- User profile management

**Note**: This application requires an active internet connection to function properly, as it relies on Firebase services for authentication and data storage.

