package com.fissa.kotlin.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.fissa.kotlin.ui.location.LocationField
import com.fissa.kotlin.ui.location.LocationPickerRoute
import com.fissa.kotlin.ui.map.MapPickerRoute
import com.fissa.kotlin.ui.trip.TripSearchRoute
import com.fissa.kotlin.ui.trip.TripSearchViewModel

private const val TRIP_GRAPH = "trip_graph"
private const val TRIP_SEARCH = "trip_search"
private const val LOCATION_PICKER = "location_picker/{field}"
private const val MAP_PICKER = "map_picker/{field}"
private const val ARG_FIELD = "field"
private const val TRANSITION_DURATION_MS = 300

private fun locationPickerRoute(field: LocationField) = "location_picker/${field.name}"
private fun mapPickerRoute(field: LocationField) = "map_picker/${field.name}"

@Composable
fun FissaNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = TRIP_GRAPH,
        // Navigation "avant" : la nouvelle page glisse depuis la droite, l'ancienne part à gauche.
        enterTransition = {
            slideInHorizontally(tween(TRANSITION_DURATION_MS)) { fullWidth -> fullWidth } + fadeIn(tween(TRANSITION_DURATION_MS))
        },
        exitTransition = {
            slideOutHorizontally(tween(TRANSITION_DURATION_MS)) { fullWidth -> -fullWidth } + fadeOut(tween(TRANSITION_DURATION_MS))
        },
        // Retour ("pop") : effet inverse — l'ancienne page revient depuis la gauche, la page
        // quittée part vers la droite.
        popEnterTransition = {
            slideInHorizontally(tween(TRANSITION_DURATION_MS)) { fullWidth -> -fullWidth } + fadeIn(tween(TRANSITION_DURATION_MS))
        },
        popExitTransition = {
            slideOutHorizontally(tween(TRANSITION_DURATION_MS)) { fullWidth -> fullWidth } + fadeOut(tween(TRANSITION_DURATION_MS))
        },
    ) {
        navigation(startDestination = TRIP_SEARCH, route = TRIP_GRAPH) {
            composable(TRIP_SEARCH) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(TRIP_GRAPH) }
                val viewModel: TripSearchViewModel = hiltViewModel(parentEntry)
                TripSearchRoute(
                    viewModel = viewModel,
                    onDepartureFieldClick = { navController.navigate(locationPickerRoute(LocationField.DEPARTURE)) },
                    onArrivalFieldClick = { navController.navigate(locationPickerRoute(LocationField.ARRIVAL)) },
                )
            }

            composable(
                route = LOCATION_PICKER,
                arguments = listOf(navArgument(ARG_FIELD) { type = NavType.StringType }),
            ) { backStackEntry ->
                val field = LocationField.valueOf(backStackEntry.arguments?.getString(ARG_FIELD) ?: LocationField.DEPARTURE.name)
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(TRIP_GRAPH) }
                val tripViewModel: TripSearchViewModel = hiltViewModel(parentEntry)

                LocationPickerRoute(
                    field = field,
                    onPlaceSelected = { place ->
                        if (field == LocationField.DEPARTURE) tripViewModel.onDepartureSelected(place) else tripViewModel.onArrivalSelected(place)
                        navController.popBackStack()
                    },
                    onMapPickerRequested = { navController.navigate(mapPickerRoute(field)) },
                    onBack = { navController.popBackStack() },
                )
            }

            composable(
                route = MAP_PICKER,
                arguments = listOf(navArgument(ARG_FIELD) { type = NavType.StringType }),
            ) { backStackEntry ->
                val field = LocationField.valueOf(backStackEntry.arguments?.getString(ARG_FIELD) ?: LocationField.DEPARTURE.name)
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(TRIP_GRAPH) }
                val tripViewModel: TripSearchViewModel = hiltViewModel(parentEntry)

                MapPickerRoute(
                    onPlaceConfirmed = { place ->
                        if (field == LocationField.DEPARTURE) tripViewModel.onDepartureSelected(place) else tripViewModel.onArrivalSelected(place)
                        navController.popBackStack(TRIP_SEARCH, inclusive = false)
                    },
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
