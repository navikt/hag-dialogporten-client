package no.nav.helsearbeidsgiver.dialogporten

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import no.nav.helsearbeidsgiver.utils.test.resource.readResource
import java.util.UUID

class DialogportenClientTest :
    FunSpec({
        test("Opprett dialog med sykmelding gir id tilbake") {
            val dialogportenClient = mockDialogportenClient(HttpStatusCode.Created, MockData.gyldingRespons)
            dialogportenClient
                .opprettDialogMedSykmelding(
                    orgnr = MockData.orgnr,
                    dialogTittel = "testTittel",
                    dialogSammendrag = "testSammendrag",
                    sykmeldingId = UUID.randomUUID(),
                    sykmeldingJsonUrl = "testurl.no",
                ) shouldBe MockData.gyldingRespons
        }

        test("Oppdater dialog med søknad gir ingen respons tilbake") {
            val dialogportenClient = mockDialogportenClient(HttpStatusCode.NoContent)
            dialogportenClient
                .oppdaterDialogMedSykepengesoknad(
                    dialogId = UUID.randomUUID(),
                    soknadJsonUrl = "testurl.no",
                )
        }

        test("Oppretting av dialog med sykmelding kaster valideringsfeil videre ved 400-feil") {
            val dialogportenClient =
                mockDialogportenClient(
                    HttpStatusCode.BadRequest,
                    "dialog-response/validation-error.json".readResource(),
                )
            shouldThrowExactly<DialogportenClientException> {
                dialogportenClient
                    .opprettDialogMedSykmelding(
                        orgnr = MockData.orgnr,
                        dialogTittel = "testTittel",
                        dialogSammendrag = "testSammendrag",
                        sykmeldingId = UUID.randomUUID(),
                        sykmeldingJsonUrl = "testurl.no",
                    )
            }
        }

        test("Oppdatering av dialog med søknad kaster valideringsfeil videre ved 400-feil") {
            val dialogportenClient =
                mockDialogportenClient(
                    HttpStatusCode.BadRequest,
                    "dialog-response/validation-error.json".readResource(),
                )
            shouldThrowExactly<DialogportenClientException> {
                dialogportenClient
                    .oppdaterDialogMedSykepengesoknad(
                        dialogId = UUID.randomUUID(),
                        soknadJsonUrl = "testurl.no",
                    )
            }
        }
    })
