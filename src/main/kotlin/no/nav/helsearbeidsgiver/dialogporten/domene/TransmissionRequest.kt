package no.nav.helsearbeidsgiver.dialogporten.domene

import io.ktor.http.ContentType

sealed class TransmissionRequest {
    val vedleggMediaType = ContentType.Application.Json.toString()
    val vedleggConsumerType = Transmission.AttachmentUrlConsumerType.Api
    abstract val extendedType: String
    abstract val tittle: String
    abstract val summary: String?
    abstract val vedleggNavn: String
    abstract val vedleggUrl: String
    abstract val type: Transmission.TransmissionType
}

fun lagTransmissionMedVedlegg(transmissionRequest: TransmissionRequest): Transmission =
    Transmission(
        type = transmissionRequest.type,
        extendedType = transmissionRequest.extendedType,
        sender = Transmission.Sender("ServiceOwner"),
        content =
            Content.create(
                title = transmissionRequest.tittle,
                summary = transmissionRequest.summary,
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
