package it.polito.mad.sportcamp.openMatchScreens


import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import it.polito.mad.sportcamp.ui.theme.SportCampTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.sportcamp.R
import it.polito.mad.sportcamp.bottomnav.DETAIL_ARGUMENT_KEY
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.classes.User
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.common.CustomToolbarWithBackArrow
import it.polito.mad.sportcamp.ui.theme.*

class PlayerProfileViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val user = MutableLiveData<User>()

    fun getUserDocument(id_user : String) :MutableLiveData<User>{
        db
            .collection("users")
            .document(id_user)
            .addSnapshotListener { value, error ->
                if(error != null) Log.w(TAG, "Error getting documents.")
                if(value != null) user.value = value.toObject(User::class.java)
            }
        return user
    }


    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerProfileViewModel()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlayerProfileScreen(
    navController: NavController,
    vm: PlayerProfileViewModel = viewModel(factory = PlayerProfileViewModel.factory)
) {

    val idUser = navController.currentBackStackEntry?.arguments?.getString(DETAIL_ARGUMENT_KEY).toString()
    val user by vm.getUserDocument(idUser).observeAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SportCampTheme {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                CustomToolbarWithBackArrow(
                    title = "",
                    navController = navController as NavHostController
                )


                user?.let { Profile(user = it) }


            }

        }
    }
}


@Composable
fun Profile(user: User) {


        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if(user.name!=null) {

                item {
                    UserDetails(user = user)
                }


                item{
                    Spacer(modifier = Modifier.height(20.dp))
                    UserDetailsRow(user = user)
                    Spacer(modifier = Modifier.height(20.dp))
                }



                item {
                    SportsListRow(user = user)
                }

                if(user.sports!!.contains("Tennis")) {
                    item {
                        TriStateToggle("Tennis", user.tennis_level)
                    }
                }

                if(user.sports.contains("Basketball")){
                    item{
                        TriStateToggle("Basket", user.basket_level)
                    }
                }

                if(user.sports.contains("Football")){
                    item{
                        TriStateToggle("Football", user.football_level)
                    }
                }

                if(user.sports.contains("Volleyball")) {
                    item {
                        TriStateToggle("Volley", user.volley_level)
                    }
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
                    .padding(horizontal = 16.dp),
            ) {

                // User's name
                user.nickname?.let {
                    Text(
                        text = it,
                        fontSize = 20.sp,
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

}

@Composable
private fun UserDetailsRow(user:User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {



            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(weight = 3f, fill = false)
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
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(weight = 3f, fill = false)


            ) {

                user.age?.let {
                    Text(
                        text = "Age",
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                user.age?.let {
                    Text(
                        text = it.toString(),
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
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(weight = 3f, fill = false)

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

@Composable
fun SportsListRow(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom=5.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Sports",
                fontSize = 16.sp,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val sportsList = listOf(
                Pair("Tennis", Icons.Outlined.SportsTennis),
                Pair("Basketball", Icons.Outlined.SportsBasketball),
                Pair("Football", Icons.Outlined.SportsSoccer),
                Pair("Volleyball", Icons.Outlined.SportsVolleyball)
            )

            sportsList.forEach { (sport, icon) ->
                if (user.sports?.contains(sport) == true) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier.size(25.dp),
                            imageVector = icon,
                            contentDescription = sport,
                            tint = MaterialTheme.colors.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = sport,
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}



data class OptionsData(val icon: ImageVector, val title: String, val subTitle: String)

@Composable
fun TriStateToggle(sport: String, level: String?) {
    val states = listOf(
        "Beginner",
        "Intermediate",
        "Advanced",
    )


    if (level != null) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "$sport level",
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.height(5.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {


            Surface(
                shape = RoundedCornerShape(24.dp),
                elevation = 4.dp,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 10.dp)
            ) {

                Row(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(24.dp))
                        .background(Color.LightGray)
                ) {
                    states.forEach { text ->
                        Text(
                            text = text,
                            color = Color.White,
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(24.dp))
                                .background(
                                    if (text == level) {
                                        MaterialTheme.colors.primary
                                    } else {
                                        Color.LightGray
                                    }
                                )
                                .padding(
                                    vertical = 12.dp,
                                    horizontal = 10.dp,
                                ),
                        )
                    }
                }

            }
        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}