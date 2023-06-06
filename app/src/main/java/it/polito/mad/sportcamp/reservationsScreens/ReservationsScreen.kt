package it.polito.mad.sportcamp.reservationsScreens

import android.content.ContentValues
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import it.polito.mad.sportcamp.ui.theme.SportCampTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.sportcamp.Calendar.Calendar
import it.polito.mad.sportcamp.R
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.common.CustomToolBar
import it.polito.mad.sportcamp.classes.Reservation
import it.polito.mad.sportcamp.classes.ReservationTimed
import it.polito.mad.sportcamp.classes.TimeSlot
import it.polito.mad.sportcamp.classes.User
import it.polito.mad.sportcamp.ui.theme.*


class ReservationsViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val timeSlots = MutableLiveData<List<TimeSlot>>()
    private val _loadingState = MutableLiveData(true)
    val loadingState: LiveData<Boolean> = _loadingState
    lateinit var user: FirebaseUser
    private val userDocument = MutableLiveData<User>()
    val userData: LiveData<User> get() = userDocument

     fun getUserUID(): String {
        if (!::user.isInitialized) {
            user = Firebase.auth.currentUser!!
        }
        return user.uid
    }

    fun getUserDocument() {
        db.collection("users")
            .document(getUserUID())
            .get()
            .addOnSuccessListener { document ->
                val userObject = document.toObject(User::class.java)
                userDocument.value = userObject
            }
            .addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Error getting user document: ${e.message}")
            }
    }


    fun getLoadingState(): Boolean {
        return _loadingState.value ?: false
    }

    private fun setLoadingState(loading: Boolean) {
        _loadingState.value = loading
    }


    fun getTimeSlots(): MutableLiveData<List<TimeSlot>> {
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
        return timeSlots
    }


    fun getReservationsByUser(): MutableLiveData<List<Reservation>> {
        val reservations = MutableLiveData<List<Reservation>>()

        db.collection("reservations")
            .whereArrayContains("users", getUserUID())
            .whereEqualTo("state", "Confirmed")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(ContentValues.TAG, "Error getting documents.")
                }
                if (value != null) {
                    val reservationList = mutableListOf<Reservation>()
                    for (doc in value.documents) {
                        val reservation = doc.toObject(Reservation::class.java)
                        if (reservation != null) {
                            reservationList.add(reservation)
                        }
                    }
                    reservations.value = reservationList
                    setLoadingState(false)
                }
            }

        return reservations
    }

    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ReservationsViewModel()
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationsScreen(
    navController: NavController,
    vm: ReservationsViewModel = viewModel(factory = ReservationsViewModel.factory)
) {

    val reservationsTimed = mutableListOf<ReservationTimed>()
    val reservations by vm.getReservationsByUser().observeAsState()
    val timeSlots by vm.getTimeSlots().observeAsState()
    val isLoading = vm.loadingState.value ?: true
    LaunchedEffect(Unit) {
        vm.getUserDocument() // Fetch the initial user document
    }

    val user by vm.userData.observeAsState()

    reservations?.let { reservationList -> // Ottieni la lista di oggetti TimeSlot
        for (reservation in reservationList) {
            val timeSlot = timeSlots?.find { it.id_time_slot == reservation.id_time_slot }
            if (timeSlot != null) {
                val reservationTimed = ReservationTimed(
                    reservation.id_reservation,
                    reservation.users,
                    reservation.id_user,
                    reservation.id_court,
                    timeSlot.time_slot,
                    reservation.date,
                    reservation.equipments,
                    reservation.options
                )
                reservationsTimed.add(reservationTimed)
            }
        }
    }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SportCampTheme {
            CustomToolBar(title = "My reservations")
            Spacer(modifier = Modifier.height(3.dp))
            Box(modifier = Modifier.weight(1f)) { // Use Box to allow Calendar to occupy available space
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.material3.CircularProgressIndicator() // Spinner
                            }
                        }
                        else -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 16.dp)
                            ) {
                                user?.nickname?.let { WelcomeCard(it) }
                            }
                            Calendar(
                                navController = navController,
                                reservationsList = reservationsTimed
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                androidx.compose.material3.FloatingActionButton(
                    onClick = { navController.navigate(route = Screen.AddReservations.route) },
                    containerColor = MaterialTheme.colors.primary,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(16.dp)
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

@Composable
fun WelcomeCard(userName: String) {

    //val userImage = user?.image?.let { BitmapConverter.converterStringToBitmap(it) }


    Card(
        elevation = 5.dp,
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .padding(top = 5.dp, bottom = 30.dp),
        shape = RoundedCornerShape(10.dp),
    ) {
        Row {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(5.dp)
                    .padding(top=20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.sport_camp),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(60.dp)
                            .border(2.dp, Blue.copy(0.6f), CircleShape),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(3f)
                    .padding(horizontal = 2.dp)
                    .padding(bottom = 10.dp)
            ) {
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = "Hey, $userName!",
                    fontSize = 15.sp
                )
                Row {
                    Text(
                        modifier = Modifier.padding(top = 2.dp, end = 5.dp),
                        text = "This is your reservations calendar to manage and view" +
                                " your bookings. Add new reservations or publish open " +
                                "matches by clicking on the plus button to try your hand " +
                                "at new challenges! Have fun!",
                        color = Color.Gray,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }

}






