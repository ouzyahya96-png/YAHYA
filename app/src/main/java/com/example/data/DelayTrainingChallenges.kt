package com.example.data

object DelayTrainingChallenges {
    val challenges = listOf(
        "Attends 10 minutes avant de consulter ton téléphone après avoir reçu une notification.",
        "Termine entièrement une tâche ou un dossier avant de t'autoriser une pause ou de regarder l'heure.",
        "Attends 5 minutes devant ton repas chaud ou ta boisson avant de prendre la première bouchée.",
        "Dès que tu as envie d'ouvrir un réseau social ou de scroller, attends 10 minutes en respirant calmement.",
        "Attends 15 minutes avant de boire ton café ou ton thé du matin après qu'il soit prêt.",
        "Fais la vaisselle ou range ton bureau immédiatement après utilisation, sans remettre à plus tard.",
        "Quand tu penses à un achat impulsif en ligne, ajoute-le au panier et attends 24 heures entières avant de valider.",
        "Ne consulte pas tes messages ou réseaux sociaux durant la première heure après ton réveil.",
        "Attends d'avoir fini de boire ton verre d'eau complètement avant de poser ton regard à nouveau sur un écran.",
        "Passe 15 minutes en silence complet sans musique, podcast ou distraction lors de ton prochain trajet à pied ou en voiture.",
        "Si tu as une envie irrésistible de grignoter entre les repas, bois un grand verre d'eau et attends 20 minutes.",
        "Laisse passer 3 cycles complets de respiration diaphragmatique lente avant de répondre à un message important.",
        "Prends une douche entièrement froide pendant les 2 dernières minutes, sans précipitation.",
        "Fais ton lit parfaitement dès le saut du lit avant de faire quoi que ce soit d'autre.",
        "Écris pendant 5 minutes tes pensées ou tes objectifs sur papier libre avant d'allumer ton ordinateur.",
        "Attends 15 minutes après la fin de ta journée de travail avant d'allumer la télévision ou de lancer un jeu vidéo.",
        "Ne clique sur aucun lien distrayant ou 'clickbait' pendant toute la journée, concentre-toi sur tes tâches."
    )

    fun getChallengeForDay(dateStr: String): String {
        // Use hash of date string to get a stable random challenge for that day
        val hashCode = dateStr.hashCode()
        val index = kotlin.math.abs(hashCode) % challenges.size
        return challenges[index]
    }
}
