package it.polito.mad.sportcamp.favoritesScreens

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.polito.mad.sportcamp.common.BitmapConverter
import it.polito.mad.sportcamp.database.AppViewModel
import it.polito.mad.sportcamp.database.Court
import androidx.compose.material.icons.outlined.Star

data class ChipsModel(
    val name: String,
    //val subList: List<String>? = null,
    val textExpanded: String? = null,
    val leadingIcon: ImageVector? = null,
    val trailingIcon: ImageVector? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: AppViewModel = viewModel(factory = AppViewModel.factory)
) {
    val courtsList: List<Court> by viewModel.getAllCourts().observeAsState(listOf())
    var sportFilter by remember { mutableStateOf("") }
    var all: Boolean by remember { mutableStateOf(true) }
    val courts: List<Court> by viewModel.getCourtsBySport(sportFilter).observeAsState(listOf())

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomToolBar(title = "Favorites")

        val filterList = listOf(
          /*  ChipsModel(
                name = "Order by",
                subList = listOf("Most starred", "Ascending A-Z", "Descending Z-A"),
                trailingIcon = Icons.Default.ArrowDropDown,
                leadingIcon = Icons.Default.Check
            ),*/
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

        var selectedItem by remember { mutableStateOf("") }
        var isSelected by remember { mutableStateOf(false) }

        LazyRow {
            items(filterList) { item ->
                isSelected = selectedItem==item.name
                Spacer(modifier = Modifier.padding(5.dp))
              //  if (item.subList != null) {
                    //ChipWithSubItems(chipLabel = item.name, chipItems = item.subList, courtsList=courtsList)
               // } else {
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            when (selectedItem==item.name) {
                                true -> {selectedItem="";  all=true }
                                false -> {selectedItem=item.name; all=false; sportFilter=item.name }
                            }
                        },
                        label = { Text(text = item.name) },
                        leadingIcon = {
                            val isCheckIcon = item.leadingIcon == Icons.Default.Check
                            if (item.leadingIcon != null && isCheckIcon && isSelected) {
                                Icon(item.leadingIcon, contentDescription = item.name)
                            }
                            if (item.leadingIcon != null && !isCheckIcon) {
                                Icon(item.leadingIcon, contentDescription = item.name)
                            }
                        },
                       /* trailingIcon = {
                            if (item.trailingIcon != null && isSelected)
                                Icon(item.trailingIcon, contentDescription = item.name)
                        }*/
                    )
                //}
            }
        }
        //FilterChip(if(all) courtsList else filteredList, sportFilter, all)
        LazyColumn(
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp),
        ) {
                items(items = if(all) courtsList else courts) { court ->
                    CourtCard(court = court, navController = navController)
            }

        }
    }
}

@Composable
fun CourtCard(court: Court, navController: NavController) {

    var expanded by remember { mutableStateOf(false) }
    val bitmap = court.image?.let { BitmapConverter.converterStringToBitmap(it) }


    ElevatedCard (
        modifier = Modifier
            .clickable { expanded = !expanded }
            .padding(5.dp)
            .fillMaxWidth()
            .background(Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation =  CardDefaults.cardElevation(
            defaultElevation = 5.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(2.dp)
                .background(Color.White)
        ) {
            Row(modifier = Modifier
                .padding(horizontal = 10.dp)) {

                if (bitmap != null) {
                    Image(
                        painter = BitmapPainter(bitmap.asImageBitmap()),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .clip(shape = RectangleShape)
                            .fillMaxWidth()
                            .height(210.dp)
                    )
                }
            }
            Row {
                Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                    court.court_name?.let {
                        Text(
                            text = it,
                            fontSize = 18.sp,
                        )
                        court.court_rating?.let {
                            RatingBar(rating = it)
                        }

                    }
                }
            }
                Spacer(modifier = Modifier.width(20.dp))
                Column {

                    Spacer(modifier = Modifier.height(10.dp))
                    if (expanded) {
                        court.address?.let { it1 ->
                            Text(
                                text = it1,
                                fontSize = 14.sp,
                            )
                        }
                    }

                }

            }
        }
    }

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipWithSubItems(chipLabel: String, chipItems: List<String>, courtsList: List<Court>) {
    var isSelected by remember { mutableStateOf(false) }
    var showSubList by remember { mutableStateOf(false) }
    var filterName by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = showSubList,
        onExpandedChange = { showSubList = !showSubList }
    ) {
        FilterChip(
            modifier = Modifier.menuAnchor(),
            selected = isSelected,
            onClick = {
                isSelected = true
            },
            label = { Text(text = filterName.ifEmpty { chipLabel }) },
            trailingIcon = {
                Icon(
                    modifier = Modifier.rotate(if (showSubList) 180f else 0f),
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "List"
                )
            }
        )
        ExposedDropdownMenu(
            expanded = showSubList,
            onDismissRequest = { showSubList = false },
        ) {
            chipItems.forEach { subListItem ->
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        filterName = subListItem
                        showSubList = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = if (subListItem == filterName || subListItem == chipLabel) {
                            MaterialTheme.colorScheme.onPrimary
                        } else { Color.Transparent }
                    )
                ) {
                    Text(text = subListItem, color = Color.Black)
                }
            }
        }
    }
}*/

@Composable
private fun RatingBar(
    rating: Float
) {
    var rat = rating
    Row{
        for(i in 0 until 5){
           if (rat>=1){
               androidx.compose.material.Icon(
                   modifier = Modifier
                       .size(20.dp),
                   imageVector = Icons.Filled.Star,
                   contentDescription = null,
                   tint = androidx.compose.material.MaterialTheme.colors.primary
               )
           } else if(rat>=0) {
               androidx.compose.material.Icon(
                   modifier = Modifier
                       .size(20.dp),
                   imageVector = Icons.Default.StarHalf,
                   contentDescription = null,
                   tint = androidx.compose.material.MaterialTheme.colors.primary
               )
           } else if(rat<0){
               androidx.compose.material.Icon(
                   modifier = Modifier
                       .size(20.dp),
                   imageVector = Icons.Outlined.Star,
                   contentDescription = null,
                   tint = androidx.compose.material.MaterialTheme.colors.primary
               )
           }
            rat -= 1
        }
    }
}


/*

//card that expands
@Composable
fun CardComponent() {

    Card(
        elevation = 50.dp,
        modifier = Modifier.padding(50.dp)
    ) {
        var expanded by remember { mutableStateOf(false) }
        //raster image
        val image = ImageBitmap.imageResource(id = R.drawable.sport_camp_logo)
        Column(Modifier.clickable { expanded = !expanded }) {
            Image(bitmap = image, contentDescription = "image")
            AnimatedVisibility(expanded) {
                Text(
                    text = "Add reservations screen",
                    style = MaterialTheme.typography.body1
                )
            }
        }

    }
}*/




