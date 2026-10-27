package de.ad26.team07.application.port.`in`

import de.ad26.team07.domain.model.Spendenabsicht

interface RegistriereSpendenabsichtUseCase {
    fun registriere(command: RegistriereSpendenabsichtCommand): Spendenabsicht
}
