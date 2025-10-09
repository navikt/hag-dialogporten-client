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
    val mediaType: String = MEDIA_TYPE,
) {
    companion object {
        const val MEDIA_TYPE: String = "text/plain"
    }
}

@Serializable
data class ContentValueItem(
    val value: String,
    val languageCode: String = LANGUAGE_CODE,
) {
    companion object {
        const val LANGUAGE_CODE: String = "nb"
    }
}

fun String.toContentValue() =
    ContentValue(
        value = listOf(ContentValueItem(this)),
    )

fun Content.Companion.create(
    title: String,
    summary: String?,
): Content = Content(title.toContentValue(), summary?.toContentValue())
