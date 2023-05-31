package it.polito.mad.sportcamp.reservationsScreens

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.common.CustomToolbarWithBackArrow
import it.polito.mad.sportcamp.classes.Court
import it.polito.mad.sportcamp.favoritesScreens.RatingStar
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.math.roundToInt


class AddReservationsViewModel : ViewModel() {
    var sportFilter by mutableStateOf("Basketball")
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val currentDate = sdf.format(Date())
    var dateFilter by mutableStateOf(currentDate)
    private val db = Firebase.firestore
    private val courts = MutableLiveData<List<Court>>()

    fun getCourtsBySport(sport: String): MutableLiveData<List<Court>> {
        db.collection("courts")
            .whereEqualTo("sport", sport)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(ContentValues.TAG, "Error getting documents.")
                }
                if (value != null && !value.isEmpty) {

                    val courtsList = mutableListOf<Court>()
                    for (doc in value.documents) {
                        val court = doc.toObject(Court::class.java)
                        if (court != null) {
                            courtsList.add(court)
                        }
                    }
                    courts.value = courtsList
                }
            }
        return courts
    }

    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AddReservationsViewModel()
            }
        }
    }
}
@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AddReservationsScreen(
    navController: NavController,
    vm: AddReservationsViewModel = viewModel(factory = AddReservationsViewModel.factory)
) {

    val courts by vm.getCourtsBySport(vm.sportFilter).observeAsState()
    val calendarState = rememberSheetState()




    val context = LocalContext.current
    val sports = arrayOf( "Basketball","Football", "Tennis", "Volleyball")
    var expanded by remember { mutableStateOf(false) }
    var selectedIcon by remember {mutableStateOf(Icons.Filled.SportsFootball) }

    selectedIcon = when (vm.sportFilter) {
        "Football" -> Icons.Filled.SportsSoccer
        "Tennis" -> Icons.Filled.SportsTennis
        "Volleyball" -> Icons.Filled.SportsVolleyball
        "Basketball" -> Icons.Filled.SportsBasketball
        else -> {
            Icons.Filled.Man
        }
    }

    val today: LocalDate = LocalDate.now()


    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
        ),
        selection = CalendarSelection.Date{ date ->
            Log.d ("SelectedDate", date.toString())
            vm.dateFilter = date.toString()
        })



    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomToolbarWithBackArrow(title = "Add Reservations", navController = navController as NavHostController)

        Spacer(modifier = Modifier.height(10.dp))
        Text(text="Select a date and a sport",
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp))

        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {

            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { calendarState.show()},
                    modifier= Modifier.padding(horizontal = 10.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                border = BorderStroke(2.dp, MaterialTheme.colors.primary)
            ) {

                    Icon(
                        Icons.Filled.CalendarMonth,
                        contentDescription = "Calendar",
                        tint = Color.Black
                    )

                }
                Text(vm.dateFilter)

            }

            Spacer(modifier = Modifier.height(20.dp))


            Box {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    /*TextField(
                        value = selectedText,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    )*/
                    Column(verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = {  },
                            modifier= Modifier.padding(horizontal = 10.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                            border = BorderStroke(2.dp, MaterialTheme.colors.primary)) {

                            Icon(
                                selectedIcon,
                                contentDescription = "Sport",
                                tint = Color.Black
                            )
                        }
                        Text(vm.sportFilter)
                    }
                    ExposedDropdownMenu(
                        expanded = expanded,
                        modifier = Modifier.fillMaxWidth(),
                        onDismissRequest = { expanded = false }
                    ) {
                        sports.forEach { item ->
                            DropdownMenuItem(
                                content = { Text(text = item) },
                                onClick = {
                                    vm.sportFilter = item
                                    expanded = false
                                    Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }

        }

        if(!LocalDate.parse(vm.dateFilter).isBefore(today)) {
            courts?.let {
                CourtsList(
                    courts = it,
                    dateFilter = vm.dateFilter,
                    navController = navController
                )
            }
        }
        else{
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),) {
                Text(
                    text = "You cannot book courts for past dates, sorry!",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color=MaterialTheme.colors.primary
                )
            }
        }
    }



}

@Composable
private fun CourtsList(courts: List<Court>, dateFilter: String , navController: NavHostController) {
    LazyColumn {
        item {
            courts.forEach { courtContent ->
                CourtCard(courtContent, dateFilter,  navController)
            }
        }
    }
}


@Composable
private fun CourtCard(court: Court, dateFilter: String , navController: NavHostController) {

    val bitmap = court.image?.let { BitmapConverter.converterStringToBitmap(it) }


    Card(
        elevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 5.dp),
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(modifier = Modifier.padding(4.dp).weight(4f)) {

                            Row {
                                court.court_name?.let {
                                    Text(
                                        text = it,
                                    )
                                }
                            }
                            Row(modifier = Modifier
                                .clickable (
                                    onClick = {
                                        navController.navigate(
                                            route = Screen.CourtReviewList.passIdCourt(court.id_court!!)
                                        )
                                    }
                                )
                            ) {
                                court.court_rating?.let {
                                    RatingStar(rating = it)
                                }
                                court.court_rating?.let {
                                    val z = ((it * 10.0).roundToInt() / 10.0)
                                    androidx.compose.material3.Text(
                                        text = "($z)",
                                        modifier = Modifier.padding(start = 2.dp)
                                    )
                                }
                            }
                            Row {
                                court.address?.let {
                                    Text(
                                        text = "$it, ${court.city}",
                                        fontSize = 14.sp,
                                    )
                                }
                            }
                        }


                        Column(
                            modifier = Modifier.fillMaxWidth().weight(2f),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    shape = RoundedCornerShape(5.dp),
                                    onClick = {
                                        navController.navigate(
                                            route = Screen.BookReservation.passValues(
                                                court.id_court, dateFilter
                                            )
                                        )
                                    }) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            maxLines = 1,
                                            text = "Book now",
                                            fontSize = 13.sp,
                                            textAlign = TextAlign.Center
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

