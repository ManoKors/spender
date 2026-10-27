package de.ad26.team07.domain.model

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals

@DisplayName("Spendenabsicht (Pledge Invarianten)")
class SpendenabsichtTest {

    private val adresse = Adresse("Spendenweg", "7", "48149", "Münster")

    private val verein = Zuwendungsempfaenger(
        name = "AD26 Förderverein e. V.",
        steuernummer = "12/345/67890",
        finanzamt = "Münster",
        sitz = adresse,
        iban = "DE21400600000123456789",
        bic = "GENODED1MUE",
        kontoinhaber = "AD26 Förderverein e. V.",
        satzungszwecke = listOf(SteuerbeguenstigterZweck("§ 52 Abs. 2 Nr. 1 AO", "Wissenschaft und Forschung")),
        gemeinnuetzigkeitsbescheide = listOf(
            Freistellungsbescheid("12/345/67890", "Münster", LocalDate.of(2024, 1, 1), 2023)
        ),
        whiteLabelKonfiguration = WhiteLabelKonfiguration(
            slug = "ad26-foerderverein",
            wizardTitel = "Spenden für Wissenschaft"
        )
    )

    private val spender = Spender("Max Mustermann", adresse, "max@example.com")
    private val zweck = SteuerbeguenstigterZweck("§ 52 Abs. 2 Nr. 1 AO", "Wissenschaft und Forschung")
    private val verzicht = Gegenleistungsverzichtserklaerung(istGegenleistungsfrei = true)

    @Test
    fun `erzeugt gueltige Spendenabsicht im Status ANGEKUENDIGT`() {
        val absicht = Spendenabsicht(
            id = "uuid-123",
            referenzCode = "SP-2026-4821",
            betrag = BigDecimal("75.00"),
            empfaenger = verein,
            spender = spender,
            gewidmeterZweck = zweck,
            verzichtserklaerung = verzicht,
            giroCodePayload = "BCD..."
        )

        assertEquals(SpendenabsichtStatus.ANGEKUENDIGT, absicht.status)
        assertEquals("SP-2026-4821", absicht.referenzCode)
        assertEquals(BigDecimal("75.00"), absicht.betrag)
    }

    @Test
    fun `verweigert Betrag kleiner gleich 0`() {
        assertThrows<IllegalArgumentException> {
            Spendenabsicht(
                id = "uuid-123",
                referenzCode = "SP-2026-4821",
                betrag = BigDecimal.ZERO,
                empfaenger = verein,
                spender = spender,
                gewidmeterZweck = zweck,
                verzichtserklaerung = verzicht,
                giroCodePayload = "BCD..."
            )
        }
    }

    @Test
    fun `erlaubt Statusuebergang von ANGEKUENDIGT auf EINGEGANGEN`() {
        val absicht = Spendenabsicht(
            id = "uuid-123",
            referenzCode = "SP-2026-4821",
            betrag = BigDecimal("75.00"),
            empfaenger = verein,
            spender = spender,
            gewidmeterZweck = zweck,
            verzichtserklaerung = verzicht,
            giroCodePayload = "BCD..."
        )

        val aktualisiert = absicht.markiereAlsEingegangen()
        assertEquals(SpendenabsichtStatus.EINGEGANGEN, aktualisiert.status)
    }

    @Test
    fun `verhindert erneuten Statusuebergang wenn bereits EINGEGANGEN`() {
        val absicht = Spendenabsicht(
            id = "uuid-123",
            referenzCode = "SP-2026-4821",
            betrag = BigDecimal("75.00"),
            empfaenger = verein,
            spender = spender,
            gewidmeterZweck = zweck,
            verzichtserklaerung = verzicht,
            giroCodePayload = "BCD...",
            status = SpendenabsichtStatus.EINGEGANGEN
        )

        assertThrows<IllegalStateException> {
            absicht.markiereAlsEingegangen()
        }
    }
}
