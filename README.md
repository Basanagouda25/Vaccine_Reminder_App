💉 Vaccine Reminder App (Parent–Provider System)

A real-time mobile application built with Kotlin (Jetpack Compose), Firebase, and Room Database, helping parents track their child’s vaccination schedule while allowing healthcare providers to send reminders instantly.

## 🚀 Features

👨‍👩‍👧 Parent App

- 🧒 Add & Manage Children – Register children with DOB to auto-generate vaccine timelines.

- 📅 Track Vaccine Schedule – Upcoming, Completed, and Missed vaccines.

- 🔔 Real-Time Notifications – Receive alerts sent by healthcare providers.

- 📄 PDF Reports – Download full vaccination reports anytime.

- 📚 Learn Section – Simple guides explaining vaccine importance.

- ❓ FAQ Section – Parent-friendly explanations.

- 👤 Parent Profile – View your details and linked children.

- 📦 Offline Support – Data stored locally using Room.

🏥 Provider App

- 👨‍⚕️ Provider Login & Profile

- 👶 View All Registered Children

- 📤 Send Notifications to Parents

- ⚡ Instant Delivery via Firestore real-time listeners

- 🔐 Role-based session handling

## 🛠️ Tech Stack
Language & UI

- Kotlin

- Jetpack Compose

- Material 3

- Navigation Compose

- ViewModel + StateFlow

Backend & Cloud

- Node.js (for testing server-side FCM sends)

- Firebase Authentication

- Firebase Firestore

- Firebase Cloud Messaging (FCM)

- Firestore Security Rules

- Real-time Firestore listeners (collectionGroup)

- Local Storage

Room Database

- SharedPreferences (Session Manager)

Architecture

- MVVM

- Repository Pattern

- Unidirectional Data Flow (UDF)

## 🎥 Demo Video

- https://drive.google.com/file/d/1pI9Y7Rojo2MfvurI6Mt0oJeXRrBRLvOx/view?usp=drive_link

## ⚙️ Setup Instructions

1.Clone the repository

    git clone https://github.com/Basanagouda25/Vaccine_Reminder_App.git
  
    cd Vaccine_Reminder_App
  
2. Open in Android Studio

- Use Android Studio Giraffe/Koala or above for best Jetpack Compose support.

3. Connect Firebase

- Add your google-services.json into the app/ folder

- Enable Firebase Authentication (Email/Password)

- Enable Firestore

- Enable Cloud Messaging (optional but recommended)

4. Run the App

- Launch on an emulator or physical Android device.

## ⭐ Highlights

- Fully functional Parent + Provider dual-role app

- Clean Jetpack Compose UI

- Offline + Online hybrid data model

- Real-time notifications

- PDF reports with updated vaccine status

- Smooth navigation and state management
