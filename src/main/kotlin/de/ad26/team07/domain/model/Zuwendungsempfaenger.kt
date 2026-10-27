package de.ad26.team07.domain.model

data class Zuwendungsempfaenger(
    val name: String,
    val steuernummer: String,
    val finanzamt: String,
    val rechtsform: String = "e. V.",
    val sitz: Adresse,
    val iban: String,
    val bic: String,
    val kontoinhaber: String,
    val satzungszwecke: List<SteuerbeguenstigterZweck>,
    val gemeinnuetzigkeitsbescheide: List<Gemeinnuetzigkeitsbescheid>,
    val whiteLabelKonfiguration: WhiteLabelKonfiguration
) {
    init {
        require(name.isNotBlank()) { "Vereinsname darf nicht leer sein" }
        require(iban.replace(" ", "").length in 15..34) { "Ungültiges IBAN-Format" }
        require(bic.isNotBlank()) { "BIC darf nicht leer sein" }
        require(kontoinhaber.isNotBlank()) { "Kontoinhaber darf nicht leer sein" }
        require(satzungszwecke.isNotEmpty()) { "Mindestens ein Satzungszweck muss hinterlegt sein" }
    }

    val ibanBereinigt: String get() = iban.replace(" ", "").uppercase()
}
