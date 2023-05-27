package it.polito.mad.sportcamp.profileScreens


import android.content.ContentValues.TAG
import android.util.Log
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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import it.polito.mad.sportcamp.SportCampApplication
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.database.Dao
import it.polito.mad.sportcamp.database.User
import it.polito.mad.sportcamp.ui.theme.*
import java.lang.System.err


class ProfileViewModel (): ViewModel() {

    private val db = Firebase.firestore
    private val user = MutableLiveData<User>()


    fun getUserDocument() :MutableLiveData<User>{
        db
            .collection("users")
            .document("user1")
            .addSnapshotListener { value, error ->
                if(error != null) Log.w(TAG, "Error getting documents.")
                if(value != null) user.value = value?.toObject(User::class.java)
            }
        return user
    }

    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ProfileViewModel()
            }
        }
    }
}

@Composable
fun ProfileScreen(
    navController: NavController,
    vm: ProfileViewModel = viewModel(factory = ProfileViewModel.factory)
) {

    val user by vm.getUserDocument().observeAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SportCampTheme {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                CustomToolbarWithEditButton(title = "Profile", navController= navController as NavHostController)

                user?.let { Profile(user = it) }
            }
        }
    }
}

private val optionsList: ArrayList<OptionsData> = ArrayList()

@Composable
fun Profile(user: User) {

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
            item{
                UserDetails(user = user)
            }
            // Show the options
            items(optionsList) { item ->
                Card(
                    elevation = 10.dp,
                    modifier = Modifier.padding(horizontal = 30.dp)

                ) {
                    OptionsItemStyle(item = item)
                }
            }
            item{
                SportsListRow(user = user)
                Spacer(modifier = Modifier.height(50.dp))
            }

        }

    }
}



@Composable
private fun UserDetails(user: User) {


    val bitmap = user.image?.let { BitmapConverter.converterStringToBitmap(it) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
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
private fun OptionsItemStyle(item: OptionsData) {

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
            tint = MaterialTheme.colors.primaryVariant
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
fun SportsListRow(user: User){

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
                        fontSize = 10.sp,
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
                        text = "Basket",
                        fontSize = 10.sp,
                        letterSpacing = (0.8).sp,
                        color = Color.Gray

                    )

                }
            }
            if(user.sports?.contains("Football") == true) {
                Column(
                    modifier = Modifier
                        .weight(weight = 4f, fill = false)
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
                        fontSize = 10.sp,
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
                        text = "Volley",
                        fontSize = 10.sp,
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

}

data class OptionsData(val icon: ImageVector, val title: String, val subTitle: String)