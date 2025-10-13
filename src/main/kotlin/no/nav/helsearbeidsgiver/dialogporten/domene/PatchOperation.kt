package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable

@Serializable
sealed class PatchOperation {
    abstract val op: String
    abstract val path: String
}

@Serializable
data class AddApiActions(
    val value: List<CreateApiActionRequest>,
    override val op: String = "add",
    override val path: String = "/apiActions",
) : PatchOperation()
