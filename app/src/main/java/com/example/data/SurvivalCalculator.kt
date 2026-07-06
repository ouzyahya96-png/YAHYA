package com.example.data

data class TargetInfo(
    val category: String,
    val targetQuantity: Float,
    val unit: String,
    val why: String
)

object SurvivalCalculator {
    fun calculateTargets(householdSize: Int): Map<String, TargetInfo> {
        val size = householdSize.coerceAtLeast(1)
        return mapOf(
            "Eau" to TargetInfo(
                category = "Eau",
                targetQuantity = 84f * size,
                unit = "L",
                why = "Stock tampon de 3 semaines de sécurité absolue pour la boisson et l'hygiène de base (4L/jour par personne)."
            ),
            "Céréales" to TargetInfo(
                category = "Céréales",
                targetQuantity = 150f * size,
                unit = "kg",
                why = "Base calorique principale (riz, pâtes, blé, avoine). Fournit l'énergie essentielle quotidienne et se stocke sur des décennies."
            ),
            "Légumineuses" to TargetInfo(
                category = "Légumineuses",
                targetQuantity = 35f * size,
                unit = "kg",
                why = "Apport protéique végétal indispensable (lentilles, haricots, pois chiches), complément idéal des céréales."
            ),
            "Conserves" to TargetInfo(
                category = "Conserves",
                targetQuantity = 40f * size,
                unit = "kg",
                why = "Protéines animales ou plats préparés (viande, poisson, légumes en conserve) pour le moral et la diversité nutritionnelle."
            ),
            "Graisses" to TargetInfo(
                category = "Graisses",
                targetQuantity = 10f * size,
                unit = "L",
                why = "Apport calorique dense, lipides essentiels et cuisson (huile d'olive, de tournesol, ghee)."
            ),
            "Sucre" to TargetInfo(
                category = "Sucre",
                targetQuantity = 25f * size,
                unit = "kg",
                why = "Sucre, miel ou confitures. Réserve de glucides rapides, réconfort moral et excellent conservateur naturel."
            ),
            "Lait en poudre" to TargetInfo(
                category = "Lait en poudre",
                targetQuantity = 25f * size,
                unit = "kg",
                why = "Apport calcium, vitamines et protéines. Facile à reconstituer pour la cuisine ou les enfants du foyer."
            ),
            "Sel" to TargetInfo(
                category = "Sel",
                targetQuantity = 4f * size,
                unit = "kg",
                why = "Minéral vital pour l'organisme, exhausteur de goût et fondamental pour la conservation des aliments (salaison)."
            ),
            "Hygiène" to TargetInfo(
                category = "Hygiène",
                targetQuantity = 50f * size,
                unit = "unités",
                why = "Savon, dentifrice, papier toilette, produits d'entretien. Crucial pour éviter la propagation de maladies."
            ),
            "Médical" to TargetInfo(
                category = "Médical",
                targetQuantity = 20f * size,
                unit = "unités",
                why = "Trousse de premiers soins complète, pansements, antiseptiques et traitements de base pour parer aux blessures."
            )
        )
    }
}
