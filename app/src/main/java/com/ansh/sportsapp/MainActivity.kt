package com.ansh.sportsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ansh.sportsapp.data.local.AuthPreferences
import com.ansh.sportsapp.presentation.main.MainScreen
import com.ansh.sportsapp.presentation.main.MainViewModel
import com.ansh.sportsapp.ui.theme.SportsAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authPreferences: AuthPreferences

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            !viewModel.isAuthChecked.value
        }
        setContent {
            val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = null)

            SportsAppTheme {
                MainScreen(authPreferences,isLoggedIn)
            }
        }
    }
}
