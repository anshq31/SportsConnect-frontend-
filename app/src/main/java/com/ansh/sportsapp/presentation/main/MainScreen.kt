package com.ansh.sportsapp.presentation.main
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ansh.sportsapp.presentation.navigation.AppNavigation
import com.ansh.sportsapp.presentation.navigation.Screen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Gigs.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar){
                BottomNavigationBar(navController)
            }
        },
        floatingActionButton = {
            if (currentRoute == Screen.Home.route ){
                FloatingActionButton(onClick = {navController.navigate(Screen.CreateGig.route)}) {
                    Icon(Icons.Default.Add, contentDescription = "CreateGig")
                }
            }
        }
    ) {innerPadding->
        Box(modifier = Modifier.padding(innerPadding)){
            AppNavigation(navController = navController)
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavController){
    NavigationBar {
        val items = listOf(Screen.Home, Screen.Gigs, Screen.Profile)
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}