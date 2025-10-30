package no.nav.helsearbeidsgiver.dialogporten

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import no.nav.helsearbeidsgiver.dialogporten.domene.Action
import no.nav.helsearbeidsgiver.dialogporten.domene.ApiAction
import no.nav.helsearbeidsgiver.dialogporten.domene.Content
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
    })
