package no.nav.helsearbeidsgiver.dialogporten.domene

import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr

data class CreateDialogRequest(
    val orgnr: Orgnr,
    val title: String,
    val summary: String,
    val externalReference: String,
    val idempotentKey: String,
    val isApiOnly: Boolean = true,
    val transmissions: List<Transmission>,
)
