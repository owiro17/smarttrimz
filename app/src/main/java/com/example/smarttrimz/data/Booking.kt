package com.example.smarttrimz.data

// This is a "data class" - a simple class just for holding data.
// The backend team will eventually provide this.
data class Booking(
    val id: String,
    val barberName: String,
    val service: String,
    val date: String,
    val time: String,
    val status: BookingStatus
)

enum class BookingStatus {
    UPCOMING,
    COMPLETED,
    CANCELLED
}

// --- Placeholder Data ---
val placeholderUpcomingBookings = listOf(
    Booking(
        id = "1",
        barberName = "Mike Johnson",
        service = "Classic Haircut",
        date = "Dec 15, 2024",
        time = "10:00 AM",
        status = BookingStatus.UPCOMING
    ),
    Booking(
        id = "2",
        barberName = "Alex Smith",
        service = "Haircut & Beard Trim",
        date = "Dec 22, 2024",
        time = "02:30 PM",
        status = BookingStatus.UPCOMING
    )
)

val placeholderPastBookings = listOf(
    Booking(
        id = "3",
        barberName = "Chris Brown",
        service = "Beard Styling",
        date = "Nov 28, 2024",
        time = "01:15 PM",
        status = BookingStatus.COMPLETED
    )
)