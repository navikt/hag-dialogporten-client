package no.nav.helsearbeidsgiver.dialogporten.domene

import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

// stjålet fra SAS sin spleis kode
// https://github.com/navikt/helse-spleis/blob/main/sykepenger-model/src/main/kotlin/no/nav/helse/UUIDV7.kt

@OptIn(ExperimentalUuidApi::class)
internal fun nyUuidv7(): UUID = Uuid.generateV7().toJavaUuid()

fun UUID.erUuidv7(): Boolean = version() == 7

fun UUID?.requireGyldigUuidv7orNull() = require(this?.erUuidv7() ?: true) { "Må være gyldig UUIDv7 eller null, men var $this" }
