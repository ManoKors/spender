package de.ad26.team07.verein

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DisplayName("Gemeinnützigkeitsbescheid Gültigkeitsprüfung")
class GemeinnuetzigkeitsbescheidTest {

    @Nested
    @DisplayName("Freistellungsbescheid (§ 63 Abs. 5 AO - max. 5 Jahre)")
    inner class FreistellungsbescheidTests {

        private val bescheid = Freistellungsbescheid(
            steuernummer = "12/345/67890",
            finanzamt = "Münster-Innenstadt",
            bescheidDatum = LocalDate.of(2020, 3, 1),
            letzterVeranlagungszeitraum = 2019
        )

        @Test
        fun `ist am Bescheiddatum selbst gueltig`() {
            assertTrue(bescheid.istGueltigAm(LocalDate.of(2020, 3, 1)))
        }

        @Test
        fun `ist exakt 5 Jahre nach Ausstellung noch gueltig`() {
            assertTrue(bescheid.istGueltigAm(LocalDate.of(2025, 3, 1)))
        }

        @Test
        fun `ist nach Ablauf von 5 Jahren ungueltig`() {
            assertFalse(bescheid.istGueltigAm(LocalDate.of(2025, 3, 2)))
        }

        @Test
        fun `ist vor Bescheiddatum noch nicht gueltig`() {
            assertFalse(bescheid.istGueltigAm(LocalDate.of(2020, 2, 29)))
        }
    }

    @Nested
    @DisplayName("Feststellungsbescheid nach § 60a AO (max. 3 Jahre)")
    inner class FeststellungsbescheidTests {

        private val bescheid = Feststellungsbescheid(
            steuernummer = "12/345/67890",
            finanzamt = "Münster-Innenstadt",
            bescheidDatum = LocalDate.of(2022, 6, 15)
        )

        @Test
        fun `ist innerhalb von 3 Jahren gueltig`() {
            assertTrue(bescheid.istGueltigAm(LocalDate.of(2025, 6, 15)))
        }

        @Test
        fun `ist nach mehr als 3 Jahren ungueltig`() {
            assertFalse(bescheid.istGueltigAm(LocalDate.of(2025, 6, 16)))
        }

        @Test
        fun `ist vor Bescheiddatum noch nicht gueltig`() {
            assertFalse(bescheid.istGueltigAm(LocalDate.of(2022, 6, 14)))
        }
    }
}
