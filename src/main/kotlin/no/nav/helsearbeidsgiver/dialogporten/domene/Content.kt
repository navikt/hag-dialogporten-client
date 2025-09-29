package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val title: ContentValue,
    val summary: ContentValue? = null,
)

@Serializable
data class ContentValue(
    val value: List<ContentValueItem>,
) {
    @Suppress("unused")
    val mediaType: String = "text/plain"
}

@Serializable
data class ContentValueItem(
    val value: String,
) {
    @Suppress("unused")
    val languageCode: String = "nb"
}
