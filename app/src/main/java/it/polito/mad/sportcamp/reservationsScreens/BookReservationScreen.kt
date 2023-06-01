package it.polito.mad.sportcamp.reservationsScreens

import android.content.ContentValues
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.sportcamp.bottomnav.DETAIL_ARGUMENT_KEY
import it.polito.mad.sportcamp.common.CustomToolbarWithBackArrow
import it.polito.mad.sportcamp.bottomnav.DETAIL_ARGUMENT_KEY2
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.classes.Court
import it.polito.mad.sportcamp.classes.Reservation
import it.polito.mad.sportcamp.classes.TimeSlot
import it.polito.mad.sportcamp.ui.theme.Orange
import kotlinx.coroutines.tasks.await


class BookReservationsViewModel : ViewModel() {
    var expandedTimeSlot by mutableStateOf(false)
    var expandedEquipments by mutableStateOf(false)

    var selectedEquipments by mutableStateOf("Select equipments")
    var selectedTimeSlot by  mutableStateOf("Select time slot")

    private val db = Firebase.firestore
    private val court = MutableLiveData<Court>()
    private val timeSlots = MutableLiveData<List<TimeSlot>>()
    private var user: FirebaseUser = Firebase.auth.currentUser!!


    fun getUserUID(): String{
        return user.uid
    }


