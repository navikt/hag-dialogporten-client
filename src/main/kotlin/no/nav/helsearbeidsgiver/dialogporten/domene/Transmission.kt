@file:UseSerializers(UuidSerializer::class)

package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.UuidSerializer
import java.util.UUID

@Serializable
data class Transmission(
    val type: TransmissionType,
    val extendedType: String,
    val sender: Sender,
    val content: Content,
    val relatedTransmissionId: UUID? = null,
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

        // Notification of important events
        Alert,

        // Formal decision related to a previous submission
        Decision,

        // Submission of data, e.g., a form submission
        Submission,

        // Update to previously submitted data
        Correction,
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
