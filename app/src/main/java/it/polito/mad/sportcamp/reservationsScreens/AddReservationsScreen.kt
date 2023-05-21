package it.polito.mad.sportcamp.reservationsScreens

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Man
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsFootball
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.database.AppViewModel
import it.polito.mad.sportcamp.database.Court
import it.polito.mad.sportcamp.common.CustomToolbarWithBackArrow
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AddReservationsScreen(
    navController: NavController,
    viewModel: AppViewModel = viewModel(factory = AppViewModel.factory)
) {
    var sportFilter by remember { mutableStateOf("Football") }
    val courts by viewModel.getCourtsBySport(sportFilter).observeAsState()

    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val currentDate = sdf.format(Date())

    val calendarState = rememberSheetState()

    var dateFilter by remember { mutableStateOf(currentDate) }



    val context = LocalContext.current
    val sports = arrayOf( "Basketball","Football", "Tennis", "Volleyball")
    var expanded by remember { mutableStateOf(false) }
    var selectedIcon by remember {mutableStateOf(Icons.Filled.SportsFootball) }

    when (sportFilter) {
        "Football" -> selectedIcon = Icons.Filled.SportsFootball
        "Tennis" -> selectedIcon = Icons.Filled.SportsTennis
        "Volleyball" -> selectedIcon = Icons.Filled.SportsVolleyball
        "Basketball" -> selectedIcon = Icons.Filled.SportsBasketball
        else -> {selectedIcon = Icons.Filled.Man}
    }


    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
            //disabledDates = listOf(LocalDate.now().plusDays(7))
        ),
        selection = CalendarSelection.Date{ date ->
            Log.d ("SelectedDate", date.toString())
            dateFilter = date.toString()
        })



    Column(
        modifier = Modifier.fillMaxSize()
    ) {
       // CustomToolBar(title = "Favorites")
        //CustomToolbarWithCalendarButton(title = "Add reservations", calendarState = calendarState )
        CustomToolbarWithBackArrow(title = "Add Reservations", navController = navController as NavHostController)
       // CustomToolbarWithCalendarButton(title = "Add Res", calendarState = calendarState)

        Spacer(modifier = Modifier.height(10.dp))
        Text(text="Select a date and a sport:",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())

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
                Text(dateFilter)

            }

            Spacer(modifier = Modifier.height(20.dp))


            Box(

            ) {
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
                        Text(sportFilter)
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
                                    sportFilter = item
                                    expanded = false
                                    Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }

        }
        courts?.let { CourtsList(courts = it, dateFilter = dateFilter, viewModel = viewModel, navController = navController as NavHostController) }
    }



}

@Composable
private fun CourtsList(courts: List<Court>, dateFilter: String , viewModel: AppViewModel, navController: NavHostController) {
    LazyColumn {
        item {
            courts.forEach { courtContent ->
                CourtCard(courtContent, dateFilter,  viewModel, navController)
            }
        }
    }
}


@Composable
private fun CourtCard(court: Court, dateFilter: String , viewModel: AppViewModel, navController: NavHostController) {

    val bitmap = court.image?.let { BitmapConverter.converterStringToBitmap(it) }
    // We keep track if the message is expanded or not in this
    // variable
    var isExpanded by remember { mutableStateOf(false) }
    //val surfaceColor = MaterialTheme.colors.background,


    Card(
        elevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp),
        shape = RoundedCornerShape(10.dp),
    ) {

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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.padding(4.dp)) {
                            Row() {
                                Icon(
                                    modifier = Modifier
                                        .size(25.dp),
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = "Location",
                                )
                                court.court_name?.let {
                                    Text(
                                        text = it,
                                    )
                                }
                            }
                            Row() {
                                Spacer(modifier = Modifier.width(25.dp))
                                court.address?.let {
                                    Text(
                                        text = it,
                                        fontSize = 14.sp,
                                    )
                                }
                            }
                        }

                        Column( modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Row() {
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


                            Row(){
                                court.city?.let {
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

                                Column() {
                                    court.sport?.let {
                                        Text(
                                            text = "Sport: $it",
                                        )
                                    }
                                }


                            }
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
                                            text = "Book now",
                                            fontSize = 13.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                    }


                    /*
                    // Add a vertical space between the author and message texts
                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = MaterialTheme.shapes.medium, elevation = 5.dp,
                        // surfaceColor color will be changing gradually from primary to surface
                        color = MaterialTheme.colors.background,
                        // animateContentSize will change the Surface size gradually
                        modifier = Modifier
                            .animateContentSize()
                            .padding(1.dp)
                    ) {
                        reservation.time_slot?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(all = 4.dp),
                                // If the message is expanded, we display all its content
                                // otherwise we only display the first line
                                maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                            )
                        }
                    }
*/
                }
            }
        }
    }
}

