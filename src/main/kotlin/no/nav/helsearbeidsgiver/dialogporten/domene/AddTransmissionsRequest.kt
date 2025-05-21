package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable

@Serializable
data class AddTransmissionsRequest(
    val value: List<Transmission>,
) {
    @Suppress("unused")
    val op = "add"
    val path = "/transmissions"
}
