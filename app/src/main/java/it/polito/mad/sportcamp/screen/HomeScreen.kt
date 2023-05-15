package it.polito.mad.sportcamp.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import it.polito.mad.sportcamp.R
import androidx.compose.ui.unit.dp
import it.polito.mad.sportcamp.common.CustomToolBar


@Composable
fun HomeScreen() {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomToolBar(title = "Home")
        Text(text = "Home Screen", fontSize = 20.sp)
       // CardComponent()

    }
}


//card that expands
@Composable
fun CardComponent() {

    Card(
        elevation = 50.dp,
        modifier = Modifier.padding(50.dp)
    ) {
        var expanded by remember { mutableStateOf(false) }
        //raster image
        val image = ImageBitmap.imageResource(id = R.drawable.sport_camp_logo)
        Column(Modifier.clickable { expanded = !expanded }) {
            Image(bitmap = image, contentDescription = "image")
            AnimatedVisibility(expanded) {
                Text(
                    text = "Home Screen",
                    style = MaterialTheme.typography.body1
                )
            }
        }

    }
}