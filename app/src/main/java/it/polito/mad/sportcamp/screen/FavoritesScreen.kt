package it.polito.mad.sportcamp.screen


import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import it.polito.mad.sportcamp.common.CustomToolBar


@Composable
fun FavoritesScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomToolBar(title = "Favorites")
        //CustomToolbarWithCalendarButton(title = "Add reservations", calendarState = calendarState )

    }
}


/*

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
                    text = "Add reservations screen",
                    style = MaterialTheme.typography.body1
                )
            }
        }

    }
}*/




