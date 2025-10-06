@file:UseSerializers(UuidSerializer::class)

package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.UuidSerializer
import java.util.UUID

@Serializable
data class Transmission(
    val type: TransmissionType,
    val relatedTransmissionId: UUID? = null,
    val extendedType: ExtendedType,
    val sender: Sender,
    val content: Content,
    val attachments: List<Attachment>,
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

        // Actual submission of information
        Submission,
    }

    /**
     *  Kan brukes av LPS-systemer til å gjenkjenne ulike typer vedlegg (f.eks. sykmelding, søknad om sykepenger eller forespørsel om inntektsmelding).
     */
    @Serializable
    enum class ExtendedType {
        SYKMELDING,
        SYKEPENGESOEKNAD,
        FORESPOERSEL,
        INNTEKTSMELDING,
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
