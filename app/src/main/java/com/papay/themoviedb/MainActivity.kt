package com.papay.themoviedb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.papay.themoviedb.navigation.AppNavHost
import com.papay.themoviedb.ui.theme.TheMovieDbTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheMovieDbTheme {
                TheMovieDbApp()
            }
        }
    }
}

@Composable
fun TheMovieDbApp() {
    val navController = rememberNavController()
    AppNavHost(navController = navController)
}
