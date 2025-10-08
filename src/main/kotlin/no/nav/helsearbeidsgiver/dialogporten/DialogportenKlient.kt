package no.nav.helsearbeidsgiver.dialogporten

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import no.nav.helsearbeidsgiver.dialogporten.domene.AddApiActions
import no.nav.helsearbeidsgiver.dialogporten.domene.ApiAction
import no.nav.helsearbeidsgiver.dialogporten.domene.CreateDialogRequest
import no.nav.helsearbeidsgiver.dialogporten.domene.PatchOperation
import no.nav.helsearbeidsgiver.dialogporten.domene.Transmission
import no.nav.helsearbeidsgiver.utils.log.logger
import no.nav.helsearbeidsgiver.utils.log.sikkerLogger
import java.util.UUID

private const val ENDPOINT = "dialogporten/api/v1/serviceowner/dialogs"

class DialogportenKlient(
    val baseUrl: String,
    getToken: () -> String,
) {
    private val httpClient = createHttpClient(1, getToken)

    private val logger = this.logger()
    private val sikkerLogger = sikkerLogger()

    suspend fun getTransmissions(dialogId: UUID): List<Transmission> =
        runCatching {
            httpClient
                .get("$baseUrl/$ENDPOINT/$dialogId/transmissions") {
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                    header(HttpHeaders.Accept, ContentType.Application.Json)
                }.body<List<Transmission>>()
        }.getOrElse { e ->
            handleError("Feil ved kall til Dialogporten for å hente transmissions", e)
        }

    suspend fun createDialog(createDialogRequest: CreateDialogRequest): UUID =
        runCatching<DialogportenKlient, UUID> {
            val response =
                httpClient
                    .post("$baseUrl/$ENDPOINT") {
                        header(HttpHeaders.ContentType, ContentType.Application.Json)
                        header(HttpHeaders.Accept, ContentType.Application.Json)

                        setBody(createDialogRequest)
                    }.body<String>()
            return UUID.fromString(response.removeSurrounding("\""))
        }.getOrElse { e ->
            handleError("Feil ved kall til Dialogporten for å opprette dialog", e)
        }

    suspend fun addTransmission(
        dialogId: UUID,
        transmission: Transmission,
    ): UUID =
        runCatching {
            val response =
                httpClient
                    .post("$baseUrl$ENDPOINT/$dialogId/transmissions") {
                        header(HttpHeaders.ContentType, ContentType.Application.Json)
                        header(HttpHeaders.Accept, ContentType.Application.Json)
                        setBody(transmission)
                    }.body<String>()

            return UUID.fromString(response.removeSurrounding("\""))
        }.getOrElse { e ->
            handleError("Feil ved kall til Dialogporten for å legge til transmission", e)
        }

    suspend fun addAction(
        dialogId: UUID,
        apiAction: ApiAction,
    ) {
        updateDialog(dialogId, listOf(AddApiActions(listOf(apiAction))))
    }

    suspend fun updateDialog(
        dialogId: UUID,
        patchOperations: List<PatchOperation>,
    ) {
        runCatching<DialogportenKlient, Unit> {
            httpClient
                .patch("$baseUrl$ENDPOINT/$dialogId") {
                    header(HttpHeaders.ContentType, "application/json-patch+json")
                    setBody(patchOperations)
                }
        }.getOrElse { e ->
            handleError("Feil ved kall til Dialogporten for å oppdatere dialog", e)
        }
    }

    private fun handleError(
        msg: String,
        e: Throwable,
    ): Nothing {
        logger.error(msg)
        sikkerLogger
            .error(msg, e)
        throw DialogportenClientException(msg)
    }
}
