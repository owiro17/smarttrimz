package com.example.smarttrimz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smarttrimz.screens.BookAppointmentScreen
import com.example.smarttrimz.screens.BookingsScreen
import com.example.smarttrimz.screens.HomeScreen
import com.example.smarttrimz.screens.LoginScreen
import com.example.smarttrimz.screens.ProfileScreen
import com.example.smarttrimz.screens.SignUpScreen
import com.example.smarttrimz.ui.theme.SmartTrimzTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartTrimzTheme {
                AppNavigation()
            }
        }
    }
}

// --- DEFINE OUR NAVIGATION ROUTES ---
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object Bookings : Screen("bookings")
    object Profile : Screen("profile")
    object BookAppointment : Screen("book_appointment")
}

// A list of the screens that show the bottom nav bar
val screensWithBottomBar = listOf(
    Screen.Home.route,
    Screen.Bookings.route,
    Screen.Profile.route
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    // Determine the start destination based on the user's login state
    val startDestination = if (auth.currentUser != null) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    // Get the current route to decide if we show the bottom bar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = screensWithBottomBar.any { it == currentDestination?.route }

    Scaffold(
        bottomBar = {
            // Only show the bottom bar if 'showBottomBar' is true
            if (showBottomBar) {
                NavigationBar {
                    val items = listOf(
                        Screen.Home to Icons.Default.Home,
                        Screen.Bookings to Icons.Default.DateRange,
                        Screen.Profile to Icons.Default.AccountCircle
                    )
                    items.forEach { (screen, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = null) },
                            label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination, // Use the dynamic start destination
            modifier = Modifier.padding(innerPadding)
        ) {
            // Login Screen
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginClick = {
                        // Navigate to Home and clear the login screen from the back stack
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onSignUpClick = {
                        // Navigate to the SignUp screen
                        navController.navigate(Screen.SignUp.route)
                    }
                )
            }
            // SignUp Screen
            composable(Screen.SignUp.route) {
                SignUpScreen(
                    onSignUpClick = {
                        // After sign up, go to Login and remove SignUp from back stack
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    },
                    onLoginClick = {
                        // If user has an account, go to Login and remove SignUp from back stack
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    }
                )
            }

            // --- Main App Screens (inside the bottom bar) ---
            composable(Screen.Home.route) {
                HomeScreen(
                    onBookAppointmentClick = {
                        navController.navigate(Screen.BookAppointment.route)
                    }
                )
            }
            composable(Screen.Bookings.route) {
                BookingsScreen(
                    upcomingBookings = emptyList(),
                    pastBookings = emptyList()
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onSaveChangesClick = {
                        // TODO: Backend team will call their updateUser() API
                    },
                    onChangePasswordClick = {
                        // TODO: Backend team will trigger navigation to a
                        // new '''ChangePasswordScreen''' or show a dialog
                    },
                    onLogoutClick = {
                        // Sign out from Firebase and navigate to Login
                        auth.signOut()
                        navController.navigate(Screen.Login.route) {
                            // Clear the entire back stack
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            // Ensure the new destination is the only one in the stack
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Screen.BookAppointment.route) {
                BookAppointmentScreen(
                    // This callback will send the user back
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onConfirmBookingClick = {
                        navController.navigate(Screen.Bookings.route)
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SmartTrimzTheme {
        AppNavigation()
    }
}
