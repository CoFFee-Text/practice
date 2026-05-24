package ci.nsu.mobile.main

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ci.nsu.mobile.main.ui.theme.PracticeTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import ci.nsu.mobile.main.Screens.MainScreen
import ci.nsu.mobile.main.Screens.LoginScreen
import ci.nsu.mobile.main.Screens.RegistrationScreen
import ci.nsu.mobile.main.ViewModel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PracticeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    MainScreenActivity(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

sealed class ScreenRoutes(val route: String) {
    object MainActivity : ScreenRoutes("MainScreen")
    object Login : ScreenRoutes("LoginScreen")
    object Registration : ScreenRoutes("RegistrationScreen")
}

@Composable
fun MainScreenActivity(navController: NavHostController, modifier: Modifier = Modifier) {
    val viewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.MainActivity.route,
        modifier = modifier
    ) {
        composable(ScreenRoutes.MainActivity.route) {
            MainScreen(navController, viewModel, modifier)
        }
        composable(ScreenRoutes.Login.route) {
            LoginScreen(navController, viewModel, modifier)
        }
        composable(ScreenRoutes.Registration.route) {
            RegistrationScreen(navController, viewModel, modifier)
        }
    }
}