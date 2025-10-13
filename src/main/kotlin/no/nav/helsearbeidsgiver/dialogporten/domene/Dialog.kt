@file:UseSerializers(UuidSerializer::class)

package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.UuidSerializer
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr
import java.util.UUID

@Serializable
data class Dialog(
    val id: UUID? = null,
    val serviceResource: String,
    val party: String,
    val externalReference: String,
    val status: DialogStatus? = null,
    val content: Content,
    val transmissions: List<Transmission>,
    val isApiOnly: Boolean? = true,
)

@Serializable
data class CreateDialogRequest(
    val orgnr: Orgnr,
    val title: String,
    val summery: String,
    val externalReference: String,
    val isApiOnly: Boolean = true,
    val transmissions: List<Transmission>,
)
