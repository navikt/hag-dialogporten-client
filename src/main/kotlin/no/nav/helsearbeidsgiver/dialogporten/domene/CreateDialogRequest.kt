package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable

@Serializable
data class CreateDialogRequest(
    val serviceResource: String,
    val party: String,
    val externalRefererence: String,
    val status: DialogStatus,
    val content: Content,
    val transmissions: List<Transmission>,
)
