package com.chrisp.healthdetect.ui.dashboard

import androidx.annotation.RawRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.theme.DarkText
import com.chrisp.healthdetect.ui.theme.HeartRateGreen
import com.chrisp.healthdetect.ui.theme.LightGrayText
import com.chrisp.healthdetect.ui.theme.OxygenBlue
import com.chrisp.healthdetect.ui.util.formatTimeAgo
import kotlinx.coroutines.delay

@Composable
fun LottieAnimationPlayer(
    modifier: Modifier = Modifier,
    @RawRes animationRes: Int
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationRes))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}

@Composable
fun VitalsSection(
    heartRate: Int,
    oxygenLevel: Int,
    lastUpdateTimestamp: Long,
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ){
        HeartRateCard(
            modifier = Modifier.weight(1f),
            heartRate = heartRate,
            lastUpdateTimestamp = lastUpdateTimestamp
        )

        OxygenCard(
            modifier = Modifier
                .weight(1f),
            oxygenLevel = oxygenLevel
        )
    }
}

@Composable
fun HeartRateCard(
    modifier: Modifier,
    heartRate: Int,
    lastUpdateTimestamp: Long
) {
    var timeAgo by remember { mutableStateOf(formatTimeAgo(lastUpdateTimestamp)) }

    LaunchedEffect(lastUpdateTimestamp) {
        while (true) {
            timeAgo = formatTimeAgo(lastUpdateTimestamp)
            delay(60000)
        }
    }
    Card (
        modifier = modifier.height(260.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HeartRateGreen)
    ){
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painterResource(id = R.drawable.heart_rate_icon),
                    "Heart Rate", tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Detak Jantung",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {
                LottieAnimationPlayer(
                    animationRes = R.raw.heartbeat_animation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .offset(y = (-35).dp)
                        .align(Alignment.BottomCenter)
                )
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Spacer(Modifier.height(10.dp))
                        Text("$heartRate",
                            fontSize = 40.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 40.sp
                        )
                        Spacer(Modifier.width(8.dp))

                        Text("BPM",
                            fontSize = 16.sp,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 1.dp)
                        )
                    }
                    Text(timeAgo,
                        fontSize = 12.sp,
                        color = Color.LightGray.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun OxygenCard(modifier: Modifier = Modifier, oxygenLevel: Int) {
    Card(
        modifier = modifier.height(260.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, OxygenBlue)
    ) {
        Column(Modifier.padding(top = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painterResource(id = R.drawable.oxygen_icon),
                    "Oxygen",
                    tint = OxygenBlue,
                    modifier = Modifier.padding(top = 10.dp, start = 15.dp, bottom = 10.dp, end = 5.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "Oksigen",
                    color = OxygenBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Box(
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimationPlayer(
                    animationRes = R.raw.waves_animation,
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(1.5f)

                )
                Icon(
                    painter = painterResource(id = R.drawable.water_drop),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.8f),
                    tint = Color.Unspecified
                )
                Text(
                    "$oxygenLevel%",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = OxygenBlue
                )
            }
        }
    }
}

@Composable
fun RiskScoreCard(score: Int, riskInfo: RiskInfo) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(start = 16.dp)
                .size(80.dp)
                .background(riskInfo.color.copy(alpha = 0.3f), CircleShape)
                .border(2.dp, riskInfo.color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = score.toString(),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = riskInfo.color
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text("Anda memiliki risiko", fontSize = 16.sp, color = DarkText)
            Text(
                text = riskInfo.level,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = riskInfo.color
            )
            Text(
                "untuk estimasi 10 tahun",
                fontSize = 12.sp,
                color = LightGrayText
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F2F5)
@Composable
fun VitalsSectionPreview() {
    VitalsSection(
        heartRate = 88,
        oxygenLevel = 98,
        lastUpdateTimestamp = System.currentTimeMillis(),
    )
}

@Preview(showBackground = true)
@Composable
fun RiskScoreCardLowRiskPreview() {
    RiskScoreCard(score = 4, riskInfo = getAscvdRisk(4))
}

@Preview(showBackground = true)
@Composable
fun RiskScoreCardHighRiskPreview() {
    RiskScoreCard(score = 12, riskInfo = getAscvdRisk(12))
}