    private fun getTimeSlots() {
        db.collection("slots")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(ContentValues.TAG, "Error getting documents.")
                }
                if (value != null) {
                    val timeSlotsList = mutableListOf<TimeSlot>()
                    for (doc in value.documents) {
                        val timeSlot = doc.toObject(TimeSlot::class.java)
                        if (timeSlot != null) {
                            timeSlotsList.add(timeSlot)
                        }
                    }
                    timeSlots.value = timeSlotsList

                }
            }
    }
    fun getCourtById(id_court: Int): MutableLiveData<Court> {
        db.collection("courts")
            .whereEqualTo("id_court", id_court)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(ContentValues.TAG, "Error getting documents.")
                }
                if (value != null && !value.isEmpty) {
                    val courtDocument = value.documents[0]
                    court.value = courtDocument.toObject(Court::class.java)
                }
            }
        return court
    }

    suspend fun getAvailableTimeSlots(courtId: Int?, date: String?): MutableLiveData<List<String>> {
        getTimeSlots()
        val availableTimeSlots = MutableLiveData<List<String>>()

        val reservationsRef = db.collection("reservations")
        val query = reservationsRef
            .whereEqualTo("id_court", courtId)
            .whereEqualTo("date", date)

        try {
            val reservationsSnapshot = query.get().await()
            val reservedTimeSlots = reservationsSnapshot.documents.mapNotNull {
                it.getLong("id_time_slot")?.toString()
            }

            timeSlots.value?.let { allTimeSlots ->
                val availableTimeSlotsList = allTimeSlots
                    .sortedBy { it.id_time_slot }
                    .filterNot { reservedTimeSlots.contains(it.id_time_slot.toString()) }
                    .mapNotNull { it.time_slot }

                availableTimeSlots.value = availableTimeSlotsList
            }
        } catch (e: Exception) {
            Log.w(ContentValues.TAG, "Error getting available time slots", e)
        }

        return availableTimeSlots
    }
    fun addReservation(
        idUser: String?,
        idCourt: Int?,
        timeSlot: String?,
        date: String?,
        equipments: String?,
        options: String?
    ) {
        val reservation = Reservation(
            id_user = idUser,
            id_court = idCourt,
            id_time_slot = getTimeSlotId(timeSlot),
            date = date,
            equipments = equipments,
            options = options
        )

        db.collection("reservations")
            .add(reservation)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "Reservation added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding reservation", e)
            }
    }

    private fun getTimeSlotId(timeSlot: String?): Int? {
        return timeSlots.value?.find { it.time_slot == timeSlot }?.id_time_slot
    }

    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                BookReservationsViewModel()
            }
        }
    }
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookReservationScreen(
    navController: NavHostController,
    vm: BookReservationsViewModel = viewModel(factory = BookReservationsViewModel.factory)
) {

    val idCourt = navController.currentBackStackEntry?.arguments?.getInt(DETAIL_ARGUMENT_KEY).toString()
    val date = navController.currentBackStackEntry?.arguments?.getString(DETAIL_ARGUMENT_KEY2).toString()


    var timeSlots by remember { mutableStateOf(emptyList<String>()) }

    LaunchedEffect(idCourt.toInt(), date) {
        val availableTimeSlots = vm.getAvailableTimeSlots(idCourt.toInt(), date).value
        timeSlots = availableTimeSlots ?: emptyList()
    }

    val courtDetails by vm.getCourtById(idCourt.toInt()).observeAsState()


    val equipments = listOf("Not requested", "Requested")
    val bitmap = courtDetails?.image?.let { BitmapConverter.converterStringToBitmap(it) }
    val context = LocalContext.current


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


        if(timeSlots.isNotEmpty()) {


            Column {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 45.dp, vertical = 10.dp)
                        .background(Color.White)
                ) {
Surface(color=Color.White) {


                    Box(
                        modifier = Modifier.background(Color.White)
                    ) {

                            ExposedDropdownMenuBox(
                                expanded = vm.expandedTimeSlot,
                                onExpandedChange = {
                                    vm.expandedTimeSlot = !vm.expandedTimeSlot
                                },
                                modifier = Modifier.background(Color.White)
                            ) {
                                TextField(
                                    value = vm.selectedTimeSlot,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White),
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = vm.expandedTimeSlot
                                        )
                                    },
                                    colors=TextFieldDefaults.textFieldColors(
                                        textColor = Color.Black,
                                        disabledTextColor = Color.Black,
                                        backgroundColor = Color.White,
                                        cursorColor = Color.Black,
                                        focusedIndicatorColor = Orange,
                                        unfocusedIndicatorColor = Color.Gray
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = vm.expandedTimeSlot,
                                    onDismissRequest = { vm.expandedTimeSlot = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    timeSlots.forEach { item ->
                                        DropdownMenuItem(
                                            content = { Text(text = item) },
                                            onClick = {
                                                vm.expandedTimeSlot = false
                                                vm.selectedTimeSlot = item
                                            }
                                        )
                                    }
                                }
                            }

                    }}

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
                            expanded = vm.expandedEquipments,
                            onExpandedChange = {
                                vm.expandedEquipments = !vm.expandedEquipments
                            },
                            modifier = Modifier.background(Color.Transparent)
                        ) {
                            TextField(
                                value = vm.selectedEquipments,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = vm.expandedEquipments) },
                                colors=TextFieldDefaults.textFieldColors(
                                    textColor = Color.Black,
                                    disabledTextColor = Color.Black,
                                    backgroundColor = Color.White,
                                    cursorColor = Color.Black,
                                    focusedIndicatorColor = Orange,
                                    unfocusedIndicatorColor = Color.Gray
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = vm.expandedEquipments,
                                onDismissRequest = { vm.expandedEquipments = false }
                            ) {
                                equipments.forEach { item ->
                                    DropdownMenuItem(
                                        content = { Text(text = item) },
                                        onClick = {
                                            vm.expandedEquipments = false
                                            vm.selectedEquipments = item
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
                            if (vm.selectedTimeSlot != "Select time slot" && vm.selectedEquipments != "Select equipments") {
                                courtDetails?.id_court?.let {
                                    vm.addReservation(
                                         vm.getUserUID(),
                                        it, vm.selectedTimeSlot, date, vm.selectedEquipments, ""
                                    )
                                }

                                Toast.makeText(context, "Booking successfully completed!", Toast.LENGTH_SHORT).show()

                            } else {
                                //dialog please chose timeslot and equipments
                                Toast.makeText(context, "Select both time slot and equipments!", Toast.LENGTH_SHORT).show()
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