package no.nav.helsearbeidsgiver.dialogporten

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import no.nav.helsearbeidsgiver.utils.test.resource.readResource
import java.util.UUID

class DialogportenClientTest :
    FunSpec({
        test("Opprett dialog gir id tilbake") {
            val dialogportenClient = mockDialogportenClient(HttpStatusCode.Created, MockData.gyldingRespons)
            dialogportenClient.opprettDialog(MockData.orgnr, "testurl").getOrNull() shouldBe MockData.gyldingRespons
        }

        test("Opprett dialog gir valideringsfeil") {
            val dialogportenClient =
                mockDialogportenClient(HttpStatusCode.BadRequest, "create-dialog-response/validation-error.json".readResource())
            shouldThrowExactly<DialogportenClientException> { dialogportenClient.opprettDialog(MockData.orgnr, "testurl").getOrThrow() }
        }

        test("Opprett dialog med sykmelding gir id tilbake") {
            val dialogportenClient = mockDialogportenClient(HttpStatusCode.Created, MockData.gyldingRespons)
            dialogportenClient
                .opprettNyDialogMedSykmelding(
                    orgnr = MockData.orgnr,
                    dialogTittel = "testTittel",
                    dialogSammendrag = "testSammendrag",
                    sykmeldingId = UUID.randomUUID(),
                    sykmeldingJsonUrl = "testurl.no",
                ).getOrNull() shouldBe MockData.gyldingRespons
        }
    })
