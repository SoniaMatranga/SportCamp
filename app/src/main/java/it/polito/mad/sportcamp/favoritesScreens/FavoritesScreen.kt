package it.polito.mad.sportcamp.favoritesScreens

import android.content.ContentValues
import android.util.Log
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import it.polito.mad.sportcamp.common.CustomToolBar
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.classes.Court
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.sportcamp.bottomnav.Screen
import it.polito.mad.sportcamp.classes.Reservation
import it.polito.mad.sportcamp.ui.theme.Blue
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

data class ChipsModel(
    val name: String,
    val leadingIcon: ImageVector? = null,
    val trailingIcon: ImageVector? = null,
)

class FavoriteViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _loadingState = MutableLiveData<Boolean>(true)
    val loadingState: LiveData<Boolean> = _loadingState
    var sportFilter by mutableStateOf("Tennis")
    var selectedItem by  mutableStateOf("Tennis")

    fun getLoadingState(): Boolean {
        return _loadingState.value ?: false
    }

    fun setLoadingState(loading: Boolean) {
        _loadingState.value = loading
    }

    fun getFilteredCourtsUserPlayed(sportFilter :String): LiveData<List<Court>> {
        setLoadingState(true)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val courts = MediatorLiveData<List<Court>>()

        db.collection("reservations")
            .whereEqualTo("id_user", 1)

            .whereLessThan("date", currentDate)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(ContentValues.TAG, "Error getting documents.")
                }
                if (value != null) {
                    val courtList = mutableListOf<Court>()
                    for (doc in value.documents) {
                        val reservation = doc.toObject(Reservation::class.java)
                        if (reservation != null) {
                            val courtId = reservation.id_court ?: continue
                            db.collection("courts")
                                .whereEqualTo("id_court", courtId)
                                .whereEqualTo("sport", sportFilter)
                                .get()
                                .addOnSuccessListener { courtSnapshot ->
                                    for (courtDoc in courtSnapshot.documents) {
                                        val court = courtDoc.toObject(Court::class.java)
                                        court?.let {
                                            if(!courtList.contains(it)) {
                                                courtList.add(it)
                                            }
                                        }
                                    }
                                    courts.value = courtList
                                }
                                .addOnFailureListener { e ->
                                    Log.w(ContentValues.TAG, "Error getting court documents.", e)
                                }
                        }
                    }
                }
            }
        setLoadingState(false)
        return courts
    }

    fun getAllCourtsUserPlayed(): LiveData<List<Court>> {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val courts = MediatorLiveData<List<Court>>()

        db.collection("reservations")
            .whereEqualTo("id_user", 1)
            .whereLessThan("date", currentDate)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(ContentValues.TAG, "Error getting documents.")
                }
                if (value != null) {
                    val courtList = mutableListOf<Court>()
                    for (doc in value.documents) {
                        val reservation = doc.toObject(Reservation::class.java)
                        if (reservation != null) {
                            val courtId = reservation.id_court
                            db.collection("courts")
                                .whereEqualTo("id_court", courtId)
                                .get()
                                .addOnSuccessListener { courtSnapshot ->
                                    for (courtDoc in courtSnapshot.documents) {
                                        val court = courtDoc.toObject(Court::class.java)
                                        court?.let {
                                            if(!courtList.contains(it)) {
                                                courtList.add(it)
                                            }
                                        }
                                    }
                                    courts.value = courtList



                                }
                                .addOnFailureListener { e ->
                                    Log.w(ContentValues.TAG, "Error getting court documents.", e)
                                }
                        }
                    }

                }
            }

        setLoadingState(false)
        return courts
    }


    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FavoriteViewModel()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController,
    vm: FavoriteViewModel = viewModel(factory = FavoriteViewModel.factory)
) {


    //var sportFilter by remember {mutableStateOf("Tennis")}
    val isLoading = vm.loadingState.value ?: true

    //val courts by vm.getAllCourtsUserPlayed().observeAsState()
    val courtsList by vm.getFilteredCourtsUserPlayed(vm.sportFilter).observeAsState()



    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomToolBar(title = "Ratings")
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically){
           Text(
               text="Leave a review and rate the courts you've played. Click stars to se all the ratings",
               color = Color.Gray,
                textAlign = TextAlign.Center,
               modifier = Modifier
                   .fillMaxWidth()
                   .padding(horizontal = 40.dp)
           )
        }

        val filterList = listOf(
            ChipsModel(
                name = "Tennis",
                leadingIcon = Icons.Default.SportsTennis,
                trailingIcon = Icons.Default.Close
            ),
            ChipsModel(
                name = "Football",
                leadingIcon = Icons.Default.SportsSoccer,
                trailingIcon = Icons.Default.Close
            ),
            ChipsModel(
                name = "Basketball",
                leadingIcon = Icons.Default.SportsBasketball,
                trailingIcon = Icons.Default.Close
            ),
            ChipsModel(
                name = "Volleyball",
                leadingIcon = Icons.Default.SportsVolleyball,
                trailingIcon = Icons.Default.Close
            ),
        )


        var isSelected by remember { mutableStateOf(false) }

        LazyRow {
            items(filterList) { item ->
                isSelected = vm.selectedItem == item.name
                Spacer(modifier = Modifier.padding(5.dp))
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            when (vm.selectedItem == item.name) {
                                true -> {
                                    vm.selectedItem = item.name //; vm.all = true

                                }
                                false -> {
                                    vm.selectedItem = item.name;
                                    vm.sportFilter = item.name
                                }
                            }
                        },
                        label = {
                            Text(
                                text = item.name,
                                color= if(isSelected){Color.White} else Color.Black)
                                },
                        leadingIcon = {
                            if (item.leadingIcon != null && isSelected)
                                Icon(item.leadingIcon, contentDescription = item.name, tint = Color.White)
                            else
                                item.leadingIcon?.let { Icon(it, contentDescription = item.name, tint = Color.Black) }
                        },
                         colors = FilterChipDefaults.filterChipColors(containerColor = Color.Transparent, selectedContainerColor = Blue.copy(alpha = 0.6f))
                    )
            }
        }
        LazyColumn(
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp),
        ) {
            when {
                isLoading == true -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator() // Spinner
                        }
                    }
                }

                courtsList?.isNotEmpty() == true -> {
                    items(items = courtsList!!) { court ->
                        CourtCard(court = court, navController = navController)
                    }
                }

                courtsList?.isEmpty() == true -> {
                    item {
                        Text(
                            text = "No courts available to be rated on this sport",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun CourtCard(court: Court, navController: NavController) {

    var expanded by remember { mutableStateOf(false) }
    val bitmap = court.image?.let { BitmapConverter.converterStringToBitmap(it) }


    ElevatedCard(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .background(Color.White)
            .clickable(onClick = { expanded = !expanded }),
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
            )
            {

                if (bitmap != null) {
                    Image(
                        painter = BitmapPainter(bitmap.asImageBitmap()),
                        contentDescription = "Court Picture",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .clip(shape = RectangleShape)
                            .fillMaxWidth()
                            .height(210.dp)
                            .padding(10.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier
                            .padding(4.dp)
                            .weight(4f)) {
                            Row {

                                court.court_name?.let {
                                    Text(
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines =2,
                                        text = it,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(start = 2.dp)
                                    )
                                }
                            }
                            Row(modifier = Modifier
                                .clickable (
                                    onClick = {
                                        navController.navigate(
                                            route = Screen.CourtReviewList.passIdCourt(court.id_court!!)
                                        )
                                    }
                                )
                            ) {
                                court.court_rating?.let {
                                    RatingStar(rating = it)
                                }
                                court.court_rating?.let {
                                    val z = ((it * 10.0).roundToInt() / 10.0)
                                    Text(
                                        text = "($z)",
                                        modifier = Modifier.padding(start = 2.dp)
                                    )
                                }
                            }

                        }
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f)) {


                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                androidx.compose.material.Button(
                                    shape = RoundedCornerShape(5.dp),
                                    onClick = {
                                        navController.navigate(
                                            route = Screen.CourtReview.passIdCourt(
                                                court.id_court!!
                                            )
                                        )
                                    }) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        androidx.compose.material.Text(
                                            maxLines=1,
                                            text = "Review",
                                            fontSize = 13.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (expanded) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .padding(bottom = 10.dp)
                        ) {
                            Row {
                                court.address?.let { it1 ->
                                    androidx.compose.material.Text(
                                        text = "Address: $it1",
                                        fontSize = 14.sp,
                                    )
                                }
                            }
                            Row {
                                court.city?.let { it1 ->
                                    androidx.compose.material.Text(
                                        text = "City: $it1",
                                        fontSize = 14.sp,
                                    )
                                }
                            }
                            Row {

                                court.sport?.let {
                                    androidx.compose.material.Text(
                                        text = "Sport: $it",
                                        fontSize = 14.sp
                                        )
                                    }
                                }

                            }
                        }
                    }
                }

            }

        }
    }



@Composable
fun RatingStar(rating: Float) {
    val filledStars = rating.toInt()
    val hasHalfStar = rating - filledStars >= 0.5f

    Row {
        repeat(filledStars) {
            androidx.compose.material.Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = androidx.compose.material.MaterialTheme.colors.primary
            )
        }

        if (hasHalfStar) {
            androidx.compose.material.Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Default.StarHalf,
                contentDescription = null,
                tint = androidx.compose.material.MaterialTheme.colors.primary
            )
        }
    }
}




