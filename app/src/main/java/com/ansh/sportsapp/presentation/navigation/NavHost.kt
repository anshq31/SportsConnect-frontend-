package com.ansh.sportsapp.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ansh.sportsapp.presentation.auth.login.LoginScreen
import com.ansh.sportsapp.presentation.auth.signup.RegisterScreen
import com.ansh.sportsapp.presentation.chat.ChatScreen
import com.ansh.sportsapp.presentation.create_gig.CreateGigScreen
import com.ansh.sportsapp.presentation.gig_detail.GigDetailScreen
import com.ansh.sportsapp.presentation.home.HomeScreen
import com.ansh.sportsapp.presentation.my_gigs.MyGigsScreen

@Composable
fun AppNavigation(navController : NavHostController){
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ){
        composable(Screen.Login.route) {
            LoginScreen(navController=navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        // --- MAIN TABS ---
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.CreateGig.route){
            CreateGigScreen(navController = navController)
        }

        composable(
            route = "gig_detail/{gigId}",
            arguments = listOf(navArgument("gigId") { type = NavType.StringType })
        ) {
            GigDetailScreen(navController = navController)
        }

        composable(Screen.Gigs.route) {
            MyGigsScreen(navController = navController)
        }

        composable(
            route = "chat/{gigId}", // Hardcoded matching Screen.Chat logic
            arguments = listOf(navArgument("gigId") { type = NavType.LongType }) // Pass as String/Long
        ) {
            ChatScreen(navController = navController)
        }

        composable(Screen.Profile.route) {
            PlaceholderScreen("User Profile")
        }

    }
}

@Composable
fun PlaceholderScreen(name : String, onClick: (()-> Unit)? = null){
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Button(onClick = {onClick?.invoke()}) {
            Text(text = "$name (click to simulate nav)")
        }
    }

}