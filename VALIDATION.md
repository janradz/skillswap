# Prüfung am 8. Oktober 2026

Statische Prüfung der bereinigten Dateien einschließlich Gradle-Wrapper-Archiv.
Keycloak-JSON lässt sich parsen; exportierte private Schlüssel und ursprüngliche
Passwort-Hashes wurden entfernt. Demo-Realm-Zugangsdaten sind explizit als lokal
gekennzeichnet. Anwendungspasswörter kommen aus Umgebungsvariablen.

Build und Tests nicht ausgeführt: nur Java 17 verfügbar, erforderlich ist Java 21;
Docker fehlt. Die Entfernung der SQL-Demokonten und der neue Realm-Import sind
noch nicht durch einen Integrationstest bestätigt. Flyway-Änderung nur für eine
frische lokale Demo-Datenbank verwenden, nicht für bestehende Installationen.
