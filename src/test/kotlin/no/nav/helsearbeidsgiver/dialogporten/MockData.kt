package no.nav.helsearbeidsgiver.dialogporten

import java.util.UUID

object MockData {
    val orgnr = "123456789"
    val ressurs = "ressursnavn"
    val dialogRequest =
        opprettDialogMedSykmeldingRequest(
            ressurs = ressurs,
            orgnr = orgnr,
            dialogTittel = "testtittel",
            dialogSammendrag = "testdialogsammendrag",
            sykmeldingId = UUID.randomUUID(),
            sykmeldingJsonUrl = "test-sykmelding-url.no",
        )
    val gyldingRespons = "0194cb3a-6f4e-7707-a506-a1db2b5c37fa"
}
