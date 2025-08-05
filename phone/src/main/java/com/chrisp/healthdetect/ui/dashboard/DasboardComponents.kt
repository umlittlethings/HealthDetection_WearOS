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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.*
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.theme.DarkText
import com.chrisp.healthdetect.ui.theme.HeartRateGreen
import com.chrisp.healthdetect.ui.theme.LightGrayText
import com.chrisp.healthdetect.ui.theme.OxygenBlue
import com.chrisp.healthdetect.ui.theme.RiskLowGreen

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
fun VitalsSection(heartRate: Int, oxygenLevel: Int) {
    Row (
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ){
        HeartRateCard(
            modifier = Modifier.weight(1f),
            heartRate = heartRate
        )

        OxygenCard(
            modifier = Modifier.weight(1f), oxygenLevel = oxygenLevel
        )
    }
}

@Composable
fun HeartRateCard(
    modifier: Modifier,
    heartRate: Int
) {
    Card (
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HeartRateGreen)
    ){
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(id = R.drawable.heart_rate_icon), "Heart Rate", tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Detak Jantung", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
                LottieAnimationPlayer(
                    animationRes = R.raw.heartbeat_animation,
                    modifier = Modifier.fillMaxWidth().height(90.dp).offset(y = (-10).dp)
                )
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("$heartRate",
                            fontSize = 40.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 40.sp
                        )
                        Spacer(Modifier.width(4.dp))

                        Text("BPM",
                            fontSize = 16.sp,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text("2 mins ago",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OxygenCard(modifier: Modifier = Modifier, oxygenLevel: Int) {
    Card(
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, OxygenBlue)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painterResource(id = R.drawable.oxygen_icon),
                    "Oxygen",
//                    tint = OxygenBlue
                )
                Spacer(Modifier.width(8.dp))
                Text("Oksigen", color = OxygenBlue, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimationPlayer(
                    animationRes = R.raw.waves_animation,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                )
                Icon(
                    painter = painterResource(id = R.drawable.water_drop),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.8f),
                    tint = Color.Unspecified
                )
                Text(
                    "$oxygenLevel",
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
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
        ,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
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
                    color = Color.White
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
                Text("untuk estimasi 10 tahun", fontSize = 12.sp, color = LightGrayText)
            }
        }
    }
}

@Preview
@Composable
fun RiskScoreCardPreview() {

}