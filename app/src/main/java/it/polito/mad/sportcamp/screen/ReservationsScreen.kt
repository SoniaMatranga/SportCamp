package it.polito.mad.sportcamp.screen

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.res.painterResource
import it.polito.mad.sportcamp.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import it.polito.mad.sportcamp.ui.theme.SportCampTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.common.CustomToolBar
import it.polito.mad.sportcamp.ui.theme.GreenActionBar

@Composable
fun ReservationsScreen(
    navController: NavController
) {
   val lazyListState = rememberLazyListState()

    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SportCampTheme{
            Column() {
                CustomToolBar(title = "Reservations")
                Surface (modifier = Modifier.fillMaxWidth()) {
                    ReservationsList(mutableListOf(
                        Content("Reservation name 1", "Info \n Info2 \n Info 3"),
                        Content("Reservation name 2", "Info"),
                        Content("Reservation name 3", "Info"),
                        Content("Reservation name 4", "Info"),
                    ))
                    //ReservationCard(Content("Author name", "Info"))

                }
                Row(modifier = Modifier.fillMaxWidth()
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
                            AnimatedVisibility(visible = !lazyListState.isScrollingUp()) {
                                Text(
                                    text = "Add",
                                    modifier = Modifier
                                        .padding(start = 8.dp, top = 3.dp),
                                    color= MaterialTheme.colors.secondary

                                )
                            }

                        }

                    }
                }

            }

           
        }
    }
}
data class Content(val author: String, val body: String)

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





