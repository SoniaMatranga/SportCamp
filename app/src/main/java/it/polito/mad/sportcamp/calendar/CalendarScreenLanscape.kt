package it.polito.mad.sportcamp.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.database.ReservationTimed

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreenLandscape(
    navController: NavController,
    reservationsList: List<ReservationTimed>
) {


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MaterialTheme.colors.surface
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())) {

                SelectableCalendar(
                    modifier = Modifier.animateContentSize(),
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

            Box(modifier = Modifier
                .fillMaxHeight()
                .weight(1f), contentAlignment = Alignment.BottomEnd) {

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                    horizontalArrangement = Arrangement.End) {
                    androidx.compose.material3.FloatingActionButton(
                        onClick = {navController.navigate(route = Screen.AddReservations.route)},
                        containerColor = MaterialTheme.colors.primary,
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add button",
                                tint = MaterialTheme.colors.background
                            )

                        }

                    }
                }

            }
        }
    }
}
