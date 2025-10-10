package no.nav.helsearbeidsgiver.dialogporten

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import no.nav.helsearbeidsgiver.dialogporten.domene.ApiAction
import no.nav.helsearbeidsgiver.dialogporten.domene.Content
import no.nav.helsearbeidsgiver.dialogporten.domene.Transmission
import no.nav.helsearbeidsgiver.dialogporten.domene.create
import java.util.UUID

class DialogportenClientTest :
    FunSpec({
        test("Opprett dialog  gir id tilbake") {

            val dialogportenKlient = mockDialogportenKlient(HttpStatusCode.Created, MockData.gyldingRespons)
            val request = MockData.dialogMock

            dialogportenKlient.createDialog(request) shouldBe UUID.fromString(MockData.gyldingRespons)
        }
        test("addTransmission gir id tilbake") {
            val dialogportenClient = mockDialogportenKlient(HttpStatusCode.Accepted, MockData.gyldingRespons)
            val dialogId = UUID.randomUUID()
            dialogportenClient.addTransmission(
                dialogId,
                Transmission(
                    id = UUID.randomUUID(),
                    type = Transmission.TransmissionType.Information,
                    extendedType = "extendedType",
                    sender = Transmission.Sender(actorType = "actorType"),
                    content = Content.create("title", null),
                    attachments = emptyList(),
                ),
            ) shouldBe UUID.fromString(MockData.gyldingRespons)
        }
        test("getTransmissions gir liste av transmissions tilbake") {

            val dialogportenClient = mockDialogportenKlient(HttpStatusCode.OK, MockData.transmissionsJson)
            val dialogId = UUID.randomUUID()
            val transmissions = dialogportenClient.getTransmissions(dialogId)
            transmissions.size shouldBe 1
            transmissions[0].id shouldBe UUID.fromString("0194cb3a-6f4e-7707-a506-a1db2b5c37fa")
            transmissions[0].type shouldBe Transmission.TransmissionType.Information
            transmissions[0].extendedType shouldBe "extendedType"
            transmissions[0].sender.actorType shouldBe "actorType"
            transmissions[0]
                .content.title.value[0]
                .value shouldBe "title"
        }
        test("addApiActions returnerer ingenting ved sukksess") {
            val dialogportenClient = mockDialogportenKlient(HttpStatusCode.NoContent)
            val dialogId = UUID.randomUUID()
            val apiActions =
                ApiAction(
                    action = ApiAction.Action.READ.value,
                    name = "name",
                    endpoints = emptyList(),
                )
            dialogportenClient.addAction(dialogId, apiActions) shouldBe Unit
        }

        test("createDialog kaster exception ved feil response") {
            val dialogportenKlient = mockDialogportenKlient(HttpStatusCode.InternalServerError, "error")
            val request = MockData.dialogMock

            shouldThrow<DialogportenClientException> {
                dialogportenKlient.createDialog(request)
            }
        }
    })
