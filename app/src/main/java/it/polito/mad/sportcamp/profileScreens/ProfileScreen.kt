package it.polito.mad.sportcamp.profileScreens


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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.sportcamp.R
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.classes.User
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.common.LoadingState
import it.polito.mad.sportcamp.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

class ProfileViewModel : ViewModel() {

    private val db = Firebase.firestore
    val loadingState = MutableStateFlow(LoadingState.IDLE)
    private lateinit var auth: FirebaseAuth
    private lateinit var userFirebase: FirebaseUser
    val data = hashMapOf(
        "lastLogin" to com.google.firebase.Timestamp(Date(System.currentTimeMillis()))
    )
    var updated = false
    private val userDocument = MutableLiveData<User>()
    val user: LiveData<User> get() = userDocument

    fun signWithCredential(credential: AuthCredential) = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            Firebase.auth.signInWithCredential(credential).await()
            auth = Firebase.auth
            userFirebase = auth.currentUser!!
            db.collection("users").document(auth.uid!!).set(data, SetOptions.merge())
            db.collection("users")
                .document(getUserUID())
                .get()
                .addOnSuccessListener { document ->
                    val userObject = document.toObject(User::class.java)
                    userDocument.value = userObject
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error getting user document: ${e.message}")
                }
            loadingState.emit(LoadingState.LOADED)
        } catch (e: Exception) {
            loadingState.emit(LoadingState.error(e.localizedMessage))
        }
    }

    fun linkAccount(credential: AuthCredential) = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            Firebase.auth.currentUser!!.linkWithCredential(credential).await()
            auth = Firebase.auth
            userFirebase = auth.currentUser!!
            db.collection("users").document(auth.uid!!).set(data, SetOptions.merge())
            db.collection("users")
                .document(getUserUID())
                .get()
                .addOnSuccessListener { document ->
                    val userObject = document.toObject(User::class.java)
                    userDocument.value = userObject
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error getting user document: ${e.message}")
                }
            loadingState.emit(LoadingState.LOADED)
        } catch (e: Exception) {
            loadingState.emit(LoadingState.error(e.localizedMessage))
        }

    }
    fun getUserDocument() {
        db.collection("users")
            .document(getUserUID())
            .get()
            .addOnSuccessListener { document ->
                val userObject = document.toObject(User::class.java)
                userDocument.value = userObject
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting user document: ${e.message}")
            }
    }

private fun getUserUID(): String {
        if (!::userFirebase.isInitialized) {
            userFirebase = Firebase.auth.currentUser!!
        }
        return userFirebase.uid
    }

    fun isAnonymous(): Boolean{
        if (!::userFirebase.isInitialized) {
            userFirebase = Firebase.auth.currentUser!!
        }
        return userFirebase.isAnonymous
    }

    fun signOut() = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            Firebase.auth.signOut()
            loadingState.emit(LoadingState.LOADED)
        } catch (e: Exception) {
            loadingState.emit(LoadingState.error(e.localizedMessage))
        }

    }

    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ProfileViewModel()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    navController: NavController,
    vm: ProfileViewModel = viewModel(factory = ProfileViewModel.factory)
) {

    val user by vm.user.observeAsState()

    LaunchedEffect(Unit) {
        vm.getUserDocument() // Fetch the initial user document
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SportCampTheme {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                CustomToolbarWithEditButton(
                    title = "Profile",
                    navController = navController as NavHostController
                )


                   user?.let { Profile(navController=navController) }






            }

        }
    }
}

@Composable
private fun LinkLoginLogoutButtons(
    navController: NavController,
    vm: ProfileViewModel = viewModel(factory = ProfileViewModel.factory)
){

    val state by vm.loadingState.collectAsState()


    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            vm.linkAccount(credential)
        } catch (e: ApiException) {
            Log.w("TAG", "Google sign in failed", e)
        }
    }

    val launcher2 = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            vm.signWithCredential(credential)
        } catch (e: ApiException) {
            Log.w("TAG", "Google sign in failed", e)
        }
    }

    val context = LocalContext.current
    val token = stringResource(R.string.default_web_client_id)

    if (vm.isAnonymous()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedButton(
                border = ButtonDefaults.outlinedBorder.copy(width = 1.dp),
                modifier = Modifier
                    .height(50.dp)
                    .padding(horizontal = 50.dp)
                    .fillMaxWidth(),
                onClick = {
                    val gso =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(token)
                            .requestEmail()
                            .build()

                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    launcher.launch(googleSignInClient.signInIntent)
                },
                content = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Icon(
                                tint = Color.Unspecified,
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = null,
                            )
                            Text(
                                style = MaterialTheme.typography.button,
                                color = MaterialTheme.colors.onSurface,
                                text = "Link with your Google"
                            )

                        }
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            border = ButtonDefaults.outlinedBorder.copy(width = 1.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 50.dp),
            onClick = {
                val gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(token)
                        .requestEmail()
                        .build()

                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                launcher2.launch(googleSignInClient.signInIntent)
            },
            content = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Icon(
                            tint = Color.Unspecified,
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = null,
                        )
                        Text(
                            style = MaterialTheme.typography.button,
                            color = MaterialTheme.colors.onSurface,
                            text = "Login with Google"
                        )
                    }
                )
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        when (state.status) {
            LoadingState.Status.SUCCESS -> {
                Toast.makeText(
                    context,
                    "Sign in completed!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            LoadingState.Status.FAILED -> {
                Toast.makeText(
                    context,
                    state.msg ?: "Error",
                    Toast.LENGTH_SHORT
                ).show()
            }

            LoadingState.Status.RUNNING -> {
                Row {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }

            else -> {}
        }
    } else {
            LogoutButton(navController = navController)
    }

}

