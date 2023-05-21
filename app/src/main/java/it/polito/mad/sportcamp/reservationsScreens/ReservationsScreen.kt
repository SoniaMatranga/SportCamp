package it.polito.mad.sportcamp.reservationsScreens

import android.os.Build
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.polito.mad.sportcamp.Calendar.Calendar
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.common.CustomToolBar
import it.polito.mad.sportcamp.database.AppViewModel
import it.polito.mad.sportcamp.ui.theme.*


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationsScreen(
    navController: NavController,
    viewModel: AppViewModel = viewModel(factory = AppViewModel.factory)
) {

   val reservations by viewModel.getReservationsByUser(1).observeAsState()

    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SportCampTheme{
            CustomToolBar(title = "My reservations")
            Spacer(modifier = Modifier.height(3.dp))
            Column( modifier =Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
                ){
                reservations?.let { Calendar(navController =navController, reservationsList = it) }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End) {
                    androidx.compose.material3.FloatingActionButton(
                        onClick = {navController.navigate(route = Screen.AddReservations.route)},
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





