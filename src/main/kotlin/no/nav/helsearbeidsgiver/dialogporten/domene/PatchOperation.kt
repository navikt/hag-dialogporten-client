package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable

@Serializable
sealed class PatchOperation(
    @Suppress("unused")
    val op: String,
    val path: String,
)

@Serializable
data class AddTransmissions(
    val value: List<Transmission>,
) : PatchOperation(
        op = "add",
        path = "/transmissions",
    )

@Serializable
data class AddStatus(
    val value: DialogStatus,
) : PatchOperation(
        op = "add",
        path = "/status",
    )

@Serializable
data class AddApiActions(
    val value: List<ApiAction>,
) : PatchOperation(
        op = "add",
        path = "/apiActions",
    )
