package it.polito.mad.sportcamp.favoritesScreens

import android.content.ContentValues
import android.util.Log
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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.sportcamp.bottomnav.DETAIL_ARGUMENT_KEY3
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.common.CustomToolbarWithBackArrow
import it.polito.mad.sportcamp.classes.Rating
import it.polito.mad.sportcamp.classes.RatingContent
import it.polito.mad.sportcamp.classes.User
import it.polito.mad.sportcamp.ui.theme.Blue


class CourtReviewListViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val ratings = MutableLiveData<List<Rating>>()

    //private val user = MutableLiveData<User>()
    private var fuser: FirebaseUser = Firebase.auth.currentUser!!

    private fun getUserUID(): String{
        return fuser.uid
    }



    fun getCourtReviewsById(id_court: String): LiveData<List<RatingContent>> {
        val ratingContents = MutableLiveData<List<RatingContent>>()

        db.collection("ratings")
            .whereEqualTo("id_court", id_court)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(ContentValues.TAG, "Error getting documents.")
                    ratingContents.value = emptyList() // Notify an empty list in case of error
                }
                if (value != null) {
                    val ratingContentList = mutableListOf<RatingContent>()
                    val userIds = value.documents.mapNotNull { it.getString("id_user") }.distinct()
                    val users = mutableMapOf<String, User>()

                    // Fetch users corresponding to the ratings
                    val userFetchTasks = userIds.mapNotNull { userId ->
                        db.collection("users")
                            .document(userId)
                            .get()
                            .addOnSuccessListener { document ->
                                val user = document.toObject(User::class.java)
                                if (user != null) {
                                    users[userId] = user
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.w(ContentValues.TAG, "Error getting user: $exception")
                            }
                    }

                    Tasks.whenAllComplete(userFetchTasks)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Create RatingContent objects with user information
                                for (doc in value.documents) {
                                    val rating = doc.toObject(Rating::class.java)
                                    if (rating != null && users.containsKey(rating.id_user)) {
                                        val user = users[rating.id_user]
                                        val ratingContent = RatingContent(
                                            id = rating.id,
                                            id_user = rating.id_user,
                                            id_court = rating.id_court,
                                            rating = rating.rating,
                                            review = rating.review,
                                            nickname = user?.nickname,
                                            image = user?.image
                                        )
                                        ratingContentList.add(ratingContent)
                                    }
                                }
                                ratingContents.value = ratingContentList
                            } else {
                                Log.w(ContentValues.TAG, "Error fetching user data: ${task.exception}")
                                ratingContents.value = emptyList()
                            }
                        }
                }
            }

        return ratingContents
    }
    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CourtReviewListViewModel()
            }
        }
    }
}

@Composable
fun CourtReviewListScreen(
    navController: NavHostController,
    viewModel: CourtReviewListViewModel = viewModel(factory = CourtReviewListViewModel.factory)
) {
    val idCourt = navController.currentBackStackEntry?.arguments?.getString(DETAIL_ARGUMENT_KEY3)?: ""
    val reviews by viewModel.getCourtReviewsById(idCourt).observeAsState(listOf())


    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        CustomToolbarWithBackArrow(title = "Court reviews", navController = navController)

        Spacer(modifier = Modifier.height(10.dp))


        if (reviews.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp),
            ) {
                items(items = reviews) { review ->
                    ReviewCard(review = review, viewModel = viewModel)
                }
            }
        }
    }

}


@Composable
fun ReviewCard(review: RatingContent, viewModel: CourtReviewListViewModel) {

    //val user by viewModel.getUserById(review.id_user!!).observeAsState()
    val bitmap = review?.image?.let { BitmapConverter.converterStringToBitmap(it) }

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
                    review?.nickname?.let {
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
                                text = it,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                }
            }


        }
    }
}
