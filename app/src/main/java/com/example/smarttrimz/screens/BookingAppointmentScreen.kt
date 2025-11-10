package com.example.smarttrimz.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smarttrimz.ui.theme.SmartTrimzTheme

// --- Placeholder Data ---
// The backend team will provide this data later.
val placeholderBarbers = listOf("Mike Johnson", "Alex Smith", "Chris Brown")
val placeholderDates = listOf(
    "Tue 10" to "Dec",
    "Wed 11" to "Dec",
    "Fri 13" to "Dec",
    "Sat 14" to "Dec",
    "Mon 16" to "Dec",
    "Tue 17" to "Dec",
    "Wed 18" to "Dec"
)
val placeholderTimes = listOf(
    "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM",
    "12:00 PM", "12:30 PM", "01:00 PM", "01:30 PM", "02:00 PM", "02:30 PM"
)
// -------------------------


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookAppointmentScreen(
    onBackClick: () -> Unit = {},
    onConfirmBookingClick: () -> Unit = {} // New callback
) {
    // --- State variables to remember selections ---
    var isBarberDropdownExpanded by remember { mutableStateOf(false) }
    var selectedBarber by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<Pair<String, String>?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Appointment") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        // We add a Button at the bottom
        bottomBar = {
            Button(
                onClick = {
                    // We'll pass all the selected data to the backend team
                    // For now, just call the callback
                    onConfirmBookingClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                // Only enable the button if all selections are made
                enabled = selectedBarber != null && selectedDate != null && selectedTime != null
            ) {
                Text(
                    text = "Confirm Booking",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp) // Add side padding for content
                .verticalScroll(rememberScrollState()) // Make the whole page scrollable
        ) {

            // --- 1. Select Barber Dropdown ---
            Text(
                text = "Select Barber",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = isBarberDropdownExpanded,
                onExpandedChange = { isBarberDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedBarber ?: "Choose your barber",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor() // This is important
                )
                ExposedDropdownMenu(
                    expanded = isBarberDropdownExpanded,
                    onDismissRequest = { isBarberDropdownExpanded = false }
                ) {
                    placeholderBarbers.forEach { barber ->
                        DropdownMenuItem(
                            text = { Text(barber) },
                            onClick = {
                                selectedBarber = barber
                                isBarberDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // --- 2. Select Date ---
            Text(
                text = "Select Date",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(placeholderDates) { date ->
                    DateChip(
                        date = date,
                        isSelected = selectedDate == date,
                        onClick = { selectedDate = date }
                    )
                }
            }

            // --- 3. Select Time ---
            Text(
                text = "Select Time",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 3 columns
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(250.dp) // Give the grid a fixed height
            ) {
                items(placeholderTimes) { time ->
                    TimeChip(
                        time = time,
                        isSelected = selectedTime == time,
                        onClick = { selectedTime = time }
                    )
                }
            }
            Spacer(modifier = Modifier.height(100.dp)) // Padding at bottom
        }
    }
}

// --- Reusable Components for this screen ---

@Composable
private fun DateChip(
    date: Pair<String, String>,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (day, month) = date
    val cardColors = if (isSelected) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    }
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .width(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(12.dp)),
        colors = cardColors,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = day, fontWeight = FontWeight.Bold, color = textColor)
            Text(text = month, color = textColor)
        }
    }
}

@Composable
private fun TimeChip(
    time: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val chipColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = chipColor)
    ) {
        Text(
            text = time,
            textAlign = TextAlign.Center,
            color = textColor,
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 8.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun BookAppointmentScreenPreview() {
    SmartTrimzTheme {
        BookAppointmentScreen()
    }
}