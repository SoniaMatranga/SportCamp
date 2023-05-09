package it.polito.mad.sportcamp.bottomnav

import it.polito.mad.sportcamp.R

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: Int,
    val icon_focused: Int
) {

    //for home
    object Home: BottomBarScreen(
        route = "home",
        title = "Home",
        icon = R.drawable.outline_home_24,
        icon_focused = R.drawable.baseline_home_24
    )

    //for reservations
    object Reservations: BottomBarScreen(
        route = "reservations",
        title = "Reservations",
        icon = R.drawable.outline_dashboard_24,
        icon_focused = R.drawable.baseline_dashboard_24
    )

    //for profile
    object Profile: BottomBarScreen(
        route = "profile",
        title = "Profile",
        icon = R.drawable.outline_person_24,
        icon_focused = R.drawable.baseline_person_24
    )

}