@Composable
private fun LogoutButton(
    navController: NavController,
    vm: ProfileViewModel = viewModel(factory = ProfileViewModel.factory)
){

    var logout by remember {
        mutableStateOf(false)
    }
    val state by vm.loadingState.collectAsState()
    val context = LocalContext.current
    Column(
        modifier =
        Modifier
            .fillMaxWidth()
    ) {
        OutlinedButton(
            border = ButtonDefaults.outlinedBorder.copy(width = 1.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 50.dp),
            onClick = {
                vm.signOut()
                logout = true
            },
            content = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Icon(
                            tint = Color.Unspecified,
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = null,
                        )
                        Text(
                            style = MaterialTheme.typography.button,
                            color = MaterialTheme.colors.onSurface,
                            text = "Logout"
                        )

                    }
                )
            }
        )
        when (state.status) {
            LoadingState.Status.SUCCESS -> {
                if (logout) {
                    Toast.makeText(
                        context,
                        "Logout completed!",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.navigate(route = Screen.Login.route) {
                        popUpTo("profile") { inclusive = true }
                    }
                    logout = false
                }
            }

            LoadingState.Status.FAILED -> {
                Toast.makeText(
                    context,
                    state.msg ?: "Error",
                    Toast.LENGTH_SHORT
                ).show()
                logout = false
            }

            LoadingState.Status.RUNNING -> {
                Row {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }

            else -> {}
        }
    }


}

private val optionsList: ArrayList<OptionsData> = ArrayList()

@Composable
fun Profile(
    navController: NavController,
    vm: ProfileViewModel = viewModel(factory = ProfileViewModel.factory)
    ) {

        val user by vm.user.observeAsState()
    // This indicates if the optionsList has data or not
    // Initially, the list is empty. So, its value is false.
    var listPrepared by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            optionsList.clear()

            // Add the data to optionsList
            prepareOptionsData(user!!)

            listPrepared = true
        }
    }

    if (listPrepared) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if(user!!.name!=null) {

                item {
                    UserDetails()
                }



                item{
                    Spacer(modifier = Modifier.height(20.dp))
                    UserDetailsRow(user = user!!)
                    Spacer(modifier = Modifier.height(20.dp))
                }


                item {
                    SportsListRow(user = user!!)
                }

                if(user!!.sports!!.contains("Tennis")) {
                    item {
                        TriStateToggle("Tennis", user!!.tennis_level)
                    }
                }

                if(user!!.sports!!.contains("Basketball")){
                    item{
                      TriStateToggle("Basket", user!!.basket_level)
                   }
                }

                if(user!!.sports!!.contains("Football")){
                    item{
                        TriStateToggle("Football", user!!.football_level)
                    }
                }

                if(user!!.sports!!.contains("Volleyball")) {
                    item {
                        TriStateToggle("Volley", user!!.volley_level)
                    }
                }

                items(optionsList) { item ->
                    Spacer(modifier = Modifier.height(50.dp))
                    OptionsItemStyle(item = item)
                }
                


                item{
                    Spacer(modifier = Modifier.height(20.dp))
                    LinkLoginLogoutButtons(navController = navController)
                    Spacer(modifier = Modifier.height(50.dp))
                }

            } else
            {
                item{
                    NewUserDetails(user = user!!)
                }
            }

        }

    }
}



@Composable
private fun UserDetails(
    vm: ProfileViewModel = viewModel(factory = ProfileViewModel.factory)
) {

    val user by vm.user.observeAsState()

    val bitmap = user!!.image?.let { BitmapConverter.converterStringToBitmap(it) }

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
                user!!.nickname?.let {
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
                user!!.bio?.let {
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
private fun UserDetailsRow(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start=26.dp, top = 3.dp, bottom=10.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier
            .weight(2f)
            .padding(start = 16.dp),
            verticalArrangement = Arrangement.Bottom) {
            user.name?.let {
                Text(
                    text = "Name:",
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Column(modifier = Modifier
            .weight(5f),
        horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom) {
            // User's name value
            user.name?.let {
                Text(
                    text = it,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    letterSpacing = (0.8).sp,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(3f)
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
                .weight(3f)
        ) {
            // User's age
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

            // User's age value
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
            modifier = Modifier
                .weight(3f)
                .padding(end = 16.dp)
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

@Composable
private fun NewUserDetails(user: User) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.user_image1),
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

            Text(
                text = "Complete your profile to book courts and let other user knows about yourself!",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(horizontal = 5.dp)
            )

        }

    }

}

// Row style for options
@Composable
private fun OptionsItemStyle(item: OptionsData) {

    if(item.subTitle != null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(weight = 3f, fill = false)
                        .padding(start = 10.dp)
                ) {
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
}

@Composable
fun CustomToolbarWithEditButton(title: String, navController: NavHostController) {
    TopAppBar(
        title = { Text(text = title, fontFamily = fonts) },
        actions = {
            IconButton(onClick = {navController.navigate(route = Screen.EditProfile.route)}) {
                Icon(Icons.Filled.Edit,
                    contentDescription = "edit",
                    tint = Color.White)
            }
        })



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
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
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


private fun prepareOptionsData(user: User) {

    val appIcons = Icons.Outlined

    /*
    optionsList.add(
        OptionsData(
            icon = appIcons.Person,
            title = "Name",
            subTitle = user.name.toString()
        )

    )*/

    if(user.mail != null) {
        optionsList.add(
            OptionsData(
                icon = appIcons.Mail,
                title = "Mail",
                subTitle = user.mail.toString()
            )
        )
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
