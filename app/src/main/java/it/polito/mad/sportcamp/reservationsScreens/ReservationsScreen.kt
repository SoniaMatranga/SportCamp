package it.polito.mad.sportcamp.reservationsScreens

import android.content.ContentValues
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import it.polito.mad.sportcamp.ui.theme.SportCampTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.sportcamp.Calendar.Calendar
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.common.CustomToolBar
import it.polito.mad.sportcamp.classes.Reservation
import it.polito.mad.sportcamp.classes.ReservationTimed
import it.polito.mad.sportcamp.classes.TimeSlot
import it.polito.mad.sportcamp.ui.theme.*


class ReservationsViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val reservations = MutableLiveData<List<Reservation>>()
    private val timeSlots = MutableLiveData<List<TimeSlot>>()
    private val _loadingState = MutableLiveData<Boolean>(true)
    val loadingState: LiveData<Boolean> = _loadingState

    fun getLoadingState(): Boolean {
        return _loadingState.value ?: false
    }

    fun setLoadingState(loading: Boolean) {
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


    fun getReservationsByUser(id: Int): MutableLiveData<List<Reservation>> {

        db.collection("reservations")
            .whereEqualTo("id_user", id)
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
    val reservations by vm.getReservationsByUser(1).observeAsState()
    val timeSlots by vm.getTimeSlots().observeAsState()
    val isLoading = vm.loadingState.value ?: true

    reservations?.let { reservationList -> // Ottieni la lista di oggetti TimeSlot
        for (reservation in reservationList) {
            val timeSlot = timeSlots?.find { it.id_time_slot == reservation.id_time_slot }
            if (timeSlot != null) {
                val reservationTimed = ReservationTimed(
                    reservation.id_reservation,
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
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                when {
                    isLoading == true -> {
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
                        reservationsTimed?.let {
                            Calendar(
                                navController = navController,
                                reservationsList = it
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    androidx.compose.material3.FloatingActionButton(
                        onClick = { navController.navigate(route = Screen.AddReservations.route) },
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






