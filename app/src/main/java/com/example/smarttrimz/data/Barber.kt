package com.example.smarttrimz.data

import com.google.firebase.firestore.DocumentId

data class Barber(
    @DocumentId val id: String = "",
    val name: String = "",
    val specialties: List<String> = emptyList(),
    val rating: Double = 0.0,
    val imageUrl: String? = null
)
