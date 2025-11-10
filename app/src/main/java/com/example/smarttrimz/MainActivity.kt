package com.example.smarttrimz // New package name

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.smarttrimz.screens.LoginScreen // We'll import our new screen
import com.example.smarttrimz.ui.theme.SmartTrimzTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartTrimzTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Instead of 'Greeting', we'll call our new LoginScreen
                    LoginScreen()
                }
            }
        }
    }
}

// We can update the preview to show our LoginScreen too
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SmartTrimzTheme {
        LoginScreen()
    }
}