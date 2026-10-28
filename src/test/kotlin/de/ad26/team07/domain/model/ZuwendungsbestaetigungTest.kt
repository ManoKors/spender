package de.ad26.team07.domain.model

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DisplayName("Zuwendungsbestätigung (Muster 034122 Compliance & OCL-Prüfungen)")
class ZuwendungsbestaetigungTest {

    private val adresseVerein = Adresse("Katzenallee", "9", "48149", "Münster")
    private val adresseSpender = Adresse("Universitätsstraße", "14", "48149", "Münster")
    private val zweckTierschutz = SteuerbeguenstigterZweck("§ 52 Abs. 2 Nr. 14 AO", "Förderung des Tierschutzes")
    private val zweckSport = SteuerbeguenstigterZweck("§ 52 Abs. 2 Nr. 21 AO", "Förderung des Sports")

    private val bescheidGueltig = Freistellungsbescheid(
        steuernummer = "12/345/67890",
        finanzamt = "Münster-Innenstadt",
        bescheidDatum = LocalDate.of(2023, 5, 10),
        letzterVeranlagungszeitraum = 2022
    )

    private val bescheidAbgelaufen = Freistellungsbescheid(
        steuernummer = "12/345/67890",
        finanzamt = "Münster-Innenstadt",
        bescheidDatum = LocalDate.of(2015, 1, 1),
        letzterVeranlagungszeitraum = 2014
    )

    private val verein = Zuwendungsempfaenger(
        name = "Katzenschutz Münster e. V.",
        steuernummer = "12/345/67890",
        finanzamt = "Münster-Innenstadt",
        sitz = adresseVerein,
        iban = "DE21400600000123456789",
        bic = "GENODED1MUE",
        kontoinhaber = "Katzenschutz Münster e. V.",
        satzungszwecke = listOf(zweckTierschutz),
        gemeinnuetzigkeitsbescheide = listOf(bescheidGueltig),
        whiteLabelKonfiguration = WhiteLabelKonfiguration("katzenschutz-ms", "Katzenschutz")
    )

    private val spender = Spender(
        name = "Mano Korsanke",
        anschrift = adresseSpender,
        email = "spende@example.org"
    )

    @Test
    fun `erstellt rechtsgueltige Zuwendungsbestaetigung mit korrekter Betragsumwandlung`() {
        val spende = Geldspende(
            betrag = BigDecimal("75.00"),
            zuwendungsDatum = LocalDate.of(2026, 10, 28),
            zuwendender = spender,
            empfaenger = verein
        )

        val bestaetigung = Zuwendungsbestaetigung(
            bestaetigungsNummer = "ZB-2026-0001",
            ausstellungsDatum = LocalDate.of(2026, 10, 28),
            ausstellungsOrt = "Münster",
            geldzuwendung = spende,
            gemeinnuetzigkeitsbescheid = bescheidGueltig,
            gefoerderterZweck = zweckTierschutz,
            unterschrift = "M. Mustermann (Kassenwart)"
        )

        assertEquals("ZB-2026-0001", bestaetigung.bestaetigungsNummer)
        assertEquals("fünfundsiebzig Euro", bestaetigung.betragInWorten)
        assertTrue(bestaetigung.pruefeBescheidGueltigkeit())
        assertTrue(bestaetigung.vereinfachterNachweisMoeglich) // 75 € <= 300 €
    }

    @Test
    fun `verweigert Ausstellung wenn Bescheid zum Ausstellungsdatum abgelaufen ist OCL 3`() {
        val spende = Geldspende(
            betrag = BigDecimal("150.00"),
            zuwendungsDatum = LocalDate.of(2026, 10, 28),
            zuwendender = spender,
            empfaenger = verein
        )

        val ex = assertThrows<IllegalArgumentException> {
            Zuwendungsbestaetigung(
                bestaetigungsNummer = "ZB-2026-0002",
                ausstellungsDatum = LocalDate.of(2026, 10, 28),
                ausstellungsOrt = "Münster",
                geldzuwendung = spende,
                gemeinnuetzigkeitsbescheid = bescheidAbgelaufen, // Zu alt!
                gefoerderterZweck = zweckTierschutz
            )
        }
        assertTrue(ex.message!!.contains("OCL 3"))
    }

    @Test
    fun `verweigert Ausstellung wenn Zweck nicht in Satzung enthalten ist OCL 4`() {
        val spende = Geldspende(
            betrag = BigDecimal("50.00"),
            zuwendungsDatum = LocalDate.of(2026, 10, 28),
            zuwendender = spender,
            empfaenger = verein
        )

        val ex = assertThrows<IllegalArgumentException> {
            Zuwendungsbestaetigung(
                bestaetigungsNummer = "ZB-2026-0003",
                ausstellungsDatum = LocalDate.of(2026, 10, 28),
                ausstellungsOrt = "Münster",
                geldzuwendung = spende,
                gemeinnuetzigkeitsbescheid = bescheidGueltig,
                gefoerderterZweck = zweckSport // Nicht in Satzung!
            )
        }
        assertTrue(ex.message!!.contains("OCL 4"))
    }

    @Test
    fun `erzwingt Verzichtserklaerung bei Aufwandsspende OCL 5`() {
        // Ohne Verzichtserklärung muss eine Aufwandsspende fehlschlagen
        assertThrows<IllegalArgumentException> {
            Geldspende(
                betrag = BigDecimal("100.00"),
                zuwendungsDatum = LocalDate.of(2026, 10, 28),
                zuwendender = spender,
                empfaenger = verein,
                istAufwandsspende = true,
                verzichtserklaerung = null // Fehlt!
            )
        }

        // Mit Verzichtserklärung ist sie zulässig
        val aufwandsspende = Geldspende(
            betrag = BigDecimal("100.00"),
            zuwendungsDatum = LocalDate.of(2026, 10, 28),
            zuwendender = spender,
            empfaenger = verein,
            istAufwandsspende = true,
            verzichtserklaerung = Gegenleistungsverzichtserklaerung(istGegenleistungsfrei = true)
        )
        assertTrue(aufwandsspende.istAufwandsspende)
    }

    @Test
    fun `erkennt Betraege ueber 300 Euro fuer formelle Bestaetigungspflicht`() {
        val spendeGros = Geldspende(
            betrag = BigDecimal("500.00"),
            zuwendungsDatum = LocalDate.of(2026, 10, 28),
            zuwendender = spender,
            empfaenger = verein
        )

        val bestaetigung = Zuwendungsbestaetigung(
            bestaetigungsNummer = "ZB-2026-0004",
            ausstellungsDatum = LocalDate.of(2026, 10, 28),
            ausstellungsOrt = "Münster",
            geldzuwendung = spendeGros,
            gemeinnuetzigkeitsbescheid = bescheidGueltig,
            gefoerderterZweck = zweckTierschutz
        )

        assertFalse(bestaetigung.vereinfachterNachweisMoeglich) // 500 € > 300 €
        assertEquals("fünfhundert Euro", bestaetigung.betragInWorten)
    }
}
