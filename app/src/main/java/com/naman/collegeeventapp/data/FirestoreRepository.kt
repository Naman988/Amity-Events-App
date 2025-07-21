package com.naman.collegeeventapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.naman.collegeeventapp.model.Event
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    // Existing function to get events
    suspend fun getEvents(): List<Event> {
        return try {
            val snapshot = db.collection("events").get().await()
            snapshot.documents.mapNotNull { doc ->
                val event = doc.toObject(Event::class.java)
                event?.copy(id = doc.id) // 👈 attach Firestore doc ID to Event object
            }
        } catch (e: Exception) {
            emptyList()
        }
    }


    // ✅ New function to get the role of a user by their UID
    suspend fun getUserRole(uid: String): String? {
        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            snapshot.getString("role")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteEvent(eventId: String) {
        try {
            db.collection("events").document(eventId).delete().await()
        } catch (e: Exception) {
            // You can log this if needed
        }
    }

}
