package de.ad26.team07.application.service

import de.ad26.team07.application.port.`in`.StelleZuwendungsbestaetigungAusCommand
import de.ad26.team07.application.port.out.SpendenabsichtRepositoryPort
import de.ad26.team07.application.port.out.ZuwendungsbestaetigungDokumentPort
import de.ad26.team07.domain.model.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("ZuwendungsbestaetigungService (Application Service UC-4)")
class ZuwendungsbestaetigungServiceTest {

    private class MockSpendenabsichtRepository : SpendenabsichtRepositoryPort {
        val list = mutableListOf<Spendenabsicht>()
        override fun speichere(spendenabsicht: Spendenabsicht) {
            list.removeIf { it.referenzCode == spendenabsicht.referenzCode }
            list.add(spendenabsicht)
        }
        override fun findeNachReferenzCode(referenzCode: String): Spendenabsicht? =
            list.find { it.referenzCode == referenzCode }
        override fun findeAlleFuerVerein(vereinSlug: String): List<Spendenabsicht> =
            list.filter { it.empfaenger.whiteLabelKonfiguration.slug == vereinSlug }
    }

    private class MockDokumentPort : ZuwendungsbestaetigungDokumentPort {
        var generiertesDokument: String? = null
        var aufgerufenMit: Zuwendungsbestaetigung? = null

        override fun generiereDokument(bestaetigung: Zuwendungsbestaetigung): String {
            aufgerufenMit = bestaetigung
            generiertesDokument = "<html>Muster 034122: ${bestaetigung.bestaetigungsNummer}</html>"
            return generiertesDokument!!
        }
    }

    private lateinit var repo: MockSpendenabsichtRepository
    private lateinit var dokumentPort: MockDokumentPort
    private lateinit var service: ZuwendungsbestaetigungService

    private val bescheid = Freistellungsbescheid(
        steuernummer = "12/345/67890",
        finanzamt = "Münster",
        bescheidDatum = LocalDate.of(2023, 1, 1),
        letzterVeranlagungszeitraum = 2022
    )

    private val zweck = SteuerbeguenstigterZweck("§ 52 Abs. 2 Nr. 14 AO", "Tierschutz")

    private val verein = Zuwendungsempfaenger(
        name = "Katzenschutz Münster e. V.",
        steuernummer = "12/345/67890",
        finanzamt = "Münster",
        sitz = Adresse("Katzenallee", "9", "48149", "Münster"),
        iban = "DE21400600000123456789",
        bic = "GENODED1MUE",
        kontoinhaber = "Katzenschutz Münster e. V.",
        satzungszwecke = listOf(zweck),
        gemeinnuetzigkeitsbescheide = listOf(bescheid),
        whiteLabelKonfiguration = WhiteLabelKonfiguration("katzenschutz-ms", "Katzenschutz")
    )

    private val spender = Spender(
        name = "Mano Korsanke",
        anschrift = Adresse("Musterstraße", "12", "48143", "Münster")
    )

    private val absicht = Spendenabsicht(
        id = "id-123",
        referenzCode = "SP-2026-9999",
        betrag = BigDecimal("75.00"),
        empfaenger = verein,
        spender = spender,
        gewidmeterZweck = zweck,
        verzichtserklaerung = Gegenleistungsverzichtserklaerung(istGegenleistungsfrei = true),
        giroCodePayload = "BCD..."
    )

    @BeforeEach
    fun setUp() {
        repo = MockSpendenabsichtRepository()
        dokumentPort = MockDokumentPort()
        repo.speichere(absicht)

        service = ZuwendungsbestaetigungService(repo, dokumentPort)
    }

    @Test
    fun `stellt Zuwendungsbestaetigung erfolgreich aus und ruft Dokumenten-Port auf`() {
        val command = StelleZuwendungsbestaetigungAusCommand(
            referenzCode = "SP-2026-9999",
            ausstellungsOrt = "Münster",
            ausstellungsDatum = LocalDate.of(2026, 10, 28),
            unterschrift = "Kassenwart Mustermann"
        )

        val bestaetigung = service.ausstellen(command)

        assertNotNull(bestaetigung)
        assertEquals("ZB-2026-9999", bestaetigung.bestaetigungsNummer)
        assertEquals("fünfundsiebzig Euro", bestaetigung.betragInWorten)
        assertEquals(SpendenabsichtStatus.EINGEGANGEN, repo.findeNachReferenzCode("SP-2026-9999")!!.status)

        assertNotNull(dokumentPort.aufgerufenMit)
        assertEquals(bestaetigung.bestaetigungsNummer, dokumentPort.aufgerufenMit!!.bestaetigungsNummer)
    }

    @Test
    fun `wirft Fehler wenn Referenzcode unbekannt ist`() {
        val command = StelleZuwendungsbestaetigungAusCommand(
            referenzCode = "UNBEKANNT",
            ausstellungsOrt = "Münster"
        )

        assertThrows<IllegalArgumentException> {
            service.ausstellen(command)
        }
    }
}
