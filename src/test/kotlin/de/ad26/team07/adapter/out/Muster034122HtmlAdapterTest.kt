package de.ad26.team07.adapter.out

import de.ad26.team07.domain.model.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertTrue

@DisplayName("Muster034122HtmlAdapter (Amtliche Formulargenerierung BMF 034122)")
class Muster034122HtmlAdapterTest {

    private val adapter = Muster034122HtmlAdapter()

    private val verein = Zuwendungsempfaenger(
        name = "Katzenschutz Münster e. V.",
        steuernummer = "12/345/67890",
        finanzamt = "Münster-Innenstadt",
        sitz = Adresse("Katzenallee", "9", "48149", "Münster"),
        iban = "DE21400600000123456789",
        bic = "GENODED1MUE",
        kontoinhaber = "Katzenschutz Münster e. V.",
        satzungszwecke = listOf(SteuerbeguenstigterZweck("§ 52 Abs. 2 Nr. 14 AO", "Förderung des Tierschutzes")),
        gemeinnuetzigkeitsbescheide = listOf(
            Freistellungsbescheid("12/345/67890", "Münster-Innenstadt", LocalDate.of(2023, 5, 10), 2022)
        ),
        whiteLabelKonfiguration = WhiteLabelKonfiguration("katzenschutz-ms", "Katzenschutz")
    )

    private val spender = Spender(
        name = "Mano Korsanke",
        anschrift = Adresse("Universitätsstraße", "14", "48149", "Münster")
    )

    @Test
    fun `generiert vollstaendiges amtliches HTML-Formular nach BMF-Vorgabe`() {
        val spende = Geldspende(
            betrag = BigDecimal("75.00"),
            zuwendungsDatum = LocalDate.of(2026, 10, 28),
            zuwendender = spender,
            empfaenger = verein
        )

        val bestaetigung = Zuwendungsbestaetigung(
            bestaetigungsNummer = "ZB-2026-3280",
            ausstellungsDatum = LocalDate.of(2026, 10, 28),
            ausstellungsOrt = "Münster",
            geldzuwendung = spende,
            gemeinnuetzigkeitsbescheid = verein.gemeinnuetzigkeitsbescheide.first(),
            gefoerderterZweck = verein.satzungszwecke.first(),
            unterschrift = "Mano Korsanke (Kassenwart)"
        )

        val html = adapter.generiereDokument(bestaetigung)

        // Pflichtangaben nach Muster 034122 prüfen
        assertTrue(html.contains("034122 (Fassung 2013)"), "Musterbezeichnung muss enthalten sein")
        assertTrue(html.contains("Bestätigung über Geldzuwendungen / Mitgliedsbeitrag"), "Formulartitel muss stimmen")
        assertTrue(html.contains("Katzenschutz Münster e. V."), "Vereinsname muss enthalten sein")
        assertTrue(html.contains("Mano Korsanke"), "Spendername muss enthalten sein")
        assertTrue(html.contains("75,00 EUR"), "Betrag in Ziffern muss formatiert sein")
        assertTrue(html.contains("fünfundsiebzig Euro"), "Betrag in Worten muss stimmen")
        assertTrue(html.contains("Förderung des Tierschutzes"), "Satzungszweck muss stimmen")
        assertTrue(html.contains("Finanzamtes <strong>Münster-Innenstadt</strong>"), "Finanzamt muss stimmen")
        assertTrue(html.contains("StNr. <strong>12/345/67890</strong>"), "Steuernummer muss stimmen")
        assertTrue(html.contains("Veranlagungszeitraum <strong>2022</strong>"), "Veranlagungszeitraum muss stimmen")
        assertTrue(html.contains("ZB-2026-3280"), "Bestätigungsnummer muss enthalten sein")
        assertTrue(html.contains("§ 10b Abs. 4 EStG"), "Haftungshinweis muss enthalten sein")
        assertTrue(html.contains("window.print()"), "Druckfunktion muss enthalten sein")
    }
}
