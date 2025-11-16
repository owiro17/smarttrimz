package com.example.smarttrimz.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarttrimz.data.Barber
import com.example.smarttrimz.data.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val userId = auth.currentUser?.uid

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _barbers = MutableStateFlow<List<Barber>>(emptyList())
    val barbers: StateFlow<List<Barber>> = _barbers

    init {
        if (userId != null) {
            fetchUser()
            fetchBarbers()
        }
    }

    private fun fetchUser() {
        viewModelScope.launch {
            db.collection("users").document(userId!!)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        _user.value = document.toObject<User>()
                    } else {
                        Log.d("HomeViewModel", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("HomeViewModel", "get failed with ", exception)
                }
        }
    }

    private fun fetchBarbers() {
        viewModelScope.launch {
            db.collection("barbers")
                .get()
                .addOnSuccessListener { result ->
                    _barbers.value = result.toObjects<Barber>()
                }
                .addOnFailureListener { exception ->
                    Log.w("HomeViewModel", "Error getting documents.", exception)
                }
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onBookAppointmentClick: () -> Unit = {}
) {
    val user by viewModel.user.collectAsState()
    val barbers by viewModel.barbers.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 72.dp) // Space for the button
        ) {
            item {
                Text(
                    text = "Hello, ${user?.name ?: "User"}!",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Ready for your next appointment?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text(
                    text = "Our Barbers",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(barbers) { barber ->
                BarberCard(barber = barber)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Button(
            onClick = onBookAppointmentClick,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = "Book Appointment",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun BarberCard(barber: Barber) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = barber.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "Specialties: ${barber.specialties.joinToString()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
