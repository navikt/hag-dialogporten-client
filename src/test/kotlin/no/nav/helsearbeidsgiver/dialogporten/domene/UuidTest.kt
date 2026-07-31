package no.nav.helsearbeidsgiver.dialogporten.domene

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import java.util.UUID

class UuidTest :
    FunSpec({
        test("nyUuidv7 genererer en gyldig UUIDv7") {
            repeat(50) {
                nyUuidv7().erUuidv7().shouldBeTrue()
            }
        }

        test("erUuidv7 er true for kjent UUIDv7") {
            UUID.fromString("0194cb3a-6f4e-7707-a506-a1db2b5c37fa").erUuidv7().shouldBeTrue()
        }

        test("erUuidv7 er false for UUIDv4") {
            UUID.randomUUID().erUuidv7().shouldBeFalse()
        }
    })
