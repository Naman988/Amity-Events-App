
# 📱 College Event Management App

A Kotlin-based Android application built with **Jetpack Compose** and **Firebase** to simplify college event management.  
This app supports user authentication, event registration, role-based access, and an admin panel with full CRUD functionality.

---

## 🔗 Links

- **LinkedIn**: [namangupta14](https://www.linkedin.com/in/namangupta14)
- **GitHub**: [Naman988](https://github.com/Naman988)

---

## 🚀 Features

- 📧 Email/Password Authentication (Firebase Auth)
- 👨‍🎓 Register as Participant or Audience with academic details
- 📋 View and register for events
- 🔒 Role-based access (User/Admin)
- 🛠 Admin Panel for event & user management (CRUD operations)
- 🔍 Filter registrations by event, role, course, year
- ☁️ Firebase Firestore integration

---

## 📦 Project Structure

```
📦AmityEventsApp
 ┣ 📂data
 ┃ ┗ FirestoreRepository.kt
 ┣ 📂model
 ┃ ┗ Event.kt
 ┣ 📂ui
 ┃ ┣ 📂theme
 ┃ ┣ 📂screens
 ┃ ┃ ┗ 📂auth
 ┃ ┃ ┃ ┣ AdminScreen.kt
 ┃ ┃ ┃ ┣ EventDetailsScreen.kt
 ┃ ┃ ┃ ┣ HomeScreen.kt
 ┃ ┃ ┃ ┣ LoginScreen.kt
 ┃ ┃ ┃ ┣ ProfileScreen.kt
 ┃ ┃ ┃ ┗ SignupScreen.kt
 ┣ 📂viewmodel
 ┃ ┣ AuthViewModel.kt
 ┃ ┗ EventViewModel.kt
 ┗MainActivity.kt

```

---

## 🔐 Admin Access

Only verified users with the `role` field set to `admin` in Firestore can access the Admin Panel.  
No direct admin registration is allowed via UI to ensure secure access.

---

## 📈 Roadmap

- [x] Firebase Authentication
- [x] User Registration Form
- [x] View & Register for Events
- [x] Admin Role-based Visibility
- [x] Admin Event CRUD Features
- [x] Filter Registrations by Role/Course/Year
- [ ] Push Notifications (Coming Soon)
- [ ] In-App Event Analytics (Coming Soon)

---

## 🧪 Tech Stack

- **Kotlin** + **Jetpack Compose**
- **Firebase**: Auth, Firestore, Storage
- **MVVM Architecture**
- **Material Design 3**

---

## 🤝 Contributions

Contributions, feature requests, and suggestions are welcome!  
Fork the repo and submit a pull request with meaningful commits.

---

## 📬 Contact

- Email: [vanshg.1411@gmail.com](mailto:vanshg.1411@gmail.com)
- LinkedIn: [namangupta14](https://www.linkedin.com/in/namangupta14)
