package no.nav.helsearbeidsgiver.dialogporten

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import no.nav.helsearbeidsgiver.utils.json.jsonConfig

class CreateDialogRequestTest :
    FunSpec({
        test("Serialialiser request for person riktig") {
            val requestString = jsonConfig.encodeToString(CreateDialogRequest.serializer(), MockData.dialogRequest)
            requestString shouldContain "nav_sykepenger_inntektsmelding-nedlasting"
            requestString shouldContain "urn:altinn:organization:identifier-no:${MockData.orgnr}"
        }
    })
