package it.polito.mad.sportcamp.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

const val DETAIL_ARGUMENT_KEY = "id"

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val icon_focused: ImageVector
) {

    //for home
    object Favorites: Screen(
        route = "favorites",
        title = "Favorites",
        icon = Icons.Outlined.Favorite,
        icon_focused = Icons.Outlined.Favorite
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

}