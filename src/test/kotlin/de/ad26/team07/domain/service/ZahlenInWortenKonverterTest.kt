package de.ad26.team07.domain.service

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals

@DisplayName("ZahlenInWortenKonverter (Betrag in Worten für Muster 034122)")
class ZahlenInWortenKonverterTest {

    @Test
    fun `konvertiert einfache Eurobetraege korrekt`() {
        assertEquals("ein Euro", ZahlenInWortenKonverter.inWorten(BigDecimal("1.00")))
        assertEquals("fünf Euro", ZahlenInWortenKonverter.inWorten(BigDecimal("5.00")))
        assertEquals("zwölf Euro", ZahlenInWortenKonverter.inWorten(BigDecimal("12.00")))
        assertEquals("fünfundsiebzig Euro", ZahlenInWortenKonverter.inWorten(BigDecimal("75.00")))
        assertEquals("einhundert Euro", ZahlenInWortenKonverter.inWorten(BigDecimal("100.00")))
        assertEquals("einhundertfünfzig Euro", ZahlenInWortenKonverter.inWorten(BigDecimal("150.00")))
        assertEquals("dreihundert Euro", ZahlenInWortenKonverter.inWorten(BigDecimal("300.00")))
        assertEquals("eintausendzweihundertvierunddreißig Euro", ZahlenInWortenKonverter.inWorten(BigDecimal("1234.00")))
    }

    @Test
    fun `konvertiert Betraege mit Cents korrekt`() {
        assertEquals("ein Euro und ein Cent", ZahlenInWortenKonverter.inWorten(BigDecimal("1.01")))
        assertEquals("fünfundsiebzig Euro und fünfzig Cent", ZahlenInWortenKonverter.inWorten(BigDecimal("75.50")))
        assertEquals("null Euro und fünfzig Cent", ZahlenInWortenKonverter.inWorten(BigDecimal("0.50")))
    }

    @Test
    fun `verweigert Betraege kleiner gleich 0`() {
        assertThrows<IllegalArgumentException> {
            ZahlenInWortenKonverter.inWorten(BigDecimal("0.00"))
        }
        assertThrows<IllegalArgumentException> {
            ZahlenInWortenKonverter.inWorten(BigDecimal("-10.00"))
        }
    }
}
