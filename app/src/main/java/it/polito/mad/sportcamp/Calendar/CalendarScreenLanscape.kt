package it.polito.mad.sportcamp.Calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.boguszpawlowski.composecalendar.CalendarState
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreenLandscape(
    state: CalendarState<DynamicSelectionState>,
) {


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MaterialTheme.colors.surface
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())) {

                SelectableCalendar(
                    modifier = Modifier.animateContentSize(),
                    dayContent = { dayState ->
                        EventDay(
                            state = dayState,
                        )
                    },
                    showAdjacentMonths = true,
                )
            }

            Box(modifier = Modifier
                .fillMaxHeight()
                .weight(1f), contentAlignment = Alignment.Center) {

                    CardEvent()

            }
        }
    }
}
