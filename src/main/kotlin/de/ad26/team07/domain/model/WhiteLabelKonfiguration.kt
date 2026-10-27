package de.ad26.team07.domain.model

data class WhiteLabelKonfiguration(
    val slug: String,
    val wizardTitel: String,
    val logoUrl: String? = null,
    val primaerFarbe: String = "#2563eb",
    val dankesNachricht: String = "Vielen Dank für Ihre Unterstützung!"
) {
    init {
        require(slug.isNotBlank()) { "Slug darf nicht leer sein" }
        require(slug.matches(Regex("^[a-z0-9-]+$"))) { "Slug darf nur aus Kleinbuchstaben, Ziffern und Bindestrichen bestehen" }
        require(wizardTitel.isNotBlank()) { "Titel darf nicht leer sein" }
    }
}
