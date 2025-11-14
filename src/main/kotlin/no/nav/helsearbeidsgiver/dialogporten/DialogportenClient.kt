package no.nav.helsearbeidsgiver.dialogporten

import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import no.nav.helsearbeidsgiver.dialogporten.domene.AddApiActions
import no.nav.helsearbeidsgiver.dialogporten.domene.AddGuiActions
import no.nav.helsearbeidsgiver.dialogporten.domene.AddStatus
import no.nav.helsearbeidsgiver.dialogporten.domene.AddTransmissions
import no.nav.helsearbeidsgiver.dialogporten.domene.ApiAction
import no.nav.helsearbeidsgiver.dialogporten.domene.Content
import no.nav.helsearbeidsgiver.dialogporten.domene.CreateDialogRequest
import no.nav.helsearbeidsgiver.dialogporten.domene.Dialog
import no.nav.helsearbeidsgiver.dialogporten.domene.DialogStatus
import no.nav.helsearbeidsgiver.dialogporten.domene.GuiAction
import no.nav.helsearbeidsgiver.dialogporten.domene.PatchOperation
import no.nav.helsearbeidsgiver.dialogporten.domene.RemoveGuiActions
import no.nav.helsearbeidsgiver.dialogporten.domene.Transmission
import no.nav.helsearbeidsgiver.dialogporten.domene.create
import no.nav.helsearbeidsgiver.utils.log.logger
import no.nav.helsearbeidsgiver.utils.log.sikkerLogger
import java.util.UUID

class DialogportenClient(
    baseUrl: String,
    getToken: () -> String,
    val ressurs: String,
) {
    private val httpClient = createHttpClient(1, getToken)
    private val dialogportenUrl = "$baseUrl/dialogporten/api/v1/serviceowner/dialogs"
    private val logger = this.logger()
    private val sikkerLogger = sikkerLogger()

    suspend fun createDialog(createDialogRequest: CreateDialogRequest): UUID {
        val dialog =
            buildDialogFromRequest(createDialogRequest)
        return runCatching<DialogportenClient, UUID> {
            val response =
                httpClient
                    .post(dialogportenUrl) {
                        header(HttpHeaders.ContentType, ContentType.Application.Json)
                        header(HttpHeaders.Accept, ContentType.Application.Json)

                        setBody(dialog)
                    }.body<String>()
            UUID.fromString(response.removeSurrounding("\""))
        }.getOrElse { e ->
            logAndThrow("Feil ved kall til Dialogporten for å opprette dialog", e)
        }
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

            UUID.fromString(response.removeSurrounding("\""))
        }.getOrElse { e ->
            logAndThrow("Feil ved kall til Dialogporten for å legge til transmission", e)
        }

    suspend fun addTransmissionsWithStatusRequiresAttention(
        dialogId: UUID,
        transmissions: List<Transmission>,
    ) {
        updateDialog(
            dialogId,
            listOf(
                AddTransmissions(transmissions),
                AddStatus(DialogStatus.RequiresAttention),
            ),
        )
    }

    suspend fun addTransmissionsWithStatusCompleted(
        dialogId: UUID,
        transmissions: List<Transmission>,
    ) {
        updateDialog(
            dialogId,
            listOf(
                AddTransmissions(transmissions),
                AddStatus(DialogStatus.Completed),
            ),
        )
    }

    suspend fun addTransmissionsWithRemoveActions(
        dialogId: UUID,
        transmissions: List<Transmission>,
    ) {
        updateDialog(
            dialogId,
            listOf(
                AddTransmissions(transmissions),
                RemoveGuiActions(),
                RemoveGuiActions(),
            ),
        )
    }

    suspend fun addAction(
        dialogId: UUID,
        apiAction: ApiAction,
        guiActions: GuiAction?,
    ) {
        if (guiActions == null) {
            updateDialog(dialogId, listOf(AddApiActions(listOf(apiAction))))
        } else {
            updateDialog(dialogId, listOf(AddApiActions(listOf(apiAction)), AddGuiActions(listOf(guiActions))))
        }
    }

    private suspend fun updateDialog(
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
            logAndThrow("Feil ved kall til Dialogporten for å oppdatere dialog", e)
        }
    }

    private fun logAndThrow(
        msg: String,
        e: Throwable,
    ): Nothing {
        logger.error(msg)
        sikkerLogger
            .error(msg, e)
        throw DialogportenClientException(msg)
    }
}

private fun DialogportenClient.buildDialogFromRequest(createDialogRequest: CreateDialogRequest): Dialog =
    Dialog(
        serviceResource = "urn:altinn:resource:$ressurs",
        party = "urn:altinn:organization:identifier-no:${createDialogRequest.orgnr}",
        externalReference = createDialogRequest.externalReference,
        content =
            Content.create(
                title =
                    createDialogRequest.title,
                summary =
                    createDialogRequest.summary,
            ),
        transmissions = createDialogRequest.transmissions,
        isApiOnly = createDialogRequest.isApiOnly,
    )
