package de.ad26.team07.adapter.out

import de.ad26.team07.application.port.out.ZuwendungsbestaetigungDokumentPort
import de.ad26.team07.domain.model.Feststellungsbescheid
import de.ad26.team07.domain.model.Freistellungsbescheid
import de.ad26.team07.domain.model.Geldspende
import de.ad26.team07.domain.model.Zuwendungsbestaetigung
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Secondary Outbound Adapter zur Generierung des amtlichen BMF-Musters 034122 (Fassung 2013)
 * als druckfertiges, rechtssicheres HTML/CSS DIN-A4-Dokument.
 */
class Muster034122HtmlAdapter : ZuwendungsbestaetigungDokumentPort {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val currencyFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.GERMAN))

    override fun generiereDokument(bestaetigung: Zuwendungsbestaetigung): String {
        val verein = bestaetigung.geldzuwendung.empfaenger
        val spender = bestaetigung.geldzuwendung.zuwendender
        val spende = bestaetigung.geldzuwendung as? Geldspende
        val bescheid = bestaetigung.gemeinnuetzigkeitsbescheid

        val betragFormatiert = currencyFormat.format(bestaetigung.geldzuwendung.betrag)
        val zuwendungsDatumFormatiert = bestaetigung.geldzuwendung.zuwendungsDatum.format(dateFormatter)
        val ausstellungsDatumFormatiert = bestaetigung.ausstellungsDatum.format(dateFormatter)

        val istAufwand = spende?.istAufwandsspende == true
        val aufwandJaChecked = if (istAufwand) "☑" else "☐"
        val aufwandNeinChecked = if (!istAufwand) "☑" else "☐"

        val bescheidText = when (bescheid) {
            is Freistellungsbescheid -> buildString {
                append("Wir sind wegen Förderung <strong>${escapeHtml(bestaetigung.gefoerderterZweck.bezeichnung)}</strong> ")
                append("nach dem Freistellungsbescheid bzw. nach der Anlage zum Körperschaftsteuerbescheid des Finanzamtes ")
                append("<strong>${escapeHtml(bescheid.finanzamt)}</strong>, StNr. <strong>${escapeHtml(bescheid.steuernummer)}</strong>, ")
                append("vom <strong>${bescheid.bescheidDatum.format(dateFormatter)}</strong> für den letzten Veranlagungszeitraum ")
                append("<strong>${bescheid.letzterVeranlagungszeitraum}</strong> nach § 5 Abs. 1 Nr. 9 des Körperschaftsteuergesetzes ")
                append("von der Körperschaftsteuer und nach § 3 Nr. 6 des Gewerbesteuergesetzes von der Gewerbesteuer befreit.")
            }
            is Feststellungsbescheid -> buildString {
                append("Die Einhaltung der satzungsmäßigen Voraussetzungen nach den §§ 51, 59, 60 und 61 AO wurde vom Finanzamt ")
                append("<strong>${escapeHtml(bescheid.finanzamt)}</strong>, StNr. <strong>${escapeHtml(bescheid.steuernummer)}</strong>, ")
                append("mit Bescheid vom <strong>${bescheid.bescheidDatum.format(dateFormatter)}</strong> nach § 60a Abs. 1 AO gesondert festgestellt. ")
                append("Wir fördern die Zwecke: <strong>${escapeHtml(bestaetigung.gefoerderterZweck.bezeichnung)}</strong>.")
            }
        }

        return """
<!DOCTYPE html>
<html lang="de">
<head>
  <meta charset="UTF-8">
  <title>Zuwendungsbestätigung ${escapeHtml(bestaetigung.bestaetigungsNummer)}</title>
  <style>
    @page {
      size: A4 portrait;
      margin: 15mm 20mm;
    }
    * {
      box-sizing: border-box;
      margin: 0;
      padding: 0;
      font-family: Arial, "Helvetica Neue", Helvetica, sans-serif;
      color: #111;
    }
    body {
      background: #f4f6f8;
      padding: 20px;
    }
    .document-page {
      background: #fff;
      width: 210mm;
      min-height: 297mm;
      padding: 16mm 20mm;
      margin: 0 auto;
      box-shadow: 0 4px 15px rgba(0,0,0,0.1);
      position: relative;
    }
    .header-table {
      width: 100%;
      border-collapse: collapse;
      margin-bottom: 12px;
    }
    .header-table td {
      vertical-align: top;
      width: 50%;
      font-size: 11pt;
      line-height: 1.4;
    }
    .box-border {
      border: 1px solid #333;
      padding: 10px 12px;
      min-height: 105px;
    }
    .box-title {
      font-size: 8.5pt;
      font-weight: bold;
      color: #555;
      text-transform: uppercase;
      margin-bottom: 4px;
    }
    .title-block {
      text-align: center;
      margin: 14px 0 12px 0;
      border-top: 2px solid #000;
      border-bottom: 2px solid #000;
      padding: 6px 0;
    }
    .title-main {
      font-size: 13pt;
      font-weight: bold;
      letter-spacing: 0.5px;
    }
    .title-sub {
      font-size: 8.5pt;
      margin-top: 3px;
      color: #333;
    }
    .data-table {
      width: 100%;
      border-collapse: collapse;
      margin: 10px 0;
      font-size: 10.5pt;
    }
    .data-table th, .data-table td {
      border: 1px solid #444;
      padding: 6px 8px;
      vertical-align: middle;
    }
    .data-table th {
      background: #f2f2f2;
      text-align: left;
      font-size: 9.5pt;
      width: 32%;
    }
    .amount-words {
      font-style: italic;
      font-weight: bold;
    }
    .legal-text {
      font-size: 9pt;
      line-height: 1.45;
      text-align: justify;
      margin: 12px 0;
    }
    .confirmation-section {
      border: 1px solid #444;
      padding: 10px 12px;
      margin: 12px 0;
      background: #fafafa;
      font-size: 9.5pt;
      line-height: 1.4;
    }
    .signature-grid {
      display: flex;
      justify-content: space-between;
      margin-top: 28px;
      font-size: 10pt;
    }
    .signature-box {
      width: 46%;
      border-top: 1px solid #000;
      padding-top: 6px;
      text-align: center;
    }
    .footer-note {
      position: absolute;
      bottom: 12mm;
      left: 20mm;
      right: 20mm;
      font-size: 7.5pt;
      line-height: 1.35;
      color: #555;
      border-top: 1px solid #ccc;
      padding-top: 6px;
    }
    .print-bar {
      width: 210mm;
      margin: 0 auto 16px auto;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .btn {
      padding: 10px 20px;
      background: #059669;
      color: #fff;
      border: none;
      border-radius: 6px;
      font-size: 14px;
      font-weight: bold;
      cursor: pointer;
    }
    .btn:hover { background: #047857; }
    @media print {
      body { background: #fff; padding: 0; }
      .document-page { box-shadow: none; margin: 0; width: 100%; min-height: auto; }
      .print-bar { display: none !important; }
    }
  </style>
</head>
<body>

  <div class="print-bar">
    <span style="font-size: 14px; color: #4b5563;">Amtliche Vorlage nach BMF Muster 034122 (Fassung 2013)</span>
    <button class="btn" onclick="window.print()">🖨️ Drucken / Als PDF speichern</button>
  </div>

  <div class="document-page">
    <table class="header-table">
      <tr>
        <td style="padding-right: 8px;">
          <div class="box-border">
            <div class="box-title">Aussteller (Steuerbegünstigte Körperschaft)</div>
            <strong>${escapeHtml(verein.name)}</strong><br>
            ${escapeHtml(verein.sitz.strasse)} ${escapeHtml(verein.sitz.hausnummer)}<br>
            ${escapeHtml(verein.sitz.plz)} ${escapeHtml(verein.sitz.ort)}<br>
            Finanzamt: ${escapeHtml(verein.finanzamt)} | StNr: ${escapeHtml(verein.steuernummer)}
          </div>
        </td>
        <td style="padding-left: 8px;">
          <div class="box-border">
            <div class="box-title">Zuwendender (Name & Anschrift)</div>
            <strong>${escapeHtml(spender.name)}</strong><br>
            ${escapeHtml(spender.anschrift.strasse)} ${escapeHtml(spender.anschrift.hausnummer)}<br>
            ${escapeHtml(spender.anschrift.plz)} ${escapeHtml(spender.anschrift.ort)}<br>
            ${escapeHtml(spender.anschrift.land)}
          </div>
        </td>
      </tr>
    </table>

    <div class="title-block">
      <div class="title-main">Bestätigung über Geldzuwendungen / Mitgliedsbeitrag</div>
      <div class="title-sub">im Sinne des § 10b des Einkommensteuergesetzes an eine der in § 5 Abs. 1 Nr. 9 des Körperschaftsteuergesetzes bezeichneten Körperschaften, Personenvereinigungen oder Vermögensmassen</div>
    </div>

    <table class="data-table">
      <tr>
        <th>Betrag in Ziffern</th>
        <td><strong style="font-size: 12pt;">${betragFormatiert} EUR</strong></td>
      </tr>
      <tr>
        <th>Betrag in Buchstaben</th>
        <td class="amount-words">${escapeHtml(bestaetigung.betragInWorten)}</td>
      </tr>
      <tr>
        <th>Tag der Zuwendung</th>
        <td>${zuwendungsDatumFormatiert}</td>
      </tr>
      <tr>
        <th>Verzicht auf Erstattung von Aufwendungen</th>
        <td>
          <span style="font-size: 11pt;">${aufwandJaChecked} Ja &nbsp;&nbsp;&nbsp;&nbsp; ${aufwandNeinChecked} Nein</span>
        </td>
      </tr>
    </table>

    <div class="legal-text">
      ${bescheidText}
    </div>

    <div class="confirmation-section">
      Es wird bestätigt, dass die Zuwendung nur zur Förderung <strong>${escapeHtml(bestaetigung.gefoerderterZweck.bezeichnung)}</strong> verwendet wird.
      ${if (bestaetigung.hinweisKeinAusschlussBeitrag) """
      <br><br>
      <strong>☑ Es wird bestätigt, dass es sich nicht um einen Mitgliedsbeitrag handelt, dessen Abzug nach § 10b Abs. 1 Satz 8 EStG ausgeschlossen ist.</strong>
      """ else ""}
    </div>

    <div style="margin-top: 14px; font-size: 9.5pt; color: #444;">
      Bestätigungsnummer: <strong>${escapeHtml(bestaetigung.bestaetigungsNummer)}</strong>
    </div>

    <div class="signature-grid">
      <div class="signature-box">
        ${escapeHtml(bestaetigung.ausstellungsOrt)}, den ${ausstellungsDatumFormatiert}<br>
        <span style="font-size: 8pt; color: #666;">(Ort, Datum der Ausstellung)</span>
      </div>
      <div class="signature-box">
        ${escapeHtml(bestaetigung.unterschrift)}<br>
        <span style="font-size: 8pt; color: #666;">(Unterschrift des Zuwendungsempfängers)</span>
      </div>
    </div>

    <div class="footer-note">
      <strong>Hinweis:</strong> Wer vorsätzlich oder grob fahrlässig eine unrichtige Zuwendungsbestätigung erstellt oder wer veranlasst, dass Zuwendungen nicht zu den in der Zuwendungsbestätigung angegebenen steuerbegünstigten Zwecken verwendet werden, haftet für die entgangene Steuer (§ 10b Abs. 4 EStG, § 9 Abs. 3 KStG, § 9 Nr. 5 GewStG).<br>
      Diese Bestätigung wird nicht als Nachweis für die steuerliche Berücksichtigung der Zuwendung anerkannt, wenn das Datum des Freistellungsbescheides länger als 5 Jahre bzw. das Datum der Feststellung der Einhaltung der satzungsmäßigen Voraussetzungen nach § 60a Abs. 1 AO länger als 3 Jahre seit Ausstellung der Bestätigung zurückliegt (BMF vom 15.12.1994 - BStBl I S. 884).<br>
      <span style="float: right; margin-top: 2px;">BMF Muster 034122 (Fassung 2013)</span>
    </div>
  </div>

</body>
</html>
""".trimIndent()
    }

    private fun escapeHtml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
    }
}
