package com.example.smarttrimz.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smarttrimz.ui.theme.SmartTrimzTheme

@Composable
fun LoginScreen() {

    // These are the "State" variables. They are the "memory"
    // for what the user types into the text fields.
    // 'remember' keeps the value alive.
    // 'mutableStateOf' makes it changeable.
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        // 'modifier' is like a set of instructions for a Composable
        modifier = Modifier
            .fillMaxSize() // Fills the whole screen
            .padding(16.dp), // Adds 16.dp of padding on all sides
        // 'verticalArrangement' controls how items are spaced vertically
        verticalArrangement = Arrangement.Center, // Puts everything in the vertical center
        // 'horizontalAlignment' controls how items are aligned horizontally
        horizontalAlignment = Alignment.CenterHorizontally // Puts everything in the horizontal center
    ) {

        Text(
            text = "Welcome Back",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp)) // An invisible 16dp spacer

        // This is the Email text field
        OutlinedTextField(
            value = email, // The text to *display* (comes from our 'email' state)
            onValueChange = { email = it }, // When the user types, *update* our 'email' state
            label = { Text("Email") }, // The floating label
            modifier = Modifier.fillMaxWidth() // Make this field fill the width
        )

        Spacer(modifier = Modifier.height(8.dp))

        // This is the Password text field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(), // This hides the text as '...'
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* We'll add navigation logic here later! */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // This is our Row for the "Sign Up" text
        Row {
            Text("Don't have an account?")
            // TextButton makes text clickable
            TextButton(onClick = { /* We'll navigate to Sign Up later! */ }) {
                Text("Sign Up")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SmartTrimzTheme {
        LoginScreen()
    }
}