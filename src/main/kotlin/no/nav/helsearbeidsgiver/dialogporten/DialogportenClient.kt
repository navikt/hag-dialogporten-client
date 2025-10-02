package no.nav.helsearbeidsgiver.dialogporten

import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import no.nav.helsearbeidsgiver.utils.log.logger
import no.nav.helsearbeidsgiver.utils.log.sikkerLogger
import java.util.UUID

class DialogportenClient(
    private val baseUrl: String,
    private val ressurs: String,
    getToken: () -> String,
) {
    private val httpClient = createHttpClient(1, getToken)

    private val logger = this.logger()
    private val sikkerLogger = sikkerLogger()

    suspend fun opprettDialogMedSykmelding(
        orgnr: String,
        dialogTittel: String,
        dialogSammendrag: String? = null,
        sykmeldingId: UUID,
        sykmeldingJsonUrl: String,
        kunForApi: Boolean = true,
    ): String {
        val dialogRequest =
            opprettDialogMedSykmeldingRequest(
                ressurs = ressurs,
                orgnr = orgnr,
                dialogTittel = dialogTittel,
                dialogSammendrag = dialogSammendrag,
                sykmeldingId = sykmeldingId,
                sykmeldingJsonUrl = sykmeldingJsonUrl,
                kunForApi = kunForApi,
            )
        return runCatching<DialogportenClient, String> {
            httpClient
                .post("$baseUrl/dialogporten/api/v1/serviceowner/dialogs") {
                    header("Content-Type", "application/json")
                    header("Accept", "application/json")
                    setBody(dialogRequest)
                }.body()
        }.getOrElse { e ->
            "Feil ved kall til Dialogporten for å opprette dialog med sykemelding".also {
                logger.error(it)
                sikkerLogger.error(it, e)
                throw DialogportenClientException(it)
            }
        }
    }

    suspend fun oppdaterDialogMedSykepengesoeknad(
        dialogId: UUID,
        soeknadJsonUrl: String,
    ) {
        val dialogPatchRequest = oppdaterDialogMedSykepengesoeknadRequest(soeknadJsonUrl = soeknadJsonUrl)
        runCatching {
            httpClient
                .patch("$baseUrl/dialogporten/api/v1/serviceowner/dialogs/$dialogId") {
                    header("Content-Type", "application/json-patch+json")
                    setBody(dialogPatchRequest)
                }
        }.getOrElse { e ->
            "Feil ved kall til Dialogporten for å oppdatere dialog med sykepengesøknad".also {
                logger.error(it)
                sikkerLogger.error(it, e)
                throw DialogportenClientException(it)
            }
        }
    }

    suspend fun oppdaterDialogMedInntektsmeldingsforespoersel(
        dialogId: UUID,
        forespoerselUrl: String,
        forespoerselDokumentasjonUrl: String,
    ) {
        val dialogPatchRequest =
            oppdaterDialogMedInntektsmeldingsforespoerselRequest(
                forespoerselUrl = forespoerselUrl,
                forespoerselDokumentasjonUrl = forespoerselDokumentasjonUrl,
            )
        runCatching {
            httpClient
                .patch("$baseUrl/dialogporten/api/v1/serviceowner/dialogs/$dialogId") {
                    header("Content-Type", "application/json-patch+json")
                    setBody(dialogPatchRequest)
                }
        }.getOrElse { e ->
            "Feil ved kall til Dialogporten for å oppdatere dialog med forespørsel om inntektsmelding".also {
                logger.error(it)
                sikkerLogger.error(it, e)
                throw DialogportenClientException(it)
            }
        }
    }

    suspend fun oppdaterDialogMedInntektsmelding(
        dialogId: UUID,
        inntektsmeldingUrl: String,
        relatertForespoerselId: UUID,
    ) {
        val inntektsmeldingPatchRequest =
            oppdaterDialogMedInntektsmeldingRequest(
                inntektsmeldingUrl = inntektsmeldingUrl,
                relatedTransmissionId = relatertForespoerselId,
                vedleggUrl = inntektsmeldingUrl,
            )
        runCatching {
            httpClient
                .patch("$baseUrl/dialogporten/api/v1/serviceowner/dialogs/$dialogId") {
                    header("Content-Type", "application/json-patch+json")
                    setBody(inntektsmeldingPatchRequest)
                }
        }.getOrElse { e ->
            "Feil ved kall til Dialogporten for å oppdatere dialog med inntektsmelding".also {
                logger.error(it)
                sikkerLogger.error(it, e)
                throw DialogportenClientException(it)
            }
        }
    }
}

class DialogportenClientException(
    message: String,
) : Exception(message)
