package ci.nsu.mobile.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Card
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TopAppBar
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import ci.nsu.mobile.main.Screens.DepositAddStages.FirstStageScreen
import ci.nsu.mobile.main.Screens.DepositAddStages.SecondStageScreen
import ci.nsu.mobile.main.Screens.DepositAddStages.ResultStageScreen
import ci.nsu.mobile.main.Screens.HistoryStageScreen
import ci.nsu.mobile.main.Screens.Auth.LoginScreen
import ci.nsu.mobile.main.Screens.Auth.RegistrationScreen
import ci.nsu.mobile.main.Screens.Users.UsersScreen
import ci.nsu.mobile.main.Token.TokenManager
import ci.nsu.mobile.main.Token.UserManager
import ci.nsu.mobile.main.ViewModel.AuthViewModel
import ci.nsu.mobile.main.ViewModel.DepositViewModel
import ci.nsu.mobile.main.DI.*
import ci.nsu.mobile.main.ui.theme.PracticeTheme

class MainActivity : ComponentActivity() {
    private lateinit var serviceLocator: ServiceLocator
    private lateinit var viewModelFactory: ViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.init(this)
        UserManager.init(this)

        serviceLocator = ServiceLocator(applicationContext)
        viewModelFactory = ViewModelFactory(serviceLocator)
        enableEdgeToEdge()
        setContent {
            PracticeTheme {
                AppNavigation(viewModelFactory)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModelFactory: ViewModelFactory) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)

    val isLoggedIn by remember {
        derivedStateOf { TokenManager.isLoggedIn() && UserManager.isLoggedIn() }
    }

    if (!isLoggedIn) {
        AuthNavHost(navController, authViewModel) {
            navController.navigate("main") {
                popUpTo(0) { inclusive = true }
            }
        }
    } else {
        MainAppNavHost(navController, authViewModel, viewModelFactory)
    }
}

@Composable
fun AuthNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = onLoginSuccess,
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegistrationScreen(
                viewModel = authViewModel,
                onRegisterSuccess = onLoginSuccess,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    viewModelFactory: ViewModelFactory
) {
    val depositViewModel: DepositViewModel = viewModel(factory = viewModelFactory)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculation of deposits") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Text("Leave")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                NavigationBarItem(
                    selected = currentRoute == "calculate",
                    onClick = { navController.navigate("calculate") },
                    icon = { },
                    label = { Text("Calculation") }
                )
                NavigationBarItem(
                    selected = currentRoute == "history",
                    onClick = { navController.navigate("history") },
                    icon = { },
                    label = { Text("History") }
                )
                NavigationBarItem(
                    selected = currentRoute == "users",
                    onClick = { navController.navigate("users") },
                    icon = { },
                    label = { Text("Users") }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "calculate",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("calculate") {
                CalculationNavHost(depositViewModel)
            }
            composable("history") {
                HistoryStageScreen(navController, depositViewModel)
            }
            composable("users") {
                UsersScreen(viewModel = authViewModel)
            }
        }
    }
}

@Composable
fun CalculationNavHost(depositViewModel: DepositViewModel) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "first_stage") {
        composable("first_stage") {
            FirstStageScreen(navController, depositViewModel)
        }
        composable("second_stage") {
            SecondStageScreen(navController, depositViewModel)
        }
        composable("result") {
            ResultStageScreen(navController, depositViewModel)
        }
    }
}

//@Composable
//fun UsersScreen(viewModel: AuthViewModel) {
//    LaunchedEffect(Unit) {
//        viewModel.loadUsers()
//    }
//
//    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//        Text(
//            text = "Users list",
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        if (viewModel.isLoading) {
//            Box(modifier = Modifier.fillMaxSize()) {
//                CircularProgressIndicator()
//            }
//        } else {
//            LazyColumn {
//                items(viewModel.users) { user ->
//                    Card(
//                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
//                    ) {
//                        Column(modifier = Modifier.padding(12.dp)) {
//                            Text("ID: ${user.id}")
//                            Text("Login: ${user.login}")
//                            Text("Email: ${user.email}")
//                            // здесь же можно вызвать метод UserCard?
//                        }
//                    }
//                }
//            }
//        }
//
//        viewModel.error?.let {
//            Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
//        }
//    }
//}