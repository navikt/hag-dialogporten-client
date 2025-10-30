@file:UseSerializers(UuidSerializer::class)

package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.UuidSerializer

@Serializable
data class ApiAction(
    val action: String,
    val name: String,
    val endpoints: List<Endpoint>? = null,
) {
    @Serializable
    data class Endpoint(
        val url: String,
        val httpMethod: HttpMethod,
        val documentationUrl: String,
    )

    @Serializable
    enum class HttpMethod {
        GET,
        POST,
        PUT,
        DELETE,
        PATCH,
    }
}

enum class Action(
    val value: String,
) {
    READ("read"),
    WRITE("write"),
}

@Serializable
data class GuiAction(
    val action: String,
    val name: String,
    val url: String,
    val title: List<ContentValueItem>,
    val priority: Priority,
) {
    enum class Priority {
        Primary,
        Secondary,
        Tertiary,
    }
}
