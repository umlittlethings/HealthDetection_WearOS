package com.chrisp.healthdetect.ui.heartrate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.dashboard.LottieAnimationPlayer
import com.chrisp.healthdetect.ui.theme.DarkText
import com.chrisp.healthdetect.ui.theme.LightGrayText
import com.chrisp.healthdetect.ui.util.formatTimeAgo

@Composable
fun MainBpmDisplay(bpm: Int, timestamp: Long) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        LottieAnimationPlayer(
            animationRes = R.raw.heart,
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$bpm",
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    lineHeight = 70.sp
                )
                Text(
                    "BPM",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFD172E),
                    modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                )
            }
            Text(
                text = formatTimeAgo(timestamp),
                color = LightGrayText,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                "$value",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                lineHeight = 38.sp
            )

            Text(
                "BPM",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
        }
    }
}

@Composable
fun StatsRow(avg:Int, min: Int, max: Int) {
    Column(
        modifier = Modifier
            .padding(vertical = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(label = "Average", value = avg, color = Color(0xFFC75400))
            StatItem(label = "Min", value = min, color = Color(0xFF8B00C7))
            StatItem(label = "Max", value = max, color = Color(0xFF8B00C7))
        }
        Divider(
            modifier = Modifier.padding(top = 24.dp),
            color = Color.LightGray.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun SectionTitle(title: String) {}

@Composable
fun InterpretationCard(text: String) {}

@Composable
fun DetailInterpretationSection(isExpanded: Boolean, onToggle: () -> Unit) {}

@Composable
fun InterpretationTable() {}

@Composable
fun RowScope.TableCell(text: String, weight: Float, isHeader: Boolean = false) {}