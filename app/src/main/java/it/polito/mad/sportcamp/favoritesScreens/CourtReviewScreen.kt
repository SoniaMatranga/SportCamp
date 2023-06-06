package it.polito.mad.sportcamp.favoritesScreens

import android.content.ContentValues
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import it.polito.mad.sportcamp.bottomnav.DETAIL_ARGUMENT_KEY3
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.common.CustomToolbarWithBackArrow
import it.polito.mad.sportcamp.classes.Court
import it.polito.mad.sportcamp.classes.Rating
import it.polito.mad.sportcamp.ui.theme.Blue
import kotlin.math.roundToInt

class CourtReviewViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val court = MutableLiveData<Court>()
    val rating = MutableLiveData<Rating>()
    private var user: FirebaseUser = Firebase.auth.currentUser!!
    var id_court = ""
    var alreadyRated =  true
    var alreadyReviewed =  true

     fun getUserUID(): String{
        return user.uid
    }

    fun getCourtById(): MutableLiveData<Court> {
        db.collection("courts")
            .whereEqualTo("id_court", id_court)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(ContentValues.TAG, "Error getting documents.")
                }
                if (value != null && !value.isEmpty) {
                    val courtDocument = value.documents[0]
                    court.value = courtDocument.toObject(Court::class.java)
                }
            }
        return court
    }

    fun getCourtReviewById(): LiveData<Rating> {
        db.collection("ratings")
            .whereEqualTo("id_court", id_court)
            .whereEqualTo("id_user", getUserUID())
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(ContentValues.TAG, "Error getting documents.")
                }
                if (value != null && !value.isEmpty) {
                    val ratingDocument = value.documents[0]
                    rating.value = ratingDocument.toObject(Rating::class.java)
                }
            }
        return rating
    }

    fun deleteReviewById(id: String) {
        db.collection("ratings")
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val reviewDocuments = querySnapshot.documents
                if (reviewDocuments.isNotEmpty()) {
                    val reviewDocument = reviewDocuments[0]
                    reviewDocument.reference.delete()
                        .addOnSuccessListener {
                            updateCourtRatingById()
                        }
                        .addOnFailureListener { exception ->
                            Log.w(ContentValues.TAG, "Error deleting reservation.", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting reservation document.", exception)
            }
    }

    fun updateCourtRatingById() {
        val ratingsRef = db.collection("ratings")
        val query = ratingsRef.whereEqualTo("id_court", id_court)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                var totalRating = 0f
                var totalReviews = 0

                for (documentSnapshot in querySnapshot.documents) {
                    val rating = documentSnapshot.toObject(Rating::class.java)
                    if (rating?.rating != null) {
                        totalRating += rating.rating
                        totalReviews++
                    }
                }

                val averageRating = if (totalReviews > 0) totalRating / totalReviews else 0f

                val courtsRef = db.collection("courts")
                val courtQuery = courtsRef.whereEqualTo("id_court", id_court)

                courtQuery.get()
                    .addOnSuccessListener { courtSnapshot ->
                        for (courtDocument in courtSnapshot.documents) {
                            courtDocument.reference.update(
                                mapOf(
                                    "court_rating" to averageRating
                                )
                            )
                                .addOnSuccessListener {
                                    updateCourtRatingById()
                                    Log.d(ContentValues.TAG, "Court rating updated successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(ContentValues.TAG, "Error updating court rating", e)
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error getting court document", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error getting ratings", e)
            }
    }

    fun updateReview(id: String, rating: Float, review: String){
        val ratingRef = db.collection("ratings")
        val query = ratingRef.whereEqualTo("id", id)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                for (documentSnapshot in querySnapshot.documents) {
                    val ratingTmp = documentSnapshot.toObject(Rating::class.java)
                    if (ratingTmp != null) {
                        documentSnapshot.reference.update(
                            mapOf(
                                "rating" to rating,
                                "review" to review
                            )
                        )
                            .addOnSuccessListener {

                                Log.d(ContentValues.TAG, " Rating updated successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error updating rating", e)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error getting rating", e)
            }
    }

    fun insertReview(id_user: String, rating: Float, review: String?) {
        val ratingTmp = Rating(
            id_user = id_user,
            id_court =id_court,
            rating =rating,
            review =review
        )
            val ratingsCollection = db.collection("ratings")

            ratingsCollection
                .add(ratingTmp)
                .addOnSuccessListener { documentReference ->
                    val generatedId = documentReference.id
                    ratingTmp.id= generatedId

                    // Update the reservation document with the generated ID
                    ratingsCollection.document(generatedId)
                        .set(ratingTmp)
                        .addOnSuccessListener {
                            Log.d(ContentValues.TAG, "Rating added with ID: $generatedId")
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error adding rating", e)
                        }
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error adding rating", e)
                }


    }

    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CourtReviewViewModel()
            }
        }
    }
}

@Composable
fun CourtReviewScreen(
    navController: NavHostController,
    viewModel: CourtReviewViewModel = viewModel(factory = CourtReviewViewModel.factory)
) {
    val idCourt = navController.currentBackStackEntry?.arguments?.getString(DETAIL_ARGUMENT_KEY3)?: ""
    viewModel.id_court=idCourt
    val courtDetails by viewModel.getCourtById().observeAsState()
    val feedback by viewModel.getCourtReviewById().observeAsState()
    var initialRating: Float by remember { mutableStateOf(0f) }
    var text by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(feedback) {
        if (viewModel.alreadyRated) {
            feedback?.rating?.let {
                initialRating = feedback!!.rating!!
            }
        }

        if (viewModel.alreadyReviewed) {
            feedback?.review?.let {
                text = feedback!!.review!!
            }
        }
    }

    if(viewModel.alreadyReviewed){
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
                        val z = ((it * 10.0).roundToInt() / 10.0)
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
                                    viewModel.alreadyRated = false
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
                                            viewModel.alreadyReviewed = false
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
                            viewModel.rating.value?.id?.let { viewModel.deleteReviewById(it) }
                            viewModel.alreadyRated = false
                            viewModel.alreadyReviewed = false
                            initialRating=0f
                            text=""
                            viewModel.updateCourtRatingById()
                            openDialog.value = false
                            Toast.makeText(context, "Review correctly deleted", Toast.LENGTH_SHORT).show()
                            navController.navigate(
                                route = Screen.Favorites.route,
                                builder = {
                                    popUpTo(Screen.CourtReview.route) { inclusive = true }
                                }
                            )
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

        if(viewModel.rating.value?.rating!=null){
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
                Button(onClick = {
                    viewModel.updateReview(viewModel.rating.value!!.id!!,initialRating, text)
                    Toast.makeText(context, "Review correctly updated", Toast.LENGTH_SHORT).show()
                    navController.navigate(
                        route = Screen.CourtReviewList.passIdCourt(viewModel.id_court),
                        builder = {
                            popUpTo(Screen.CourtReview.route) { inclusive = true }
                        }
                    )
                },
                    enabled = (!viewModel.alreadyRated && initialRating!=0f && initialRating!= viewModel.rating.value!!.rating) || (!viewModel.alreadyReviewed && initialRating!=0f && text!= viewModel.rating.value!!.review)
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
                        viewModel.insertReview( viewModel.getUserUID(), initialRating, text)
                        viewModel.updateCourtRatingById()
                        Toast.makeText(context, "Review correctly published", Toast.LENGTH_SHORT).show()
                    navController.navigate(
                        route = Screen.CourtReviewList.passIdCourt(viewModel.id_court),
                        builder = {
                            popUpTo(Screen.CourtReview.route) { inclusive = true }
                        }
                    )
                    },
                enabled = !viewModel.alreadyRated && initialRating!=0f
            ) {
                Text(text = "Publish Review")
            }
        }
    }

    }
}