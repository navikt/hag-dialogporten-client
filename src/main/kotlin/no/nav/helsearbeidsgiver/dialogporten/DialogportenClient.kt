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

    suspend fun opprettDialog(
        orgnr: String,
        url: String,
    ): Result<String> {
        val dialogRequest =
            lagCreateDialogRequest(
                ressurs = ressurs,
                orgnr = orgnr,
                status = "New",
                tittel = "Nav trenger inntektsmelding",
                sammendrag =
                    "En av dine ansatte har søkt om sykepenger for perioden DD.MM.ÅÅÅÅ - DD.MM.ÅÅÅÅ og vi trenger inntektsmelding for å behandle søknaden. Logg inn på Min side – arbeidsgiver hos Nav.",
                url = url,
                knappTittel = "Gå til inntektsmeldingskjema på Nav",
            )
        val dialogResponse: Result<String> =
            runCatching<DialogportenClient, String> {
                httpClient
                    .post("$baseUrl/dialogporten/api/v1/serviceowner/dialogs") {
                        header("Content-Type", "application/json")
                        header("Accept", "application/json")
                        setBody(dialogRequest)
                    }.body()
            }.recover { e ->
                "Feil ved kall til dialogporten endepunkt".also {
                    logger.error(it)
                    sikkerLogger.error(it, e)
                }
                throw DialogportenClientException("Feil ved kall til dialogporten endepunkt")
            }
        return dialogResponse
    }

    suspend fun opprettNyDialogMedSykmelding(
        orgnr: String,
        dialogTittel: String,
        dialogSammendrag: String,
        sykmeldingId: UUID,
        sykmeldingJsonUrl: String,
    ): String {
        val dialogRequest =
            lagNyDialogMedSykmeldingRequest(
                ressurs = ressurs,
                orgnr = orgnr,
                dialogTittel = dialogTittel,
                dialogSammendrag = dialogSammendrag,
                sykmeldingId = sykmeldingId,
                sykmeldingJsonUrl = sykmeldingJsonUrl,
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

    suspend fun oppdaterDialogMedSoknad(
        dialogId: String,
        soknadJsonUrl: String,
    ) {
        val dialogPatchRequest = listOf(oppdaterDialogMedSoknadRequest(soknadJsonUrl = soknadJsonUrl))
        runCatching<DialogportenClient, String> {
            httpClient
                .patch("$baseUrl/dialogporten/api/v1/serviceowner/dialogs/$dialogId") {
                    header("Content-Type", "application/json")
                    header("Accept", "application/json")
                    setBody(dialogPatchRequest)
                }.body()
        }.getOrElse { e ->
            "Feil ved kall til Dialogporten for å oppdatere dialog med søknad".also {
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
