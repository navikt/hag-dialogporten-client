package no.nav.helsearbeidsgiver.dialogporten

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.headersOf
import io.mockk.every
import no.nav.helsearbeidsgiver.utils.test.mock.mockStatic

fun mockDialogportenClient(
    status: HttpStatusCode,
    content: String = "",
): DialogportenClient = mockDialogportenClientMedRespons(status, content).first

fun mockDialogportenClientMedRespons(
    status: HttpStatusCode,
    content: String = "",
): Pair<DialogportenClient, () -> String> {
    var sisteRequestBody = ""
    val mockEngine =
        MockEngine { request ->
            sisteRequestBody =
                when (val body = request.body) {
                    is OutgoingContent.ByteArrayContent -> body.bytes().decodeToString()
                    else -> ""
                }
            respond(
                content = content,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
    val mockHttpClient = HttpClient(mockEngine) { configure(1) { "" } }
    val client =
        mockStatic(::createHttpClient) {
            every { createHttpClient(any(), any()) } returns mockHttpClient
            DialogportenClient(baseUrl = "url", ressurs = "ressurs", getToken = { "" })
        }
    return client to { sisteRequestBody }
}
