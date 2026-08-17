@file:UseSerializers(UuidSerializer::class)

package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.UuidSerializer
import java.util.UUID

@Serializable
data class Activity(
    val type: ActivityType,
    val transmissionId: UUID? = null,
    val performedBy: Transmission.Sender? = null,
) {
    @Serializable
    enum class ActivityType {
        TransmissionOpened,
    }
}
