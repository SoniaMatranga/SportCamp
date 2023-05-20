package it.polito.mad.sportcamp.Calendar

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import it.polito.mad.sportcamp.database.Reservation
import it.polito.mad.sportcamp.database.ReservationTimed

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendar(
    navController: NavController,
    reservationsList: List<ReservationTimed>
) {

    //val context = LocalContext.current
   // val activity = LocalContext.current as Activity
    val configuration = LocalConfiguration.current

    val state = rememberSelectableCalendarState(
        confirmSelectionChange = {  true },
        initialSelectionMode = SelectionMode.Single,
    )
    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT ->
            CalendarScreenPortrait(
                state = state,
                navController= navController,
                reservationsList = reservationsList
            )
                    // Landscape
        else -> {}
            //CalendarScreenLandscape()
    }
}
