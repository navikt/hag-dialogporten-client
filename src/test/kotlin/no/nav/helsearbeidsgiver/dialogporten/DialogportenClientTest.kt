package no.nav.helsearbeidsgiver.dialogporten

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import no.nav.helsearbeidsgiver.dialogporten.domene.Action
import no.nav.helsearbeidsgiver.dialogporten.domene.ApiAction
import no.nav.helsearbeidsgiver.dialogporten.domene.Content
import no.nav.helsearbeidsgiver.dialogporten.domene.DialogStatus
import no.nav.helsearbeidsgiver.dialogporten.domene.GuiAction
import no.nav.helsearbeidsgiver.dialogporten.domene.Transmission
import no.nav.helsearbeidsgiver.dialogporten.domene.create
import java.util.UUID

class DialogportenClientTest :
    FunSpec({
        test("Opprett dialog  gir id tilbake") {

            val dialogportenKlient = mockDialogportenClient(HttpStatusCode.Created, MockData.gyldingRespons)
            val request = MockData.createDialogRequest

            dialogportenKlient.createDialog(request) shouldBe UUID.fromString(MockData.gyldingRespons)
        }
        test("addTransmission gir id tilbake") {
            val dialogportenClient = mockDialogportenClient(HttpStatusCode.Accepted, MockData.gyldingRespons)
            val dialogId = UUID.randomUUID()
            dialogportenClient.addTransmission(
                dialogId,
                Transmission(
                    type = Transmission.TransmissionType.Information,
                    extendedType = "extendedType",
                    externalReference = "externalReference",
                    sender = Transmission.Sender(actorType = "actorType"),
                    content = Content.create("title", null),
                    attachments = emptyList(),
                ),
            ) shouldBe UUID.fromString(MockData.gyldingRespons)
        }
        test("addApiActions returnerer ingenting ved sukksess") {
            val dialogportenClient = mockDialogportenClient(HttpStatusCode.NoContent)
            val dialogId = UUID.randomUUID()
            val apiActions =
                ApiAction(
                    action = Action.READ.value,
                    name = "name",
                    endpoints = emptyList(),
                )
            dialogportenClient.addAction(dialogId, apiActions, null) shouldBe Unit
        }

        test("addApiAction and guiActions returnerer ingenting ved sukksess") {
            val dialogportenClient = mockDialogportenClient(HttpStatusCode.NoContent)
            val dialogId = UUID.randomUUID()
            val apiActions =
                ApiAction(
                    action = Action.READ.value,
                    name = "name",
                    endpoints = emptyList(),
                )
            val guiActions =
                GuiAction(
                    action = Action.READ.value,
                    name = "name",
                    url = "url",
                    title = listOf(),
                    priority = GuiAction.Priority.Primary,
                )
            dialogportenClient.addAction(dialogId, apiActions, guiActions) shouldBe Unit
        }
        test("createDialog kaster exception ved feil response") {
            val dialogportenKlient = mockDialogportenClient(HttpStatusCode.InternalServerError, "error")
            val request = MockData.createDialogRequest

            shouldThrow<DialogportenClientException> {
                dialogportenKlient.createDialog(request)
            }
        }

        test("replaceAttachmentsAndActions returnerer ingenting ved suksess") {
            val dialogportenClient = mockDialogportenClient(HttpStatusCode.NoContent)
            val dialogId = UUID.randomUUID()
            val apiAction = ApiAction(action = Action.READ.value, name = "name", endpoints = emptyList())
            val guiAction =
                GuiAction(
                    action = Action.READ.value,
                    name = "name",
                    url = "url",
                    title = listOf(),
                    priority = GuiAction.Priority.Primary,
                )

            dialogportenClient.replaceAttachmentsAndActions(
                dialogId,
                attachments = emptyList(),
                apiActions = listOf(apiAction),
                guiActions = listOf(guiAction),
            ) shouldBe Unit
        }

        test("replaceAttachmentsAndActions kaster exception ved feil response") {
            val dialogportenClient = mockDialogportenClient(HttpStatusCode.InternalServerError, "error")
            val dialogId = UUID.randomUUID()

            shouldThrow<DialogportenClientException> {
                dialogportenClient.replaceAttachmentsAndActions(
                    dialogId,
                    attachments = emptyList(),
                    apiActions = emptyList(),
                    guiActions = emptyList(),
                )
            }
        }

        test("createDialog returnerer eksisterende dialogId ved idempotent conflict") {
            val existingDialogId = "019e874f-49c6-724a-8412-0de51e3200ff"
            val conflictResponse =
                """{"errors":{"IdempotentKey":["'6e449899-d758-4237-88f5-8be48eb3966a' already exists with DialogId '$existingDialogId'"]}}"""
            val dialogportenKlient = mockDialogportenClient(HttpStatusCode.Conflict, conflictResponse)

            val result = dialogportenKlient.createDialog(MockData.createDialogRequest)

            result shouldBe UUID.fromString(existingDialogId)
        }
        test("createDialog returnerer eksisterende dialogId ved idempotent conflict annen variant 1") {
            val existingDialogId = "019e874f-49c6-724a-8412-0de51e3200ff"
            val conflictResponse =
                """'6e449899-d758-4237-88f5-8be48eb3966a' already exists with DialogId '$existingDialogId'""""
            val dialogportenKlient = mockDialogportenClient(HttpStatusCode.Conflict, conflictResponse)

            val result = dialogportenKlient.createDialog(MockData.createDialogRequest)

            result shouldBe UUID.fromString(existingDialogId)
        }
        test("createDialog returnerer eksisterende dialogId ved idempotent conflict annen varian 2") {
            val existingDialogId = "019e874f-49c6-724a-8412-0de51e3200ff"
            val conflictResponse ="""                {
                    "type": "https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.8",
                    "title": "Conflict.",
                    "status": 409,
                    "instance": "/api/v1/serviceowner/dialogs",
                    "errors": {
                    "IdempotentKey": [
                    "'019e2068-7a13-71aa-8660-20afd766fcb8' already exists with DialogId '$existingDialogId'"
                    ]
                },
                    "traceId": "00-8ae44cbc239c0358ffa5120561ac1634-8a9f27bd595da08d-01"
                }""""
            val dialogportenKlient = mockDialogportenClient(HttpStatusCode.Conflict, conflictResponse)

            val result = dialogportenKlient.createDialog(MockData.createDialogRequest)

            result shouldBe UUID.fromString(existingDialogId)
        }

    })
