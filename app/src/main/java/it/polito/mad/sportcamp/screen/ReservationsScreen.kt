package it.polito.mad.sportcamp.screen

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.res.painterResource
import it.polito.mad.sportcamp.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import it.polito.mad.sportcamp.ui.theme.SportCampTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*

@Composable
fun ReservationsScreen() {
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SportCampTheme{
            Surface (modifier = Modifier.fillMaxSize()) {
                ReservationsList(mutableListOf(
                    Content("Reservation name 1", "Info \n Info2 \n Info 3"),
                    Content("Reservation name 2", "Info"),
                    Content("Reservation name 3", "Info"),
                    Content("Reservation name 4", "Info"),
                ))
                //ReservationCard(Content("Author name", "Info"))
            }
        }
    }
}
data class Content(val author: String, val body: String)

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
            Image(
                painter = painterResource(R.drawable.sport_camp_logo),
                contentDescription = "Court picture",
                modifier = Modifier
                    // Set image size to 40 dp
                    .size(40.dp)
                    // Clip image to be shaped as a circle
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colors.primary, CircleShape)
            )
            // Add a horizontal space between the image and the column


            Spacer(modifier = Modifier.width(10.dp))

            // We keep track if the message is expanded or not in this
            // variable
            var isExpanded by remember { mutableStateOf(false) }

            // surfaceColor will be updated gradually from one color to the other
            val surfaceColor by animateColorAsState(
                if (isExpanded) MaterialTheme.colors.secondary else MaterialTheme.colors.background,
            )

            Column(modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded}) {

                Text(text = msg.author,
                    modifier = Modifier.fillMaxWidth())

                // Add a vertical space between the author and message texts
                Spacer(modifier = Modifier.height(4.dp))

                    Surface(shape = MaterialTheme.shapes.medium, elevation = 5.dp,
                        // surfaceColor color will be changing gradually from primary to surface
                        color = surfaceColor,
                        // animateContentSize will change the Surface size gradually
                        modifier = Modifier.animateContentSize().padding(1.dp)) {
                        Text(
                            text = msg.body,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.fillMaxWidth()
                                .padding(all = 4.dp),
                            // If the message is expanded, we display all its content
                            // otherwise we only display the first line
                            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                        )
                    }

            }
        }



}



