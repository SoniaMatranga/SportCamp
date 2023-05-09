package it.polito.mad.sportcamp.bottomnav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import it.polito.mad.sportcamp.screen.HomeScreen
import it.polito.mad.sportcamp.screen.ProfileScreen
import it.polito.mad.sportcamp.screen.ReservationsScreen

@Composable
fun BottomNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ){
        composable(route = BottomBarScreen.Home.route)
        {
            HomeScreen()
        }
        composable(route = BottomBarScreen.Reservations.route)
        {
            ReservationsScreen()
        }
        composable(route = BottomBarScreen.Profile.route)
        {
            ProfileScreen()
        }
    }
}