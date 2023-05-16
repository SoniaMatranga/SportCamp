package it.polito.mad.sportcamp.bottomnav

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import it.polito.mad.sportcamp.screen.*

@Composable
fun BottomNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Reservations.route
    ){
        composable(route = Screen.AddReservations.route)
        {
            AddReservationsScreen()
        }
        composable(route = Screen.Reservations.route)
        {
            ReservationsScreen()
        }
        composable(route = Screen.Profile.route)
        {
            ProfileScreen(navController = navController)
        }
        composable(
            route = Screen.EditProfile.route,
            arguments = listOf(navArgument(DETAIL_ARGUMENT_KEY){
                type= NavType.IntType
            })
        )
        {
            Log.d("Args",it.arguments?.getInt(DETAIL_ARGUMENT_KEY).toString())
            EditProfileScreen(navController = navController)
        }

    }
}