package no.nav.helsearbeidsgiver.dialogporten.domene

import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr

data class CreateDialogRequest(
    val orgnr: Orgnr,
    val title: String,
    val summery: String,
    val externalReference: String,
    val isApiOnly: Boolean = true,
    val transmissions: List<Transmission>,
)
