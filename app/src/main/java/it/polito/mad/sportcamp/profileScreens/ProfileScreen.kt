package it.polito.mad.sportcamp.profileScreens

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.polito.mad.sportcamp.database.AppViewModel
import it.polito.mad.sportcamp.ui.theme.SportCampTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.database.User
import it.polito.mad.sportcamp.ui.theme.*

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AppViewModel = viewModel(factory = AppViewModel.factory)
) {

    val user by viewModel.getUserById(1).observeAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SportCampTheme {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top appbar
                //TopAppbarProfile(context = LocalContext.current.applicationContext)

                CustomToolbarWithEditButton(title = "Profile", navController= navController as NavHostController)

                user?.let { Profile(user = it, navController= navController) }
            }
        }
    }
}

private val optionsList: ArrayList<OptionsData> = ArrayList()

/*
@Composable
fun TopAppbarProfile(context: Context) {
    TopAppBar(
        title = {
            Text(
                text = "Profile",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 4.dp,
        navigationIcon = {
            IconButton(onClick = {
                Toast.makeText(context, "Nav Button", Toast.LENGTH_SHORT).show()
            }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }
        }
    )
}*/

@Composable
fun Profile(user: User, navController: NavController, context: Context = LocalContext.current.applicationContext) {

    // This indicates if the optionsList has data or not
    // Initially, the list is empty. So, its value is false.
    var listPrepared by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            optionsList.clear()

            // Add the data to optionsList
            prepareOptionsData(user)

            listPrepared = true
        }
    }

    if (listPrepared) {

        // User's image, name, email



        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item{
                UserDetails(user = user, navController= navController)
            }
            // Show the options
            items(optionsList) { item ->
                Card(
                    elevation = 10.dp,
                    modifier = Modifier.padding(horizontal = 30.dp)

                ) {
                    OptionsItemStyle(item = item, context = context)
                }
            }
            item{
                sportsListRow(user = user)
                Spacer(modifier = Modifier.height(50.dp))
            }

        }




    }
}



