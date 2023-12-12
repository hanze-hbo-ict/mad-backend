# Inleiding

Dit document beschrijft de minimale stappen die nodig zijn om het backend deel woor de course *Mobile Application Development* draaiend te krijgen.

# Voorwaarden
Om met deze beschrijving en JHipster aan de slag te kunnen moet je het volgende op je computer hebben geïnstalleerd:

- Java JDK (minimaal versie 17)
- node.js/npm recente versie
- git (voor windows gebruikers is het aan te raden de git bash te gebruiken)

# Stappen

De backend kan te allen tijde opnieuw gegenereerd worden op basis van de `application.jdl` en de `model.jdl`. Onderstaande gaat ervan uit dat JHipster is geïnstalleerd (zie [JHipster installatie](https://www.jhipster.tech/installation/)). In onderstaand voorbeeld wordt gebruik gemaakt van een lokaal (projectdomein) installatie en wordt `npx` gebruikt om de generator aan te roepen. Dit hoeft niet, je kunt zelf bepalen hoe je JHipster installeert. Volg de instructies op [de JHipster site](https://www.jhipster.tech/installation/).

Installeer JHipster door in een lege map het volgende commando uit te voeren:

```bash
npm install generator-jhipster
```

LET OP! In versie 8.0.0 zit een bug die foutieve testclasses genereert. Zie [hieronder](#oplossing-voor-compilatiefout) voor de workaround.

- Kopieer de bestanden `application.jdl` en `model.jdl` naar je projectmap.

- Genereer eerst de applicatie zelf:

```bash
npx jhipster jdl application.jdl
```

- Daarna genereer je het model en alle verwante (al gegenereerde) objecten.

```bash
npx jhipster jdl model.jdl
```

## Oplossing voor compilatiefout

Voeg deze import:
``` java
import static org.mockito.Mockito.when;
```

Toe aan `nl.hanze.se4.automaat.service.UserServiceIT` (te vinden in `/src/test/java/`). Nu zou de applicatie moeten builden.

### Seed data

- Vervang de gegenereerde seed-data met de data uit de zip. Kopieer de `.csv`-bestanden uit de directory `seeddata` naar `src/main/resources/config/liquibase/fake-data/`. Als alles goed is gegaan kun je nu de applicatie starten:

```
./mvnw
```

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

## Docker
Het is ook mogelijk om het project in een docker container op te starten, het voordeel hiervan is dat je niet locaal Java en Jhipster hoeft te hebben.
Het bouwen van de image kan een paar (5-10) minuten duren, dus heb geduld. Je moet nog wel zelf Ngrok installeren.

```bash
docker build -t mad-backend:latest .
docker run -it -p 8080:8080 mad-backend:latest
```

*Let op:* Het is de bedoeling dat iedereen met dezelfde backend werkt. Mocht je ideeën, aanvullingen of verbeteringen hebben voor deze backend, start dan een discussie op github of doe een pull request.
