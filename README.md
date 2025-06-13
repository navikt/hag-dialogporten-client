# hag-dialogporten-client

Klient for å opprette dialoger på dialogporten

For oversikt over altinn sitt dialogporten api se [altinn sin dokumentasjon](https://docs.altinn.studio/dialogporten/reference/openapi/)

### Forutsetninger
- Maskinporten klient/integrasjon med tilgang til scope: `digdir:dialogporten.serviceprovider`
- Veksle maskinporten token til altinnToken, se [altinn sin guide](https://docs.altinn.studio/api/scenarios/authentication/#exchange-of-jwt-token)
- Registrert ressurs i ressurs registeret til altinn

### Klienten kan brukes slik
```kt
// Funksjon for å hente en gyldig token fra altinn
fun getToken(): String = "gyldig token"

val dialogportenClient = DialogportenClient(
    baseUrl = "https://platform.tt02.altinn.no",
    ressurs = "nav_sykepenger_dialogporten",
    getToken = ::getToken
)

```
### Henvendelser

Spørsmål knyttet til koden kan rettes mot <helsearbeidsgiver@nav.no>.

### For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #helse-arbeidsgiver.

## Kode generert av GitHub Copilot

Dette repoet bruker GitHub Copilot til å generere kode.
