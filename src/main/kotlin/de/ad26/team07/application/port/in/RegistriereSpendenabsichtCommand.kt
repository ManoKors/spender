package de.ad26.team07.application.port.`in`

import de.ad26.team07.domain.model.Adresse
import java.math.BigDecimal

data class RegistriereSpendenabsichtCommand(
    val vereinSlug: String,
    val zweckParagraf: String,
    val betrag: BigDecimal,
    val spenderName: String,
    val spenderAnschrift: Adresse,
    val spenderEmail: String? = null,
    val gegenleistungsverzichtBestaetigt: Boolean
)
