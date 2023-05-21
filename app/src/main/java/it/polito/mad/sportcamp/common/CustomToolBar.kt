package it.polito.mad.sportcamp.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import it.polito.mad.sportcamp.ui.theme.OrangeActionBar
import it.polito.mad.sportcamp.ui.theme.*


@Composable
fun CustomToolBar(title: String) {
    TopAppBar(
        title = { Text(text = title, fontFamily = fonts) },
        modifier = Modifier
            .fillMaxWidth()
            .background(OrangeActionBar)
    )
}

