package it.polito.mad.sportcamp.reservationsScreens

import android.graphics.drawable.shapes.Shape
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import it.polito.mad.sportcamp.bottomnav.DETAIL_ARGUMENT_KEY
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.common.BitmapConverter
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

        reservations?.let { ReservationsList(reservations = it, viewModel = viewModel, navController = navController) }

        Spacer(modifier = Modifier.height(20.dp))

    }
    Spacer(modifier = Modifier.height(20.dp))
}





@Composable
fun ReservationsList(reservations: List<ReservationContent>, viewModel: AppViewModel, navController: NavHostController) {
    LazyColumn {
        item {
            reservations.forEach { reservationContent ->
                ReservationCard(reservationContent, viewModel, navController)
            }

        }

    }
}



@Composable
fun ReservationCard(reservation: ReservationContent, viewModel: AppViewModel, navController: NavHostController) {

    val bitmap = reservation.image?.let { BitmapConverter.converterStringToBitmap(it) }
    // We keep track if the message is expanded or not in this
    // variable
    var isExpanded by remember { mutableStateOf(false) }
    //val surfaceColor = MaterialTheme.colors.background,


    Card(
        elevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp),
        shape = RoundedCornerShape(10.dp),
    ) {

        Column() {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (bitmap != null) {
                    Image(
                        painter = BitmapPainter(bitmap.asImageBitmap()),
                        contentDescription = "Court Picture",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            // Clip image to be shaped as a rectangle
                            .clip(shape = RectangleShape)
                            .fillMaxWidth()
                            .height(210.dp)
                            .padding(10.dp)
                    )
                }
            }


            Row(modifier = Modifier.padding(horizontal = 10.dp)) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.padding(4.dp)) {
                            Row() {
                                Icon(
                                    modifier = Modifier
                                        .size(25.dp),
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = "Location",
                                )
                                reservation.court_name?.let {
                                    Text(
                                        text = it,
                                    )
                                }
                            }
                            Row() {
                                Spacer(modifier = Modifier.width(25.dp))
                                reservation.time_slot?.let {
                                    Text(
                                        text = "($it)",
                                        fontSize = 14.sp,
                                    )
                                }
                            }
                        }

                        Column( modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Row() {
                                Icon(
                                    modifier = Modifier
                                        .size(25.dp)
                                        .clickable { isExpanded = !isExpanded },
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "Info",
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    if (isExpanded) {

                        Column(modifier = Modifier.padding(4.dp)) {


                            Row() {
                                reservation.address?.let {
                                    Text(
                                        text = "Address: $it",
                                    )

                                }

                            }

                            Row(){
                                reservation.city?.let {
                                    Text(
                                        text = "City: $it",
                                    )
                                }
                            }



                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Column() {
                                    reservation.equipments?.let {
                                        Text(
                                            text = "Equipments: $it",
                                        )
                                    }

                                    reservation.sport?.let {
                                        Text(
                                            text = "Sport: $it",
                                        )
                                    }
                                }


                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Button(
                                    shape = RoundedCornerShape(5.dp),
                                    onClick = {
                                        reservation.id_reservation?.let {
                                            viewModel.deleteReservationById(
                                                it
                                            )
                                        }
                                        navController.navigate(route = Screen.Reservations.route)
                                    }) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Delete",
                                            fontSize = 13.sp,
                                            textAlign = TextAlign.Center
                                        )
                                        Icon(
                                            Icons.Outlined.Delete,
                                            contentDescription = "Delete"
                                        )
                                    }
                                }
                            }
                        }

                    }


                    /*
                    // Add a vertical space between the author and message texts
                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = MaterialTheme.shapes.medium, elevation = 5.dp,
                        // surfaceColor color will be changing gradually from primary to surface
                        color = MaterialTheme.colors.background,
                        // animateContentSize will change the Surface size gradually
                        modifier = Modifier
                            .animateContentSize()
                            .padding(1.dp)
                    ) {
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
*/
                }
            }
        }
    }
}


