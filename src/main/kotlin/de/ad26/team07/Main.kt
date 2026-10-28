package de.ad26.team07

import de.ad26.team07.application.port.`in`.RegistriereSpendenabsichtCommand
import de.ad26.team07.application.port.out.SpendenabsichtRepositoryPort
import de.ad26.team07.application.port.out.VereinRepositoryPort
import de.ad26.team07.application.service.SpendenabsichtService
import de.ad26.team07.domain.model.*
import java.math.BigDecimal
import java.time.LocalDate

class InMemoryVereinRepository : VereinRepositoryPort {
    private val vereine = mutableMapOf<String, Zuwendungsempfaenger>()
    override fun findeNachSlug(slug: String): Zuwendungsempfaenger? = vereine[slug]
    override fun speichere(verein: Zuwendungsempfaenger) {
        vereine[verein.whiteLabelKonfiguration.slug] = verein
    }
}

class InMemorySpendenabsichtRepository : SpendenabsichtRepositoryPort {
    private val absichten = mutableListOf<Spendenabsicht>()
    override fun speichere(spendenabsicht: Spendenabsicht) {
        absichten.add(spendenabsicht)
    }
    override fun findeNachReferenzCode(referenzCode: String): Spendenabsicht? =
        absichten.find { it.referenzCode == referenzCode }
    override fun findeAlleFuerVerein(vereinSlug: String): List<Spendenabsicht> =
        absichten.filter { it.empfaenger.whiteLabelKonfiguration.slug == vereinSlug }
}

