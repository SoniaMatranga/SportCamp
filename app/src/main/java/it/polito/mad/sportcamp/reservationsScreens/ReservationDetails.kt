package it.polito.mad.sportcamp.reservationsScreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import it.polito.mad.sportcamp.bottomnav.DETAIL_ARGUMENT_KEY
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.database.AppViewModel
import it.polito.mad.sportcamp.database.ReservationContent
import it.polito.mad.sportcamp.ui.theme.fonts
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationDetails(
    navController: NavHostController,
    viewModel: AppViewModel = viewModel(factory = AppViewModel.factory)
) {
    val selectedDate = navController.currentBackStackEntry?.arguments?.getString(DETAIL_ARGUMENT_KEY).toString()


    val reservations by viewModel.getReservationsByUserAndDate(1, selectedDate).observeAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        CustomToolbarReservationDetails(title = "My reservations details", navController = navController)

        reservations?.let { ReservationsList(reservations = it, selectedDate= selectedDate, viewModel = viewModel, navController=navController) }

        Spacer(modifier = Modifier.height(20.dp))

    }
    Spacer(modifier = Modifier.height(20.dp))
}





@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationsList(reservations: List<ReservationContent>, selectedDate:String, viewModel: AppViewModel, navController :NavHostController) {
    LazyColumn {
        item {
            reservations.forEach { reservationContent ->
                ReservationCard(reservationContent, selectedDate,  viewModel, navController)
            }

        }

    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationCard(reservation: ReservationContent, selectedDate:String, viewModel: AppViewModel, navController :NavHostController) {

    val bitmap = reservation.image?.let { BitmapConverter.converterStringToBitmap(it) }
    // We keep track if the message is expanded or not in this
    // variable
    var isExpanded by remember { mutableStateOf(false) }
    val today: LocalDate = LocalDate.now()


    //========================= Dialog on discard ===================================
    val openDialog = remember { mutableStateOf(false) }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            text = {
                Text("This reservation will be deleted permanently. Are you sure you want to delete it anyway? ")
            },
            confirmButton = {
                Button(
                    onClick = {
                        reservation.id_reservation?.let {
                            viewModel.deleteReservationById(
                                it
                            )
                        }
                        openDialog.value = false
                        //navController.navigate(route = Screen.Reservations.route)

                    }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                    }) {
                    Text("Don't delete")
                }
            }
        )
    }


    Card(
        elevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp),
        shape = RoundedCornerShape(10.dp),
    ) {

        Column {
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
                            Row {
                                reservation.court_name?.let {
                                    Text(
                                        text = it,
                                    )
                                }
                            }
                            Row {
                                Spacer(modifier = Modifier.width(25.dp))
                                reservation.time_slot?.let {
                                    Text(
                                        text = "($it)",
                                        fontSize = 14.sp,
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Row {

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


                            Row {
                                reservation.address?.let {
                                    Text(
                                        text = "Address: $it",
                                    )

                                }

                            }

                            Row {
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

                                Column {
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

                            Spacer(modifier = Modifier.height(8.dp))


                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {

                                Column {

                                    if(!LocalDate.parse(reservation.date).isBefore(today)) {
                                        Button(
                                            shape = RoundedCornerShape(5.dp),
                                            onClick = {
                                                navController.navigate(
                                                    route = Screen.ReservationEdit.passValues(
                                                        reservation.id_reservation!!,
                                                        reservation.id_court!!,
                                                        selectedDate
                                                    )
                                                )
                                            }) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "Edit",
                                                    fontSize = 15.sp,
                                                )
                                                Icon(
                                                    Icons.Outlined.Edit,
                                                    contentDescription = "Edit"
                                                )
                                            }
                                        }
                                    }
                                }

                                Column {
                                    Button(
                                        shape = RoundedCornerShape(5.dp),
                                        onClick = {
                                            openDialog.value = true
                                        }) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Delete",
                                                fontSize = 15.sp,
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
                    }
                }
            }
        }
    }
}

@Composable
fun CustomToolbarReservationDetails(title: String, navController: NavHostController) {
    TopAppBar(
        title = { Text(text = title, fontFamily = fonts) },
        navigationIcon = {
            IconButton(onClick = {
                navController.navigate(
                    route = Screen.Reservations.route
                ) }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "arrowBack",
                    tint = Color.White
                )
            }
        }
    )
}


