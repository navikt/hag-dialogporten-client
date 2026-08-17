@file:UseSerializers(UuidSerializer::class)

package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.UuidSerializer
import java.util.UUID

@Serializable
data class Dialog(
    val id: UUID?,
    val serviceResource: String,
    val party: String,
    val externalReference: String,
    val idempotentKey: String?,
    val status: DialogStatus? = null,
    val content: Content,
    val transmissions: List<Transmission>,
    val isApiOnly: Boolean = false,
    val attachments: List<Attachment>,
) {
    init {
        id.requireGyldigUuidv7orNull()
    }
}
