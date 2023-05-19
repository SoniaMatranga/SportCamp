package it.polito.mad.sportcamp.reservationsScreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import it.polito.mad.sportcamp.ui.theme.SportCampTheme
import androidx.compose.foundation.lazy.rememberLazyListState
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationsScreen(
    navController: NavController,
    viewModel: AppViewModel = viewModel(factory = AppViewModel.factory)
) {
   val lazyListState = rememberLazyListState()

   val reservations by viewModel.getReservationsByUser(1).observeAsState()

    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SportCampTheme{
            CustomToolBar(title = "Reservations")
            Spacer(modifier = Modifier.height(10.dp))
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
                                tint = MaterialTheme.colors.secondary
                            )
                            // Toggle the visibility of the content with animation.
                           /* AnimatedVisibility(visible = !lazyListState.isScrollingUp()) {
                                Text(
                                    text = "Add",
                                    modifier = Modifier
                                        .padding(start = 8.dp, top = 3.dp),
                                    color= MaterialTheme.colors.secondary

                                )
                            }*/

                        }

                    }
                }

            }

           
        }
    }
}
data class Content(val author: String, val body: String)


/*

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@Composable
fun ReservationsList(reservations: List<Content>) {
    LazyColumn {
        items(reservations) { reservationContent ->
            ReservationCard(reservationContent)
        }
    }
}



@Composable
fun ReservationCard(msg: Content) {


        Row(modifier = Modifier.padding(all = 8.dp)) {
            /*Image(
                painter = painterResource(R.drawable.sport_camp_logo),
                contentDescription = "Court picture",
                modifier = Modifier
                    // Set image size to 40 dp
                    .size(40.dp)
                    // Clip image to be shaped as a circle
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colors.primary, CircleShape)
            )*/
            // Add a horizontal space between the image and the column


            Spacer(modifier = Modifier.width(10.dp))

            // We keep track if the message is expanded or not in this
            // variable
            var isExpanded by remember { mutableStateOf(false) }

            // surfaceColor will be updated gradually from one color to the other
            val surfaceColor by animateColorAsState(
                if (isExpanded) MaterialTheme.colors.secondary else MaterialTheme.colors.background,
            )

            Column(modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }) {

                Text(text = msg.author,
                    modifier = Modifier.fillMaxWidth())

                // Add a vertical space between the author and message texts
                Spacer(modifier = Modifier.height(4.dp))

                    Surface(shape = MaterialTheme.shapes.medium, elevation = 5.dp,
                        // surfaceColor color will be changing gradually from primary to surface
                        color = surfaceColor,
                        // animateContentSize will change the Surface size gradually
                        modifier = Modifier
                            .animateContentSize()
                            .padding(1.dp)) {
                        Text(
                            text = msg.body,
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
        }



}
*/




