package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val title: ContentValue,
    val summary: ContentValue? = null,
    val additionalInfo: ContentValue? = null,
    val contentReference: ContentValue? = null,
)

@Serializable
data class ContentValue(
    val value: List<ContentValueItem>,
    val mediaType: String = "text/plain",
)

@Serializable
data class ContentValueItem(
    val value: String,
    val languageCode: String = "nb",
)

fun String.toContentValue() =
    ContentValue(
        value = listOf(ContentValueItem(this)),
    )

fun String.toContentReferenceValue() =
    ContentValue(
        value = listOf(ContentValueItem(this)),
        mediaType = "application/vnd.dialogporten.frontchannelembed+json;type=markdown",
    )

fun String.toAdditionalInfoContentValue() =
    ContentValue(
        value = listOf(ContentValueItem(this)),
        mediaType = "text/markdown",
    )

fun Content.Companion.create(
    title: String,
    summary: String?,
    contentReferenceFceUrl: String? = null,
    additionalInfo: String? = null,
): Content =
    Content(
        title = title.toContentValue(),
        summary = summary?.toContentValue(),
        additionalInfo = additionalInfo?.toAdditionalInfoContentValue(),
        contentReference = contentReferenceFceUrl?.toContentReferenceValue(),
    )
