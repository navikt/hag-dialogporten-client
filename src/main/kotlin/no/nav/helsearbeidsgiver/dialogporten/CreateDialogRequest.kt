package no.nav.helsearbeidsgiver.dialogporten

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CreateDialogRequest(
    val serviceResource: String,
    val party: String,
    val externalRefererence: String,
    val status: String,
    val content: Content,
    val guiActions: List<GuiAction>,
    val transmissions: List<Transmission>,
) {
    @Serializable
    data class GuiAction(
        val action: String,
        val url: String,
        val isDeleteDialogAction: Boolean = false,
        val priority: String = "Primary",
        val title: List<ContentValueItem>,
    )
}

@Serializable
data class Content(
    val title: ContentValue,
    val summary: ContentValue,
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

@Serializable
data class Transmission(
    val type: TransmissionType,
    val sender: Sender,
    val content: Content,
    val attachments: List<Attachment>,

    ) {
    @Serializable
    data class Sender(
        val actorType: String,
    )

    @Serializable
    enum class TransmissionType {
        // For general information, not related to any submissions
        Information,

        // Feedback/receipt accepting a previous submission
        Acceptance,

        // Feedback/error message rejecting a previous submission
        Rejection,

        // Question/request for more information
        Request,
    }

    @Serializable
    data class Attachment(
        val displayName: ContentValueItem,
        val urls: List<Url>,
    )

    @Serializable
    data class Url(
        val url: String,
        val mediaType: String,
        val consumerType: AttachmentUrlConsumerType,
    )

    @Serializable
    enum class AttachmentUrlConsumerType {
        Gui,
        Api,
    }

}

fun lagContentValue(verdi: String) =
    ContentValue(
        value =
            listOf(
                ContentValueItem(
                    value = verdi,
                ),
            ),
    )

fun lagGuiAction(
    url: String,
    tittel: String,
) = CreateDialogRequest.GuiAction(
    action = "read",
    url = url,
    title =
        listOf(
            ContentValueItem(
                value = tittel,
            ),
        ),
)

fun lagCreateDialogRequest(
    ressurs: String,
    orgnr: String,
    status: String,
    tittel: String,
    sammendrag: String,
    url: String,
    knappTittel: String,
): CreateDialogRequest =
    CreateDialogRequest(
        serviceResource = "urn:altinn:resource:$ressurs",
        party = "urn:altinn:organization:identifier-no:$orgnr",
        status = status,
        externalRefererence = UUID.randomUUID().toString(),
        content = Content(lagContentValue(tittel), lagContentValue(sammendrag)),
        guiActions = listOf(lagGuiAction(url, knappTittel)),
        transmissions = emptyList(),
    )

fun lagNyDialogRequestMedSykmelding(
    ressurs: String,
    orgnr: String,
    dialogTittel: String,
    dialogSammendrag: String,
    sykmeldingId: String,
    sykmeldingJsonUrl: String,
): CreateDialogRequest =
    CreateDialogRequest(
        serviceResource = "urn:altinn:resource:$ressurs",
        party = "urn:altinn:organization:identifier-no:$orgnr",
        status = "New",
        externalRefererence = sykmeldingId,
        content = Content(lagContentValue(dialogTittel), lagContentValue(dialogSammendrag)),
        guiActions = emptyList(),
        transmissions = listOf(
            Transmission(
                type = Transmission.TransmissionType.Information,
                sender = Transmission.Sender("ServiceOwner"),
                content = Content(
                    title = lagContentValue("Sykmelding"),
                    summary = lagContentValue("Sykmelding")
                ),
                attachments = listOf(
                    Transmission.Attachment(
                        displayName = ContentValueItem("Sykmelding.json"),
                        urls = listOf(
                            Transmission.Url(
                                url = sykmeldingJsonUrl,
                                mediaType = "application/json",
                                consumerType = Transmission.AttachmentUrlConsumerType.Api
                            )
                        )
                    )
                )
            )
        )
    )
