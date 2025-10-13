package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable

@Serializable
sealed class PatchOperation {
    abstract val op: String
    abstract val path: String
}

@Serializable
data class AddTransmissions(
    val value: List<Transmission>,
    override val op: String = "add",
    override val path: String = "/transmissions",
) : PatchOperation()

@Serializable
data class AddStatus(
    val value: DialogStatus,
    override val op: String = "add",
    override val path: String = "/status",
) : PatchOperation()

@Serializable
data class AddApiActions(
    val value: List<ApiAction>,
    override val op: String = "add",
    override val path: String = "/apiActions",
) : PatchOperation()
