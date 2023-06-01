package it.polito.mad.sportcamp.initialScreens

import android.os.Build
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.polito.mad.sportcamp.R
import it.polito.mad.sportcamp.bottomnav.Screen
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SplashScreen(navController: NavController) = Box(
    Modifier
        .fillMaxSize()
) {

    //val navController = rememberNavController()
    val scale = remember {
        androidx.compose.animation.core.Animatable(0.0f)
    }
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(800, easing = {
                OvershootInterpolator(4f).getInterpolation(it)
            })
        )
        delay(1000)
        if(Firebase.auth.currentUser == null)
            navController.navigate(route = Screen.Login.route){
                popUpTo("splash") { inclusive = true }
            }
        else {
            navController.navigate(route = Screen.Reservations.route){
                popUpTo("splash") { inclusive = true }
            }
            Toast.makeText(context, "Welcome back to Sport Camp!", Toast.LENGTH_SHORT).show()
        }

    }


        Image(
            painter = painterResource(id = R.drawable.sport_camp),
            contentDescription = "",
            alignment = Alignment.Center, modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
                .padding(bottom = 180.dp)
                .scale(scale.value)
        )





}
