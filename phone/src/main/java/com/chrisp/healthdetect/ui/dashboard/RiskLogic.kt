package com.chrisp.healthdetect.ui.dashboard

import androidx.compose.ui.graphics.Color
import com.chrisp.healthdetect.ui.theme.RiskHighRed
import com.chrisp.healthdetect.ui.theme.RiskLowGreen
import com.chrisp.healthdetect.ui.theme.RiskMediumYellow

data class RiskInfo(val level: String, val color: Color)

fun getAscvdRisk(score: Int): RiskInfo {
    return when {
        score <= 5 -> RiskInfo("RENDAH", RiskLowGreen)
        score in 6..10 -> RiskInfo("SEDANG", RiskMediumYellow)
        else -> RiskInfo("TINGGI", RiskHighRed)
    }
}

fun getFraminghamRisk(score: Int): RiskInfo {
    return when {
        score <= 9 -> RiskInfo("RENDAH", RiskLowGreen)
        score in 10..19 -> RiskInfo("SEDANG", RiskMediumYellow)
        else -> RiskInfo("TINGGI", RiskHighRed)
    }
}