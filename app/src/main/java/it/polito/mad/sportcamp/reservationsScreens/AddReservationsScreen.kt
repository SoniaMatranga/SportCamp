package it.polito.mad.sportcamp.reservationsScreens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.common.CustomToolbarWithBackArrow
import it.polito.mad.sportcamp.database.AppViewModel
import it.polito.mad.sportcamp.database.Court
import java.text.SimpleDateFormat
import java.util.*


 class AddReservationsViewModel : ViewModel() {
    var sportFilter by mutableStateOf("Basketball")
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val currentDate = sdf.format(Date())
    var dateFilter by mutableStateOf(currentDate)
}
@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AddReservationsScreen(
    navController: NavController,
    viewModel: AppViewModel = viewModel(factory = AppViewModel.factory)
) {
    val vm: AddReservationsViewModel = viewModel()
    val courts by viewModel.getCourtsBySport(vm.sportFilter).observeAsState()
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
        courts?.let { CourtsList(courts = it, dateFilter = vm.dateFilter,  navController = navController) }
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                            Row {
                                court.court_name?.let {
                                    Text(
                                        text = it,
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

                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {

                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
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

