package no.nav.helsearbeidsgiver.dialogporten

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import no.nav.helsearbeidsgiver.dialogporten.MockData.dialogRequest
import no.nav.helsearbeidsgiver.dialogporten.domene.CreateDialogRequest
import no.nav.helsearbeidsgiver.utils.json.jsonConfig

class DialogportenRequestUtilsTest :
    FunSpec({
        test("Serialialiser request for person riktig") {
            val requestString = jsonConfig.encodeToString(CreateDialogRequest.serializer(), dialogRequest)
            requestString shouldContain "nav_sykepenger_inntektsmelding-nedlasting"
            requestString shouldContain "urn:altinn:organization:identifier-no:${MockData.orgnr}"
        }

        test("Serialize request correctly using lagCreateDialogRequest") {
            val request = dialogRequest
            val requestString = jsonConfig.encodeToString(CreateDialogRequest.serializer(), request)
            requestString shouldContain "urn:altinn:resource:nav_sykepenger_inntektsmelding-nedlasting"
            requestString shouldContain "urn:altinn:organization:identifier-no:312824450"
            requestString shouldContain "testtittel"
            requestString shouldContain "testdialogsammendrag"
            requestString shouldContain "test-sykmelding-url.no"
        }
    })
