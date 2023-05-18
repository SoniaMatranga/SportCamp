package it.polito.mad.sportcamp.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import it.polito.mad.sportcamp.ui.theme.OrangeActionBar


@Composable
fun CustomToolBar(title: String) {
    TopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.h6) },
        modifier = Modifier
            .fillMaxWidth()
            .background(OrangeActionBar)
       /* navigationIcon = {
            IconButton(onClick = { onButtonClicked() } ) {
                Icon(Icons.Default.Menu, contentDescription = "navigation drawer")
            }
        },*/
    )
}