package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calculator.screens.CalculatorScreen
import com.example.calculator.screens.GraphScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isDark by remember { mutableStateOf(false) }
            val navController = rememberNavController()
            val colorScheme = if (isDark) darkColorScheme() else lightColorScheme()

            MaterialTheme(colorScheme = colorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController, startDestination = "calculator") {
                        composable("calculator") {
                            CalculatorScreen(
                                isDark = isDark,
                                onToggleTheme = { isDark = !isDark },
                                onGraph = { expr -> navController.navigate("graph/$expr") }
                            )
                        }
                        composable("graph/{expr}") { backStackEntry ->
                            GraphScreen(
                                expr = backStackEntry.arguments?.getString("expr") ?: "",
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}