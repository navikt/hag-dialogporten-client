package no.nav.helsearbeidsgiver.dialogporten.domene

import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr

data class CreateDialogRequest(
    val orgnr: Orgnr,
    val title: String,
    val summary: String,
    val additionalInfo: String? = null,
    val externalReference: String,
    val idempotentKey: String,
    val isApiOnly: Boolean = false,
    val transmissions: List<Transmission>,
    val attachments: List<Attachment>? = null,
)
