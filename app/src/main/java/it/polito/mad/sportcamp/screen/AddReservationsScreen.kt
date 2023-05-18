package it.polito.mad.sportcamp.screen

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.android.material.datepicker.MaterialDatePicker
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.text.SimpleDateFormat
import java.time.LocalDate
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

        Row(modifier= Modifier.fillMaxWidth().
            padding(10.dp),
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomToolbarWithCalendarButton(title: String, calendarState: com.maxkeppeker.sheets.core.models.base.SheetState) {
    TopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.h6) },
        navigationIcon = {
            IconButton(onClick = {
                    calendarState.show()
            }) {
                Icon(
                    Icons.Filled.CalendarMonth,
                    contentDescription = "Calendar",
                )
            }
        })

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun dropDownSportsMenu() {
    val context = LocalContext.current
    val coffeeDrinks = arrayOf("Tennis", "Volleyball", "Basketball")
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(coffeeDrinks[0]) }

    Box(
        modifier= Modifier.fillMaxWidth().
        padding(horizontal = 10.dp, vertical = 3.dp),

    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                coffeeDrinks.forEach { item ->
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
