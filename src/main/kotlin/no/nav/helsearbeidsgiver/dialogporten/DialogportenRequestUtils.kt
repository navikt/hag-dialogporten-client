package no.nav.helsearbeidsgiver.dialogporten

import no.nav.helsearbeidsgiver.dialogporten.domene.AddTransmissionsRequest
import no.nav.helsearbeidsgiver.dialogporten.domene.Content
import no.nav.helsearbeidsgiver.dialogporten.domene.ContentValue
import no.nav.helsearbeidsgiver.dialogporten.domene.ContentValueItem
import no.nav.helsearbeidsgiver.dialogporten.domene.CreateDialogRequest
import no.nav.helsearbeidsgiver.dialogporten.domene.Transmission
import java.util.UUID

fun opprettDialogMedSykmeldingRequest(
    ressurs: String,
    orgnr: String,
    dialogTittel: String,
    dialogSammendrag: String,
    sykmeldingId: UUID,
    sykmeldingJsonUrl: String,
): CreateDialogRequest =
    CreateDialogRequest(
        serviceResource = "urn:altinn:resource:$ressurs",
        party = "urn:altinn:organization:identifier-no:$orgnr",
        status = "New",
        externalRefererence = sykmeldingId.toString(),
        content = Content(lagContentValue(dialogTittel), lagContentValue(dialogSammendrag)),
        transmissions =
            listOf(
                lagVedleggTransmission(
                    transmissionTittel = "Sykmelding",
                    transmissionSammendrag = "Sykmelding",
                    vedleggType = Transmission.ExtendedType.SYKMELDING,
                    vedleggNavn = "Sykmelding.json",
                    vedleggUrl = sykmeldingJsonUrl,
                    vedleggMediaType = "application/json",
                    vedleggConsumerType = Transmission.AttachmentUrlConsumerType.Api,
                ),
            ),
    )

fun oppdaterDialogMedSykepengesoeknadRequest(soeknadJsonUrl: String): List<AddTransmissionsRequest> =
    listOf(
        AddTransmissionsRequest(
            value =
                listOf(
                    lagVedleggTransmission(
                        transmissionTittel = "Søknad om sykepenger",
                        transmissionSammendrag = "Søknad om sykepenger",
                        vedleggType = Transmission.ExtendedType.SYKEPENGESOEKNAD,
                        vedleggNavn = "soeknad-om-sykepenger.json",
                        vedleggUrl = soeknadJsonUrl,
                        vedleggMediaType = "application/json",
                        vedleggConsumerType = Transmission.AttachmentUrlConsumerType.Api,
                    ),
                ),
        ),
    )

private fun lagContentValue(verdi: String) =
    ContentValue(
        value =
            listOf(
                ContentValueItem(
                    value = verdi,
                ),
            ),
    )

private fun lagVedleggTransmission(
    transmissionTittel: String,
    transmissionSammendrag: String,
    vedleggType: Transmission.ExtendedType,
    vedleggNavn: String,
    vedleggUrl: String,
    vedleggMediaType: String,
    vedleggConsumerType: Transmission.AttachmentUrlConsumerType,
): Transmission =
    Transmission(
        type = Transmission.TransmissionType.Information,
        extendedType = vedleggType,
        sender = Transmission.Sender("ServiceOwner"),
        content =
            Content(
                title = lagContentValue(transmissionTittel),
                summary = lagContentValue(transmissionSammendrag),
            ),
        attachments =
            listOf(
                Transmission.Attachment(
                    displayName = listOf(ContentValueItem(vedleggNavn)),
                    urls =
                        listOf(
                            Transmission.Url(
                                url = vedleggUrl,
                                mediaType = vedleggMediaType,
                                consumerType = vedleggConsumerType,
                            ),
                        ),
                ),
            ),
    )
