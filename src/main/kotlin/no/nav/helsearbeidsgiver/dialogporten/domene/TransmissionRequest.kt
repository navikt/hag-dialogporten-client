package no.nav.helsearbeidsgiver.dialogporten.domene

import io.ktor.http.ContentType
import java.util.UUID

abstract class TransmissionRequest {
    val vedleggMediaType = ContentType.Application.Json.toString()
    abstract val extendedType: String
    abstract val dokumentId: UUID
    abstract val tittel: String
    abstract val sammendrag: String?
    abstract val vedleggNavn: String
    abstract val vedleggApiBaseUrl: String
    abstract val vadleggGuiBaseUrl: String
    abstract val type: Transmission.TransmissionType
    abstract val relatedTransmissionId: UUID?
}

fun lagTransmissionMedVedlegg(transmissionRequest: TransmissionRequest): Transmission =
    Transmission(
        type = transmissionRequest.type,
        extendedType = transmissionRequest.extendedType,
        externalReference = transmissionRequest.dokumentId.toString(),
        sender = Transmission.Sender("ServiceOwner"),
        relatedTransmissionId = transmissionRequest.relatedTransmissionId,
        content =
            Content.create(
                title = transmissionRequest.tittel,
                summary = transmissionRequest.sammendrag,
            ),
        attachments =
            listOf(
                Attachment(
                    displayName = listOf(ContentValueItem(transmissionRequest.vedleggNavn)),
                    urls =
                        listOf(
                            Attachment.Url(
                                url = "${transmissionRequest.vedleggApiBaseUrl}/${transmissionRequest.dokumentId}",
                                mediaType = transmissionRequest.vedleggMediaType,
                                consumerType = Attachment.Url.AttachmentUrlConsumerType.Api,
                            ),
                        ),
                ),
                Attachment(
                    displayName = listOf(ContentValueItem(transmissionRequest.vedleggNavn)),
                    urls =
                        listOf(
                            Attachment.Url(
                                url = transmissionRequest.vadleggGuiBaseUrl,
                                mediaType = transmissionRequest.vedleggMediaType,
                                consumerType = Attachment.Url.AttachmentUrlConsumerType.Gui,
                            ),
                        ),
                ),
            ),
    )

fun createTransmission(transmissionRequest: TransmissionRequest): Transmission =
    Transmission(
        type = transmissionRequest.type,
        extendedType = transmissionRequest.extendedType,
        externalReference = transmissionRequest.dokumentId.toString(),
        sender = Transmission.Sender("ServiceOwner"),
        relatedTransmissionId = transmissionRequest.relatedTransmissionId,
        content =
            Content.create(
                title = transmissionRequest.tittel,
                summary = transmissionRequest.sammendrag,
            ),
    )

fun createAttachment(
    displayName: String,
    url: String,
    mediaType: String,
    consumerType: Attachment.Url.AttachmentUrlConsumerType,
): Attachment =
    Attachment(
        displayName = listOf(ContentValueItem(displayName)),
        urls =
            listOf(
                Attachment.Url(
                    url = url,
                    mediaType = mediaType,
                    consumerType = consumerType,
                ),
            ),
    )
