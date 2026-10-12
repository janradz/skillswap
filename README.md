# SkillSwap

Java-Backend für den Austausch von Fähigkeiten und Nachhilfe zwischen Studierenden.
Angebote und Gesuche werden über eine REST-API verwaltet und anhand gemeinsamer
Termine sowie einer lokalen KI-Textbewertung zusammengeführt.

**Hochschul-Teamprojekt · Java 21 · Spring Boot · PostgreSQL · Keycloak · Spring AI**

## Funktionen

- REST-Controller, Service-Schicht, DTOs und JPA-Repositories.
- Angebote, Gesuche, Nutzerprofile und Matches mit Flyway-Datenbankschema.
- Spring Security mit Basic Auth und JWT; Keycloak als lokaler Identity Provider.
- Semantisches Matching über Ollama; Unit- und Integrationstests im Quellbaum.

## Eigener Beitrag

Mitentwicklung von Backend-Schnittstellen, Datenbankmigrationen und Authentifizierung sowie Erweiterung des KI-gestützten Matchings im Hochschul-Teamprojekt.

## Lokal starten

Voraussetzungen: JDK 21, Docker mit Compose und eine Bash-Shell. Der erste Start
benötigt Internetzugriff für Abhängigkeiten, Container und das Ollama-Modell.

```bash
cp .env.example .env
# Die Datei .env benötigt lokale Passwörter.
set -a
source .env
set +a
chmod +x gradlew
./gradlew bootRun
```

Swagger UI: http://localhost:8080/swagger-ui/index.html
Keycloak: http://localhost:8081 (Administrator-Passwort aus `.env`).
Der importierte Realm enthält ausschließlich Demo-Nutzer mit Passwort
`local-demo-only`. Diese dürfen nur für eine lokale Demonstration verwendet werden.
Neue Basic-Auth-Anwendungsnutzer werden über `/users/register` angelegt; die alten
SQL-Testkonten wurden entfernt. Die Datenbank ist auf Port 5433 erreichbar.
Die Compose-Ports sind an die lokale Loopback-Adresse gebunden.

## Tests und Grenzen

```bash
./gradlew test
```

Unit- und Integrationstests sind enthalten. Die veröffentlichte Fassung wurde beim Import nicht vollständig gebaut oder durch Integrationstests geprüft. Einige End-to-End-Tests setzen eine laufende Anwendung und passende Testdaten voraus.

Die Konfiguration ist für lokale Entwicklung gedacht.

## Herkunft

Hochschul-Teamprojekt der FH Münster. Projektstand ohne ursprüngliche GitLab-Historie; Autorenhinweise im Quellcode sind erhalten.
