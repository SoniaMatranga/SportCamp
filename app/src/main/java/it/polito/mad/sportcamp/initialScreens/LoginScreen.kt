package it.polito.mad.sportcamp.initialScreens

import android.content.ContentValues
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
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
import it.polito.mad.sportcamp.common.LoadingState
import it.polito.mad.sportcamp.ui.theme.Orange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date


class LoginViewModel : ViewModel() {

    private var userExists by mutableStateOf(false)
    val loadingState = MutableStateFlow(LoadingState.IDLE)
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    //val googleIdToken = ""
    private val db = Firebase.firestore
    val data = hashMapOf(
        "lastLogin" to com.google.firebase.Timestamp(Date(System.currentTimeMillis()))
    )

    private val usersCollection = db.collection("users")

    private fun getUserUID(): String{
        return user.uid
    }


    fun isNotNew(callback: (Boolean) -> Unit) {
        usersCollection.document(getUserUID()).get()
            .addOnSuccessListener { documentSnapshot ->
                userExists = documentSnapshot.exists() && documentSnapshot.contains("name")
                callback(userExists)
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
                callback(false) // Assuming false if an error occurs
            }
    }

    fun signWithCredential(credential: AuthCredential) = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            Firebase.auth.signInWithCredential(credential).await()
            auth = Firebase.auth
            user = auth.currentUser!!
            loadingState.emit(LoadingState.LOADED)
            //db.collection("users").document(auth.uid!!).set(data, SetOptions.merge())
        } catch (e: Exception) {
            loadingState.emit(LoadingState.error(e.localizedMessage))
        }
    }

    fun signInAnonymously() = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            Firebase.auth.signInAnonymously().await()
            auth = Firebase.auth
            user = auth.currentUser!!
            db.collection("users").document(auth.uid!!).set(data, SetOptions.merge())
            loadingState.emit(LoadingState.LOADED)
        } catch (e: Exception) {
            loadingState.emit(LoadingState.error(e.localizedMessage))
        }
    }

    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                LoginViewModel()
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(
    navController: NavController,
    vm: LoginViewModel = viewModel(factory = LoginViewModel.factory)
) {

    val state by vm.loadingState.collectAsState()
    var first by remember {
        mutableStateOf(true)
    }

    // Equivalent of onActivityResult
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            vm.signWithCredential(credential)
        } catch (e: ApiException) {
            Log.w("TAG", "Google sign in failed", e)
        }
    }



    Scaffold(
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    Image(
                        painter = painterResource(id = R.drawable.sport_camp),
                        contentDescription = "",
                        alignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )


                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Sport Camp",
                        color = Orange,
                        fontSize = 24.sp
                    )


                    val context = LocalContext.current
                    val token = stringResource(R.string.default_web_client_id)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 80.dp)
                    ){
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
                                launcher.launch(googleSignInClient.signInIntent)
                            },
                            content = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    content = {
                                        Text(
                                            style = MaterialTheme.typography.button,
                                            color = MaterialTheme.colors.onSurface,
                                            text = "Login with Google"
                                        )
                                        Icon(
                                            tint = Color.Unspecified,
                                            painter = painterResource(id = R.drawable.ic_google),
                                            contentDescription = null,
                                        )
                                    }
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Continue as a guest",
                                color = Color.Gray,
                                modifier =
                                Modifier
                                    .height(50.dp)
                                    .clickable(
                                        onClick = {
                                            vm.signInAnonymously()
                                        }
                                    )
                            )
                        }

                        if (first) {
                            when (state.status) {
                                LoadingState.Status.SUCCESS -> {
                                    first = false
                                    vm.isNotNew { isExistingUser ->
                                        if (isExistingUser) {
                                            // Existing user logic
                                            navController.navigate(route = Screen.Reservations.route)
                                            Toast.makeText(
                                                context,
                                                "Welcome to Sport Camp!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            // New user logic
                                            navController.navigate(route = Screen.ProfileDetails.route)
                                        }
                                    }
                                }

                                LoadingState.Status.FAILED -> {
                                    Text(text = state.msg ?: "Error")
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
                }
            )
        }
    )
}