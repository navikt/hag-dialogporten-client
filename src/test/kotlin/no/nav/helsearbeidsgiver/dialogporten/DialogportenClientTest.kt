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
                .oppdaterDialogMedSykepengesoeknad(
                    dialogId = UUID.randomUUID(),
                    soeknadJsonUrl = "testurl.no",
                )
        }

        test("Oppdater dialog med forespørsel om inntektsmelding gir ingen respons tilbake") {
            val dialogportenClient = mockDialogportenClient(HttpStatusCode.NoContent)
            dialogportenClient
                .oppdaterDialogMedForespoerselOmInntektsmelding(
                    dialogId = UUID.randomUUID(),
                    forespoerselUrl = "testurl.no",
                    forespoerselDokumentasjonUrl = "testdokumentasjonurl.no",
                )
        }

        test("Oppretting av dialog med sykmelding kaster feil videre ved 400-feil") {
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

        test("Oppdatering av dialog med søknad kaster feil videre ved 400-feil") {
            val dialogportenClient =
                mockDialogportenClient(
                    HttpStatusCode.BadRequest,
                    "dialog-response/validation-error.json".readResource(),
                )
            shouldThrowExactly<DialogportenClientException> {
                dialogportenClient
                    .oppdaterDialogMedSykepengesoeknad(
                        dialogId = UUID.randomUUID(),
                        soeknadJsonUrl = "testurl.no",
                    )
            }
        }

        test("Oppdatering av dialog med forespørsel om inntektsmelding kaster feil videre ved 400-feil") {
            val dialogportenClient =
                mockDialogportenClient(
                    HttpStatusCode.BadRequest,
                    "dialog-response/validation-error.json".readResource(),
                )
            shouldThrowExactly<DialogportenClientException> {
                dialogportenClient
                    .oppdaterDialogMedForespoerselOmInntektsmelding(
                        dialogId = UUID.randomUUID(),
                        forespoerselUrl = "testurl.no",
                        forespoerselDokumentasjonUrl = "testdokumentasjonurl.no",
                    )
            }
        }
    })
