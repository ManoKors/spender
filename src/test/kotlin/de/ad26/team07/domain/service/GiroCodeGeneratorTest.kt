package de.ad26.team07.domain.service

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DisplayName("GiroCodeGenerator (EPC069-08 SEPA QR-Code)")
class GiroCodeGeneratorTest {

    @Test
    fun `generiert normgerechten EPC-Payload fuer gueltige SEPA-Ueberweisung`() {
        val payload = GiroCodeGenerator.generierePayload(
            bic = "GENODED1MUE",
            empfaengerName = "Foerderverein Sportfreunde e. V.",
            iban = "DE21 4006 0000 0123 4567 89",
            betrag = BigDecimal("150.00"),
            verwendungszweck = "SP-2026-4821"
        )

        val zeilen = payload.lines()
        assertEquals("BCD", zeilen[0], "Service Tag muss BCD sein")
        assertEquals("002", zeilen[1], "Version muss 002 sein")
        assertEquals("1", zeilen[2], "Zeichensatz muss 1 (UTF-8) sein")
        assertEquals("SCT", zeilen[3], "Identifikationscode muss SCT sein")
        assertEquals("GENODED1MUE", zeilen[4], "BIC muss uppercase ohne Leerzeichen sein")
        assertEquals("Foerderverein Sportfreunde e. V.", zeilen[5], "Empfaengername")
        assertEquals("DE21400600000123456789", zeilen[6], "IBAN muss bereinigt und uppercase sein")
        assertEquals("EUR150.00", zeilen[7], "Betragsformatierung mit zwei Dezimalstellen")
        assertEquals("", zeilen[8], "Zweckcode optional leer")
        assertEquals("", zeilen[9], "Strukturierte Referenz optional leer")
        assertEquals("SP-2026-4821", zeilen[10], "Unstrukturierter Verwendungszweck")
    }

    @Test
    fun `rundet Betraege kaufmaennisch auf zwei Nachkommastellen`() {
        val payload = GiroCodeGenerator.generierePayload(
            bic = "GENODED1MUE",
            empfaengerName = "Verein e. V.",
            iban = "DE21400600000123456789",
            betrag = BigDecimal("49.999"),
            verwendungszweck = "SP-2026-0001"
        )

        assertTrue(payload.contains("EUR50.00"))
    }

    @Test
    fun `verweigert Betrag kleiner gleich 0`() {
        assertThrows<IllegalArgumentException> {
            GiroCodeGenerator.generierePayload(
                bic = "GENODED1MUE",
                empfaengerName = "Verein e. V.",
                iban = "DE21400600000123456789",
                betrag = BigDecimal.ZERO,
                verwendungszweck = "SP-2026-0001"
            )
        }
    }

    @Test
    fun `verweigert leere Pflichtfelder`() {
        assertThrows<IllegalArgumentException> {
            GiroCodeGenerator.generierePayload(
                bic = "",
                empfaengerName = "Verein",
                iban = "DE21400600000123456789",
                betrag = BigDecimal("10.00"),
                verwendungszweck = "SP-2026-0001"
            )
        }
    }
}
