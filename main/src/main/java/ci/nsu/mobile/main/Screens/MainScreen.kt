package ci.nsu.mobile.main.Screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import ci.nsu.mobile.main.ViewModel.AuthViewModel

@Composable
fun MainScreen(navController: NavHostController, viewModel: AuthViewModel,  modifier: Modifier = Modifier) {
    val context = LocalContext.current

}