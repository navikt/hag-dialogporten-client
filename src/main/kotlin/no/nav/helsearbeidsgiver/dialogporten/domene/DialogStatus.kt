package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable

@Serializable
enum class DialogStatus {
    InProgress,
    Draft,
    RequiresAttention,
    Completed,
    NotApplicable,
    Awaiting,
}
