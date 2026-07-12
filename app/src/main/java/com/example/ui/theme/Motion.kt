package com.example.ui.theme

import androidx.compose.animation.core.CubicBezierEasing

object MotionTokens {
    // Durées, échelonnées selon la taille/portée de l'élément animé
    const val DURATION_QUICK = 100   // micro-interactions (bouton, checkbox)
    const val DURATION_STANDARD = 200 // transitions de composants (cartes, accordéons)
    const val DURATION_EMPHASIZED = 300 // transitions de page complètes

    // Courbes nommées, cohérentes avec le Material Motion System
    val EasingStandard = CubicBezierEasing(0.2f, 0f, 0f, 1f)      // entrées/sorties de composants
    val EasingEmphasized = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f) // transitions de page, plus dramatique
    val EasingDecelerate = CubicBezierEasing(0f, 0f, 0f, 1f)       // éléments qui arrivent (entrance)
    val EasingAccelerate = CubicBezierEasing(0.3f, 0f, 1f, 1f)     // éléments qui partent (exit)
}
