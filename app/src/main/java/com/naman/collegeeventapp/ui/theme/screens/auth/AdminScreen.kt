package com.naman.collegeeventapp.ui.theme.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.naman.collegeeventapp.model.Event
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    var userRole by remember { mutableStateOf<String?>(null) }

    // Data states
    var registrations by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var users by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Filter states
    var eventFilter by remember { mutableStateOf("") }
    var roleFilter by remember { mutableStateOf("") }

    // Section toggles (reverting to the original, stable method)
    var showRegistrations by remember { mutableStateOf(true) }
    var showUsers by remember { mutableStateOf(false) }
    var showEvents by remember { mutableStateOf(false) }

    // Add/Edit event form
    var editMode by remember { mutableStateOf(false) }
    var editingEventId by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(currentUser) {
        loading = true
        try {
            val uid = currentUser?.uid ?: return@LaunchedEffect
            val doc = firestore.collection("users").document(uid).get().await()
            userRole = doc.getString("role")

            if (userRole == "admin") {
                // Using the simplest, most stable data fetching method
                val regSnap = firestore.collection("registrations").get().await()
                registrations = regSnap.documents.mapNotNull { it.data }

                val userSnap = firestore.collection("users").get().await()
                users = userSnap.documents.mapNotNull { it.data }

                val eventSnap = firestore.collection("events").get().await()
                events = eventSnap.documents.mapNotNull { document ->
                    val data = document.data
                    if (data != null) {
                        Event(
                            id = document.id,
                            title = data["title"]?.toString() ?: "",
                            date = data["date"]?.toString() ?: "",
                            location = data["location"]?.toString() ?: "",
                            description = data["description"]?.toString() ?: ""
                        )
                    } else null
                }
            }
        } catch (e: Exception) {
            error = "Error loading admin data: ${e.message}"
        }
        loading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF003B6D), // Dark blue to match other screens
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                if (loading) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    return@item
                }

                if (userRole != "admin") {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Access Denied.", color = MaterialTheme.colorScheme.error)
                    }
                    return@item
                }

                // Section Toggle Buttons from original stable code
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { showRegistrations = true; showUsers = false; showEvents = false }) { Text("Registrations") }
                    Button(onClick = { showRegistrations = false; showUsers = true; showEvents = false }) { Text("Users") }
                    Button(onClick = { showRegistrations = false; showUsers = false; showEvents = true }) { Text("Events") }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // REGISTRATIONS SECTION
            if (showRegistrations) {
                item {
                    Text("Filter Registrations", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = eventFilter, onValueChange = { eventFilter = it }, label = { Text("Filter by Event") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = roleFilter, onValueChange = { roleFilter = it }, label = { Text("Filter by Role") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                }
                val filteredRegs = registrations.filter {
                    (eventFilter.isBlank() || (it["eventId"]?.toString() ?: "").contains(eventFilter, true)) &&
                            (roleFilter.isBlank() || (it["role"]?.toString() ?: "").contains(roleFilter, true))
                }
                items(filteredRegs) { reg ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Email: ${reg["userEmail"]?.toString() ?: "N/A"}", fontWeight = FontWeight.SemiBold)
                            Text("Event: ${reg["eventId"]?.toString() ?: "N/A"}")
                            Text("Role: ${reg["role"]?.toString() ?: "N/A"}")
                            Text("Course: ${reg["course"]?.toString() ?: "N/A"}, Year: ${reg["year"]?.toString() ?: "N/A"}")
                        }
                    }
                }
            }

            // USERS SECTION
            if (showUsers) {
                items(users) { user ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Name: ${user["name"]?.toString() ?: "N/A"}", fontWeight = FontWeight.SemiBold)
                            Text("Email: ${user["email"]?.toString() ?: user["emial"]?.toString() ?: "N/A"}")
                            Text("Enrollment: ${user["enrollmentNumber"]?.toString() ?: "N/A"}")
                            Text("Role: ${user["role"]?.toString() ?: "N/A"}")
                        }
                    }
                }
            }

            // MANAGE EVENTS SECTION
            if (showEvents) {
                item {
                    Text(if (editMode) "Edit Event" else "Add New Event", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            if (title.isNotBlank() && date.isNotBlank()) {
                                val eventMap = mapOf("title" to title, "date" to date, "location" to location, "description" to description)
                                if (editMode && editingEventId != null) {
                                    firestore.collection("events").document(editingEventId!!).set(eventMap)
                                } else {
                                    firestore.collection("events").add(eventMap)
                                }
                                title = ""; date = ""; location = ""; description = ""; editMode = false; editingEventId = null
                            }
                        }) { Text(if (editMode) "Update" else "Add") }
                        if (editMode) {
                            OutlinedButton(onClick = { title = ""; date = ""; location = ""; description = ""; editMode = false; editingEventId = null }) { Text("Cancel") }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Existing Events", style = MaterialTheme.typography.titleLarge)
                }
                items(events) { event ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(event.title, fontWeight = FontWeight.SemiBold)
                            Text("Date: ${event.date}")
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                TextButton(onClick = {
                                    title = event.title; date = event.date; location = event.location; description = event.description
                                    editingEventId = event.id; editMode = true
                                }) { Text("Edit") }
                                TextButton(onClick = { event.id?.let { firestore.collection("events").document(it).delete() } }) {
                                    Text("Delete", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
