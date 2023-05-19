package it.polito.mad.sportcamp.Calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import io.github.boguszpawlowski.composecalendar.CalendarState
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.header.MonthState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import it.polito.mad.sportcamp.database.Reservation
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreenPortrait(
    state: CalendarState<DynamicSelectionState>,
    navController: NavController,
    reservationsList: List<Reservation>
) {


        Column {
            SelectableCalendar(
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth()
                    .padding(15.dp),
                dayContent = { dayState ->
                    EventDay(
                        state = dayState,
                        navController = navController,
                        reservationsList = reservationsList
                    )
                },
                showAdjacentMonths = true,
            )

               // CardEvent()

        }

}





