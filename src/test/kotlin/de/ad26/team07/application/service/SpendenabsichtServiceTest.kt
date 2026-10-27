package de.ad26.team07.application.service

import de.ad26.team07.application.port.`in`.RegistriereSpendenabsichtCommand
import de.ad26.team07.application.port.out.SpendenabsichtRepositoryPort
import de.ad26.team07.application.port.out.VereinRepositoryPort
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

@DisplayName("SpendenabsichtService (Inbound Use Case)")
class SpendenabsichtServiceTest {

    private class InMemoryVereinRepository : VereinRepositoryPort {
        private val vereine = mutableMapOf<String, Zuwendungsempfaenger>()

        override fun findeNachSlug(slug: String): Zuwendungsempfaenger? = vereine[slug]

        override fun speichere(verein: Zuwendungsempfaenger) {
            vereine[verein.whiteLabelKonfiguration.slug] = verein
        }
    }

    private class InMemorySpendenabsichtRepository : SpendenabsichtRepositoryPort {
        val gespeicherteAbsichten = mutableListOf<Spendenabsicht>()

        override fun speichere(spendenabsicht: Spendenabsicht) {
            gespeicherteAbsichten.add(spendenabsicht)
        }

        override fun findeNachReferenzCode(referenzCode: String): Spendenabsicht? =
            gespeicherteAbsichten.find { it.referenzCode == referenzCode }

        override fun findeAlleFuerVerein(vereinSlug: String): List<Spendenabsicht> =
            gespeicherteAbsichten.filter { it.empfaenger.whiteLabelKonfiguration.slug == vereinSlug }
    }

    private lateinit var vereinRepository: InMemoryVereinRepository
    private lateinit var spendenabsichtRepository: InMemorySpendenabsichtRepository
    private lateinit var service: SpendenabsichtService

    private val testVerein = Zuwendungsempfaenger(
        name = "Katzenschutz Münster e. V.",
        steuernummer = "12/345/67890",
        finanzamt = "Münster",
        sitz = Adresse("Katzenallee", "9", "48149", "Münster"),
        iban = "DE21400600000123456789",
        bic = "GENODED1MUE",
        kontoinhaber = "Katzenschutz Münster e. V.",
        satzungszwecke = listOf(
            SteuerbeguenstigterZweck("§ 52 Abs. 2 Nr. 14 AO", "Tierschutz")
        ),
        gemeinnuetzigkeitsbescheide = listOf(
            Freistellungsbescheid("12/345/67890", "Münster", LocalDate.of(2023, 1, 1), 2022)
        ),
        whiteLabelKonfiguration = WhiteLabelKonfiguration(
            slug = "katzenschutz-muenster",
            wizardTitel = "Spenden für den Katzenschutz"
        )
    )

    @BeforeEach
    fun setUp() {
        vereinRepository = InMemoryVereinRepository()
        spendenabsichtRepository = InMemorySpendenabsichtRepository()
        vereinRepository.speichere(testVerein)

        service = SpendenabsichtService(vereinRepository, spendenabsichtRepository)
    }

    @Test
    fun `registriert Spendenabsicht erfolgreich und generiert Referenzcode samt GiroCode`() {
        val command = RegistriereSpendenabsichtCommand(
            vereinSlug = "katzenschutz-muenster",
            zweckParagraf = "§ 52 Abs. 2 Nr. 14 AO",
            betrag = BigDecimal("50.00"),
            spenderName = "Erika Mustermann",
            spenderAnschrift = Adresse("Musterstraße", "12", "48143", "Münster"),
            spenderEmail = "erika@example.de",
            gegenleistungsverzichtBestaetigt = true
        )

        val result = service.registriere(command)

        assertNotNull(result)
        assertEquals(SpendenabsichtStatus.ANGEKUENDIGT, result.status)
        assertEquals(BigDecimal("50.00"), result.betrag)
        assertTrue(result.referenzCode.startsWith("SP-"))
        assertTrue(result.giroCodePayload.contains("BCD"))
        assertTrue(result.giroCodePayload.contains("DE21400600000123456789"))
        assertTrue(result.giroCodePayload.contains("EUR50.00"))
        assertTrue(result.giroCodePayload.contains(result.referenzCode))

        assertEquals(1, spendenabsichtRepository.gespeicherteAbsichten.size)
    }

    @Test
    fun `wirft Fehler wenn Gegenleistungsverzicht nicht bestaetigt ist`() {
        val command = RegistriereSpendenabsichtCommand(
            vereinSlug = "katzenschutz-muenster",
            zweckParagraf = "§ 52 Abs. 2 Nr. 14 AO",
            betrag = BigDecimal("50.00"),
            spenderName = "Erika Mustermann",
            spenderAnschrift = Adresse("Musterstraße", "12", "48143", "Münster"),
            spenderEmail = "erika@example.de",
            gegenleistungsverzichtBestaetigt = false
        )

        val ex = assertThrows<IllegalArgumentException> {
            service.registriere(command)
        }
        assertTrue(ex.message!!.contains("Gegenleistungsfreiheit"))
    }

    @Test
    fun `wirft Fehler wenn Verein nicht existiert`() {
        val command = RegistriereSpendenabsichtCommand(
            vereinSlug = "unbekannter-verein",
            zweckParagraf = "§ 52 Abs. 2 Nr. 14 AO",
            betrag = BigDecimal("50.00"),
            spenderName = "Erika Mustermann",
            spenderAnschrift = Adresse("Musterstraße", "12", "48143", "Münster"),
            gegenleistungsverzichtBestaetigt = true
        )

        assertThrows<IllegalArgumentException> {
            service.registriere(command)
        }
    }

    @Test
    fun `wirft Fehler wenn gewaehlter Zweck nicht in Vereinssatzung ist`() {
        val command = RegistriereSpendenabsichtCommand(
            vereinSlug = "katzenschutz-muenster",
            zweckParagraf = "§ 52 Abs. 2 Nr. 99 AO", // Nicht in Satzung!
            betrag = BigDecimal("50.00"),
            spenderName = "Erika Mustermann",
            spenderAnschrift = Adresse("Musterstraße", "12", "48143", "Münster"),
            gegenleistungsverzichtBestaetigt = true
        )

        assertThrows<IllegalArgumentException> {
            service.registriere(command)
        }
    }
}
