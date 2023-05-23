package it.polito.mad.sportcamp.favoritesScreens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import it.polito.mad.sportcamp.bottomnav.DETAIL_ARGUMENT_KEY3
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.common.CustomToolbarWithBackArrow
import it.polito.mad.sportcamp.database.AppViewModel
import it.polito.mad.sportcamp.database.Rating
import it.polito.mad.sportcamp.ui.theme.Blue

@Composable
fun CourtReviewListScreen(
    navController: NavHostController,
    viewModel: AppViewModel = viewModel(factory = AppViewModel.factory)
) {
    val idCourt = navController.currentBackStackEntry?.arguments?.getInt(DETAIL_ARGUMENT_KEY3)
    val reviews by viewModel.getCourtReviewsById(idCourt!!).observeAsState(listOf())

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        CustomToolbarWithBackArrow(title = "Court reviews", navController = navController)

        Spacer(modifier = Modifier.height(10.dp))


        LazyColumn(
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp),
        ) {
            items(items = reviews) { review ->
                ReviewCard(review = review, navController = navController, viewModel = viewModel)
            }

        }
    }

}


@Composable
fun ReviewCard(review: Rating, navController: NavController, viewModel: AppViewModel) {

    val user by viewModel.getUserById(review.id_user!!).observeAsState()
    val bitmap = user?.image?.let { BitmapConverter.converterStringToBitmap(it) }

    ElevatedCard(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .background(Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            {
                Column {
                    if (bitmap != null) {
                        Image(
                            painter = BitmapPainter(bitmap.asImageBitmap()),
                            contentDescription = "User Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(50.dp)
                                .border(2.dp, Blue.copy(0.6f), CircleShape)
                        )
                    }
                }
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    user?.nickname?.let {
                        Text(
                            text = it,
                            fontSize = 18.sp,
                        )
                    }
                    RatingStar(rating = review.rating!!)
                }


            }
            if (review.review != "") {
                review.review?.let {
                    Row(modifier = Modifier.padding(10.dp)) {
                        Row {
                            Text(
                                text = "$it",
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                }
            }


        }
    }
}
