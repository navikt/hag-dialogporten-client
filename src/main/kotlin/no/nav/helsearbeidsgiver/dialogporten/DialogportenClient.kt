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
import no.nav.helsearbeidsgiver.dialogporten.domene.Dialog
import no.nav.helsearbeidsgiver.dialogporten.domene.PatchOperation
import no.nav.helsearbeidsgiver.dialogporten.domene.Transmission
import no.nav.helsearbeidsgiver.utils.log.logger
import no.nav.helsearbeidsgiver.utils.log.sikkerLogger
import java.util.UUID

class DialogportenClient(
    baseUrl: String,
    getToken: () -> String,
) {
    private val httpClient = createHttpClient(1, getToken)
    private val dialogportenUrl = "$baseUrl/dialogporten/api/v1/serviceowner/dialogs"
    private val logger = this.logger()
    private val sikkerLogger = sikkerLogger()

    suspend fun getTransmissions(dialogId: UUID): List<Transmission> =
        runCatching {
            httpClient
                .get("dialogportenUrl/$dialogId/transmissions") {
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                    header(HttpHeaders.Accept, ContentType.Application.Json)
                }.body<List<Transmission>>()
        }.getOrElse { e ->
            handleError("Feil ved kall til Dialogporten for å hente transmissions", e)
        }

    suspend fun createDialog(dialog: Dialog): UUID =
        runCatching<DialogportenClient, UUID> {
            val response =
                httpClient
                    .post(dialogportenUrl) {
                        header(HttpHeaders.ContentType, ContentType.Application.Json)
                        header(HttpHeaders.Accept, ContentType.Application.Json)

                        setBody(dialog)
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
                    .post("$dialogportenUrl/$dialogId/transmissions") {
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
        runCatching<DialogportenClient, Unit> {
            httpClient
                .patch("$dialogportenUrl/$dialogId") {
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
