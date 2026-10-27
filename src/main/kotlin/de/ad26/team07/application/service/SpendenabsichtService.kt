package de.ad26.team07.application.service

import de.ad26.team07.application.port.`in`.RegistriereSpendenabsichtCommand
import de.ad26.team07.application.port.`in`.RegistriereSpendenabsichtUseCase
import de.ad26.team07.application.port.out.SpendenabsichtRepositoryPort
import de.ad26.team07.application.port.out.VereinRepositoryPort
import de.ad26.team07.domain.model.Gegenleistungsverzichtserklaerung
import de.ad26.team07.domain.model.Spendenabsicht
import de.ad26.team07.domain.model.SpendenabsichtStatus
import de.ad26.team07.domain.model.Spender
import de.ad26.team07.domain.service.GiroCodeGenerator
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random

class SpendenabsichtService(
    private val vereinRepository: VereinRepositoryPort,
    private val spendenabsichtRepository: SpendenabsichtRepositoryPort
) : RegistriereSpendenabsichtUseCase {

    override fun registriere(command: RegistriereSpendenabsichtCommand): Spendenabsicht {
        require(command.gegenleistungsverzichtBestaetigt) {
            "Die Spendenabsicht kann nur registriert werden, wenn Gegenleistungsfreiheit bestätigt wurde."
        }

        val verein = vereinRepository.findeNachSlug(command.vereinSlug)
            ?: throw IllegalArgumentException("Verein mit Slug '${command.vereinSlug}' wurde nicht gefunden.")

        val zweck = verein.satzungszwecke.find { it.paragraf == command.zweckParagraf }
            ?: throw IllegalArgumentException(
                "Der Zweck '${command.zweckParagraf}' ist nicht in der Satzung des Vereins '${verein.name}' enthalten."
            )

        val referenzCode = generiereReferenzCode()

        val giroCodePayload = GiroCodeGenerator.generierePayload(
            bic = verein.bic,
            empfaengerName = verein.kontoinhaber,
            iban = verein.ibanBereinigt,
            betrag = command.betrag,
            verwendungszweck = referenzCode
        )

        val spender = Spender(
            name = command.spenderName,
            anschrift = command.spenderAnschrift,
            email = command.spenderEmail
        )

        val verzichtserklaerung = Gegenleistungsverzichtserklaerung(
            istGegenleistungsfrei = true
        )

        val absicht = Spendenabsicht(
            id = UUID.randomUUID().toString(),
            referenzCode = referenzCode,
            betrag = command.betrag,
            empfaenger = verein,
            spender = spender,
            gewidmeterZweck = zweck,
            verzichtserklaerung = verzichtserklaerung,
            giroCodePayload = giroCodePayload,
            erstellungsDatum = LocalDateTime.now(),
            status = SpendenabsichtStatus.ANGEKUENDIGT
        )

        spendenabsichtRepository.speichere(absicht)
        return absicht
    }

    private fun generiereReferenzCode(): String {
        val year = LocalDateTime.now().year
        val randomDigits = Random.nextInt(1000, 9999)
        return "SP-$year-$randomDigits"
    }
}
