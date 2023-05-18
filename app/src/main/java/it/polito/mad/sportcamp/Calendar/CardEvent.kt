package it.polito.mad.sportcamp.Calendar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun CardEvent(
    scrollStateCard: ScrollState = rememberScrollState(),
) {
    Surface(
        modifier = Modifier,
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollStateCard),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Evento",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.h4,
            )
            Text(
                text = "Eventooo",
                modifier = Modifier.padding(4.dp),
                style = MaterialTheme.typography.h5,
            )
            Text(
                text = "yes evento",
                modifier = Modifier.padding(4.dp),
                style = MaterialTheme.typography.h6,
            )
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(10.dp))
            Text(
                text = "Eventino",
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                style = MaterialTheme.typography.h6,
            )
        }
    }
}