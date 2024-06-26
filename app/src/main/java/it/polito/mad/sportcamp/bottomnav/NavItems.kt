package it.polito.mad.sportcamp.bottomnav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.polito.mad.sportcamp.ui.theme.Blue


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavItems(){
    val navController = rememberNavController()

    Scaffold (
        bottomBar = {BottomBar(navController = navController)}

    )
    { innerPadding ->
        // Apply the padding globally to the whole BottomNavScreensController
        Box(modifier = Modifier.padding(innerPadding)) {
            BottomNavGraph(navController = navController)
        }
    }

}




@Composable
fun BottomBar(navController:NavHostController)
{
    val screens = listOf(
        Screen.Reservations,
        Screen.Favorites,
        Screen.OpenMatch,
        Screen.Profile
    )

    val navStackBackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navStackBackEntry?.destination

    val showBottomBar = navController
        .currentBackStackEntryAsState().value?.destination?.route in screens.map { it.route }


    Row (
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
            ) {
        if (showBottomBar){
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
    }
    }
}

@Composable
fun AddItem(
    screen: Screen,
    currentDestination: NavDestination?,
    navController: NavHostController
){
    val selected = currentDestination?.hierarchy?.any{ it.route == screen.route} == true

    val background = if(selected) Blue.copy(alpha = 0.6f) else Color.Transparent

    val contentColor = if(selected) Color.White else Color.Black

    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            })
    ){
        Row (
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
                ){
            Icon(
                if(selected) screen.icon_focused else screen.icon,
                contentDescription = "icon",
                tint = contentColor
            )
            AnimatedVisibility(visible = selected) {
                Text(
                    text = screen.title,
                    color = contentColor
                )
            }
        }
    }
}

