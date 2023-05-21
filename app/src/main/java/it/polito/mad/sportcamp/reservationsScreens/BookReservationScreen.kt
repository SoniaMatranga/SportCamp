package it.polito.mad.sportcamp.reservationsScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.common.BookingCompletedMessage
import it.polito.mad.sportcamp.common.ValidationBookingMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookReservationScreen(
    navController: NavHostController,
    viewModel: AppViewModel = viewModel(factory = AppViewModel.factory)
) {

    val idCourt = navController.currentBackStackEntry?.arguments?.getInt(DETAIL_ARGUMENT_KEY).toString()
    val date = navController.currentBackStackEntry?.arguments?.getString(DETAIL_ARGUMENT_KEY2).toString()


    val timeSlots by viewModel.getAvailableTimeSlots(idCourt.toInt(), date).observeAsState()
    val courtDetails by viewModel.getCourtById(idCourt.toInt()).observeAsState()

    var expandedTimeSlot by remember { mutableStateOf(false) }
    var expandedEquipments by remember { mutableStateOf(false) }

    var selectedEquipments by remember { mutableStateOf("Select equipments") }
    var selectedTimeSlot by remember { mutableStateOf("Select time slot") }

    val equipments = listOf("Not requested", "Requested")
    val bitmap = courtDetails?.image?.let { BitmapConverter.converterStringToBitmap(it) }
    val coroutineScope = rememberCoroutineScope()
    var validationMessageShown by remember { mutableStateOf(false) }
    var bookingMessageShown by remember { mutableStateOf(false) }

    // Shows the validation message.
    suspend fun showEditMessage() {
        if (!validationMessageShown) {
            validationMessageShown = true
            delay(2000L)
            validationMessageShown = false
        }
    }

    // Shows the bookingmessage.
    suspend fun showBookingMessage() {
        if (!bookingMessageShown) {
            bookingMessageShown = true
            delay(2000L)
            bookingMessageShown = false
        }
    }




    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        CustomToolbarWithBackArrow(title = "Book court", navController = navController)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Choose details to complete your booking for ${courtDetails?.court_name}",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())

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
                        Column(modifier = Modifier.padding(4.dp)) {
                            Row {
                                    Text(text = "Date: $date")
                            }
                            Row {
                                courtDetails?.address?.let {
                                    Text(
                                        text = "Address: $it",
                                    )
                                }
                            }
                            Row {
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


        if(timeSlots?.isNotEmpty() == true) {


            Column {


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


                if(validationMessageShown){
                    ValidationBookingMessage(validationMessageShown)
                }

                if(bookingMessageShown){
                    BookingCompletedMessage(bookingMessageShown)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Button(
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            if (selectedTimeSlot != "Select time slot" && selectedEquipments != "Select equipments") {
                                courtDetails?.id_court?.let {
                                    viewModel.addReservation(
                                        null, 1,
                                        it, selectedTimeSlot, date, selectedEquipments, ""
                                    )
                                }

                                coroutineScope.launch {
                                    showBookingMessage()
                                }

                            } else {
                                //dialog please chose timeslot and equipments
                                coroutineScope.launch {
                                    showEditMessage()
                                }
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
        else{
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),) {
                Text(
                    text = "No available time slot for this day, sorry!",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color=MaterialTheme.colors.primary
                )
            }

        }


    }
    Spacer(modifier = Modifier.height(20.dp))
}