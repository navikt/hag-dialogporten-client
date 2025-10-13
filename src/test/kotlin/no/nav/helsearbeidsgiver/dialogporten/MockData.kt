package no.nav.helsearbeidsgiver.dialogporten

import no.nav.helsearbeidsgiver.dialogporten.domene.CreateDialogRequest
import no.nav.helsearbeidsgiver.utils.test.wrapper.genererGyldig
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr

object MockData {
    val createDialogRequest =
        CreateDialogRequest(
            externalReference = "external-reference",
            title = "dialog-title",
            summary = "dialog-summary",
            orgnr = Orgnr.genererGyldig(),
            transmissions = emptyList(),
            isApiOnly = true,
        )

    val gyldingRespons = "0194cb3a-6f4e-7707-a506-a1db2b5c37fa"

    val transmissionsJson =
        """
        [
               {
                   "id": "0194cb3a-6f4e-7707-a506-a1db2b5c37fa",
                   "type": "Information",
                   "extendedType": "extendedType",
                   "sender": {
                       "actorType": "actorType"
                   },
                   "content": {
                       "title": {
                           "value": [
                               {
                                   "value": "title",
                                   "languageCode": "nb"
                               }
                           ],
                           "mediaType": "text/plain"
                       }
                   }
               }
           ]
        """.trimIndent()
}
