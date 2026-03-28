package com.ansh.sportsapp.presentation.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.ansh.sportsapp.data.local.AuthPreferences
import com.ansh.sportsapp.presentation.navigation.AppNavigation
import com.ansh.sportsapp.presentation.navigation.Screen
import com.ansh.sportsapp.ui.theme.*

@Composable
fun MainScreen(authPreferences: AuthPreferences,isLoggedIn : Boolean? = null) {
    val navController = rememberNavController()
    val accessToken by authPreferences.accessToken.collectAsState(initial = null)
    val isLoggedIn = accessToken != null


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Gigs.route,
        Screen.Profile.route
    )

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(0)
            }
        }
    }

    val nonHomeTab = currentRoute == Screen.Gigs.route || currentRoute == Screen.Profile.route

    BackHandler(enabled = isLoggedIn && nonHomeTab) {
        navController.popBackStack(Screen.Home.route,false)
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
            AppNavigation(navController = navController)
        }
    }
}