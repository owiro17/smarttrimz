package com.example.smarttrimz.data

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Booking(
    @DocumentId val id: String = "",
    val userId: String = "",
    val barberId: String = "",
    val barberName: String = "",
    val service: String = "",
    val dateTime: Date? = null,
    val status: String = "UPCOMING"
)
