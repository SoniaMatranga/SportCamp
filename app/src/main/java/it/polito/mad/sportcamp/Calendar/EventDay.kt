package it.polito.mad.sportcamp.Calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import it.polito.mad.sportcamp.ui.theme.OrangeActionBar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDay(
    state: DayState<DynamicSelectionState>,
    modifier: Modifier = Modifier,
) {
    val date = state.date
    val selectionState = state.selectionState
    val isSelected = selectionState.isDateSelected(date)

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp),
        elevation = if (state.isFromCurrentMonth) 6.dp else 0.dp,
        border = if (state.isCurrentDay) {
            BorderStroke(3.dp, MaterialTheme.colors.primary)
        } else if (isSelected) {
            BorderStroke(1.dp, MaterialTheme.colors.primary)
        } else {
            null
        },
        backgroundColor = if (state.isFromCurrentMonth) {
            MaterialTheme.colors.secondary
        } else {
            Color.Gray
        },
        contentColor = if (isSelected) {
            MaterialTheme.colors.onPrimary
        } else {
            OrangeActionBar
        }
    ) {
        Column(
            modifier = Modifier
                .clickable {  },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = date.dayOfMonth.toString(),
            color = Color.Black)
            if (date.dayOfMonth.toString() == "18" || date.dayOfMonth.toString() == "15") {
                Row() {
                    Spacer(modifier = Modifier.height(5.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                    )

                }
                /*Text(
                    text = "Sport", //text inside calendar day
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center
                )*/
            }
        }
    }
}