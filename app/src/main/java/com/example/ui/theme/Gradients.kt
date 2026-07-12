package com.example.ui.theme

import androidx.compose.ui.graphics.Brush

object GradientTokens {
    val sunsetHorizontal = Brush.horizontalGradient(listOf(SunsetGoldLight, SunsetAmber, GoldDeep))
    val sunsetVertical = Brush.verticalGradient(listOf(SunsetGoldLight, SunsetAmber))
    val sunsetRadial = Brush.radialGradient(listOf(SunsetGoldLight, GoldDeep))
}

fun getTimeAdaptiveGradient(hour: Int): Brush {
    return when (hour) {
        in 6..10 -> {
            // Matin (6h-11h) : dégradé plus clair et doré
            Brush.horizontalGradient(listOf(SunsetGoldLight, SunsetGoldLight, SunsetAmber))
        }
        in 11..16 -> {
            // Après-midi (11h-17h) : dégradé standard déjà existant
            GradientTokens.sunsetHorizontal
        }
        in 17..20 -> {
            // Soir (17h-21h) : dégradé plus chaud, virant vers SunsetCopper plus prononcé
            Brush.horizontalGradient(listOf(SunsetAmber, SunsetCopper, GoldDeep))
        }
        else -> {
            // Nuit (21h-6h) : dégradé atténué en intensité (opacité réduite d'environ 20%)
            Brush.horizontalGradient(
                listOf(
                    SunsetGoldLight.copy(alpha = 0.8f),
                    SunsetAmber.copy(alpha = 0.8f),
                    GoldDeep.copy(alpha = 0.8f)
                )
            )
        }
    }
}
