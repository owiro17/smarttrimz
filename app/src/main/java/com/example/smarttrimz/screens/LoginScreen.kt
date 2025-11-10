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
fun LoginScreen(
    // We now accept a *function* as a parameter.
    // This is called a "callback".
    // It has a default value of {} (do nothing) for the Preview.
    onLoginClick: () -> Unit = {}
) {

    // These are the "State" variables. They are the "memory"
    // for what the user types into the text fields.
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Welcome Back",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            // When the button is clicked, we call the 'onLoginClick'
            // function that was passed in to us from MainActivity.
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("Don't have an account?")
            TextButton(onClick = { /* TODO: Navigate to Sign Up */ }) {
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