// This composable displays user's image, name, email
@Composable
private fun UserDetails(user: User,  navController: NavController) {


    val bitmap = user.image?.let { BitmapConverter.converterStringToBitmap(it) }
   // val listColors = listOf(  MaterialTheme.colors.primary, Color.Yellow)
    // User's image

    Box(
        modifier = Modifier
            .fillMaxWidth()
            /*.background(
                Brush.verticalGradient(
                    listColors,
                    tileMode = TileMode.Repeated
                )
            )*/
            .padding(all = 8.dp),
        contentAlignment = Alignment.Center
    ) {

        if (bitmap != null) {
            Image(
                painter = BitmapPainter(bitmap.asImageBitmap()),
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    // Clip image to be shaped as a circle
                    .clip(CircleShape)
                    .size(200.dp)
                    .border(
                        2.dp,
                        MaterialTheme.colors.secondaryVariant,
                        CircleShape
                    )
            )
        }

    }


    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(weight = 3f, fill = false)
                    .padding(start = 16.dp),
            ) {

                // User's name
                user.nickname?.let {
                    Text(
                        text = it,
                        fontSize = 27.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // User's bio
                user.bio?.let {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = it,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        letterSpacing = (0.8).sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            }

        }

    }
// ======================= TEST =========================
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(weight = 3f, fill = false)
                    .padding(start = 16.dp)

            ) {

                // User's level
                user.level?.let {
                    Text(
                        text = "Level",
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // User's level value
                user.level?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        letterSpacing = (0.8).sp,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }

            Column(
                modifier = Modifier
                    .weight(weight = 3f, fill = false)
                    .padding(start = 16.dp)
            ) {

                // User's city
                user.city?.let {
                    Text(
                        text = "City",
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // User's city value
                user.city?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        letterSpacing = (0.8).sp,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }

            Column(
                modifier = Modifier
                    .weight(weight = 3f, fill = false)
                    .padding(start = 16.dp)

            ) {

                // User's gender
                user.gender?.let {
                    Text(
                        text = "Gender",
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // User's gender value
                user.gender?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        letterSpacing = (0.8).sp,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }

        }

    }

}

// Row style for options
@Composable
private fun OptionsItemStyle(item: OptionsData, context: Context) {

    Row(
        modifier = Modifier
            .padding(all = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Icon
        Icon(
            modifier = Modifier
                .size(25.dp),
            imageVector = item.icon,
            contentDescription = item.title,
            tint = MaterialTheme.colors.secondary
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(weight = 3f, fill = false)
                    .padding(start = 16.dp)
            ) {

                // Title
                Text(
                    text = item.title,
                    fontSize = 16.sp,

                )

                Spacer(modifier = Modifier.height(2.dp))

                // Sub title
                Text(
                    text = item.subTitle,
                    fontSize = 14.sp,
                    letterSpacing = (0.8).sp,
                    color = Color.Gray

                )

            }

        }

    }
}

@Composable
fun CustomToolbarWithEditButton(title: String, navController: NavHostController) {
    TopAppBar(
        title = { Text(text = title, fontFamily = fonts) },
        actions = {
            IconButton(onClick = {navController.navigate(route = Screen.EditProfile.passId(1))}) {
                Icon(Icons.Filled.Edit,
                    contentDescription = "edit",
                    tint = Color.White)
            }
        })



}


@Composable
fun sportsListRow(user: User){

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Title
        Text(
            text = "Sports",
            fontSize = 16.sp,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(weight = 3f, fill = false)
                    .padding(start = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if(user.sports?.contains("Tennis") == true) {
                    // Icon
                    Icon(
                        modifier = Modifier
                            .size(25.dp),
                        imageVector = Icons.Outlined.SportsTennis,
                        contentDescription = "Tennis",
                        tint = MaterialTheme.colors.primary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Sub title
                    Text(
                        text = "Tennis",
                        fontSize = 14.sp,
                        letterSpacing = (0.8).sp,
                        color = Color.Gray
                    )
                }

            }
            if(user.sports?.contains("Basketball") == true) {
                Column(
                    modifier = Modifier
                        .weight(weight = 3f, fill = false)
                        .padding(start = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Icon
                    Icon(
                        modifier = Modifier
                            .size(25.dp),
                        imageVector = Icons.Outlined.SportsBasketball,
                        contentDescription = "Basketball",
                        tint = MaterialTheme.colors.primary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Sub title
                    Text(
                        text = "Basketball",
                        fontSize = 14.sp,
                        letterSpacing = (0.8).sp,
                        color = Color.Gray

                    )

                }
            }
            if(user.sports?.contains("Football") == true) {
                Column(
                    modifier = Modifier
                        .weight(weight = 3f, fill = false)
                        .padding(start = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Icon
                    Icon(
                        modifier = Modifier
                            .size(25.dp),
                        imageVector = Icons.Outlined.SportsFootball,
                        contentDescription = "Football",
                        tint = MaterialTheme.colors.primary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Sub title
                    Text(
                        text = "Football",
                        fontSize = 14.sp,
                        letterSpacing = (0.8).sp,
                        color = Color.Gray

                    )

                }
            }
            if(user.sports?.contains("Volleyball") == true) {
                Column(
                    modifier = Modifier
                        .weight(weight = 3f, fill = false)
                        .padding(start = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Icon
                    Icon(
                        modifier = Modifier
                            .size(25.dp),
                        imageVector = Icons.Outlined.SportsVolleyball,
                        contentDescription = "Volleyball",
                        tint = MaterialTheme.colors.primary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Sub title
                    Text(
                        text = "Volleyball",
                        fontSize = 14.sp,
                        letterSpacing = (0.8).sp,
                        color = Color.Gray

                    )

                }
            }

        }

    }

}

private fun prepareOptionsData(user: User) {

    val appIcons = Icons.Outlined

    optionsList.add(
        OptionsData(
            icon = appIcons.Person,
            title = "Name",
            subTitle = user.name.toString()
        )

    )

    optionsList.add(
        OptionsData(
            icon = appIcons.CalendarMonth,
            title = "Age",
            subTitle = user.age.toString()
        )
    )

    optionsList.add(
        OptionsData(
            icon = appIcons.Mail,
            title = "Mail",
            subTitle = user.mail.toString()
        )
    )


    /*
       optionsList.add(
           OptionsData(
               icon = appIcons.LocationCity,
               title = "City",
               subTitle = user.city.toString()
           )
       )





       optionsList.add(
           OptionsData(
               icon = appIcons.Transgender,
               title = "Gender",
               subTitle = user.gender.toString()
           )
       )

       optionsList.add(
           OptionsData(
               icon = appIcons.TrendingUp,
               title = "Level",
               subTitle = user.level.toString()
           )
       )


       optionsList.add(
           OptionsData(
               icon = appIcons.DirectionsRun,
               title = "Sports",
               subTitle = user.sports.toString()
           )
       )

        */

}

data class OptionsData(val icon: ImageVector, val title: String, val subTitle: String)