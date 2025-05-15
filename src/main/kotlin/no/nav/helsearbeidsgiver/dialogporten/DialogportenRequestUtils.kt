package no.nav.helsearbeidsgiver.dialogporten

import no.nav.helsearbeidsgiver.dialogporten.domene.AddTransmissionsRequest
import no.nav.helsearbeidsgiver.dialogporten.domene.Content
import no.nav.helsearbeidsgiver.dialogporten.domene.ContentValue
import no.nav.helsearbeidsgiver.dialogporten.domene.ContentValueItem
import no.nav.helsearbeidsgiver.dialogporten.domene.CreateDialogRequest
import no.nav.helsearbeidsgiver.dialogporten.domene.GuiAction
import no.nav.helsearbeidsgiver.dialogporten.domene.Transmission
import java.util.UUID

fun lagNyDialogMedSykmeldingRequest(
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
        guiActions = emptyList(),
        transmissions =
            listOf(
                lagVedleggTransmission(
                    transmissionTittel = "Sykmelding",
                    transmissionSammendrag = "Sykmelding",
                    vedleggNavn = "Sykmelding.json",
                    vedleggUrl = sykmeldingJsonUrl,
                    vedleggMediaType = "application/json",
                    vedleggConsumerType = Transmission.AttachmentUrlConsumerType.Api,
                ),
            ),
    )

fun oppdaterDialogMedSoknadRequest(soknadJsonUrl: String): AddTransmissionsRequest =
    AddTransmissionsRequest(
        transmissions =
            listOf(
                lagVedleggTransmission(
                    transmissionTittel = "Søknad om sykepenger",
                    transmissionSammendrag = "Søknad om sykepenger",
                    vedleggNavn = "soknad-om-sykepenger.json",
                    vedleggUrl = soknadJsonUrl,
                    vedleggMediaType = "application/json",
                    vedleggConsumerType = Transmission.AttachmentUrlConsumerType.Api,
                ),
            ),
    )

fun lagGuiAction(
    url: String,
    tittel: String,
) = GuiAction(
    action = "read",
    url = url,
    title =
        listOf(
            ContentValueItem(
                value = tittel,
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

fun lagCreateDialogRequest(
    ressurs: String,
    orgnr: String,
    status: String,
    tittel: String,
    sammendrag: String,
    url: String,
    knappTittel: String,
): CreateDialogRequest =
    CreateDialogRequest(
        serviceResource = "urn:altinn:resource:$ressurs",
        party = "urn:altinn:organization:identifier-no:$orgnr",
        status = status,
        externalRefererence = UUID.randomUUID().toString(),
        content = Content(lagContentValue(tittel), lagContentValue(sammendrag)),
        guiActions = listOf(lagGuiAction(url, knappTittel)),
        transmissions = emptyList(),
    )

private fun lagVedleggTransmission(
    transmissionTittel: String,
    transmissionSammendrag: String,
    vedleggNavn: String,
    vedleggUrl: String,
    vedleggMediaType: String,
    vedleggConsumerType: Transmission.AttachmentUrlConsumerType,
): Transmission =
    Transmission(
        type = Transmission.TransmissionType.Information,
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
