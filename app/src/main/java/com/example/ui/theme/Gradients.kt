package com.example.ui.theme

import androidx.compose.ui.graphics.Brush

object GradientTokens {
    val sunsetHorizontal = Brush.horizontalGradient(listOf(SunsetGoldLight, SunsetAmber, GoldDeep))
    val sunsetVertical = Brush.verticalGradient(listOf(SunsetGoldLight, SunsetAmber))
    val sunsetRadial = Brush.radialGradient(listOf(SunsetGoldLight, GoldDeep))
}
