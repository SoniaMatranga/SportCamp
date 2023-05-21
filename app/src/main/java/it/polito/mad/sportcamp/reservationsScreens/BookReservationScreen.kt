package it.polito.mad.sportcamp.reservationsScreens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.TextField
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
import it.polito.mad.sportcamp.database.AppViewModel
import it.polito.mad.sportcamp.common.CustomToolbarWithBackArrow

import it.polito.mad.sportcamp.bottomnav.DETAIL_ARGUMENT_KEY2
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.common.BitmapConverter


var isEditedTimeSlot: Boolean = false
var isEditedEquipments: Boolean = false

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookReservationScreen(
    navController: NavHostController,
    viewModel: AppViewModel = viewModel(factory = AppViewModel.factory)
) {

    var idCourt = navController.currentBackStackEntry?.arguments?.getInt(DETAIL_ARGUMENT_KEY).toString()
    var date = navController.currentBackStackEntry?.arguments?.getString(DETAIL_ARGUMENT_KEY2).toString()


    val timeSlots by viewModel.getAvailableTimeSlots(idCourt.toInt(), date).observeAsState()
    val courtDetails by viewModel.getCourtById(idCourt.toInt()).observeAsState()

    var expandedTimeSlot by remember { mutableStateOf(false) }
    var expandedEquipments by remember { mutableStateOf(false) }

    var selectedEquipments by remember { mutableStateOf("Select equipments") }
    var selectedTimeSlot by remember { mutableStateOf("Select time slot") }

    val equipments = listOf("Not requested", "Requested")
    val bitmap = courtDetails?.image?.let { BitmapConverter.converterStringToBitmap(it) }

    //val reservations by viewModel.getReservationsByUserAndDate(1, selectedDate).observeAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        //CustomToolbarBookReservation(title = "Book reservation", navController = navController)
        //CustomToolbarWithCalendarButton(title = "Add reservations", calendarState = calendarState )
        // Text( text = reservations.toString())
        CustomToolbarWithBackArrow(title = "Book court", navController = navController)
        //reservations?.let { ReservationsList(reservations = it, viewModel = viewModel, navController = navController) }

        Spacer(modifier = Modifier.height(10.dp))

        //Text(text = "Date: $date")
        Text(text = "Choose details to complete your booking for ${courtDetails?.court_name}",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())

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
                        Column(modifier = Modifier.padding(4.dp)) {
                            Row() {
                                    Text(text = "Date: $date")
                            }
                            Row() {
                                courtDetails?.address?.let {
                                    Text(
                                        text = "Address: $it",
                                    )
                                }
                            }
                            Row(){
                                courtDetails?.city?.let {
                                    Text(
                                        text = "City: $it",
                                    )
                                }
                            }

                        }


                }
            }
        }
        

        Column() {


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 45.dp, vertical = 10.dp)
                    .background(Color.White)
            ) {

                Box(
                    modifier = Modifier.background(Color.White)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expandedTimeSlot,
                        onExpandedChange = {
                            expandedTimeSlot = !expandedTimeSlot
                        }
                    ) {
                        TextField(
                            value = selectedTimeSlot,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTimeSlot) },
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTimeSlot,
                            onDismissRequest = { expandedTimeSlot = false }
                        ) {
                            timeSlots?.forEach { item ->
                                DropdownMenuItem(
                                    content = { Text(text = item) },
                                    onClick = {
                                        expandedTimeSlot = false
                                        selectedTimeSlot = item
                                    }
                                )
                            }
                        }
                    }
                }



            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 45.dp, vertical = 10.dp)
                    .background(Color.White)
            ) {

                Box(
                    modifier = Modifier.background(Color.White)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expandedEquipments,
                        onExpandedChange = {
                            expandedEquipments = !expandedEquipments
                        }
                    ) {
                        TextField(
                            value = selectedEquipments,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEquipments) },
                        )
                        ExposedDropdownMenu(
                            expanded = expandedEquipments,
                            onDismissRequest = { expandedEquipments = false }
                        ) {
                            equipments.forEach { item ->
                                DropdownMenuItem(
                                    content = { Text(text = item) },
                                    onClick = {
                                        expandedEquipments = false
                                        selectedEquipments = item
                                    }
                                )
                            }
                        }
                    }
                }

            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    shape = RoundedCornerShape(5.dp),
                    onClick = {
                        if(selectedTimeSlot!= "Select time slot" && selectedEquipments!="Select equipments") {
                            Log.d ("Found", "Everything selected")
                           /* val reservation_court  = courtDetails?.id_court
                            viewModel.addReservation(Reservation(null,1,1,1,date,selectedEquipments,""))*/
                            courtDetails?.id_court?.let {
                                viewModel.addReservation(null,1,
                                    it,selectedTimeSlot,date,selectedEquipments,"")
                            }

                            navController.navigate(route = Screen.Reservations.route)

                        }else{
                            Log.d ("Not added", "not added")
                            //dialog please chose timeslot and equipments
                        }
                    }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Book court",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }


    }
    Spacer(modifier = Modifier.height(20.dp))
}