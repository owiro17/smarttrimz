
package com.example.smarttrimz.screens

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarttrimz.data.Barber
import com.example.smarttrimz.data.Booking
import com.example.smarttrimz.ui.theme.SmartTrimzTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

// --- Placeholder Data ---
// We'''ll keep this for the dates and times for now
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

// --- ViewModel for Booking Logic ---
class BookingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _barbers = MutableStateFlow<List<Barber>>(emptyList())
    val barbers: StateFlow<List<Barber>> = _barbers

    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Idle)
    val bookingState: StateFlow<BookingState> = _bookingState

    init {
        fetchBarbers()
    }

    private fun fetchBarbers() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("barbers").get().await()
                val barberList = snapshot.toObjects(Barber::class.java)
                _barbers.value = barberList
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun createBooking(barber: Barber, date: Pair<String, String>, time: String) {
        viewModelScope.launch {
            _bookingState.value = BookingState.Loading
            try {
                val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

                // Simple date parsing (you might want a more robust solution)
                val sdf = SimpleDateFormat("MMM dd yyyy hh:mm a", Locale.ENGLISH)
                val dateString = "${date.second} ${date.first.split(" ")[1]} ${Calendar.getInstance().get(Calendar.YEAR)} $time"
                val parsedDate = sdf.parse(dateString) ?: Date()

                val newBooking = Booking(
                    userId = userId,
                    barberId = barber.id,
                    barberName = barber.name,
                    service = "Haircut", // Placeholder service
                    dateTime = parsedDate,
                    status = "upcoming"
                )

                db.collection("bookings").add(newBooking).await()
                _bookingState.value = BookingState.Success
            } catch (e: Exception) {
                _bookingState.value = BookingState.Error(e.localizedMessage ?: "An unknown error occurred")
            }
        }
    }

    fun resetBookingState() {
        _bookingState.value = BookingState.Idle
    }
}

sealed class BookingState {
    object Idle : BookingState()
    object Loading : BookingState()
    object Success : BookingState()
    data class Error(val message: String) : BookingState()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookAppointmentScreen(
    onBackClick: () -> Unit = {},
    onBookingSuccess: () -> Unit = {},
    bookingViewModel: BookingViewModel = viewModel()
) {
    val context = LocalContext.current
    val barbers by bookingViewModel.barbers.collectAsState()
    val bookingState by bookingViewModel.bookingState.collectAsState()

    var isBarberDropdownExpanded by remember { mutableStateOf(false) }
    var selectedBarber by remember { mutableStateOf<Barber?>(null) }
    var selectedDate by remember { mutableStateOf<Pair<String, String>?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }

    // --- Handle Booking State ---
    LaunchedEffect(bookingState) {
        when (val state = bookingState) {
            is BookingState.Success -> {
                Toast.makeText(context, "Booking Confirmed!", Toast.LENGTH_LONG).show()
                bookingViewModel.resetBookingState()
                onBookingSuccess()
            }
            is BookingState.Error -> {
                Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                bookingViewModel.resetBookingState()
            }
            else -> {}
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Appointment") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (selectedBarber != null && selectedDate != null && selectedTime != null) {
                        bookingViewModel.createBooking(selectedBarber!!, selectedDate!!, selectedTime!!)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = selectedBarber != null && selectedDate != null && selectedTime != null && bookingState != BookingState.Loading
            ) {
                if (bookingState == BookingState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(
                        text = "Confirm Booking",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // --- 1. Select Barber ---
            Text("Select Barber", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = isBarberDropdownExpanded,
                onExpandedChange = { isBarberDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedBarber?.name ?: "Choose your barber",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isBarberDropdownExpanded,
                    onDismissRequest = { isBarberDropdownExpanded = false }
                ) {
                    barbers.forEach { barber ->
                        DropdownMenuItem(
                            text = { Text(barber.name) },
                            onClick = {
                                selectedBarber = barber
                                isBarberDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // --- 2. Select Date ---
            Text("Select Date", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(placeholderDates) { date ->
                    DateChip(
                        date = date,
                        isSelected = selectedDate == date,
                        onClick = { selectedDate = date }
                    )
                }
            }

            // --- 3. Select Time ---
            Text("Select Time", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(250.dp)
            ) {
                items(placeholderTimes) { time ->
                    TimeChip(
                        time = time,
                        isSelected = selectedTime == time,
                        onClick = { selectedTime = time }
                    )
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun DateChip(date: Pair<String, String>, isSelected: Boolean, onClick: () -> Unit) {
    val (day, month) = date
    val cardColors = if (isSelected) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    else CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
private fun TimeChip(time: String, isSelected: Boolean, onClick: () -> Unit) {
    val chipColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
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
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
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
