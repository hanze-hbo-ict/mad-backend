# Inleiding

Dit document beschrijft de minimale stappen die nodig zijn om het backend deel woor de course *Mobile Application Development* draaiend te krijgen.

# Voorwaarden
Om met deze beschrijving en JHipster aan de slag te kunnen moet je het volgende op je computer hebben geïnstalleerd:

- Java JDK (minimaal versie 17)
- node.js/npm recente versie
- git (voor windows gebruikers is het aan te raden de git bash te gebruiken)

# Stappen

De backend kan te allen tijde opnieuw gegenereerd worden op basis van de `application.jdl`. Onderstaande gaat ervan uit dat JHipster is geïnstalleerd (zie [JHipster installatie](https://www.jhipster.tech/installation/)). In onderstaand voorbeeld wordt gebruik gemaakt van een lokaal (projectdomein) installatie en wordt `npx` gebruikt om de generator aan te roepen. Dit hoeft niet, je kunt zelf bepalen hoe je JHipster installeert. Volg de instructies op [de JHipster site](https://www.jhipster.tech/installation/).

Installeer JHipster 8.7.3 (laatste en getest werkende versie op moment van creatie) door in een lege map het volgende commando uit te voeren:

```bash
npm install generator-jhipster@8.7.3
```

- Kopieer `application.jdl` naar je projectmap.

- Genereer de applicatie:

```bash
npx jhipster jdl application.jdl
```

## Seed data

- Vervang de gegenereerde seed-data met de data uit de zip. Kopieer de `.csv`-bestanden en de `pictures` folder uit de directory `seeddata` naar `src/main/resources/config/liquibase/fake-data/`. Als alles goed is gegaan kun je nu de applicatie starten:

```
./mvnw
```

## Registratie waarbij ook Customer wordt aangemaakt
We maken gebruik van een externe authenticatie service, keycloak. Om ervoor te zorgen dat elke gebruiker ook een `Customer` is (en de twee gekoppeld zijn) moet er een aanpassing gedaan worden aan `nl.hanze.se4.automaat.service.UserService`. Vanaf regel 132, vervang dit:
```java
            LOG.debug("Saving user '{}' in local database", user.getLogin());
            userRepository.save(user);
```

Door dit:
```java
            LOG.debug("Saving user '{}' in local database", user.getLogin());
            userRepository.save(user);
            LOG.debug("Saving user '{}' as customer in local database", user.getLogin());
            Customer customer = new Customer()
                .systemUser(user)
                .from(LocalDate.now())
                .firstName(user.getFirstName())
                .lastName(user.getLastName());
            customerRepository.save(customer);
```

Om dit werkend te kunnen krijgen moet de `customerRepository` member aan de class en constructor worden toegevoegd (regel 41):
```java
...
    private final CustomerRepository customerRepository;

    public UserService(UserRepository userRepository, AuthorityRepository authorityRepository, CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.customerRepository = customerRepository;
    }
...
```

## Extra endpoint om de ingelogde Customer op te halen
Er is geen default endpoint om de ingelogde Customer op te halen, deze stap voegt dit endpoint toe;
- Copieer `AMCustomerResource.java` uit de `modifications` map van deze repo naar `src/main/java/nl/hanze/se4/automaat/web/rest/` van het gegenereerde project.
- Copieer `AMCustomerRepository.java` uit de `modifications` map van deze repo naar `src/main/java/nl/hanze/se4/automaat/repository/` van het gegenereerde project.

Je hebt nu een extra endpoint `/api/AM/me` waarop je het `Customer` object van de ingelogde gebruiker kan ophalen.

## Remote toegang configureren in Keycloak
Wanneer je vanuit je device inlogt op Automaat, benader je de backend op basis van hostname of ip adres. Dit adres zal je in keycloak moeten configureren om in te kunnen loggen. In het onderstaande voorbeeld gebruiken we een url die via `ngrok` is ontsloten (zie verderop in deze readme)
- Open de keycloak admin pagina (`admin`/`admin`): 

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

## Locale mail server
De applicatie maakt gebruik van email om gebruikers te verifieren of om wachtwoord reset links te sturen. Hiervoor kan je een lokale test mail server starten. In de [JHipster tips](https://www.jhipster.tech/tips/015_tip_local_smtp_server.html) wordt `maildev` geadviseerd vanwege het eenvoudige gebruik. Om deze binnen keycloak te kunnen gebruiken moet de Docker compose file voor keycloak worden aangepast:

Open `src/main/docker/keycloak.yml` en voeg de service `maildev` toe. Zet daarnaast de SMTP hostname op de zojuist toegevoegde service. De gehele `keycloak.yml` ziet er als volgt uit:

```yaml
# This configuration is intended for development purpose, it's **your** responsibility to harden it for production
name: automaat
services:
  maildev:
    image: maildev/maildev
    environment:
      - MAILDEV_SMTP_PORT=25
    ports:
      - 1080:1080
  keycloak:
    image: quay.io/keycloak/keycloak:25.0.1
    command: 'start-dev --import-realm'
    volumes:
      - ./realm-config:/opt/keycloak/data/import
      - ./realm-config/keycloak-health-check.sh:/opt/keycloak/health-check.sh
    environment:
      - KC_DB=dev-file
      - KEYCLOAK_ADMIN=admin
      - KEYCLOAK_ADMIN_PASSWORD=admin
      - KC_FEATURES=scripts
      - KC_HTTP_PORT=9080
      - KC_HTTPS_PORT=9443
      - KC_HEALTH_ENABLED=true
      - KC_HTTP_MANAGEMENT_PORT=9990
    # If you want to expose these ports outside your dev PC,
    # remove the "127.0.0.1:" prefix
    ports:
      - 127.0.0.1:9080:9080
      - 127.0.0.1:9443:9443
    healthcheck:
      test: 'bash /opt/keycloak/health-check.sh'
      interval: 5s
      timeout: 5s
      # Increased retries due to slow Keycloak startup in GitHub Actions using MacOS
      retries: 50
      start_period: 10s
    labels:
      org.springframework.boot.ignore: true
```

Maildev heeft een webinterface die met bovenstaand commando op poort 1080 luistert: http://localhost:1080

*Let op:* Het is de bedoeling dat iedereen met dezelfde backend werkt. Mocht je ideeën, aanvullingen of verbeteringen hebben voor deze backend, start dan een discussie op github of doe een pull request.
