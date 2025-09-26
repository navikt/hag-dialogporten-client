package no.nav.helsearbeidsgiver.dialogporten

import no.nav.helsearbeidsgiver.dialogporten.domene.AddApiActions
import no.nav.helsearbeidsgiver.dialogporten.domene.AddStatus
import no.nav.helsearbeidsgiver.dialogporten.domene.AddTransmissions
import no.nav.helsearbeidsgiver.dialogporten.domene.ApiAction
import no.nav.helsearbeidsgiver.dialogporten.domene.Content
import no.nav.helsearbeidsgiver.dialogporten.domene.ContentValue
import no.nav.helsearbeidsgiver.dialogporten.domene.ContentValueItem
import no.nav.helsearbeidsgiver.dialogporten.domene.CreateDialogRequest
import no.nav.helsearbeidsgiver.dialogporten.domene.DialogStatus
import no.nav.helsearbeidsgiver.dialogporten.domene.PatchOperation
import no.nav.helsearbeidsgiver.dialogporten.domene.Transmission
import java.util.UUID

fun opprettDialogMedSykmeldingRequest(
    ressurs: String,
    orgnr: String,
    dialogTittel: String,
    dialogSammendrag: String? = null,
    sykmeldingId: UUID,
    sykmeldingJsonUrl: String,
    kunForApi: Boolean = true,
): CreateDialogRequest =
    CreateDialogRequest(
        serviceResource = "urn:altinn:resource:$ressurs",
        party = "urn:altinn:organization:identifier-no:$orgnr",
        status = DialogStatus.New,
        externalRefererence = sykmeldingId.toString(),
        content = Content(lagContentValue(dialogTittel), dialogSammendrag?.let { lagContentValue(it) }),
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
        isApiOnly = kunForApi,
    )

fun oppdaterDialogMedSykepengesoeknadRequest(soeknadJsonUrl: String): List<PatchOperation> =
    listOf(
        AddTransmissions(
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

fun oppdaterDialogMedInntektsmeldingsforespoerselRequest(
    forespoerselUrl: String,
    forespoerselDokumentasjonUrl: String,
): List<PatchOperation> =
    listOf(
        AddStatus(
            value = DialogStatus.RequiresAttention,
        ),
        AddApiActions(
            value =
                listOf(
                    ApiAction(
                        name = "Hent forespørsel om inntektsmelding",
                        endpoints =
                            listOf(
                                ApiAction.Endpoint(
                                    url = forespoerselUrl,
                                    httpMethod = ApiAction.HttpMethod.GET,
                                    documentationUrl = forespoerselDokumentasjonUrl,
                                ),
                            ),
                    ),
                ),
        ),
        AddTransmissions(
            value =
                listOf(
                    Transmission(
                        type = Transmission.TransmissionType.Request,
                        extendedType = Transmission.ExtendedType.INNTEKTSMELDING,
                        sender = Transmission.Sender("ServiceOwner"),
                        content =
                            Content(
                                title = lagContentValue("Forespørsel om inntektsmelding"),
                                summary = lagContentValue("Forespørsel om inntektsmelding"),
                            ),
                        attachments = emptyList(),
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
