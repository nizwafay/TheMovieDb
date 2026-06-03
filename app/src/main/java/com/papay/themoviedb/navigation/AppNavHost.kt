package com.papay.themoviedb.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.papay.themoviedb.feature.genres.GenreListRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.Genres,
        modifier = modifier
    ) {
        composable(route = AppDestinations.Genres) {
            GenreListRoute()
        }
    }
}
