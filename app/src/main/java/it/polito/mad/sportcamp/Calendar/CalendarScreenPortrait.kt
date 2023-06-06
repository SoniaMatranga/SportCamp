package it.polito.mad.sportcamp.Calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import it.polito.mad.sportcamp.classes.ReservationTimed


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreenPortrait(
    navController: NavController,
    reservationsList: List<ReservationTimed>
) {


        Column {
            SelectableCalendar(
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                dayContent = { dayState ->
                    EventDay(
                        state = dayState,
                        navController = navController,
                        reservationsList = reservationsList
                    )
                },
                showAdjacentMonths = true,
            )

        }
}





