package no.nav.helsearbeidsgiver.dialogporten

object MockData {
    val orgnr = "312824450"
    val ressurs = "nav_sykepenger_inntektsmelding-nedlasting"
    val dialogRequest = lagCreateDialogRequest(
        ressurs = ressurs,
        orgnr = orgnr,
        status = "New",
        tittel = "Test Dialog tittel",
        sammendrag = "Test Dialog sammendrag"
    )
}
