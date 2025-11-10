package com.example.smarttrimz.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smarttrimz.ui.theme.SmartTrimzTheme

@Composable
fun ProfileScreen(
    onSaveChangesClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    // --- State variables for each field ---
    // In a real app, you'd fetch this user data and pre-fill these states
    var name by remember { mutableStateOf("John Doe") }
    var email by remember { mutableStateOf("john.doe@example.com") }
    var address by remember { mutableStateOf("123 Main St, Anytown, USA") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()) // Make the page scrollable
    ) {
        // --- Header ---
        Text(
            text = "My Profile",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Full Name Field ---
        Text(text = "Full Name", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Enter your full name") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Email Field ---
        Text(text = "Email Address", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Enter your email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Address Field ---
        Text(text = "Address", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp), // Taller field for address
            label = { Text("Enter your address") },
            leadingIcon = { Icon(Icons.Default.Home, contentDescription = "Address") },
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Change Password Button ---
        OutlinedButton(
            onClick = {
                // TODO: Backend team will trigger navigation to a
                // new 'ChangePasswordScreen' or show a dialog
                onChangePasswordClick()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Change Password")
        }


        Spacer(modifier = Modifier.height(32.dp))

        // --- logout Button ---
        OutlinedButton(
            onClick = {
                // TODO: Backend team will trigger navigation to a
                // new 'ChangePasswordScreen' or show a dialog
                onLogoutClick()
            },
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log out")
        }


        // This Spacer pushes the "Save" button to the bottom
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(16.dp)) // Padding before button

        // --- Save Changes Button ---
        Button(
            onClick = {
                // TODO: Backend team will take `name`, `email`, `address`
                // and call their updateUser() API
                onSaveChangesClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
        ) {
            Text(
                text = "Save Changes",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    SmartTrimzTheme {
        ProfileScreen()
    }
}