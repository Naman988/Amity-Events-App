package com.naman.collegeeventapp.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // ✅ Internal mutable state
    private val _isUserLoggedIn = mutableStateOf(auth.currentUser != null)
    private var _userRole by mutableStateOf<String?>(null)

    // ✅ Public getters
    val isUserLoggedIn: State<Boolean> get() = _isUserLoggedIn
    val userRole: String? get() = _userRole

    // ✅ Signup logic
    fun signUp(
        email: String,
        password: String,
        name: String,
        enrollmentNumber: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d("DEBUG", "Enrollment entered: '$enrollmentNumber'")

        if (!Regex("^A\\d{11}$").matches(enrollmentNumber.trim())) {
            Log.d("DEBUG", "Regex failed for: '${enrollmentNumber.trim()}'")
            onError("Invalid enrollment number format.")
            return
        }

        auth.createUserWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = auth.currentUser

                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }
                    user?.updateProfile(profileUpdates)

                    // ✅ Store user in Firestore with default role = "user"
                    val userData = mapOf(
                        "uid" to user?.uid,
                        "email" to user?.email,
                        "name" to name,
                        "enrollmentNumber" to enrollmentNumber,
                        "role" to "user"
                    )

                    firestore.collection("users")
                        .document(user?.uid ?: "")
                        .set(userData)

                    _isUserLoggedIn.value = true
                    onSuccess()
                } else {
                    onError(it.exception?.message ?: "Signup failed.")
                }
            }
    }

    // ✅ Login logic with role fetch
    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _isUserLoggedIn.value = true
                    fetchUserRole()  // Fetch role after login
                    onSuccess()
                } else {
                    onError(it.exception?.message ?: "Login failed.")
                }
            }
    }

    // ✅ Logout function
    fun logout() {
        auth.signOut()
        _isUserLoggedIn.value = false
        _userRole = null
    }

    // ✅ Fetch user's role from Firestore
    fun fetchUserRole(onResult: (String?) -> Unit = {}) {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                val role = document.getString("role")
                _userRole = role
                Log.d("ROLE", "Fetched role: $role")
                onResult(role)
            }
            .addOnFailureListener {
                Log.e("ROLE", "Failed to fetch role", it)
                _userRole = null
                onResult(null)
            }
    }
}
