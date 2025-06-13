package no.nav.helsearbeidsgiver.dialogporten.domene

import kotlinx.serialization.Serializable

@Serializable
data class ApiAction(
    val name: String,
    val endpoints: List<Endpoint>,
) {
    @Suppress("unused")
    val action = "read"

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
