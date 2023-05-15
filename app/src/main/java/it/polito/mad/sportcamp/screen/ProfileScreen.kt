package it.polito.mad.sportcamp.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.polito.mad.sportcamp.R
import it.polito.mad.sportcamp.database.AppViewModel
import it.polito.mad.sportcamp.ui.theme.SportCampTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.polito.mad.sportcamp.common.CustomToolBar
import it.polito.mad.sportcamp.database.User


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

                CustomToolBar(title = "Profile")
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {

            item {
                // User's image, name, email and edit button
                UserDetails(user = user, navController= navController)
            }

            // Show the options
            items(optionsList) { item ->
                OptionsItemStyle(item = item, context = context)
            }

        }
    }
}

// This composable displays user's image, name, email and edit button
@Composable
private fun UserDetails(user: User,  navController: NavController) {


        // User's image
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.user_image1),
            contentDescription = "Profile picture",
            modifier = Modifier
                // Clip image to be shaped as a circle
                .clip(CircleShape)
                .border(
                    2.dp,
                    MaterialTheme.colors.primary,
                    CircleShape
                )
        )

    }

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

                // User's name
                    user.nickname?.let {
                        Text(
                            text = it,
                            style = TextStyle(
                                fontSize = 22.sp,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                // User's bio
                user.bio?.let {
                    Text(
                        text = it,
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.Gray,
                            letterSpacing = (0.8).sp
                        ),
                        overflow = TextOverflow.Ellipsis
                    )
                }

                }

                Spacer(modifier = Modifier.height(2.dp))
            // Edit button
            IconButton(
                modifier = Modifier
                    .weight(weight = 1f, fill = false),
                onClick = {
                    //Toast.makeText(context, "Edit Button", Toast.LENGTH_SHORT).show()
                    navController.navigate("editProfile")
                }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Details",
                    tint = MaterialTheme.colors.primary
                )
            }

        }



        }

}

// Row style for options
@Composable
private fun OptionsItemStyle(item: OptionsData, context: Context) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = true) {
                Toast
                    .makeText(context, item.title, Toast.LENGTH_SHORT)
                    .show()
            }
            .padding(all = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Icon
        Icon(
            modifier = Modifier
                .size(25.dp),
            imageVector = item.icon,
            contentDescription = item.title,
            tint = MaterialTheme.colors.primary
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
                    style = TextStyle(
                        fontSize = 16.sp,
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Sub title
                Text(
                    text = item.subTitle,
                    style = TextStyle(
                        fontSize = 14.sp,
                        letterSpacing = (0.8).sp,
                        color = Color.Gray

                    )
                )

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
            icon = appIcons.Mail,
            title = "Mail",
            subTitle = user.mail.toString()
        )
    )

    optionsList.add(
        OptionsData(
            icon = appIcons.LocationCity,
            title = "City",
            subTitle = user.city.toString()
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
}

data class OptionsData(val icon: ImageVector, val title: String, val subTitle: String)