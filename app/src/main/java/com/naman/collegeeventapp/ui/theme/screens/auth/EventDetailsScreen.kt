package com.naman.collegeeventapp.ui.theme.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.naman.collegeeventapp.model.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    event: Event,
    navController: NavController,
    onRegister: () -> Unit
) {
    val context = LocalContext.current
    var selectedRole by remember { mutableStateOf("Participant") }
    var course by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(event.title, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF003B6D), // Dark blue to match HomeScreen
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Make screen scrollable
        ) {
            // Event Info Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Date",
                            tint = Color(0xFF2196F3)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(event.date, style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color(0xFFF44336)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(event.location, style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Registration Form Section
            Text("Your Details", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = course,
                onValueChange = { course = it },
                label = { Text("Course (e.g., B.Tech CSE)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = year,
                onValueChange = { year = it },
                label = { Text("Year (e.g., 2nd Year)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Register as:", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedRole == "Participant",
                    onClick = { selectedRole = "Participant" }
                )
                Text("Participant")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = selectedRole == "Audience",
                    onClick = { selectedRole = "Audience" }
                )
                Text("Audience")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (course.isBlank() || year.isBlank()) {
                        Toast.makeText(context, "Please fill in all details", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isRegistering = true
                    val db = FirebaseFirestore.getInstance()
                    db.collection("registrations")
                        .whereEqualTo("eventId", event.title)
                        .whereEqualTo("userEmail", user?.email)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                Toast.makeText(context, "You're already registered for this event", Toast.LENGTH_SHORT).show()
                                isRegistering = false
                            } else {
                                val registrationData = mapOf(
                                    "eventId" to event.title,
                                    "userEmail" to user?.email,
                                    "userName" to user?.displayName,
                                    "role" to selectedRole,
                                    "course" to course,
                                    "year" to year,
                                    "timestamp" to System.currentTimeMillis()
                                )

                                db.collection("registrations")
                                    .add(registrationData)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Registered successfully!", Toast.LENGTH_SHORT).show()
                                        onRegister() // Call the lambda after successful registration.
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                                        isRegistering = false
                                    }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to check registration status", Toast.LENGTH_SHORT).show()
                            isRegistering = false
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isRegistering, // Disable button while processing
                // UPDATED: Added button color for consistency
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                if (isRegistering) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Register Now")
                }
            }
        }
    }
}
