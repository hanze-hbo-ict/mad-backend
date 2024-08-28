# Inleiding

Dit document beschrijft de minimale stappen die nodig zijn om het backend deel woor de course *Mobile Application Development* draaiend te krijgen.

# Voorwaarden
Om met deze beschrijving en JHipster aan de slag te kunnen moet je het volgende op je computer hebben geïnstalleerd:

- Java JDK (minimaal versie 17)
- node.js/npm recente versie
- git (voor windows gebruikers is het aan te raden de git bash te gebruiken)

# Stappen

De backend kan te allen tijde opnieuw gegenereerd worden op basis van de `application.jdl` en de `model.jdl`. Onderstaande gaat ervan uit dat JHipster is geïnstalleerd (zie [JHipster installatie](https://www.jhipster.tech/installation/)). In onderstaand voorbeeld wordt gebruik gemaakt van een lokaal (projectdomein) installatie en wordt `npx` gebruikt om de generator aan te roepen. Dit hoeft niet, je kunt zelf bepalen hoe je JHipster installeert. Volg de instructies op [de JHipster site](https://www.jhipster.tech/installation/).

Installeer JHipster 8.7.0 (laatste en getest werkende versie op moment van creatie) door in een lege map het volgende commando uit te voeren:

```bash
npm install generator-jhipster@8.7.0
```

- Kopieer `application.jdl` naar je projectmap.

- Genereer de applicatie:

```bash
npx jhipster jdl application.jdl
```

## Seed data

- Vervang de gegenereerde seed-data met de data uit de zip. Kopieer de `.csv`-bestanden uit de directory `seeddata` naar `src/main/resources/config/liquibase/fake-data/`. Als alles goed is gegaan kun je nu de applicatie starten:

```
./mvnw
```

## Registratie waarbij ook Customer wordt aangemaakt
De default code voor het registreren van een account luistert op endpoint `/api/registration` en creëert een User record. Voor de AutoMaat backend is een extra endpoint beschikbaar `/api/AM/registretion` waarmee niet alleen een User record wordt aangemaakt, maar ook een Customer record en de koppeling tussen deze twee. Om dit endpoint werkend te krijgen moeten de volgende acties uitgevoerd worden:
- Copieer `AMAccountResource.java` uit de `modifications` map van deze repo naar `src/main/java/nl/hanze/se4/automaat/web/rest/` van het gegenereerde project.
- Pas `/src/main/java/nl/hanze/se4/automaat/config/SecurityConfiguration.java` aan door de volgende regel toe te voegen:
```java
.requestMatchers(mvc.pattern("/api/AM/register")).permitAll()
```

Meest voor de hand liggende plek is meteen na de `"/api/register)).permitAll()"` zodat het er als volgt uitziet:
```java
.requestMatchers(mvc.pattern("/api/register")).permitAll()
.requestMatchers(mvc.pattern("/api/AM/register")).permitAll()
```

## Extra endpoint om de ingelogde Customer op te halen
Er is geen default endpoint om de ingelogde Customer op te halen, deze stap voegt dit endpoint toe;
- Copieer `AMCustomerResource.java` uit de `modifications` map van deze repo naar `src/main/java/nl/hanze/se4/automaat/web/rest/` van het gegenereerde project.
- Copieer `AMCustomerRepository.java` uit de `modifications` map van deze repo naar `src/main/java/nl/hanze/se4/automaat/repository/` van het gegenereerde project.

Je hebt nu een extra endpoint `/api/AM/me` waarop je het `Customer` object van de ingelogde gebruiker kan ophalen.

## REST Api Cars openzetten (optioneel)
Postgeneratie stap (al uitgevoerd in de `mad-backend-generated` repo):
Pas `src/main/java/nl/hanze/se4/automaat/config/SecurityConfiguration.java` aan: Voeg de regel `.requestMatchers(mvc.pattern("/api/cars")).permitAll()` toe tussen de andere `permitAll()`

## Ngrok
Om met een https-verbinding te werken kun je de applicatie achter een ngrok tunnel laten draaien. Bovenstaand commando start de server in development mode, luisterend op poort 8080.

De volgende stappen beschrijven hoe je ngrok aan de praat kan krijgen:
1. Lees de handleiding en installeer ngrok [ngrok](https://ngrok.com/docs/getting-started/)
2. Creëer een [vast domein](https://dashboard.ngrok.com/cloud-edge/domains) (zodat je niet elke keer een nieuwe, random domeinnaam krijgt)
3. Pas `src/main/resources/config/application-dev.yml aan` zodat CORS requests geaccepteerd worden vanuit het zojuist gecreëerde domein. Vul daarvoor de regel `allowed-origins` (onder jhipster/cors) aan met het ngrok domein: `allowed-origins: 'https://ladybird-sharp-alpaca.ngrok-free.app,http://localhost:8100,https://localhost:8100,http://localhost:9000,https://localhost:9000,http://localhost:4200,https://localhost:4200'` In mijn geval is het `https://ladybird-sharp-alpaca.ngrok-free.app` nieuw. Let op de komma!
4. Start ngrok, gebruik makend van het vaste domein:

```bash
ngrok http 8080 --domain ladybird-sharp-alpaca.ngrok-free.app
```

*Let op:* Het is de bedoeling dat iedereen met dezelfde backend werkt. Mocht je ideeën, aanvullingen of verbeteringen hebben voor deze backend, start dan een discussie op github of doe een pull request.
