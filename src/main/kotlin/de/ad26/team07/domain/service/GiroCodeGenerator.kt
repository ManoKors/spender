package de.ad26.team07.domain.service

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Domain Service zur Erzeugung von SEPA-EPC-GiroCode-Nutzdaten nach dem Standard EPC069-08.
 */
object GiroCodeGenerator {

    /**
     * Erzeugt den standardisierten EPC069-08 Payload für einen SEPA Credit Transfer QR-Code (GiroCode).
     */
    fun generierePayload(
        bic: String,
        empfaengerName: String,
        iban: String,
        betrag: BigDecimal,
        verwendungszweck: String
    ): String {
        require(bic.isNotBlank()) { "BIC darf nicht leer sein" }
        require(empfaengerName.isNotBlank()) { "Empfängername darf nicht leer sein" }
        require(iban.isNotBlank()) { "IBAN darf nicht leer sein" }
        require(betrag > BigDecimal.ZERO) { "Betrag muss positiv sein" }
        require(verwendungszweck.isNotBlank()) { "Verwendungszweck darf nicht leer sein" }

        val cleanIban = iban.replace(" ", "").uppercase()
        val cleanBic = bic.replace(" ", "").uppercase()
        val formattedBetrag = formatBetrag(betrag)
        val cleanName = empfaengerName.trim().take(70)
        val cleanZweck = verwendungszweck.trim().take(140)

        return buildString {
            append("BCD\n")
            append("002\n")
            append("1\n")
            append("SCT\n")
            append(cleanBic).append("\n")
            append(cleanName).append("\n")
            append(cleanIban).append("\n")
            append("EUR").append(formattedBetrag).append("\n")
            append("\n")
            append("\n")
            append(cleanZweck)
        }
    }

    private fun formatBetrag(betrag: BigDecimal): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        val df = DecimalFormat("0.00", symbols)
        return df.format(betrag.setScale(2, RoundingMode.HALF_UP))
    }
}
