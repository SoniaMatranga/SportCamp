package it.polito.mad.sportcamp.common

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import it.polito.mad.sportcamp.ui.theme.fonts

@Composable
fun CustomToolbarWithBackArrow(title: String, navController: NavHostController) {
    TopAppBar(
        title = { Text(text = title, fontFamily = fonts) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "arrowBack",
                    tint = Color.White
                )
            }
        }
    )
}