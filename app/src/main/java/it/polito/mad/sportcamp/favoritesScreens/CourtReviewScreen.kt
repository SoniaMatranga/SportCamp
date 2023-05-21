package it.polito.mad.sportcamp.favoritesScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import it.polito.mad.sportcamp.bottomnav.DETAIL_ARGUMENT_KEY3
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.common.CustomToolbarWithBackArrow
import it.polito.mad.sportcamp.database.AppViewModel

@Composable
fun CourtReviewScreen(
    navController: NavHostController,
    viewModel: AppViewModel = viewModel(factory = AppViewModel.factory)
) {
    var idCourt = navController.currentBackStackEntry?.arguments?.getInt(DETAIL_ARGUMENT_KEY3)
    val courtDetails by viewModel.getCourtById(idCourt!!).observeAsState()
    val bitmap = courtDetails?.image?.let { BitmapConverter.converterStringToBitmap(it) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        CustomToolbarWithBackArrow(title = "Review Court", navController = navController)

        Spacer(modifier = Modifier.height(10.dp))

        Column {
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
                        Row(){
                            courtDetails?.court_name?.let {
                                Text(
                                    text= it,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 40.dp)
                                )
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center){
                            courtDetails?.court_rating?.let { RatingBar(rating = it)
                                courtDetails?.court_rating?.let {
                                    androidx.compose.material3.Text(
                                        text = "($it)",
                                        modifier = Modifier.padding(start = 2.dp)
                                    )
                                }}
                        }
                        Spacer(modifier = Modifier.height(20.dp))
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


                }
            }
        }

    }
}