fun main() {
    println("================================================================================")
    println("      AD26 Team 07: White-Label Spenden-Wizard & Compliance Engine Demo         ")
    println("================================================================================\n")

    // 1. Repositories initialisieren
    val vereinRepo = InMemoryVereinRepository()
    val spendenabsichtRepo = InMemorySpendenabsichtRepository()
    val wizardService = SpendenabsichtService(vereinRepo, spendenabsichtRepo)

    // 2. Sample-Vereine anlegen (White-Label Mandanten)
    val vereinTierschutz = Zuwendungsempfaenger(
        name = "Katzenschutz Münster e. V.",
        steuernummer = "12/345/67890",
        finanzamt = "Münster-Innenstadt",
        sitz = Adresse("Katzenallee", "9", "48149", "Münster"),
        iban = "DE21 4006 0000 0123 4567 89",
        bic = "GENODED1MUE",
        kontoinhaber = "Katzenschutz Münster e. V.",
        satzungszwecke = listOf(
            SteuerbeguenstigterZweck("§ 52 Abs. 2 Nr. 14 AO", "Förderung des Tierschutzes")
        ),
        gemeinnuetzigkeitsbescheide = listOf(
            Freistellungsbescheid(
                steuernummer = "12/345/67890",
                finanzamt = "Münster-Innenstadt",
                bescheidDatum = LocalDate.of(2023, 5, 10),
                letzterVeranlagungszeitraum = 2022
            )
        ),
        whiteLabelKonfiguration = WhiteLabelKonfiguration(
            slug = "katzenschutz-muenster",
            wizardTitel = "Spenden für den Katzenschutz Münster",
            primaerFarbe = "#059669",
            dankesNachricht = "Herzlichen Dank! Jeder Euro hilft unseren Schützlingen."
        )
    )
    vereinRepo.speichere(vereinTierschutz)

    println("✓ Verein angelegt: '${vereinTierschutz.name}'")
    println("  - White-Label URL: /spenden/${vereinTierschutz.whiteLabelKonfiguration.slug}")
    println("  - Primärfarbe: ${vereinTierschutz.whiteLabelKonfiguration.primaerFarbe}")
    println("  - IBAN: ${vereinTierschutz.ibanBereinigt} (BIC: ${vereinTierschutz.bic})\n")

    // 3. Spender durchläuft den White-Label Wizard
    println("--- SCHRITT 1: Spender füllt Online-Wizard aus ---")
    val command = RegistriereSpendenabsichtCommand(
        vereinSlug = "katzenschutz-muenster",
        zweckParagraf = "§ 52 Abs. 2 Nr. 14 AO",
        betrag = BigDecimal("75.00"),
        spenderName = "Mano Korsanke",
        spenderAnschrift = Adresse("Universitätsstraße", "14", "48149", "Münster"),
        spenderEmail = "spende@example.org",
        gegenleistungsverzichtBestaetigt = true
    )
    println("  Spender: ${command.spenderName}")
    println("  Anschrift: ${command.spenderAnschrift.strasse} ${command.spenderAnschrift.hausnummer}, ${command.spenderAnschrift.plz} ${command.spenderAnschrift.ort}")
    println("  Betrag: ${command.betrag} EUR")
    println("  Zweck: ${command.zweckParagraf}")
    println("  Gegenleistungsverzicht: ${if (command.gegenleistungsverzichtBestaetigt) '✓' else '✗'} Bestätigt\n")

    // 4. Use Case ausführen
    val absicht = wizardService.registriere(command)
    println("--- SCHRITT 2: System generiert Spendenabsicht & EPC-GiroCode ---")
    println("  Status: ${absicht.status}")
    println("  Generierter Verwendungszweck (Referenz): >>> ${absicht.referenzCode} <<<")
    println("\n  [EPC069-08 SEPA-GiroCode Nutzdaten für Banking-App]:")
    println("  " + "-".repeat(50))
    absicht.giroCodePayload.lines().forEach { println("  | $it") }
    println("  " + "-".repeat(50))

    // 5. Simulation Geldeingang & Compliance-Prüfung
    println("\n--- SCHRITT 3: Geldeingang auf Vereinskonto & Belegprüfung ---")
    println("  Kassenwart sieht Überweisung mit Betrag ${absicht.betrag} EUR und Referenz '${absicht.referenzCode}'.")

    val htmlAdapter = de.ad26.team07.adapter.out.Muster034122HtmlAdapter()
    val bestaetigungService = de.ad26.team07.application.service.ZuwendungsbestaetigungService(
        spendenabsichtRepository = spendenabsichtRepo,
        dokumentPort = htmlAdapter
    )

    // 6. Amtliche Zuwendungsbestätigung ausstellen (UC-4)
    println("\n--- SCHRITT 4: Amtliche Zuwendungsbestätigung ausstellen (UC-4 / Muster 034122) ---")
    val ausstellCommand = de.ad26.team07.application.port.`in`.StelleZuwendungsbestaetigungAusCommand(
        referenzCode = absicht.referenzCode,
        ausstellungsOrt = "Münster",
        ausstellungsDatum = LocalDate.now(),
        unterschrift = "Mano Korsanke (Kassenwart)"
    )

    val bestaetigung = bestaetigungService.ausstellen(ausstellCommand)
    val htmlFormular = htmlAdapter.generiereDokument(bestaetigung)

    // Speichern als druckfertige HTML-Datei im build-Verzeichnis
    val outputDir = java.io.File("build")
    if (!outputDir.exists()) outputDir.mkdirs()
    val outputFile = java.io.File(outputDir, "zuwendungsbestaetigung_${bestaetigung.bestaetigungsNummer}.html")
    outputFile.writeText(htmlFormular)

    println("✓ Bestätigung formell erteilt: '${bestaetigung.bestaetigungsNummer}'")
    println("  - Aussteller: ${bestaetigung.geldzuwendung.empfaenger.name} (StNr: ${bestaetigung.gemeinnuetzigkeitsbescheid.steuernummer})")
    println("  - Zuwendender: ${bestaetigung.geldzuwendung.zuwendender.name}")
    println("  - Betrag in Ziffern: ${bestaetigung.geldzuwendung.betrag} EUR")
    println("  - Betrag in Worten: >>> ${bestaetigung.betragInWorten} <<<")
    println("  - Steuerbegünstigter Zweck: ${bestaetigung.gefoerderterZweck.bezeichnung} (${bestaetigung.gefoerderterZweck.paragraf})")
    println("  - Rechtsgrundlage: Freistellungsbescheid vom ${bestaetigung.gemeinnuetzigkeitsbescheid.bescheidDatum} (Finanzamt ${bestaetigung.gemeinnuetzigkeitsbescheid.finanzamt})")
    println("  - Vereinfachter Nachweis (<= 300 €): ${if (bestaetigung.vereinfachterNachweisMoeglich) "Ja (Kontoauszug genügt, Quittung dennoch erstellt)" else "Nein"}")
    println("\n================================================================================")
    println("  Gesetzlich rechtssicheres Formular nach Muster 034122 generiert:")
    println("  📁 Datei: ${outputFile.absolutePath}")
    println("================================================================================\n")
}
