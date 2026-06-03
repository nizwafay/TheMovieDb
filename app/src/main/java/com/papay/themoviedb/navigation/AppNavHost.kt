package com.papay.themoviedb.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.papay.themoviedb.feature.genres.GenreListRoute
import com.papay.themoviedb.feature.movies.MovieDetailRoute
import com.papay.themoviedb.feature.movies.MovieListRoute

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
            GenreListRoute(
                onGenreClick = { genre ->
                    navController.navigate(
                        AppDestinations.moviesByGenre(
                            genreId = genre.id
                        )
                    )
                }
            )
        }
        composable(
            route = AppDestinations.MoviesByGenre,
            arguments = listOf(
                navArgument(AppDestinations.MovieGenreIdArg) {
                    type = NavType.IntType
                }
            )
        ) {
            MovieListRoute(
                onBackClick = navController::popBackStack,
                onMovieClick = { movie ->
                    navController.navigate(AppDestinations.movieDetail(movieId = movie.id))
                }
            )
        }
        composable(
            route = AppDestinations.MovieDetail,
            arguments = listOf(
                navArgument(AppDestinations.MovieIdArg) {
                    type = NavType.IntType
                }
            )
        ) {
            MovieDetailRoute(
                onBackClick = navController::popBackStack
            )
        }
    }
}
