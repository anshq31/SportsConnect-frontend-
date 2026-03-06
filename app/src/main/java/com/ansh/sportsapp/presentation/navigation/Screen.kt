package com.ansh.sportsapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Sports
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route : String , val title : String , val icon : ImageVector){
    // Auth
    object Login : Screen("login", "Login", Icons.Default.Login)
    object Register : Screen("register", "Register", Icons.Default.PersonAdd)

    // Main Tabs
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Gigs : Screen("gigs", "Gigs", Icons.Default.Sports)
    object Profile : Screen("profile", "Profile", Icons.Default.AccountCircle)

    object EditProfile : Screen("edit_profile", "Edit Profile", Icons.Default.Person)

    // Gig Actions
    object CreateGig : Screen("create_gig", "Create Gig", Icons.Default.Add)

    object GigDetail : Screen("gig_detail/{gigId}", "Gig Detail", Icons.Default.Sports) {
        fun createRoute(gigId: Long) = "gig_detail/$gigId"
    }

    object ChatScreen: Screen("chat/{gigId}","Team Chat",Icons.Default.Sports){
        fun createRoute(gigId: Long) = "chat/$gigId"
    }




    // Add logic to parse arguments if needed later (e.g., detail/{id})
}