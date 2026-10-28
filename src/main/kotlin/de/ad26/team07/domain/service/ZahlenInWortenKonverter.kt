package de.ad26.team07.domain.service

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Domain Service zur Konvertierung von numerischen Beträgen in ausgeschriebene deutsche Worte
 * gemäß den Vorgaben des BMF-Musters 034122 (Spendenquittung: "Betrag in Buchstaben").
 */
object ZahlenInWortenKonverter {

    private val EINER = arrayOf(
        "", "ein", "zwei", "drei", "vier", "fünf", "sechs", "sieben", "acht", "neun",
        "zehn", "elf", "zwölf", "dreizehn", "vierzehn", "fünfzehn", "sechzehn", "siebzehn", "achtzehn", "neunzehn"
    )

    private val ZEHMER = arrayOf(
        "", "", "zwanzig", "dreißig", "vierzig", "fünfzig", "sechzig", "siebzig", "achtzig", "neunzig"
    )

    /**
     * Konvertiert einen Geldbetrag in Euro in ausgeschriebenen Text (z. B. "fünfundsiebzig Euro").
     */
    fun inWorten(betrag: BigDecimal): String {
        require(betrag > BigDecimal.ZERO) { "Der Betrag muss größer als 0 sein." }

        val gerundet = betrag.setScale(2, RoundingMode.HALF_UP)
        val euroTeil = gerundet.toBigInteger().toLong()
        val centTeil = gerundet.remainder(BigDecimal.ONE).multiply(BigDecimal(100)).toInt()

        val euroText = if (euroTeil == 0L) {
            "null Euro"
        } else if (euroTeil == 1L) {
            "ein Euro"
        } else {
            "${zahlBisMilliarden(euroTeil)} Euro"
        }

        return if (centTeil > 0) {
            val centText = if (centTeil == 1) "ein Cent" else "${zahlBisHundert(centTeil)} Cent"
            "$euroText und $centText"
        } else {
            euroText
        }
    }

    private fun zahlBisMilliarden(n: Long): String {
        if (n == 0L) return ""
        val sb = StringBuilder()

        val millionen = n / 1_000_000
        val restMillionen = n % 1_000_000

        if (millionen > 0) {
            if (millionen == 1L) {
                sb.append("eine Million ")
            } else {
                sb.append(zahlBisTausend(millionen.toInt())).append(" Millionen ")
            }
        }

        val tausend = restMillionen / 1_000
        val restTausend = (restMillionen % 1_000).toInt()

        if (tausend > 0) {
            if (tausend == 1L) {
                sb.append("eintausend")
            } else {
                sb.append(zahlBisTausend(tausend.toInt())).append("tausend")
            }
        }

        if (restTausend > 0) {
            sb.append(zahlBisTausend(restTausend))
        }

        return sb.toString().trim()
    }

    private fun zahlBisTausend(n: Int): String {
        val sb = StringBuilder()
        val hunderter = n / 100
        val rest = n % 100

        if (hunderter > 0) {
            if (hunderter == 1) {
                sb.append("einhundert")
            } else {
                sb.append(EINER[hunderter]).append("hundert")
            }
        }

        if (rest > 0) {
            sb.append(zahlBisHundert(rest))
        }

        return sb.toString()
    }

    private fun zahlBisHundert(n: Int): String {
        if (n < 20) {
            return if (n == 1) "eins" else EINER[n]
        }
        val zehner = n / 10
        val einer = n % 10

        return if (einer == 0) {
            ZEHMER[zehner]
        } else {
            val einerText = if (einer == 1) "ein" else EINER[einer]
            "${einerText}und${ZEHMER[zehner]}"
        }
    }
}
