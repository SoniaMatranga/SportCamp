package it.polito.mad.sportcamp.openMatchScreens
import android.content.ContentValues
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import it.polito.mad.sportcamp.ui.theme.SportCampTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.classes.Court
import it.polito.mad.sportcamp.classes.Reservation
import it.polito.mad.sportcamp.classes.ReservationContent
import it.polito.mad.sportcamp.classes.TimeSlot
import it.polito.mad.sportcamp.classes.User
import it.polito.mad.sportcamp.common.CustomToolBar
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.ui.theme.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate


class OpenMatchViewModel : ViewModel() {

    private val db = Firebase.firestore
    private var user: FirebaseUser = Firebase.auth.currentUser!!
    private val timeSlots = MutableLiveData<List<TimeSlot>>()
    private val usersList = MutableLiveData<List<String>>()
    private val userDocument = MutableLiveData<User>()
     val matchesList = MutableLiveData<List<ReservationContent>>()

    init{
        getUserDocument()
    }

    private fun getUserDocument() :MutableLiveData<User>{
        db
            .collection("users")
            .document(getUserUID())
            .addSnapshotListener { value, error ->
                if(error != null) Log.w(ContentValues.TAG, "Error getting documents.")
                if(value != null) userDocument.value = value.toObject(User::class.java)
            }
        return userDocument
    }


    private fun getUserUID(): String{
        return user.uid
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


    fun getPlayers(users: List<String>): Deferred<List<User>> = viewModelScope.async {
        val playersList = mutableListOf<User>()

        val querySnapshot = db.collection("users")
            .whereIn("id_user", users)
            .get()
            .await()

        for (doc in querySnapshot.documents) {
            val user = doc.toObject(User::class.java)
            if (user != null) {
                playersList.add(user)
            }
        }

        playersList
    }

    suspend fun getOpenMatches(): MutableLiveData<List<ReservationContent>> {
        getTimeSlots()

        val querySnapshot = db.collection("reservations")
            .whereEqualTo("state", "Pending")
            .get()
            .await()

        val reservationList = mutableListOf<ReservationContent>()

        for (doc in querySnapshot.documents) {
            val reservation = doc.toObject(Reservation::class.java)

            if (!reservation?.users?.contains(getUserUID())!!) {
                val courtId = reservation?.id_court

                // Get players using getPlayers and await for the result
                val playersDeferred = getPlayers(reservation.users)
                val players = playersDeferred.await()

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
                    val timeSlotValue =
                        timeSlots.value?.find { it.id_time_slot == reservation?.id_time_slot }
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
                        users = reservation?.users,
                        players = reservation?.players,
                        players_info = players,
                        players_number = reservation?.players_number
                    )
                    reservationList.add(reservationContent)
                }
            }
        }

