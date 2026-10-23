package com.drinkwater.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drinkwater.app.data.DailyTotal
import java.time.LocalDate

@Composable
fun BarChart(
    dailyTotals: List<DailyTotal>,
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val scrollState = rememberScrollState()

    LaunchedEffect(dailyTotals.size) {
        withFrameNanos { }
        scrollState.scrollTo(scrollState.maxValue)
    }

    val maxMl = maxOf(dailyTotals.maxOfOrNull { it.totalMl } ?: 0, 200)
    val selected = dailyTotals.firstOrNull { it.date == selectedDate }

    Column(modifier = modifier) {
        Text(
            text = if (selected != null) {
                "${selected.date.monthValue}月${selected.date.dayOfMonth}日 · ${selected.totalMl} ml"
            } else {
                "点击柱子查看单日详情"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            dailyTotals.forEach { dayTotal ->
                val isSelected = selectedDate == dayTotal.date
                val fraction = (dayTotal.totalMl.toFloat() / maxMl).coerceIn(0.02f, 1f)
                val barColor = when {
                    isSelected -> MaterialTheme.colorScheme.tertiary
                    dayTotal.totalMl > 0 -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(26.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectedDate = if (isSelected) null else dayTotal.date }
                        .padding(vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(22.dp)
                            .height(150.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(fraction)
                                .background(
                                    color = barColor,
                                    shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                )
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = dayTotal.date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
