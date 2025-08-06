package com.chrisp.healthdetect.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chrisp.healthdetect.ui.theme.BackgroundGray
import com.chrisp.healthdetect.ui.theme.DarkText
import com.chrisp.healthdetect.ui.theme.LightGrayText
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.theme.HeartRateGreen

@Composable
fun DashboardScreen(heartRate: Int, lastUpdatedTimestamp: Long) {
    val username = "USERNAME"
    val oxygenLevel = 98
    val ascvdScore = 4
    val framinghamScore = 6

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = { AppBottomNavigation() }
    ) { paddingValues ->
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ){
            item {GreetingHeader(username = username) }
            item { VitalsSection(heartRate = heartRate, oxygenLevel = oxygenLevel) }

            item {
                Text(
                    "Skor ASCVD",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )
                RiskScoreCard(score = ascvdScore, riskInfo = getAscvdRisk(ascvdScore))
            }

            item {
                Text(
                    text = "Skor FRAMINGHAM",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                RiskScoreCard(score = framinghamScore, riskInfo = getFraminghamRisk(framinghamScore))
            }
        }
    }
}

@Composable
fun GreetingHeader(username: String) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column {
            Text("Selamat datang,", fontSize = 18.sp, color = LightGrayText)
            Text(username, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DarkText)
        }
    }
}

@Composable
fun AppBottomNavigation() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Dashboard", "Aktivitas", "Cek Gizi", "Profil")
    val icons = listOf(R.drawable.dashboard, R.drawable.activity, R.drawable.nutrition, R.drawable.profile)
    val selectedIcons = listOf(R.drawable.dashboard_clicked, R.drawable.activity_clicked, R.drawable.nutrition_clicked, R.drawable.profile_clicked)

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = if (selectedItem == index) selectedIcons[index] else icons[index]),
                        contentDescription = item,
                        tint = Color.Unspecified
                    )
                },
                selected = selectedItem == index,
                onClick = {selectedItem = index},
                label = { Text(item) },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = HeartRateGreen,
                    unselectedTextColor = LightGrayText,
                    indicatorColor = Color.Transparent
                ),
            )
        }
    }
}

//@Preview
//@Composable
//fun DashboardScreenPreview() {
//    DashboardScreen(heartRate = 90,)
//}