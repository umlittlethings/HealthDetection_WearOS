package com.chrisp.healthdetect.ui.heartrate

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.chrisp.healthdetect.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chrisp.healthdetect.ui.dashboard.LottieAnimationPlayer
import com.chrisp.healthdetect.ui.theme.DarkText
import com.chrisp.healthdetect.ui.theme.HeartRateGreen
import com.chrisp.healthdetect.ui.theme.LightGrayText
import com.chrisp.healthdetect.ui.util.formatTimeAgo
import java.sql.Timestamp
import kotlin.math.max

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HeartRateDetailScreen (
    currentBpm: Int,
    avgBpm: Int,
    minBpm: Int,
    maxBpm: Int,
    lastUpdateTimestamp: Long,
    onBackClick: () -> Unit
) {
    val interpretation = getInterpretationForBpm(currentBpm)
    var isDetailExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    "Detak Jantung",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 30.sp
                ) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painterResource(id = R.drawable.arrow_left),
                            contentDescription = "Kembali",
                            tint = HeartRateGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            item {
                Spacer(modifier = Modifier.height(24.dp))
                MainBpmDisplay(bpm = currentBpm, timestamp = lastUpdateTimestamp)

                Image(
                    painter = painterResource(id = R.drawable.heart_line),
                    contentDescription = "Garis BPM",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(top = 8.dp)
                )
            }
            
            item {
                StatsRow(avg = avgBpm, min = minBpm, max = maxBpm)
            }

            item {
                SectionTitle(title = "Interpretasi")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeartRateDetailScreenPreview() {
        HeartRateDetailScreen(
            currentBpm = 55,
            avgBpm = 97,
            minBpm = 42,
            maxBpm = 120,
            lastUpdateTimestamp = System.currentTimeMillis(),
            onBackClick = {}
        )
}