package no.nav.helsearbeidsgiver.dialogporten.domene

import no.nav.helsearbeidsgiver.dialogporten.domene.Transmission.Sender.ActorType
import java.util.UUID

abstract class TransmissionRequest {
    abstract val id: UUID
    abstract val extendedType: String
    abstract val dokumentId: UUID
    abstract val tittel: String
    abstract val sammendrag: String?
    abstract val contentReferenceFceUrl: String?
    abstract val type: Transmission.TransmissionType
    abstract val relatedTransmissionId: UUID?
    abstract val attachments: List<Attachment>
    open val isSilentUpdate: Boolean? = false

    init {
        id.requireGyldigUuidv7orNull()
    }
}

fun TransmissionRequest.toTransmission(): Transmission =
    Transmission(
        type = type,
        extendedType = extendedType,
        externalReference = dokumentId.toString(),
        sender = Transmission.Sender(ActorType.ServiceOwner),
        relatedTransmissionId = relatedTransmissionId,
        content =
            Content.create(
                title = tittel,
                summary = sammendrag,
                contentReferenceFceUrl = contentReferenceFceUrl,
            ),
        attachments = attachments,
        isSilentUpdate = isSilentUpdate,
        id = id,
    )
