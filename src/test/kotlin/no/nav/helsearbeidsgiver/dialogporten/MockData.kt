package no.nav.helsearbeidsgiver.dialogporten

import no.nav.helsearbeidsgiver.dialogporten.domene.Content
import no.nav.helsearbeidsgiver.dialogporten.domene.Dialog
import no.nav.helsearbeidsgiver.dialogporten.domene.create
import java.util.UUID

object MockData {
    val orgnr = "123456789"
    val ressurs = "ressursnavn"
    val dialogMock =
        Dialog(
            serviceResource = ressurs,
            party = "party",
            externalRefererence = "external-reference",
            content = Content.create("title", null),
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
