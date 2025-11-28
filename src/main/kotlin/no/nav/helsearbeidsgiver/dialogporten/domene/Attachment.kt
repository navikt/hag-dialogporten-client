package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val displayName: List<ContentValueItem>,
    val urls: List<Url>,
) {
    @Serializable
    data class Url(
        val url: String,
        val mediaType: String,
        val consumerType: AttachmentUrlConsumerType,
    ) {
        @Serializable
        enum class AttachmentUrlConsumerType {
            Gui,
            Api,
        }
    }
}
