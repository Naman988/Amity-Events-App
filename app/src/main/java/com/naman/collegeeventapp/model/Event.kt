package com.naman.collegeeventapp.model

import kotlinx.serialization.Serializable

data class Event(
    val title: String = "",
    val date: String = "",
    val location: String = "",
    val description: String = "",
    val id: String? = null,
)
