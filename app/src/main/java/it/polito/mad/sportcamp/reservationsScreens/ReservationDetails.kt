package it.polito.mad.sportcamp.reservationsScreens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import it.polito.mad.sportcamp.bottomnav.DETAIL_ARGUMENT_KEY
import it.polito.mad.sportcamp.database.AppViewModel
import it.polito.mad.sportcamp.database.Reservation
import it.polito.mad.sportcamp.database.ReservationContent
import it.polito.mad.sportcamp.profileScreens.CustomToolbarWithBackArrow

@Composable
fun ReservationDetails(
    navController: NavHostController,
    viewModel: AppViewModel = viewModel(factory = AppViewModel.factory)
) {
    var selectedDate = navController.currentBackStackEntry?.arguments?.getString(DETAIL_ARGUMENT_KEY).toString()

    val reservations by viewModel.getReservationsByUserAndDate(1, selectedDate).observeAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomToolbarWithBackArrow(title = "Reservations details", navController = navController)
        //CustomToolbarWithCalendarButton(title = "Add reservations", calendarState = calendarState )
       // Text( text = reservations.toString())

        reservations?.let { ReservationsList(reservations = it) }

    }
}





@Composable
fun ReservationsList(reservations: List<ReservationContent>) {
    LazyColumn {
        item {
            reservations.forEach { reservationContent ->
                ReservationCard(reservationContent)
            }

        }

    }
}



@Composable
fun ReservationCard(reservation: ReservationContent) {


        Row(modifier = Modifier.padding(all = 8.dp)) {

            Spacer(modifier = Modifier.width(10.dp))

            // We keep track if the message is expanded or not in this
            // variable
            var isExpanded by remember { mutableStateOf(false) }

            // surfaceColor will be updated gradually from one color to the other
            val surfaceColor by animateColorAsState(
                if (isExpanded) MaterialTheme.colors.secondary else MaterialTheme.colors.background,
            )

            Column(modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }) {
                reservation.court_name?.let {
                    Text(text = it,
                        modifier = Modifier.fillMaxWidth())
                }

                reservation.address?.let {
                    Text(text = it,
                        modifier = Modifier.fillMaxWidth())
                }

                reservation.date?.let {
                    Text(text = it,
                        modifier = Modifier.fillMaxWidth())
                }


                // Add a vertical space between the author and message texts
                Spacer(modifier = Modifier.height(4.dp))

                    Surface(shape = MaterialTheme.shapes.medium, elevation = 5.dp,
                        // surfaceColor color will be changing gradually from primary to surface
                        color = surfaceColor,
                        // animateContentSize will change the Surface size gradually
                        modifier = Modifier
                            .animateContentSize()
                            .padding(1.dp)) {
                        reservation.time_slot?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(all = 4.dp),
                                // If the message is expanded, we display all its content
                                // otherwise we only display the first line
                                maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                            )
                        }
                    }

            }
        }



}
