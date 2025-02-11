package com.example.firebasechattingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.firebasechattingapp.ui.screen.chat.ChatRoute
import com.example.firebasechattingapp.ui.screen.chat.ChatViewModel
import com.example.firebasechattingapp.ui.screen.login.LoginRoute
import com.example.firebasechattingapp.ui.screen.login.LoginViewModel
import com.example.firebasechattingapp.ui.theme.FirebaseChattingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirebaseChattingAppTheme {
                AppEntryPoint()
            }
        }
    }
}

@Composable
internal fun AppEntryPoint() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel()
            LoginRoute(
                viewModel = loginViewModel,
                navigateToChatScreen = { myId, friendsId ->
                    navController.navigate("chat/${myId}/${friendsId}")
                }
            )
        }
        composable(
            "chat/{$ARG_MY_ID}/{$ARG_FRIENDS_ID}",
            arguments = listOf(
                navArgument(ARG_MY_ID) {
                    type = NavType.IntType
                    defaultValue = 0
                },
                navArgument(ARG_FRIENDS_ID) {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) {
            val chatViewModel = viewModel<ChatViewModel>()
            ChatRoute(
                viewModel = chatViewModel
            )
        }
    }
}