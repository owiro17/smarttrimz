package com.example.smarttrimz.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarttrimz.data.Booking
import com.example.smarttrimz.ui.theme.SmartTrimzGreen
import com.example.smarttrimz.ui.theme.SmartTrimzRed
import com.example.smarttrimz.ui.theme.SmartTrimzTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- ViewModel ---
class BookingsViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val userId = auth.currentUser?.uid

    private val _upcomingBookings = MutableStateFlow<List<Booking>>(emptyList())
    val upcomingBookings: StateFlow<List<Booking>> = _upcomingBookings

    private val _pastBookings = MutableStateFlow<List<Booking>>(emptyList())
    val pastBookings: StateFlow<List<Booking>> = _pastBookings

    init {
        fetchUserBookings()
    }

    private fun fetchUserBookings() {
        if (userId == null) {
            Log.e("BookingsViewModel", "User is not logged in.")
            return
        }

        db.collection("bookings")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("BookingsViewModel", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val allBookings = snapshots.toObjects<Booking>()
                    val now = Date()

                    // Partition into true upcoming bookings and everything else
                    val (upcoming, other) = allBookings.partition {
                        it.status.equals("upcoming", ignoreCase = true) && it.dateTime?.after(now) == true
                    }

                    // For display, treat past "upcoming" bookings as "Completed"
                    val past = other.map { booking ->
                        if (booking.dateTime?.before(now) == true && booking.status.equals("upcoming", ignoreCase = true)) {
                            booking.copy(status = "Completed")
                        } else {
                            booking
                        }
                    }

                    _upcomingBookings.value = upcoming.sortedBy { it.dateTime }
                    _pastBookings.value = past.sortedByDescending { it.dateTime }
                }
            }
    }

    fun cancelBooking(bookingId: String) {
        if (bookingId.isBlank()) {
            Log.e("BookingsViewModel", "Booking ID is blank, cannot cancel.")
            return
        }
        viewModelScope.launch {
            db.collection("bookings").document(bookingId)
                .update("status", "cancelled")
                .addOnSuccessListener {
                    Log.d("BookingsViewModel", "Booking $bookingId successfully cancelled.")
                    // The snapshot listener will automatically refresh the lists
                }
                .addOnFailureListener { e ->
                    Log.e("BookingsViewModel", "Error cancelling booking $bookingId", e)
                }
        }
    }
}


@Composable
fun BookingsScreen(
    viewModel: BookingsViewModel = viewModel(),
    onRescheduleClick: (String) -> Unit = {},
) {
    val upcomingBookings by viewModel.upcomingBookings.collectAsState()
    val pastBookings by viewModel.pastBookings.collectAsState()

    // LazyColumn is the best way to show a scrollable list
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header ---
        item {
            Text(
                text = "My Bookings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        // --- Upcoming Section ---
        if (upcomingBookings.isNotEmpty()) {
            item {
                Text(
                    text = "Upcoming",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            items(upcomingBookings) { booking ->
                AppointmentCard(
                    booking = booking,
                    onRescheduleClick = onRescheduleClick,
                    onCancelClick = { viewModel.cancelBooking(booking.id) }
                )
            }
        }


        // --- Past Appointments Section ---
        if (pastBookings.isNotEmpty()) {
            item {
                Text(
                    text = "Past Appointments",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            items(pastBookings) { booking ->
                AppointmentCard(
                    booking = booking,
                    onRescheduleClick = {},
                    onCancelClick = {} // No actions for past bookings
                )
            }
        }

        // --- Empty State ---
        if (upcomingBookings.isEmpty() && pastBookings.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No bookings yet.", style = MaterialTheme.typography.bodyLarge)
                    Text("Book an appointment to see it here.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }


        // Add spacer at the bottom
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- Reusable Appointment Card ---
@Composable
fun AppointmentCard(
    booking: Booking,
    onRescheduleClick: (String) -> Unit,
    onCancelClick: (String) -> Unit
) {
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val dateString = dateFormatter.format(booking.dateTime)
    val timeString = timeFormatter.format(booking.dateTime)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // --- Top Row: Barber Info ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Barber",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = booking.barberName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = booking.service,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                // --- Status Text ---
                when {
                    booking.status.equals("upcoming", ignoreCase = true) -> {
                        // For upcoming, we show buttons, so no text here, but you could add one
                    }
                    booking.status.equals("completed", ignoreCase = true) -> {
                        Text(
                            text = "Completed",
                            color = SmartTrimzGreen,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    booking.status.equals("cancelled", ignoreCase = true) -> {
                        Text(
                            text = "Cancelled",
                            color = SmartTrimzRed,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Middle Row: Date & Time ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = "Date",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Time",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = timeString,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // --- Bottom Row: Buttons (Only for Upcoming) ---
            if (booking.status.equals("upcoming", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onRescheduleClick(booking.id) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reschedule")
                    }
                    TextButton(
                        onClick = { onCancelClick(booking.id) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColors(contentColor = SmartTrimzRed)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BookingsScreenPreview() {
    SmartTrimzTheme {
        // We can't easily preview the ViewModel, so we create a dummy screen
        val sampleUpcoming = listOf(
            Booking(id = "1", userId = "", barberId = "", barberName = "John Doe", service = "Haircut", dateTime = Date(), status = "upcoming")
        )
        val samplePast = listOf(
            Booking(id = "2", userId = "", barberId = "", barberName = "Jane Smith", service = "Beard Trim", dateTime = Date(), status = "completed"),
            Booking(id = "3", userId = "", barberId = "", barberName = "Sam Wilson", service = "Shave", dateTime = Date(), status = "cancelled")
        )
        // This preview is now static and won't reflect ViewModel logic
         LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "My Bookings",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            item {
                Text(
                    text = "Upcoming",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            items(sampleUpcoming) { booking ->
                AppointmentCard(
                    booking = booking,
                    onRescheduleClick = {},
                    onCancelClick = {}
                )
            }
            item {
                 Text(
                    text = "Past Appointments",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            items(samplePast) { booking ->
                AppointmentCard(
                    booking = booking,
                    onRescheduleClick = {},
                    onCancelClick = {}
                )
            }
        }
    }
}
