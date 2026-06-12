package no.nav.helsearbeidsgiver.dialogporten

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import no.nav.helsearbeidsgiver.dialogporten.domene.AddApiActions
import no.nav.helsearbeidsgiver.dialogporten.domene.AddGuiActions
import no.nav.helsearbeidsgiver.dialogporten.domene.AddStatus
import no.nav.helsearbeidsgiver.dialogporten.domene.ApiAction
import no.nav.helsearbeidsgiver.dialogporten.domene.Attachment
import no.nav.helsearbeidsgiver.dialogporten.domene.Content
import no.nav.helsearbeidsgiver.dialogporten.domene.CreateDialogRequest
import no.nav.helsearbeidsgiver.dialogporten.domene.Dialog
import no.nav.helsearbeidsgiver.dialogporten.domene.DialogResponse
import no.nav.helsearbeidsgiver.dialogporten.domene.DialogStatus
import no.nav.helsearbeidsgiver.dialogporten.domene.GuiAction
import no.nav.helsearbeidsgiver.dialogporten.domene.PatchOperation
import no.nav.helsearbeidsgiver.dialogporten.domene.RemoveApiAction
import no.nav.helsearbeidsgiver.dialogporten.domene.RemoveGuiActions
import no.nav.helsearbeidsgiver.dialogporten.domene.ReplaceApiActions
import no.nav.helsearbeidsgiver.dialogporten.domene.ReplaceAttachments
import no.nav.helsearbeidsgiver.dialogporten.domene.ReplaceGuiActions
import no.nav.helsearbeidsgiver.dialogporten.domene.ReplaceIsApiOnly
import no.nav.helsearbeidsgiver.dialogporten.domene.Transmission
import no.nav.helsearbeidsgiver.dialogporten.domene.TransmissionRequest
import no.nav.helsearbeidsgiver.dialogporten.domene.create
import no.nav.helsearbeidsgiver.dialogporten.domene.toTransmission
import no.nav.helsearbeidsgiver.utils.log.logger
import no.nav.helsearbeidsgiver.utils.log.sikkerLogger
import java.util.UUID

private val dialogIdConflictRegex =
    Regex("""(?i)DialogId\s+'?([0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12})'?""")

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

        return try {
            val response =
                httpClient
                    .post(dialogportenUrl) {
                        header(HttpHeaders.ContentType, ContentType.Application.Json)
                        header(HttpHeaders.Accept, ContentType.Application.Json)

                        setBody(dialog)
                    }.body<String>()
            UUID.fromString(response.removeSurrounding("\"")).also { logger.info("Dialog opprettet med id: $it") }
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Conflict) {
                val existingDialogId = e.response.bodyAsText().extractDialogIdFromConflict()
                if (existingDialogId != null) {
                    logger.info(
                        "Dialog finnes allerede for ${createDialogRequest.idempotentKey}, bruker eksisterende id: $existingDialogId",
                    )
                    existingDialogId
                } else {
                    logAndThrow("Response fra Dialogporten inneholder ikke dialogId", e)
                }
            } else {
                logAndThrow("Feil ved kall til Dialogporten for å opprette dialog", e)
            }
        } catch (e: Throwable) {
            logAndThrow("Feil ved kall til Dialogporten for å opprette dialog", e)
        }
    }

    suspend fun getDialog(dialogId: UUID): Result<DialogResponse> =
        runCatching {
            val response =
                httpClient.get("$dialogportenUrl/$dialogId") {
                    header(HttpHeaders.Accept, ContentType.Application.Json)
                }
            if (!response.status.isSuccess()) {
                throw IllegalStateException("Uventet status ${response.status.value} ved henting av dialog $dialogId")
            }
            response.body<DialogResponse>()
        }

    suspend fun addTransmission(
        dialogId: UUID,
        transmissionRequest: TransmissionRequest,
    ): UUID =
        addTransmission(
            dialogId,
            transmission = transmissionRequest.toTransmission(),
        )

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

            UUID.fromString(response.removeSurrounding("\"")).also { logger.info("Transmission opprettet med id: $it") }
        }.getOrElse { e ->
            logAndThrow("Feil ved kall til Dialogporten for å legge til transmission", e)
        }

    suspend fun setDialogStatus(
        dialogId: UUID,
        dialogStatus: DialogStatus,
    ) {
        updateDialog(
            dialogId,
            listOf(
                AddStatus(dialogStatus),
            ),
        )
    }

