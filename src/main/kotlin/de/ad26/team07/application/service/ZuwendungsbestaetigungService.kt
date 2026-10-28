package de.ad26.team07.application.service

import de.ad26.team07.application.port.`in`.StelleZuwendungsbestaetigungAusCommand
import de.ad26.team07.application.port.`in`.StelleZuwendungsbestaetigungAusUseCase
import de.ad26.team07.application.port.out.SpendenabsichtRepositoryPort
import de.ad26.team07.application.port.out.ZuwendungsbestaetigungDokumentPort
import de.ad26.team07.domain.model.Geldspende
import de.ad26.team07.domain.model.SpendenabsichtStatus
import de.ad26.team07.domain.model.Zuwendungsbestaetigung

/**
 * Application Service zur Orchestrierung des Use Case UC-4: Zuwendungsbestätigung ausstellen.
 */
class ZuwendungsbestaetigungService(
    private val spendenabsichtRepository: SpendenabsichtRepositoryPort,
    private val dokumentPort: ZuwendungsbestaetigungDokumentPort? = null
) : StelleZuwendungsbestaetigungAusUseCase {

    override fun ausstellen(command: StelleZuwendungsbestaetigungAusCommand): Zuwendungsbestaetigung {
        val spendenabsicht = spendenabsichtRepository.findeNachReferenzCode(command.referenzCode)
            ?: throw IllegalArgumentException("Spendenabsicht mit Referenzcode '${command.referenzCode}' nicht gefunden.")

        // Falls noch nicht als eingegangen markiert, jetzt Statusuebergang vollziehen
        val realisierteAbsicht = if (spendenabsicht.status == SpendenabsichtStatus.ANGEKUENDIGT) {
            val eingegangen = spendenabsicht.markiereAlsEingegangen()
            spendenabsichtRepository.speichere(eingegangen)
            eingegangen
        } else if (spendenabsicht.status == SpendenabsichtStatus.EINGEGANGEN) {
            spendenabsicht
        } else {
            throw IllegalStateException("Für eine Spendenabsicht im Status ${spendenabsicht.status} kann keine Bestätigung ausgestellt werden.")
        }

        // Gültigen Bescheid für das Ausstellungsdatum finden (OCL 3)
        val bescheid = realisierteAbsicht.empfaenger.gemeinnuetzigkeitsbescheide
            .find { it.istGueltigAm(command.ausstellungsDatum) }
            ?: throw IllegalStateException(
                "Kein am Ausstellungsdatum (${command.ausstellungsDatum}) gültiger Gemeinnützigkeitsbescheid für den Verein '${realisierteAbsicht.empfaenger.name}' vorhanden."
            )

        // Eindeutige Bestätigungsnummer generieren (Format: ZB-YYYY-NNNN)
        val jahr = command.ausstellungsDatum.year
        val refPart = realisierteAbsicht.referenzCode.removePrefix("SP-$jahr-").removePrefix("SP-")
        val bestaetigungsNummer = "ZB-$jahr-$refPart"

        // Geldspende erzeugen
        val geldspende = Geldspende(
            betrag = realisierteAbsicht.betrag,
            zuwendungsDatum = command.ausstellungsDatum,
            zuwendender = realisierteAbsicht.spender,
            empfaenger = realisierteAbsicht.empfaenger,
            istAufwandsspende = false,
            spendenabsicht = realisierteAbsicht,
            verzichtserklaerung = realisierteAbsicht.verzichtserklaerung
        )

        // Zuwendungsbestätigung erstellen
        val bestaetigung = Zuwendungsbestaetigung(
            bestaetigungsNummer = bestaetigungsNummer,
            ausstellungsDatum = command.ausstellungsDatum,
            ausstellungsOrt = command.ausstellungsOrt,
            geldzuwendung = geldspende,
            gemeinnuetzigkeitsbescheid = bescheid,
            gefoerderterZweck = realisierteAbsicht.gewidmeterZweck,
            hinweisKeinAusschlussBeitrag = realisierteAbsicht.gewidmeterZweck.istAusschlussZweck,
            unterschrift = command.unterschrift
        )

        // Optional: Dokument generieren
        dokumentPort?.generiereDokument(bestaetigung)

        return bestaetigung
    }
}
