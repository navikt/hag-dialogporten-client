package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable

@Serializable
enum class DialogStatus {
    New,
    InProgress,
    Draft,
    Sent,
    RequiresAttention,
    Completed,
}
