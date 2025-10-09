package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable

@Serializable
data class Dialog(
    val serviceResource: String,
    val party: String,
    val externalRefererence: String,
    val status: DialogStatus? = null,
    val content: Content,
    val transmissions: List<Transmission>,
    val isApiOnly: Boolean? = true,
)
