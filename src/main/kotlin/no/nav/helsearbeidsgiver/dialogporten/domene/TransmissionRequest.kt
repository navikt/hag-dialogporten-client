package no.nav.helsearbeidsgiver.dialogporten.domene

import io.ktor.http.ContentType
import java.util.UUID

abstract class TransmissionRequest {
    val vedleggMediaType = ContentType.Application.Json.toString()
    val vedleggConsumerType = Transmission.AttachmentUrlConsumerType.Api
    abstract val extendedType: String
    abstract val externalReference: String?
    abstract val tittel: String
    abstract val sammendrag: String?
    abstract val vedleggNavn: String
    abstract val vedleggUrl: String
    abstract val type: Transmission.TransmissionType
    abstract val relatedTransmissionId: UUID?
}

fun lagTransmissionMedVedlegg(transmissionRequest: TransmissionRequest): Transmission =
    Transmission(
        type = transmissionRequest.type,
        extendedType = transmissionRequest.extendedType,
        externalReference = transmissionRequest.externalReference,
        sender = Transmission.Sender("ServiceOwner"),
        relatedTransmissionId = transmissionRequest.relatedTransmissionId,
        content =
            Content.create(
                title = transmissionRequest.tittel,
                summary = transmissionRequest.sammendrag,
            ),
        attachments =
            listOf(
                Transmission.Attachment(
                    displayName = listOf(ContentValueItem(transmissionRequest.vedleggNavn)),
                    urls =
                        listOf(
                            Transmission.Url(
                                url = transmissionRequest.vedleggUrl,
                                mediaType = transmissionRequest.vedleggMediaType,
                                consumerType = transmissionRequest.vedleggConsumerType,
                            ),
                        ),
                ),
            ),
    )
