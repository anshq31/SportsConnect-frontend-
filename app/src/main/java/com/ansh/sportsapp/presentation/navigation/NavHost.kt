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
import com.ansh.sportsapp.presentation.edit_user.EditProfileScreen
import com.ansh.sportsapp.presentation.gig_detail.GigDetailScreen
import com.ansh.sportsapp.presentation.home.HomeScreen
import com.ansh.sportsapp.presentation.my_gigs.MyGigsScreen
import com.ansh.sportsapp.presentation.user.ProfileScreen

@Composable
fun AppNavigation(navController : NavHostController,startDestination : String){
    NavHost(
        navController = navController,
        startDestination = startDestination
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
            arguments = listOf(navArgument("gigId") { type = NavType.LongType })
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
            ProfileScreen(navController = navController)
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController = navController)
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