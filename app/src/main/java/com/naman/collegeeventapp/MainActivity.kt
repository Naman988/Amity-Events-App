package com.naman.collegeeventapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.naman.collegeeventapp.model.Event
import com.naman.collegeeventapp.ui.theme.CollegeEventAppTheme
import com.naman.collegeeventapp.ui.theme.Routes
import com.naman.collegeeventapp.ui.theme.screens.auth.*
import com.naman.collegeeventapp.viewmodel.AuthViewModel
import com.naman.collegeeventapp.viewmodel.EventViewModel
import java.net.URLDecoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            CollegeEventAppTheme {
                val navController = rememberNavController()
                val authViewModel = remember { AuthViewModel() }
                val eventViewModel = remember { EventViewModel() }


                val currentUser = FirebaseAuth.getInstance().currentUser
                val startDestination = if (currentUser != null) {
                    Routes.HOME
                } else {

                    Routes.LOGIN
                }


                // 3. Use the dynamic startDestination
                NavHost(navController = navController, startDestination = startDestination) {

                    // 🔐 Login Screen
                    composable(Routes.LOGIN) {
                        LoginScreen(
                            onLoginClick = { email, password ->
                                authViewModel.login(
                                    email,
                                    password,
                                    onSuccess = {
                                        navController.navigate(Routes.HOME) {
                                            popUpTo(Routes.LOGIN) { inclusive = true }
                                        }
                                    },
                                    onError = { message ->
                                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            onSignupNavigate = { navController.navigate(Routes.SIGNUP) }
                        )
                    }

                    // 🔐 Signup Screen
                    composable(Routes.SIGNUP) {
                        SignupScreen(
                            onSignupClick = { name, enrollmentNumber, email, password ->
                                authViewModel.signUp(
                                    email = email.trim(),
                                    password = password.trim(),
                                    name = name.trim(),
                                    enrollmentNumber = enrollmentNumber.trim(),
                                    onSuccess = {
                                        navController.navigate(Routes.HOME) {
                                            popUpTo(Routes.SIGNUP) { inclusive = true }
                                        }
                                    },
                                    onError = { message ->
                                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            onLoginNavigate = { navController.popBackStack() }
                        )
                    }

                    // 🏠 Home Screen
                    composable(Routes.HOME) {
                        HomeScreen(
                            navController = navController
                        )
                    }

                    // 📄 Event Details
                    composable(
                        "${Routes.EVENT_DETAILS}/{eventJson}",
                        arguments = listOf(navArgument("eventJson") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val eventJson = backStackEntry.arguments?.getString("eventJson")
                        val decodedJson = URLDecoder.decode(eventJson ?: "", "UTF-8")
                        val event = Gson().fromJson(decodedJson, Event::class.java)

                        EventDetailsScreen(
                            event = event,
                            navController = navController,
                            onRegister = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // 🛡 Admin Panel
                    composable(Routes.ADMIN) {
                        AdminScreen(navController = navController)
                    }

                    composable(Routes.PROFILE) {
                        ProfileScreen(
                            navController = navController,
                            onLogout = {
                                authViewModel.logout()
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
