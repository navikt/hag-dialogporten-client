@file:UseSerializers(UuidSerializer::class)

package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import no.nav.helsearbeidsgiver.utils.json.serializer.UuidSerializer
import java.util.UUID

@Serializable
data class Transmission(
    val id: UUID?=null,
    val type: TransmissionType,
    val extendedType: String,
    val sender: Sender,
    val content: Content,
    val attachments: List<Attachment>? = null,
) {
    @Serializable
    data class Sender(
        val actorType: String,
    )

    @Serializable
    enum class TransmissionType {
        // For general information, not related to any submissions
        Information,

        // Feedback/receipt accepting a previous submission
        Acceptance,

        // Feedback/error message rejecting a previous submission
        Rejection,

        // Question/request for more information
        Request,
    }

    @Serializable
    data class Attachment(
        val displayName: List<ContentValueItem>,
        val urls: List<Url>,
    )

    @Serializable
    data class Url(
        val url: String,
        val mediaType: String,
        val consumerType: AttachmentUrlConsumerType,
    )

    @Serializable
    enum class AttachmentUrlConsumerType {
        Gui,
        Api,
    }
}
