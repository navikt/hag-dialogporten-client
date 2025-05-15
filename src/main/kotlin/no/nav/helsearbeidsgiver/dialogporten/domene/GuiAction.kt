package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable

@Serializable
data class GuiAction(
    val action: String,
    val url: String,
    val title: List<ContentValueItem>,
) {
    @Suppress("unused")
    val priority = "Primary"

    @Suppress("unused")
    val isDeleteDialogAction = false
}
