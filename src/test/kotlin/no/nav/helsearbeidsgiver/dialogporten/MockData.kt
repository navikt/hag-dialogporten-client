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
    val gyldingRespons = "0194cb3a-6f4e-7707-a506-a1db2b5c37fa"

}
