package com.example.hydrohero.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.hydrohero.ui.theme.MainViewModel
import com.example.hydrohero.database.WaterIntake
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.hydrohero.R

@Composable
fun HistoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedView by remember { mutableStateOf(HistoryView.DAILY) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HistoryView.values().forEach { view ->
                Button(
                    onClick = { selectedView = view },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedView == view)
                            colorResource(R.color.dark_blue)
                        else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(view.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedView) {
            HistoryView.DAILY -> DailyView(uiState.weeklyIntakes)
            HistoryView.WEEKLY -> WeeklyView(uiState.weeklyIntakes)
            HistoryView.MONTHLY -> MonthlyView(uiState.monthlyIntakes)
        }
    }
}

@Composable
fun DailyView(intakes: Map<LocalDate, WaterIntake>) {
    LazyColumn {
        items(intakes.entries.toList().sortedByDescending { it.key }) { (date, intake) ->
            HistoryItem(date.format(DateTimeFormatter.ISO_LOCAL_DATE), "${intake.amount} ml")
        }
    }
}

@Composable
fun WeeklyView(intakes: Map<LocalDate, WaterIntake>) {
    val weeklyTotals = intakes.entries.groupBy {
        it.key.minusDays(it.key.dayOfWeek.value.toLong() - 1)
    }.mapValues { (_, dailyIntakes) ->
        dailyIntakes.sumOf { it.value.amount }
    }

    LazyColumn {
        items(weeklyTotals.entries.toList().sortedByDescending { it.key }) { (weekStart, total) ->
            val weekEnd = weekStart.plusDays(6)
            HistoryItem(
                "${weekStart.format(DateTimeFormatter.ISO_LOCAL_DATE)} - ${weekEnd.format(DateTimeFormatter.ISO_LOCAL_DATE)}",
                "$total ml"
            )
        }
    }
}

@Composable
fun MonthlyView(monthlyIntakes: Map<LocalDate, Int>) {
    LazyColumn {
        items(monthlyIntakes.entries.toList().sortedByDescending { it.key }) { (monthStart, total) ->
            HistoryItem(
                monthStart.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                "$total ml"
            )
        }
    }
}

@Composable
fun HistoryItem(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

enum class HistoryView {
    DAILY, WEEKLY, MONTHLY
}