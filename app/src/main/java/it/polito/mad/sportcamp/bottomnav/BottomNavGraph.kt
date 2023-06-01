package it.polito.mad.sportcamp.bottomnav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import it.polito.mad.sportcamp.profileScreens.EditProfileScreen
import it.polito.mad.sportcamp.profileScreens.ProfileScreen
import it.polito.mad.sportcamp.reservationsScreens.AddReservationsScreen
import it.polito.mad.sportcamp.reservationsScreens.ReservationsScreen
import it.polito.mad.sportcamp.favoritesScreens.*
import it.polito.mad.sportcamp.initialScreens.LoginScreen
import it.polito.mad.sportcamp.initialScreens.SplashScreen
import it.polito.mad.sportcamp.reservationsScreens.BookReservationScreen
import it.polito.mad.sportcamp.reservationsScreens.ReservationDetails
import it.polito.mad.sportcamp.reservationsScreens.ReservationEditScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

            composable(route = Screen.Reservations.route)
            {
                ReservationsScreen(navController = navController)
            }
            composable(
                route = Screen.ReservationDetails.route,
                arguments = listOf(navArgument(DETAIL_ARGUMENT_KEY) {
                    type = NavType.StringType
                })
            )
            {
                ReservationDetails(navController = navController)
            }

            composable(route = Screen.AddReservations.route)
            {
                AddReservationsScreen(navController = navController)
            }

            composable(
                route = Screen.BookReservation.route,
                arguments = listOf(
                    navArgument(DETAIL_ARGUMENT_KEY) {
                        type = NavType.IntType
                    },
                    navArgument(DETAIL_ARGUMENT_KEY2) {
                        type = NavType.StringType
                    },
                )
            )
            {
                BookReservationScreen(navController = navController)
            }

            composable(
                route = Screen.ReservationEdit.route,
                arguments = listOf(navArgument(DETAIL_ARGUMENT_KEY4) {
                    type = NavType.IntType
                }, navArgument(DETAIL_ARGUMENT_KEY3) {
                    type = NavType.IntType
                }, navArgument(DETAIL_ARGUMENT_KEY2) {
                    type = NavType.StringType
                }
                )
            )
            {
                ReservationEditScreen(navController = navController)
            }


            composable(route = Screen.Favorites.route)
            {
                FavoritesScreen(navController = navController)
            }

            composable(
                route = Screen.CourtReview.route,
                arguments = listOf(navArgument(DETAIL_ARGUMENT_KEY3) {
                    type = NavType.IntType
                })
            )
            {
                CourtReviewScreen(navController = navController)
            }

            composable(
                route = Screen.CourtReviewList.route,
                arguments = listOf(navArgument(DETAIL_ARGUMENT_KEY3) {
                    type = NavType.IntType
                })
            )
            {
                CourtReviewListScreen(navController = navController)
            }


            composable(route = Screen.Profile.route)
            {
                ProfileScreen(navController = navController)
            }
            composable(
                route = Screen.EditProfile.route,
                arguments = listOf(navArgument(DETAIL_ARGUMENT_KEY) {
                    type = NavType.IntType
                })
            )
            {
                // Log.d("Args",it.arguments?.getInt(DETAIL_ARGUMENT_KEY).toString())
                EditProfileScreen(navController = navController)
            }



            composable(route = Screen.Login.route)
            {
                LoginScreen(navController = navController)
            }

            composable(route = Screen.Splash.route)
            {
                SplashScreen(navController = navController)
            }


    }


}
