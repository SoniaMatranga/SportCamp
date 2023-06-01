package it.polito.mad.sportcamp.initialScreens

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import it.polito.mad.sportcamp.R
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.common.LoadingState
import it.polito.mad.sportcamp.profileScreens.LoginScreenViewModel
import it.polito.mad.sportcamp.ui.theme.Orange

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginScreenViewModel = viewModel()) {

    val state by viewModel.loadingState.collectAsState()
    var first by remember {
        mutableStateOf(true)
    }

    // Equivalent of onActivityResult
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            viewModel.signWithCredential(credential)
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

                    Column() {
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
                                            viewModel.signInAnonymously()
                                        }
                                    )
                            )
                        }

                            if (first) {
                                when (state.status) {
                                    LoadingState.Status.SUCCESS -> {
                                        first = false
                                        navController.navigate(route = Screen.Reservations.route)
                                        Toast.makeText(
                                            context,
                                            "Welcome to Sport Camp!",
                                            Toast.LENGTH_SHORT
                                        ).show()
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