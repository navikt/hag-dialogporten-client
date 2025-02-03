package no.nav.helsearbeidsgiver.dialogporten

import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import no.nav.helsearbeidsgiver.utils.log.logger
import no.nav.helsearbeidsgiver.utils.log.sikkerLogger

class DialogportenClient(
    private val baseUrl: String,
    private val ressurs: String,
    private val getToken: () -> String,
) {
    private val httpClient = createHttpClient(1, getToken)

    private val logger = this.logger()
    private val sikkerLogger = sikkerLogger()


    private suspend fun opprettDialog(
        orgnr: String,
    ): Result<String> {
        val dialogRequest = lagCreateDialogRequest(
            ressurs = ressurs,
            orgnr = orgnr,
            status = "New",
            tittel = "",
            sammendrag = "",
        )
        val pdpResponseResult: Result<String> =
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
                throw DialogportenClientException()
            }
        return pdpResponseResult
    }
}

class DialogportenClientException :
    Exception(
        "Feil ved kall til dialogporten endepunkt",
    )
