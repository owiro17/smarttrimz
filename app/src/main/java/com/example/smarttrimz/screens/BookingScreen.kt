package com.example.smarttrimz.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smarttrimz.data.Booking
import com.example.smarttrimz.data.BookingStatus
import com.example.smarttrimz.data.placeholderPastBookings
import com.example.smarttrimz.data.placeholderUpcomingBookings
import com.example.smarttrimz.ui.theme.SmartTrimzGreen
import com.example.smarttrimz.ui.theme.SmartTrimzRed
import com.example.smarttrimz.ui.theme.SmartTrimzTheme

@Composable
fun BookingsScreen(
    onRescheduleClick: (String) -> Unit = {}, // Passes the booking ID
    onCancelClick: (String) -> Unit = {}      // Passes the booking ID
) {
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
        item {
            Text(
                text = "Upcoming",
                style = MaterialTheme.typography.titleLarge
            )
        }
        items(placeholderUpcomingBookings) { booking ->
            AppointmentCard(
                booking = booking,
                onRescheduleClick = onRescheduleClick,
                onCancelClick = onCancelClick
            )
        }

        // --- Past Appointments Section ---
        item {
            Text(
                text = "Past Appointments",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        items(placeholderPastBookings) { booking ->
            AppointmentCard(
                booking = booking,
                onRescheduleClick = onRescheduleClick,
                onCancelClick = onCancelClick
            )
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
                if (booking.status == BookingStatus.UPCOMING) {
                    IconButton(onClick = { /* TODO: Show menu */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
                    }
                } else if (booking.status == BookingStatus.COMPLETED) {
                    Text(
                        text = "Completed",
                        color = SmartTrimzGreen,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
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
                        text = booking.date,
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
                        text = booking.time,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // --- Bottom Row: Buttons (Only for Upcoming) ---
            if (booking.status == BookingStatus.UPCOMING) {
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
        BookingsScreen()
    }
}