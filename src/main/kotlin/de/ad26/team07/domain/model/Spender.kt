package de.ad26.team07.domain.model

data class Spender(
    val name: String,
    val anschrift: Adresse,
    val email: String? = null,
    val spenderArt: String = "natürliche Person"
) {
    init {
        require(name.isNotBlank()) { "Spendername darf nicht leer sein" }
    }
}
