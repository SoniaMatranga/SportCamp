package it.polito.mad.sportcamp.reservationsScreens

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Man
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import it.polito.mad.sportcamp.profileScreens.CustomToolbarWithBackArrow
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReservationsScreen(
    navController: NavController
) {
    val sdf = SimpleDateFormat("yyyy-MM-dd")

    // on below line we are creating a variable for
    // current date and time and calling a simple
    // date format in it.
    val currentDateAndTime = sdf.format(Date())

    //val sdf = SimpleDateFormat("'dd-MM-yyyy'")

    // on below line we are creating a variable for
    // current date and time and calling a simple
    // date format in it.
    val currentDate = sdf.format(Date())

    val calendarState = rememberSheetState()
    var sportFilter by remember { mutableStateOf("Tennis") }
    var dateFilter by remember { mutableStateOf(currentDate) }

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
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {

            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { calendarState.show()},
                    modifier= Modifier.padding(horizontal = 10.dp),
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


                dropDownSportsMenu()


        }

       // displayTxtClock()
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun dropDownSportsMenu() {
    val context = LocalContext.current
    val sports = arrayOf("Tennis", "Volleyball", "Basketball")
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(sports[0]) }
    var selectedIcon by remember {mutableStateOf(Icons.Filled.SportsTennis) }
    when (selectedText) {
        "Tennis" -> selectedIcon = Icons.Filled.SportsTennis
        "Volleyball" -> selectedIcon = Icons.Filled.SportsVolleyball
        "Basketball" -> selectedIcon = Icons.Filled.SportsBasketball
        else -> {selectedIcon = Icons.Filled.Man}
    }
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
                        modifier= Modifier.padding(horizontal = 10.dp),) {

                    Icon(
                        selectedIcon,
                        contentDescription = "Sport",
                        tint = Color.Black
                    )
                }
                Text(selectedText)
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
                            selectedText = item
                            expanded = false
                            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}
