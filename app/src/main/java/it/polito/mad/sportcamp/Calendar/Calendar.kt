package it.polito.mad.sportcamp.Calendar


import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavController
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

    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT ->
            CalendarScreenPortrait(
                navController= navController,
                reservationsList = reservationsList
            )
                    // Landscape
        else -> {
            CalendarScreenLandscape(
                navController= navController,
                reservationsList = reservationsList
            )

        }
            //CalendarScreenLandscape()
    }
}
