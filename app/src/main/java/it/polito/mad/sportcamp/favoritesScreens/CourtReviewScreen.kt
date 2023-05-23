package it.polito.mad.sportcamp.favoritesScreens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import it.polito.mad.sportcamp.bottomnav.DETAIL_ARGUMENT_KEY3
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.common.CustomToolbarWithBackArrow
import it.polito.mad.sportcamp.database.AppViewModel
import it.polito.mad.sportcamp.ui.theme.Blue
import kotlin.math.roundToInt

@Composable
fun CourtReviewScreen(
    navController: NavHostController,
    viewModel: AppViewModel = viewModel(factory = AppViewModel.factory)
) {
    val idCourt = navController.currentBackStackEntry?.arguments?.getInt(DETAIL_ARGUMENT_KEY3)
    val courtDetails by viewModel.getCourtById(idCourt!!).observeAsState()
    val feedback by viewModel.getCourtReviewById(idCourt!!, 1).observeAsState()
    var alreadyRated by remember { mutableStateOf(true) }
    var alreadyReviewed by remember { mutableStateOf(true) }
    var initialRating: Float by remember { mutableStateOf(0f) }
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current

    if (alreadyRated) {
        feedback?.rating?.let {
            initialRating = feedback!!.rating!!
        }
    }

    if(alreadyReviewed){
        feedback?.review?.let {
            text = feedback!!.review!!
        }
    }

    val bitmap = courtDetails?.image?.let { BitmapConverter.converterStringToBitmap(it) }
    val maxLength = 500
    val scrollState = rememberScrollState()
    val openDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState)
    ) {

        CustomToolbarWithBackArrow(title = "Review Court", navController = navController)

        Spacer(modifier = Modifier.height(10.dp))

        Column {
            Row {
                courtDetails?.court_name?.let {
                    Text(
                        text = it,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center){
            Row(
                modifier = Modifier
                    .clickable (onClick = {navController.navigate(
                        route = Screen.CourtReviewList.passIdCourt(
                            courtDetails!!.id_court!!
                        )
                    )} ),
                horizontalArrangement = Arrangement.Center
            ) {
                courtDetails?.court_rating?.let {
                    RatingStar(rating = it)
                    courtDetails?.court_rating?.let {
                        var z = ((it * 10.0).roundToInt() / 10.0)
                        androidx.compose.material3.Text(
                            text = "($z)",
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }

            }}
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (bitmap != null) {
                    Image(
                        painter = BitmapPainter(bitmap.asImageBitmap()),
                        contentDescription = "Court Picture",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            // Clip image to be shaped as a rectangle
                            .clip(shape = RectangleShape)
                            .fillMaxWidth()
                            .height(210.dp)
                            .padding(10.dp)
                    )
                }
            }


            Row(modifier = Modifier.padding(horizontal = 10.dp)) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Column(modifier = Modifier.padding(4.dp)) {

                        Row {
                            courtDetails?.address?.let {
                                Text(
                                    text = "Address: $it",
                                )
                            }
                        }
                        Row {
                            courtDetails?.city?.let {
                                Text(
                                    text = "City: $it",
                                )
                            }
                        }
                        Row {
                            courtDetails?.sport?.let {
                                Text(
                                    text = "Sport: $it",
                                )
                            }
                        }

                    }

                    Spacer(modifier = Modifier.height(50.dp))

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Row {
                            Text(
                                text = "Rate this court",
                                fontSize = 18.sp
                            )
                        }
                        Row {
                            Text(
                                text = "Tell others what you think",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row {

                            RatingBar(
                                value = initialRating,
                                config = RatingBarConfig()
                                    .style(RatingBarStyle.HighLighted).padding(6.dp),
                                onValueChange = {
                                    initialRating = it
                                },
                                onRatingChanged = {
                                    alreadyRated = false
                                }
                            )
                        }
                            Spacer(modifier = Modifier.height(5.dp))
                            Row {
                                if (initialRating == 5f)
                                    Text(
                                        text = "Awesome",
                                        fontSize = 14.sp,
                                        color = Color.Gray

                                    )
                                if (initialRating == 4f)
                                    Text(
                                        text = "Good",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                if (initialRating == 3f)
                                    Text(
                                        text = "Average",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                if (initialRating == 2f)
                                    Text(
                                        text = "Poor",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                if (initialRating == 1f)
                                    Text(
                                        text = "Bad",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                if (initialRating == 0f)
                                    Text(
                                        text = "Unrated",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                            }


                        Row {

                            text.let {
                                androidx.compose.material.OutlinedTextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 10.dp, end = 10.dp, top = 30.dp),
                                        value = it,
                                        onValueChange = {
                                            if (it.length <= maxLength) text = it
                                            alreadyReviewed = false
                                        },
                                        placeholder = {
                                            Text(
                                                text = "Describe your experience (optional)",
                                                color = Color.Gray
                                            )
                                        },
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = Color.Transparent,
                                            focusedLabelColor = Blue.copy(alpha = 0.6f),
                                            focusedIndicatorColor = Blue.copy(alpha = 0.6f),
                                            unfocusedIndicatorColor = Blue.copy(alpha = 0.6f)
                                        )
                                    )
                            }
                        }
                        Row {
                            Text(
                                text = "${text.length} / $maxLength",
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, end = 10.dp),
                                textAlign = TextAlign.End,
                                color = Blue.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

            }
        }
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                text = {
                    Text("This review will be deleted permanently. Are you sure you want to delete it anyway? ")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteReviewById(feedback?.id!!)
                            alreadyRated = false
                            alreadyReviewed = false
                            initialRating=0f
                            text=""
                            viewModel.updateCourtRatingById(courtDetails?.id_court!!)
                            openDialog.value = false
                            Toast.makeText(context, "Review correctly deleted", Toast.LENGTH_SHORT).show()
                        }) {
                        Text("Yes, delete it")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            openDialog.value = false
                        }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if(feedback?.rating!=null){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly)
            {
                Button(
                    onClick = {
                        openDialog.value = true
                    }
                ){
                    Text(text = "Delete")
                }
                Button(onClick = { text.let {
                    viewModel.updateReview(feedback?.id!!,initialRating,
                        it
                    )
                }
                    viewModel.updateCourtRatingById(courtDetails?.id_court!!)
                    Toast.makeText(context, "Review correctly updated", Toast.LENGTH_SHORT).show()
                },
                    enabled = (!alreadyRated && initialRating!=0f && initialRating!=feedback?.rating) || (!alreadyReviewed && initialRating!=0f && text!=feedback?.review)
                ) {
                    Text(text = "Update")
                }

            }
        }else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
            Button(
                onClick = {
                    if (idCourt != null) {
                        viewModel.insertReview(null, 1, idCourt, initialRating, text)
                        viewModel.updateCourtRatingById(courtDetails?.id_court!!)
                        Toast.makeText(context, "Review correctly published", Toast.LENGTH_SHORT).show()
                    }
                    },
                enabled = !alreadyRated && initialRating!=0f
            ) {
                Text(text = "Publish Review")
            }
        }
    }

    }
}