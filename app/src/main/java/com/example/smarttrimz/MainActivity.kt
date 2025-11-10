package com.example.smarttrimz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.smarttrimz.screens.BookingsScreen
import com.example.smarttrimz.screens.HomeScreen
import com.example.smarttrimz.screens.LoginScreen
import com.example.smarttrimz.screens.ProfileScreen
import com.example.smarttrimz.ui.theme.SmartTrimzTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartTrimzTheme {
                // Surface is no longer needed here, Scaffold handles it
                AppNavigation()
            }
        }
    }
}

// --- DEFINE OUR NAVIGATION ROUTES ---
// We create sealed classes for type-safe navigation
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Bookings : Screen("bookings")
    object Profile : Screen("profile")
    // We'll add this one later
    // object BookAppointment : Screen("book_appointment")
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

    // Get the current route to decide if we show the bottom bar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = screensWithBottomBar.any { it == currentDestination?.route }

    // Scaffold is a Material 3 layout that gives us slots for
    // top bars, bottom bars, floating action buttons, etc.
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
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid re-launching the same screen
                                    launchSingleTop = true
                                    // Restore state when re-selecting
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // This is the "content area"
        // NavHost holds all our screen composables
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding) // Pass padding from Scaffold
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
//                    onSignUpClick = {
//                        // TODO: Create and navigate to SignUpScreen
//                        // For now, we'll just go to Home as well
//                        navController.navigate(Screen.Home.route) {
//                            popUpTo(Screen.Login.route) { inclusive = true }
//                        }
//                    }
                )
            }

            // --- Main App Screens (inside the bottom bar) ---
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Bookings.route) {
                BookingsScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
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