/*
Brukes til å oppdatere eksisterende dialogmeldinger som har satt ApiOnly.
Dette fordi vi må gjøre meldingen synlig for bruker i GUI
når vi får en søknad på en eksisterende apiOnly-dialogmelding
 */
    suspend fun fjernApiOnly(dialogId: UUID) {
        updateDialog(
            dialogId,
            listOf(
                ReplaceIsApiOnly(),
            ),
        )
    }

    suspend fun removeActionsAndStatus(dialogId: UUID) {
        updateDialog(
            dialogId,
            listOf(
                RemoveGuiActions(),
                RemoveApiAction(),
                AddStatus(DialogStatus.NotApplicable),
            ),
        )
    }

    suspend fun addGuiAction(
        dialogId: UUID,
        guiAction: GuiAction,
    ) {
        updateDialog(dialogId, listOf(AddGuiActions(listOf(guiAction))))
    }

    suspend fun addAction(
        dialogId: UUID,
        apiAction: ApiAction,
        guiAction: GuiAction?,
    ) {
        if (guiAction == null) {
            updateDialog(dialogId, listOf(AddApiActions(listOf(apiAction)), AddStatus(DialogStatus.RequiresAttention)))
        } else {
            updateDialog(
                dialogId,
                listOf(AddApiActions(listOf(apiAction)), AddGuiActions(listOf(guiAction)), AddStatus(DialogStatus.RequiresAttention)),
            )
        }
    }

    suspend fun replaceAttachmentsAndActions(
        dialogId: UUID,
        attachments: List<Attachment>,
        apiActions: List<ApiAction>,
        guiActions: List<GuiAction>,
    ) {
        updateDialog(
            dialogId,
            listOf(
                ReplaceAttachments(attachments),
                ReplaceApiActions(apiActions),
                ReplaceGuiActions(guiActions),
            ),
        )
    }

    suspend fun replaceAttachments(
        dialogId: UUID,
        attachments: List<Attachment>,
    ) {
        updateDialog(
            dialogId,
            listOf(
                ReplaceAttachments(attachments),
            ),
        )
    }

    suspend fun replaceTransmission(
        dialogId: UUID,
        existingTransmissionId: UUID,
        transmission: Transmission,
    ) {
        try {
            val response =
                httpClient
                    .put("$dialogportenUrl/$dialogId/transmissions/$existingTransmissionId") {
                        header(HttpHeaders.ContentType, ContentType.Application.Json)
                        setBody(transmission)
                    }

            if (!response.status.isSuccess()) {
                throw IllegalStateException(
                    "Uventet status ${response.status.value} ved erstatning av transmission for dialogId $dialogId med transmissionId $existingTransmissionId",
                )
            }
        } catch (e: Exception) {
            logAndThrow("Feil ved kall til Dialogporten for å erstatte transmission", e)
        }
    }

    private suspend fun updateDialog(
        dialogId: UUID,
        patchOperations: List<PatchOperation>,
    ) {
        try {
            val response =
                httpClient
                    .patch("$dialogportenUrl/$dialogId") {
                        header(HttpHeaders.ContentType, "application/json-patch+json")
                        setBody(patchOperations)
                    }

            if (!response.status.isSuccess()) {
                throw IllegalStateException("Uventet status ${response.status.value} ved oppdatering av dialogId $dialogId")
            }
        } catch (e: Exception) {
            logAndThrow("Feil ved kall til Dialogporten for å oppdatere dialog", e)
        }
    }

    private fun logAndThrow(
        msg: String,
        e: Throwable,
    ): Nothing {
        logger.error(msg)
        sikkerLogger.error(msg, e)
        throw DialogportenClientException(msg)
    }
}

private fun DialogportenClient.buildDialogFromRequest(createDialogRequest: CreateDialogRequest): Dialog =
    Dialog(
        serviceResource = "urn:altinn:resource:$ressurs",
        party = "urn:altinn:organization:identifier-no:${createDialogRequest.orgnr}",
        externalReference = createDialogRequest.externalReference,
        idempotentKey = createDialogRequest.idempotentKey,
        content =
            Content.create(
                title =
                    createDialogRequest.title,
                summary =
                    createDialogRequest.summary,
                additionalInfo =
                    createDialogRequest.additionalInfo,
            ),
        transmissions = createDialogRequest.transmissions,
        isApiOnly = createDialogRequest.isApiOnly,
        attachments = createDialogRequest.attachments.orEmpty(),
    )

private fun String.extractDialogIdFromConflict(): UUID? =
    dialogIdConflictRegex
        .find(this)
        ?.groupValues
        ?.getOrNull(1)
        ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