        matchesList.value = reservationList
        return matchesList
    }

    fun updateReservationById(idReservation: String) {
        val reservationsRef = db.collection("reservations")
        val query = reservationsRef.whereEqualTo("id_reservation", idReservation)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                for (documentSnapshot in querySnapshot.documents) {
                    val reservation = documentSnapshot.toObject(Reservation::class.java)
                    if (reservation != null) {
                        val playerList = reservation.players + ", ${userDocument.value?.nickname.toString()}"
                        val users = reservation.users?.plus("${getUserUID()}")
                        val playersNum = reservation.players_number?.dec()
                        documentSnapshot.reference.update(
                            mapOf(
                                "state" to "Confirmed",
                                "players" to playerList,
                                "users" to users,
                                "players_number" to playersNum
                            )
                        )
                            .addOnSuccessListener {
                                Log.d(ContentValues.TAG, "Reservation confirmed successfully!")
                                val currentMatches = matchesList.value?.toMutableList()
                                currentMatches?.removeAll { it.id_reservation.equals(idReservation) ?: false }
                                matchesList.value = currentMatches
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error updating reservation", e)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error getting reservation", e)
            }
    }

    fun updateReservationPlayersNumberById(idReservation: String) {
        val reservationsRef = db.collection("reservations")
        val query = reservationsRef.whereEqualTo("id_reservation", idReservation)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                for (documentSnapshot in querySnapshot.documents) {
                    val reservation = documentSnapshot.toObject(Reservation::class.java)
                    if (reservation != null) {
                        val playerList = reservation.players + ", ${userDocument.value?.nickname.toString()}"
                        val users = reservation.users?.plus("${getUserUID()}")
                        val playersNum = reservation.players_number?.dec()
                        documentSnapshot.reference.update(
                            mapOf(
                                "players" to playerList,
                                "users" to users,
                                "players_number" to playersNum
                            )
                        )
                            .addOnSuccessListener {
                                Log.d(ContentValues.TAG, "Reservation confirmed successfully!")
                                val currentMatches = matchesList.value?.toMutableList()
                                currentMatches?.removeAll { it.id_reservation.equals(idReservation) ?: false }
                                matchesList.value = currentMatches
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error updating reservation", e)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error getting reservation", e)
            }
    }



    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                OpenMatchViewModel()
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OpenMatchScreen(
    navController: NavController,
    vm: OpenMatchViewModel = viewModel(factory = OpenMatchViewModel.factory)
) {


    //val matches by vm.getOpenMatches().observeAsState(listOf())
    val coroutineScope = rememberCoroutineScope()
    val matchesState = coroutineScope.run {
        val matches: MutableLiveData<List<ReservationContent>> = remember { MutableLiveData<List<ReservationContent>>()}
        launch {
            matches.value = vm.getOpenMatches().value
        }
        matches.observeAsState(emptyList())
    }

    val matches by vm.matchesList.observeAsState()
    val delayedState = remember { mutableStateOf(false) }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SportCampTheme {
            CustomToolBar(title = "Open matches")

            Spacer(modifier = Modifier.height(10.dp))

            if (matches?.isNotEmpty() == true) {
                LazyColumn(
                    modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp),
                ) {
                    items(items = matches!!) { match ->
                        MatchCard(match = match, vm = vm, navController = navController)
                    }
                }
            }
            else if (delayedState.value) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .padding(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Text(
                            text = "No available open matches, sorry! If you are looking for some players you can publish a new reservation request!",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        )
                    }


            }else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                    LaunchedEffect(Unit) {
                        delay(2000L)
                        delayedState.value = true
                    }
                }
            }

        }
    }
}


@Composable
fun MatchCard(match: ReservationContent, vm: OpenMatchViewModel, navController: NavController) {

    val bitmap = match?.image?.let { BitmapConverter.converterStringToBitmap(it) }
    val context = LocalContext.current

    ElevatedCard(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .background(Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(10.dp)

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
                        .height(100.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            )
            {

                Column() {
                    Row() {
                        match?.court_name?.let {
                            Text(
                                text = it,
                                fontSize = 18.sp,
                            )
                        }
                    }
                    Row() {
                        match?.address?.let {
                            Text(
                                text = it,
                                fontSize = 15.sp,
                            )
                        }
                    }
                }


            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                match.players_info?.forEach {
                    val bitm = it.image.let { BitmapConverter.converterStringToBitmap(it!!) }

                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (bitm != null) {
                                    Image(
                                        painter = BitmapPainter(bitm.asImageBitmap()),
                                        contentDescription = "User Picture",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .size(50.dp)
                                            .border(2.dp, Blue.copy(0.6f), CircleShape)
                                            .clickable {
                                                navController.navigate(
                                                    Screen.PlayerProfile.passId(it.id_user!!)
                                                )
                                            },
                                    )
                                }
                                Text(
                                    text = it.nickname!!,
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                            }
                }
            }


            if (match.date?.isNotEmpty() == true) {
                match.date?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Date: $it",
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }
            if (match.time_slot?.isNotEmpty() == true) {
                match.time_slot?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Time slot : $it",
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }

            if (match.players_number!= 0) {
                match.players_number?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Still looking for: $it players",
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }



                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column {
                        Button(
                            shape = RoundedCornerShape(5.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                            onClick = {
                                match.id_reservation?.let {
                                    if(match.players_number == 1) {
                                        vm.updateReservationById(it)
                                        Toast.makeText(context, "Match succesfully confirmed! You can now see it on you calendar", Toast.LENGTH_SHORT).show()
                                    }
                                    else{
                                        vm.updateReservationPlayersNumberById(it)
                                        Toast.makeText(context, "Match succesfully accepted!", Toast.LENGTH_SHORT).show()
                                    } }

                            }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Accept match!",
                                    fontSize = 15.sp,
                                )

                            }
                        }
                    }

                }


        }
    }
}