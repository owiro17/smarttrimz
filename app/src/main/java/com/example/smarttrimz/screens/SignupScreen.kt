package com.example.smarttrimz.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smarttrimz.data.User
import com.example.smarttrimz.ui.theme.SmartTrimzTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SignUpScreen(
    onSignUpClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val context = LocalContext.current
    // --- State variables for each field ---
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var phoneNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Make it scrollable
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Full Name ---
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Person, "Full Name") },
            isError = errorMessage != null
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- Email ---
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, "Email") },
            isError = errorMessage != null
        )

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Password, "Email") },
            isError = errorMessage != null
        )

        // --- Password ---
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Lock, "Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = errorMessage != null
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- Confirm Password ---
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Lock, "Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = errorMessage != null
        )

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Sign Up Button ---
        Button(
            onClick = {
                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                    errorMessage = "All fields must be filled"
                    return@Button
                }
                if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                    return@Button
                }

                isLoading = true
                errorMessage = null

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = FirebaseAuth.getInstance().currentUser
                            val user = User(
                                uid = firebaseUser!!.uid,
                                name = name,
                                email = email,
                                phoneNumber = phoneNumber

                            )

                            FirebaseFirestore.getInstance().collection("users")
                                .document(user.uid)
                                .set(user)
                                .addOnSuccessListener {
                                    isLoading = false
                                    Toast.makeText(context, "Signup Successful!", Toast.LENGTH_SHORT).show()
                                    onSignUpClick()
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    errorMessage = e.message ?: "An unknown error occurred"
                                }
                        } else {
                            isLoading = false
                            errorMessage = task.exception?.localizedMessage ?: "Sign up failed."
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Link to Login ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Already have an account?")
            TextButton(onClick = onLoginClick, enabled = !isLoading) {
                Text("Log In")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SmartTrimzTheme {
        SignUpScreen()
    }
}