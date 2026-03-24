package com.ansh.sportsapp.presentation.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.ansh.sportsapp.data.local.AuthPreferences
import com.ansh.sportsapp.presentation.navigation.AppNavigation
import com.ansh.sportsapp.presentation.navigation.Screen
import com.ansh.sportsapp.ui.theme.*

@Composable
fun MainScreen(authPreferences: AuthPreferences) {
    val accessToken by authPreferences.accessToken.collectAsState(initial = null)
    val isLoggedIn = accessToken != null

    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Gigs.route,
        Screen.Profile.route
    )

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = BackgroundDark,
        bottomBar = {
            if (showBottomBar) {
                SportsBottomBar(navController = navController)
            }
        },
        floatingActionButton = {
            if (currentRoute == Screen.Home.route) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.CreateGig.route) },
                    containerColor = SportGreen,
                    contentColor = Color(0xFF0A0C0F),
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Gig")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AppNavigation(navController = navController, startDestination = startDestination)
        }
    }
}