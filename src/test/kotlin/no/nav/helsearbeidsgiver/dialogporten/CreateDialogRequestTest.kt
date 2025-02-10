package no.nav.helsearbeidsgiver.dialogporten

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import no.nav.helsearbeidsgiver.utils.json.jsonConfig
import no.nav.helsearbeidsgiver.utils.test.resource.readResource

class CreateDialogRequestTest :
    FunSpec({
        test("Serialialiser request for person riktig") {
            val requestString = jsonConfig.encodeToString(CreateDialogRequest.serializer(), MockData.dialogRequest)
            requestString shouldContain "nav_sykepenger_inntektsmelding-nedlasting"
            requestString shouldContain "urn:altinn:organization:identifier-no:${MockData.orgnr}"
        }

        test("Serialize request correctly using lagCreateDialogRequest") {
            val request =
                lagCreateDialogRequest(
                    ressurs = "some-service-identifier",
                    orgnr = "123456789",
                    status = "active",
                    tittel = "Title",
                    sammendrag = "Summary",
                    url = "https://example.com",
                    knappTittel = "Read",
                )
            val requestString = jsonConfig.encodeToString(CreateDialogRequest.serializer(), request)
            requestString shouldContain "urn:altinn:resource:some-service-identifier"
            requestString shouldContain "urn:altinn:organization:identifier-no:123456789"
            requestString shouldContain "active"
            requestString shouldContain "Title"
            requestString shouldContain "Summary"
            requestString shouldContain "https://example.com"
            requestString shouldContain "Read"
        }

        test("Serialize request with GUI action correctly") {
            val guiAction =
                lagGuiAction(
                    "https://arbeidsgiver.intern.dev.nav.no/im-dialog/8e1ac9cc-34d4-4342-b865-3fb5fe9b154c",
                    "Gå til inntektsmeldingskjema på nav.no",
                )
            guiAction.title.first().languageCode shouldBe "nb"
            val requestString = jsonConfigEncodeDefaults.encodeToString(CreateDialogRequest.GuiAction.serializer(), guiAction)
            val forventetString = "create-dialog-request/correctGuiAction.json".readResource().normalizeJsonString()
            requestString shouldBe forventetString
        }
    })

fun String.normalizeJsonString(): String {
    val jsonElement = jsonConfigEncodeDefaults.parseToJsonElement(this)
    return jsonConfigEncodeDefaults.encodeToString(JsonObject.serializer(), jsonElement.jsonObject)
}
