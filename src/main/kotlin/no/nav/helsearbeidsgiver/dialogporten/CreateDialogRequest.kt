package no.nav.helsearbeidsgiver.dialogporten

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CreateDialogRequest(
    val serviceResource: String,
    val party: String,
    val externalRefererence: String,
    val status: String,
    val content: Content
) {
    @Serializable
    data class Content(
        val title: ContentValue,
        val summary: ContentValue
    )

    @Serializable
    data class ContentValue(
        val value: List<ContentValueItem>,
        val mediaType: String = "text/plain"
    )

    @Serializable
    data class ContentValueItem(
        val value: String,
        val languageCode: String = "nb"
    )
}

fun lagContentValue(verdi: String) =
    CreateDialogRequest.ContentValue(
        value = listOf(
            CreateDialogRequest.ContentValueItem(
                value = verdi
            )
        )
    )

fun lagCreateDialogRequest(
    ressurs: String,
    orgnr: String,
    status: String,
    tittel: String,
    sammendrag: String
): CreateDialogRequest =
    CreateDialogRequest(
        serviceResource = "urn:altinn:resource:$ressurs",
        party = "urn:altinn:organization:identifier-no:$orgnr",
        status = status,
        externalRefererence = UUID.randomUUID().toString(),
        content = CreateDialogRequest.Content(lagContentValue(tittel), lagContentValue(sammendrag))
    )
