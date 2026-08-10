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
    val externalReference: String?,
    val sender: Sender,
    val content: Content,
    val relatedTransmissionId: UUID? = null,
    val attachments: List<Attachment>? = null,
    val isSilentUpdate: Boolean? = false,
    val id: UUID? = null,
) {
    init {
        id.requireGyldigUuidv7orNull()
    }

    @Serializable
    data class Sender(
        val actorType: ActorType,
    ) {
        @Serializable
        enum class ActorType {
            ServiceOwner,
        }
    }

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
}
