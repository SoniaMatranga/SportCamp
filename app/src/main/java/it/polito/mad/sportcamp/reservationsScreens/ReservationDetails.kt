package it.polito.mad.sportcamp.reservationsScreens

import android.content.ContentValues
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.sportcamp.bottomnav.DETAIL_ARGUMENT_KEY
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.classes.Court
import it.polito.mad.sportcamp.classes.Reservation
import it.polito.mad.sportcamp.classes.ReservationContent
import it.polito.mad.sportcamp.classes.TimeSlot
import it.polito.mad.sportcamp.favoritesScreens.RatingStar
import it.polito.mad.sportcamp.openMatchScreens.MatchCard
import it.polito.mad.sportcamp.ui.theme.fonts
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import kotlin.math.roundToInt


class ReservationDetailsViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val reservations = MutableLiveData<List<ReservationContent>>()
    private val timeSlots = MutableLiveData<List<TimeSlot>>()
    private var user: FirebaseUser = Firebase.auth.currentUser!!

    private fun getUserUID(): String{
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


    fun deleteReservationById(id: String) {

        db.collection("reservations")
            .whereEqualTo("id_reservation", id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val reservationDocuments = querySnapshot.documents
                if (reservationDocuments.isNotEmpty()) {
                    val reservationDocument = reservationDocuments[0]
                    reservationDocument.reference.delete()
                        .addOnSuccessListener {
                            val currentReservations = reservations.value?.toMutableList()
                            currentReservations?.removeAll { it.id_reservation?.equals(id) ?: false }
                            reservations.value = currentReservations
                        }
                        .addOnFailureListener { exception ->
                            Log.w(ContentValues.TAG, "Error deleting reservation.", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting reservation document.", exception)
            }
    }

    fun getReservationsByUserAndDate(date: String): MutableLiveData<List<ReservationContent>> {
        val reservationsContent = MutableLiveData<List<ReservationContent>>()
        getTimeSlots()

        // Utilizza viewModelScope.launch per avviare un'operazione asincrona
        viewModelScope.launch {
            val querySnapshot = db.collection("reservations")
                .whereArrayContains("users", getUserUID())
                .whereEqualTo("date", date)
                .whereEqualTo("state", "Confirmed")
                .get()
                .await()

            val reservationList = mutableListOf<ReservationContent>()

            for (doc in querySnapshot.documents) {
                val reservation = doc.toObject(Reservation::class.java)
                val courtId = reservation?.id_court

                val courtSnapshot = db.collection("courts")
                    .whereEqualTo("id_court", courtId)
                    .get()
                    .await()

                val courtDocuments = courtSnapshot.documents
                if (courtDocuments.isNotEmpty()) {
                    val courtDocument = courtDocuments[0]
                    val courtValue = courtDocument.toObject(Court::class.java)

                    // Retrieve court information
                    val courtName = courtValue?.court_name
                    val courtAddress = courtValue?.address
                    val courtCity = courtValue?.city
                    val courtSport = courtValue?.sport
                    val courtRating = courtValue?.court_rating
                    val courtImage = courtValue?.image

                    // Retrieve time slot information
                    val timeSlotValue = timeSlots.value?.find { it.id_time_slot == reservation?.id_time_slot }
                    val timeSlot = timeSlotValue?.time_slot

                    // Create ReservationContent object and add it to the list
                    val reservationContent = ReservationContent(
                        id_reservation = reservation?.id_reservation,
                        id_court = reservation?.id_court,
                        equipments = reservation?.equipments,
                        court_name = courtName,
                        address = courtAddress,
                        city = courtCity,
                        sport = courtSport,
                        time_slot = timeSlot,
                        date = reservation?.date,
                        image = courtImage,
                        court_rating = courtRating,
                        players = reservation?.players,
                    )
                    reservationList.add(reservationContent)
                }
            }

            // Update the value of reservationsContent once the list is ready
            reservationsContent.value = reservationList
            reservations.value = reservationsContent.value
        }

        return reservations
    }


    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ReservationDetailsViewModel()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationDetails(
    navController: NavHostController,
    viewModel: ReservationDetailsViewModel = viewModel(factory = ReservationDetailsViewModel.factory)
) {
    val selectedDate = navController.currentBackStackEntry?.arguments?.getString(DETAIL_ARGUMENT_KEY).toString()

    val reservations by viewModel.getReservationsByUserAndDate(selectedDate).observeAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        CustomToolbarReservationDetails(title = "My reservations details", navController = navController)

        if (reservations?.isNotEmpty() == true) {
            reservations?.let {
                ReservationsList(reservations = it, selectedDate= selectedDate, viewModel = viewModel, navController=navController)
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material.CircularProgressIndicator()
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

    }
    Spacer(modifier = Modifier.height(20.dp))
}





@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationsList(reservations: List<ReservationContent>, selectedDate:String, viewModel: ReservationDetailsViewModel, navController :NavHostController) {
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
fun ReservationCard(reservation: ReservationContent, selectedDate:String, viewModel: ReservationDetailsViewModel, navController :NavHostController) {

    val bitmap = reservation.image?.let { BitmapConverter.converterStringToBitmap(it) }
    // We keep track if the message is expanded or not in this
    // variable
    var isExpanded by remember { mutableStateOf(false) }
    val today: LocalDate = LocalDate.now()
    val context = LocalContext.current


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
                        Toast.makeText(context, "Reservation correctly deleted", Toast.LENGTH_SHORT).show()
                        //navController.navigate(route = Screen.Reservations.route)

                    }) {
                    Text("Yes, delete it")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                    }) {
                    Text("Cancel")
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
                            Row(modifier = Modifier
                                .clickable (
                                    onClick = {
                                        navController.navigate(
                                            route = Screen.CourtReviewList.passIdCourt(reservation.id_court!!)
                                        )
                                    }
                                )
                            ) {
                                reservation.court_rating?.let {
                                    RatingStar(rating = it)
                                }
                                reservation.court_rating?.let {
                                    val z = ((it * 10.0).roundToInt() / 10.0)
                                    androidx.compose.material3.Text(
                                        text = "($z)",
                                        modifier = Modifier.padding(start = 2.dp)
                                    )
                                }
                            }

                            Row {
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

                                    reservation.players?.let {
                                        Text(
                                            text = "Players: $it",
                                        )
                                    }
                                }


                            }

                            Spacer(modifier = Modifier.height(8.dp))


                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if(!LocalDate.parse(reservation.date).isBefore(today)) {
                                    Column {


                                        Button(
                                            shape = RoundedCornerShape(5.dp),
                                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
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
                                                    color = Color.White
                                                )
                                                Icon(
                                                    Icons.Outlined.Edit,
                                                    contentDescription = "Edit",
                                                    tint = Color.White
                                                )
                                            }
                                        }

                                    }
                                    Column {
                                        Button(
                                            shape = RoundedCornerShape(5.dp),
                                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
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


