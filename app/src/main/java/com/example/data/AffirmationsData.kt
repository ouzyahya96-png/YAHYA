package com.example.data

import kotlin.random.Random

object AffirmationsData {
    val affirmations = listOf(
        "Je suis discipliné et je tiens mes engagements envers moi-même.",
        "Chaque jour je deviens plus fort, plus calme, plus maître de moi.",
        "Je mérite le respect que je m'accorde à moi-même.",
        "Ma volonté est plus forte que n'importe quelle tentation passagère.",
        "Je choisis la croissance personnelle et la clarté d'esprit.",
        "Je suis le directeur de ma propre vie et de mes actions.",
        "Mon énergie est précieuse, je la canalise vers mes ambitions.",
        "Je reste concentré sur mes objectifs à long terme.",
        "Chaque jour est une nouvelle opportunité de me dépasser.",
        "Je contrôle mes pensées, mes émotions et mes habitudes.",
        "Je suis fier de ma discipline et de mes progrès quotidiens.",
        "Ma force intérieure grandit à chaque choix conscient que je fais.",
        "Je remplace la gratification immédiate par la réussite durable.",
        "Je suis capable de surmonter tous les obstacles avec calme.",
        "La maîtrise de soi est ma plus grande force et ma plus grande liberté.",
        "Je respecte mon corps, mon esprit et mes valeurs.",
        "Chaque décision consciente me rapproche de la meilleure version de moi-même.",
        "Je choisis d'agir avec intégrité, force et dignité.",
        "Je suis le gardien de ma concentration et de mon attention.",
        "Mon esprit est serein, mon corps est fort, ma vision est claire.",
        "Je tire ma force de la régularité de mes efforts.",
        "La discipline d'aujourd'hui est la liberté de demain.",
        "Je suis maître de mon destin et responsable de mes résultats.",
        "Je choisis d'élever mes standards chaque jour.",
        "Chaque respiration renforce ma détermination et ma paix intérieure.",
        "Je célèbre mes petites victoires qui construisent mes grands succès.",
        "Je reste imperturbable face aux distractions de ce monde.",
        "Mon potentiel est illimité, guidé par une discipline inébranlable.",
        "Je choisis de vivre consciemment et de progresser constamment.",
        "Je suis déterminé, fort et pleinement engagé envers ma réussite.",
        "Je suis exceptionnel et je le sais.",
        "Je vais accomplir de grandes choses.",
        "Je vis à un niveau de conscience supérieur.",
        "J'attire le meilleur dans ma vie.",
        "Mon énergie est protégée et précieuse.",
        "Je mérite le succès que je construis chaque jour.",
        "Ma discipline façonne mon avenir.",
        "Je suis en paix avec qui je deviens.",
        "Chaque seconde de mon temps est un investissement précieux.",
        "Ma vision est claire, mon engagement est inébranlable.",
        "Je refuse d'abaisser mes standards pour plaire ou m'intégrer.",
        "Je possède le courage d'agir malgré les doutes.",
        "Mon potentiel se libère à mesure que je renforce ma volonté.",
        "Je choisis la maîtrise de moi-même plutôt que la distraction facile.",
        "Ma force réside dans ma capacité à rester focus sous la pression.",
        "La réussite n'est pas un hasard, c'est mon habitude quotidienne.",
        "Je suis fier de l'homme intègre et puissant que je construis.",
        "Je purifie mon esprit de toute influence négative.",
        "Je suis le créateur de ma réalité et de ma force.",
        "Ma présence inspire la force, le calme et la discipline.",
        "Je protège ma paix intérieure comme mon bien le plus précieux.",
        "Chaque effort fourni aujourd'hui portera ses fruits demain.",
        "Je suis aligné avec mes valeurs les plus hautes.",
        "Je n'accepte que l'excellence dans mes pensées et mes actes.",
        "Je m'élève au-dessus du bruit et de la médiocrité.",
        "Mon esprit est une forteresse imprenable d'ambition et de paix."
    )
    
    val confidenceStatements = listOf(
        "Je fais confiance à mes décisions.",
        "Je n'ai pas besoin de l'approbation des autres pour avancer.",
        "Ma valeur ne dépend pas du jugement extérieur.",
        "Je me tiens debout, même dans l'incertitude.",
        "Je possède les compétences pour surmonter tous les défis d'aujourd'hui.",
        "Chaque obstacle est une opportunité de prouver ma valeur et ma résilience.",
        "Je m'exprime avec clarté, assurance et sincérité.",
        "Je suis fier de mon parcours et des batailles que j'ai gagnées en silence.",
        "Ma confiance grandit à mesure que je relève de nouveaux défis.",
        "Je ne me compare pas aux autres ; je cherche seulement à me dépasser moi-même.",
        "Je mérite de réussir et de vivre une vie épanouie.",
        "Je contrôle ma posture, mon regard et ma voix pour refléter ma force intérieure.",
        "Je suis capable d'apprendre rapidement et de m'adapter à toutes les situations.",
        "Je ne crains pas l'échec ; c'est simplement une leçon vers la maîtrise.",
        "Ma présence est calme, forte et rassurante pour moi et pour les autres.",
        "Je définis mes propres limites et je les fais respecter avec fermeté."
    )

    fun getDailyDeterministicItem(list: List<String>, date: String, offset: Int = 0): String {
        if (list.isEmpty()) return ""
        val dayOfYear = try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val parsedDate = sdf.parse(date) ?: java.util.Date()
            val cal = java.util.Calendar.getInstance()
            cal.time = parsedDate
            cal.get(java.util.Calendar.DAY_OF_YEAR)
        } catch (e: Exception) {
            date.hashCode()
        }
        val index = Math.abs(dayOfYear + offset) % list.size
        return list[index]
    }

    fun getRandomAffirmation(): String {
        val index = Random.nextInt(affirmations.size)
        return affirmations[index]
    }

    fun getCombinedAffirmations(count: Int): String {
        return affirmations.shuffled().take(count).joinToString(" ")
    }
}
