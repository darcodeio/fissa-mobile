# FISSA KOTLIN

Application mobile **native Android (Kotlin)** pour FISSA — planification de parcours en transport en commun (Tunisie / SNCFT), destinée à terme à remplacer `fissa_flutter`.

## Contexte projet

Voir `/home/medbes/Projects/FISSA/CLAUDE.md` pour l'architecture générale (Roda / OTP2 / Photon / PostgreSQL).

Structure du projet parent :
- `fissa_mobile/fissa_kotlin` — **ce projet** : nouveau client mobile natif Android
- `fissa_mobile/fissa_flutter` — client mobile Flutter existant, **destiné à être remplacé** par ce projet à terme
- `fissa_roda` — API backend Ruby/Roda (auth rodauth JWT, proxy Photon, proxy OTP2)
- `fissa_back` — backend complémentaire

Ce projet coexiste avec `fissa_flutter` pendant la transition ; pas de régression de fonctionnalité côté Roda tant que la bascule n'est pas actée.

## Roadmap fonctionnelle

1. **Phase 1 (en cours)** : planification d'itinéraire train + marche à pied.
2. **Phase 2** : ajout tramway et bus (multimodal complet, via OTP2).

## Stack technique

| Domaine | Choix | Notes |
|---|---|---|
| Langage | Kotlin | |
| UI | Jetpack Compose | pas de XML/View Binding |
| Architecture | MVVM + Repository | ViewModel + StateFlow, Repository pour l'accès réseau |
| Réseau | Retrofit + OkHttp | consomme l'API JSON `fissa_roda` |
| Asynchrone | Coroutines + Flow | |
| Injection de dépendances | Hilt | |
| Géolocalisation | FusedLocationProviderClient (Google Play Services) | permission localisation précise demandée au lancement de l'écran d'accueil ; fallback sur une liste par défaut si refus/indisponible |

## Communication avec le backend

- Réutilise le même mécanisme d'identification que Flutter : UUID généré au premier lancement, stocké localement, envoyé via `POST /api/v1/devices`, inclus dans toutes les requêtes API.
- Les prochains passages de train par gare passent par un **nouvel endpoint Roda dédié** (à créer côté `fissa_roda`, ex. `GET /api/v1/stations/nearby-departures`) qui interroge OTP2 et agrège les prochains passages dans les deux directions par gare — le mobile n'orchestre pas plusieurs appels `/api/v1/trip` pour ça.

## Écran d'accueil (MVP, sans inscription/connexion)

- Accessible sans compte ni connexion.
- Demande la permission de localisation précise pour identifier les gares les plus proches de l'utilisateur.
- Affiche la liste des gares les plus proches, et pour chaque gare : le prochain passage de train dans **chacune des deux directions**.
- Pas de favoris, pas d'historique, pas d'itinéraire à ce stade — ça viendra dans un écran de recherche/planification séparé.

## État du squelette

Généré : structure Gradle (`settings.gradle.kts`, `build.gradle.kts`, module `app`), `AndroidManifest.xml`, DI Hilt (`NetworkModule`), Retrofit (`RodaApi` + DTOs), `StationsRepository`, `HomeViewModel` + `HomeScreen` (Compose) avec états Loading/Success/Error, `MainActivity` gérant la permission de localisation avec fallback (gare de Tunis par défaut), et un test unitaire du ViewModel.

**Build validé** : `./gradlew :app:assembleDebug` et `:app:installDebug` passent, l'app s'installe et se lance sur un téléphone physique (testé sur ALT-NX1 via `adb`).

Deux ajustements faits après la première génération :
- Kotlin/KSP/plugin Compose passés de 1.9.24 à **2.0.21** (`org.jetbrains.kotlin.plugin.compose` n'existe qu'à partir de Kotlin 2.0 — la 1.9.24 ne le résout pas).
- `TopAppBar` (Material3) nécessite `@OptIn(ExperimentalMaterial3Api::class)` sur `HomeScreen`.

**Endpoint backend implémenté** : `GET /api/v1/stations/nearby-departures` existe désormais côté `fissa_roda` (GraphQL vers OTP2, filtré train uniquement) — voir `fissa_roda/CLAUDE.md` section "Stations". Réponse `[ { id, name, distanceMeters, departures: [{ direction, nextDepartureAt }] } ]`, clés camelCase exactement alignées sur `NearbyStationDto`/`DirectionDepartureDto` (Gson sans naming strategy). `distanceMeters` est la distance de **marche réelle** (calculée via OTP2, mode WALK), pas la distance à vol d'oiseau — avec repli sur la distance à vol d'oiseau si OTP2 ne trouve pas d'itinéraire piéton.

Restant à faire :
- `RODA_BASE_URL` dans `NetworkModule.kt` pointe sur `10.0.2.2:9292` (adresse spéciale émulateur → localhost). Sur téléphone physique il faut l'IP réseau locale de la machine hébergeant `fissa_roda` (ex. `192.168.x.x:9292`) ; à terme externaliser par build type (dev/staging/prod).
- Le jeu de données GTFS chargé dans l'instance OTP2 de dev a une période de validité expirée (`serviceTimeRange` se terminant avant la date courante) — l'endpoint répond `200 []` tant qu'il n'est pas rafraîchi. Pas un bug côté client ni côté route.
- Aucune icône de lancement (`mipmap/ic_launcher`) fournie — `android:icon` volontairement omis du manifest pour l'instant.
- Un projet non lié nommé `fissa_mobile/fissa_native` existe dans le repo (Expo/React Native, git séparé) — ne pas confondre avec ce projet lors des builds.
