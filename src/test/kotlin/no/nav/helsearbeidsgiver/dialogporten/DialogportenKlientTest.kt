package no.nav.helsearbeidsgiver.dialogporten

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import no.nav.helsearbeidsgiver.dialogporten.domene.Content
import no.nav.helsearbeidsgiver.dialogporten.domene.CreateDialogRequest
import no.nav.helsearbeidsgiver.dialogporten.domene.lagContentValue
import java.util.UUID

class DialogportenKlientTest :
    FunSpec({
        test("Opprett dialog  gir id tilbake") {

            val dialogportenKlient = mockDialogportenKlient(HttpStatusCode.Created, MockData.gyldingRespons)
            val request =
                CreateDialogRequest(
                    serviceResource = "urn:altinn:resource:ressursnavn",
                    party = "urn:altinn:organization:identifier-no:123456789",
                    externalRefererence = "externalReference",
                    status = no.nav.helsearbeidsgiver.dialogporten.domene.DialogStatus.New,
                    content =
                        Content(
                            title = "testtittel".lagContentValue(),
                        ),
                    transmissions = listOf(),
                    isApiOnly = true,
                )

            dialogportenKlient.createDialog(request) shouldBe UUID.fromString(MockData.gyldingRespons)
        }
    })
