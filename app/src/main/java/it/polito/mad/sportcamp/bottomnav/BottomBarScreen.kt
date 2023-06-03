package it.polito.mad.sportcamp.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

const val DETAIL_ARGUMENT_KEY = "id"
const val DETAIL_ARGUMENT_KEY2 = "date"
const val DETAIL_ARGUMENT_KEY3 = "id_court"
const val DETAIL_ARGUMENT_KEY4 = "id_reservation"
const val DETAIL_ARGUMENT_KEY5 = "court_name"

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val icon_focused: ImageVector
) {

    //for home
    object Favorites: Screen(
        route = "favorites",
        title = "Ratings",
        icon = Icons.Outlined.Star,
        icon_focused = Icons.Outlined.Star
    )

    //for reservations
    object Reservations: Screen(
        route = "reservations",
        title = "Reservations",
        icon = Icons.Outlined.Dashboard,
        icon_focused = Icons.Outlined.Dashboard
    )

    //for profile
    object Profile: Screen(
        route = "profile",
        title = "Profile",
        icon = Icons.Outlined.Person,
        icon_focused = Icons.Outlined.Person
    )

    object EditProfile: Screen(
        route = "editProfile/{$DETAIL_ARGUMENT_KEY}",
        title = "editProfile",
        icon =Icons.Outlined.Edit,
        icon_focused = Icons.Outlined.Edit
    ) {
        fun passId(id: Int): String {
            return this.route.replace(oldValue = "{$DETAIL_ARGUMENT_KEY}", newValue = id.toString())
        }
    }

    object AddReservations: Screen(
        route = "addReservations",
        title = "Add reservations",
        icon =Icons.Outlined.Add,
        icon_focused = Icons.Outlined.Add
    )

    object BookReservation: Screen(
        route = "bookReservation/{$DETAIL_ARGUMENT_KEY}/{$DETAIL_ARGUMENT_KEY2}",
        title = "Book reservation",
        icon =Icons.Outlined.Book,
        icon_focused = Icons.Outlined.Book
    ) {
       /* fun passDate(values: String): String {
            return this.route.replace(oldValue = "{$DETAIL_ARGUMENT_KEY}", newValue = values)
        }*/
        fun passValues(id: String?, date: String?): String {
            return "bookReservation/$id/$date"
        }
    }

    object ReservationDetails: Screen(
        route = "reservationDetails/{$DETAIL_ARGUMENT_KEY}",
        title = "Reservation details",
        icon =Icons.Outlined.Details,
        icon_focused = Icons.Outlined.Details
    ){
        fun passDate(date: String): String {
            return this.route.replace(oldValue = "{$DETAIL_ARGUMENT_KEY}", newValue = date)
        }
    }

    object CourtReview: Screen(
        route = "courtReview/{$DETAIL_ARGUMENT_KEY3}",
        title = "courtReview",
        icon =Icons.Outlined.Edit,
        icon_focused = Icons.Outlined.Edit
    ) {
        fun passIdCourt(id: String): String {
            return this.route.replace(oldValue = "{$DETAIL_ARGUMENT_KEY3}", newValue = id.toString())
        }
    }

    object CourtReviewList: Screen(
        route = "courtReviewList/{$DETAIL_ARGUMENT_KEY3}",
        title = "courtReviewList",
        icon =Icons.Outlined.Edit,
        icon_focused = Icons.Outlined.Edit
    ) {
        fun passIdCourt(id: String): String {
            return this.route.replace(oldValue = "{$DETAIL_ARGUMENT_KEY3}", newValue = id.toString())
        }
    }

    object ReservationEdit: Screen(
        route = "reservationEdit/{$DETAIL_ARGUMENT_KEY4}/{$DETAIL_ARGUMENT_KEY3}/{$DETAIL_ARGUMENT_KEY2}",
        title = "reservationEdit",
        icon =Icons.Outlined.Edit,
        icon_focused = Icons.Outlined.Edit
    ) {
        fun passValues(id_reservation: String, id_court: String, date: String?): String {
            return "reservationEdit/$id_reservation/$id_court/$date"
        }
    }

    object Splash: Screen(
        route = "splash",
        title = "Splash",
        icon = Icons.Outlined.Star,
        icon_focused = Icons.Outlined.Star
    )

    object Login: Screen(
        route = "login",
        title = "Login",
        icon = Icons.Outlined.Star,
        icon_focused = Icons.Outlined.Star
    )


}