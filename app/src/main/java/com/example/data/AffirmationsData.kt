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
        "Je suis déterminé, fort et pleinement engagé envers ma réussite."
    )

    fun getRandomAffirmation(): String {
        val index = Random.nextInt(affirmations.size)
        return affirmations[index]
    }
}
