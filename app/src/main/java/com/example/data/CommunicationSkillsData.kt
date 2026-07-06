package com.example.data

import java.util.*

object CommunicationSkillsData {
    val skills = listOf(
        "Fais une pause de 2 secondes avant de répondre à une question importante aujourd'hui.",
        "Baisse le ton de ta voix en fin de phrase affirmative aujourd'hui pour projeter de l'assurance.",
        "Garde tes mains visibles lors de tes discussions aujourd'hui, évite de croiser les bras.",
        "Observe un fait précis sans émettre de jugement ou d'interprétation lors de ton prochain échange.",
        "Nomme mentalement l'émotion que tu ressens avant de répondre à une situation stressante aujourd'hui.",
        "Ralentis volontairement ton débit de parole sur les points clés de tes conversations aujourd'hui.",
        "Maintiens un contact visuel stable et naturel (3-5 secondes par personne) lors de tes réunions.",
        "Pose une question ouverte au lieu de supposer les intentions de ton interlocuteur aujourd'hui.",
        "Reformule ce que ton interlocuteur vient de dire pour lui montrer que tu l'écoute activement.",
        "Évite les mots de remplissage ('euh', 'du coup', 'en fait') lors de tes prises de parole aujourd'hui.",
        "Varie le volume de ta voix aujourd'hui pour souligner les points importants de ton discours.",
        "Adopte une posture ouverte : garde le dos droit et détends tes épaules durant tes échanges.",
        "Demande l'avis de quelqu'un d'autre avec bienveillance avant de prendre une décision aujourd'hui.",
        "Prends trois respirations lentes avant de répondre si tu te sens mis au défi aujourd'hui.",
        "Accélère légèrement ton débit sur les détails secondaires pour garder l'attention de l'auditoire.",
        "Aujourd'hui, termine tes phrases déclaratives sans monter dans les aigus (évite l'uptalk).",
        "Accorde 100% de ton attention visuelle à la personne qui te parle sans regarder ton écran.",
        "Valide d'abord l'émotion de l'autre ('Je comprends ta frustration...') avant d'apporter une solution.",
        "Utilise des gestes ouverts et illustratifs alignés avec ton message pour maximiser ton impact.",
        "Remercie sincèrement un collaborateur ou proche aujourd'hui en spécifiant une action précise."
    )

    fun getSkillForDate(dateStr: String): String {
        val hash = dateStr.hashCode()
        val index = Math.abs(hash) % skills.size
        return skills[index]
    }
}
