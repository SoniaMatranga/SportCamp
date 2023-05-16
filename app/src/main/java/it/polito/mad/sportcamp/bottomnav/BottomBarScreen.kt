package it.polito.mad.sportcamp.bottomnav

import it.polito.mad.sportcamp.R

const val DETAIL_ARGUMENT_KEY = "id"

sealed class Screen(
    val route: String,
    val title: String,
    val icon: Int,
    val icon_focused: Int
) {

    //for home
    object Home: Screen(
        route = "home",
        title = "Home",
        icon = R.drawable.outline_home_24,
        icon_focused = R.drawable.baseline_home_24
    )

    //for reservations
    object Reservations: Screen(
        route = "reservations",
        title = "Reservations",
        icon = R.drawable.outline_dashboard_24,
        icon_focused = R.drawable.baseline_dashboard_24
    )

    //for profile
    object Profile: Screen(
        route = "profile",
        title = "Profile",
        icon = R.drawable.outline_person_24,
        icon_focused = R.drawable.baseline_person_24
    )

    object EditProfile: Screen(
        route = "editProfile/{$DETAIL_ARGUMENT_KEY}",
        title = "editProfile",
        icon = 1,
        icon_focused = 1
    ) {
        fun passId(id: Int): String {
            return this.route.replace(oldValue = "{$DETAIL_ARGUMENT_KEY}", newValue = id.toString())
        }
    }

}