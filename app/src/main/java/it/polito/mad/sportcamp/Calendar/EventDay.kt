package it.polito.mad.sportcamp.Calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.classes.ReservationTimed
import it.polito.mad.sportcamp.ui.theme.OrangeActionBar
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDay(
    state: DayState<DynamicSelectionState>,
    modifier: Modifier = Modifier,
    navController: NavController,
    reservationsList: List<ReservationTimed>
) {
    val date = state.date
    val selectionState = state.selectionState
    val isSelected = selectionState.isDateSelected(date)
    val today: LocalDate = LocalDate.now()

    if (state.isFromCurrentMonth) {
        val reservationsWithSameDate = reservationsList.filter { it.date.toString() == date.toString() }
        val numReservations = reservationsWithSameDate.size

        Card(
            modifier = modifier
                .aspectRatio(1f)
                .padding(2.dp),
            elevation =  6.dp ,
            border = if (state.isCurrentDay) {
                BorderStroke(3.dp, MaterialTheme.colors.secondary)
            } else if (isSelected) {
                BorderStroke(1.dp, MaterialTheme.colors.secondary)
            } else {
                null
            },
            backgroundColor = MaterialTheme.colors.background
            ,
            contentColor = if (isSelected) {
                MaterialTheme.colors.onPrimary
            } else {
                OrangeActionBar
            }
        ) {
            Column(
                modifier = Modifier
                    .clickable { },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    color = if (state.date.isBefore(today)) {
                        Color.Gray
                    } else {
                        Color.Black
                    }
                )

                Row() {

                    if (numReservations > 2) {
                        Row(
                            modifier = Modifier
                                .clickable {
                                    navController.navigate(
                                        route = Screen.ReservationDetails.passDate(
                                            date.toString()
                                        )
                                    )
                                    // Log.d("SelectedDate", date.toString())
                                }.padding(0.5.dp),
                            verticalAlignment = Alignment.CenterVertically

                            ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background( if (state.date.isBefore(today)) {Color.Gray} else {Color.Black})
                            )
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (state.date.isBefore(today)) {Color.Gray} else {Color.Black})
                            )

                            Text(
                                text = "+", //text inside calendar day
                                fontSize = 13.sp,
                                color = if (state.date.isBefore(today)) {Color.Gray} else {Color.Black}
                            )
                        }
                    }
                    else {
                        reservationsWithSameDate.forEach {
                            if (date.toString() == it.date.toString()) {
                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            navController.navigate(
                                                route = Screen.ReservationDetails.passDate(
                                                    date.toString()
                                                )
                                            )
                                            // Log.d("SelectedDate", date.toString())
                                        }.padding(0.5.dp),

                                    ) {
                                    //Spacer(modifier = Modifier.height(10.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(if (state.date.isBefore(today)) {Color.Gray} else {Color.Black})
                                    )

                                }
                            } else {

                            }
                        }
                    }
                }


            }
        }
    }
}