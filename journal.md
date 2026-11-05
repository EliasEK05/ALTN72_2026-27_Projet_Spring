    # Journal — Supervision des équipements (ALTN72, 2026-27)

**Groupe :** Elias EL KHILALI, Ashley HARINAMBININA

## README

- **Prérequis :** Java 21 (ou plus récent), Maven (ou le wrapper `mvnw` fourni), MySQL 8.
- **Configuration :** les identifiants de la base ne sont pas dans le dépôt. Deux possibilités :
  - copier `application-local.properties.example` en `application-local.properties` (à la racine, ignoré par Git) et y mettre ses identifiants MySQL :
    ```properties
    DB_USERNAME=root
    DB_PASSWORD=votre_mot_de_passe
    ```
  - ou définir les variables d'environnement `DB_USERNAME` et `DB_PASSWORD` (et `DB_URL` si la base n'est pas `jdbc:mysql://localhost:3306/altyn72_supervision`)
- **Initialiser la base :** `mysql -u root -p < db/create.sql`
  - Le script supprime puis recrée la base `altyn72_supervision`, ses tables et les données de démonstration (stations, équipements, prestataires, incidents et comptes de test)
  - Les dates des incidents sont calculés par rapport à l'heure actuelle. On peut relancé le script juste avant une démonstration pour retrouver les cas 47 h (vert), 48 h (orange) et 72 h (rouge)
- **Lancer :** `./mvnw spring-boot:run`, puis ouvrir http://localhost:8080
- **Tests :** `./mvnw verify` (pas besoin de MySQL, voir la partie TESTS).
- **Documentation de l'API :** http://localhost:8080/swagger-ui.html (après connexion)
- **Application déployée :** https://altn722026-27projetspring-production.up.railway.app
- **Comptes de test :**

  | Profil | Login | Mot de passe |
  |---|---|---|
  | Responsable d'exploitation | `responsable` | `responsable123` |
  | Agent de terrain | `agent` | `agent123` |

- **API REST :** même login et mot de passe, en HTTP Basic (par exemple avec `requetes.http` ou Postman)
- **Limites connues :**
  - Les couleurs dépendent de l'heure actuelle : si on attend plusieurs jours sans relancer `db/create.sql`, les incidents de démonstration passent tous au rouge.
  - Seules les routes de l'API sous `/incident/**` sont dispensées de jeton de sécurité

## ARCHITECTURE

- **Dépôt :** https://github.com/EliasEK05/ALTN72_2026-27_Projet_Spring
- **Schéma :**

```
 Navigateur (Thymeleaf)        Client REST (Postman, requetes.http)
          │                                  │
          └──────────────┬───────────────────┘
                         ▼
              Spring Security (SecuriteConfig)
              connexion, mots de passe BCrypt
                         ▼
   controleur/   @Controller (pages)  et  @RestController (API)
   exception/    GestionnaireExceptions : exception → code HTTP
                         ▼
   service/      règles métier (RG1 à RG11), @Transactional,
                 droits par profil (@ReserveAuResponsable)
      │                  │
      │                  ---> evenement/  IncidentModifieEvenement
      │                                   --> JournalListener (journal métier)
      ▼
   repository/   Spring Data JPA (requêtes dérivées, JPQL, 1 native)
                         ▼
   modele/       entités JPA  -->  MySQL
```

- **Packages complémentaires :** `dto/` (données échangées avec l'API), `mapper/` (entité <--> DTO), `config/` (sécurité, horloge)

## TESTS

- **Stratégie :**
  - Tests unitaires des services avec Mockito. L'horloge (`Clock`) est injectée et figée : les seuils de 48 h et 72 h sont testés à la minute près.
  - Tests des contrôleurs avec `@WebMvcTest` et MockMvc : codes HTTP et messages d'erreur
  - Tests du repository avec `@DataJpaTest` sur une base H2 en mémoire.
  - Tests des droits par profil : les vrais services sont chargés par Spring avec la sécurité active.
  - Tests des pages avec MockMvc : affichage, filtres du tableau de bord, messages d'erreur.
- **Commande :** `./mvnw verify`. Aucune base MySQL n'est nécessaire : les tests qui démarrent l'application utilisent le profil `test` et une base en mémoire
- **Couvert :**
  - Un incident non affecté ouvert depuis 47 h est vert ; à 48 h pile il est orange ; à 72 h pile il est rouge.
  - Un incident affecté a un état distinct (`AFFECTE`).
  - Un incident résolu n'apparaît plus dans les incidents actifs.
  - L'historique est trié du plus récent au plus ancien.
  - Un incident inexistant renvoie 404, une transition interdite 409, une donnée invalide 400, une action interdite au profil 403.
  - Évaluation : uniquement sur un incident résolu et affecté, une seule par incident, note de 1 à 5.
  - Connexion : bon et mauvais mot de passe, redirection vers la connexion, en-tête « Bonjour … », déconnexion.
  - Un formulaire de page envoyé sans jeton est refusé (403)
  - Droits : l'agent ne peut ni affecter, ni résoudre, ni évaluer, ni consulter le tableau de bord ou la recommandation.
  - Lot alpha : recommandation de prestataires et journal métier.
- **Non couvert :**

## CHOIX TECHNIQUES

### Droits par profil

| N° | Fonctionnalité | Responsable d'exploitation | Agent de terrain |
|---|---|---|---|
| F2–F3 | Consulter équipements et fiches | Accès complet | Accès complet |
| F4 | Déclarer un incident | Accès complet | Accès complet |
| F5–F6 | Consulter / modifier un incident | Accès complet | Accès complet |
| F7 | Affecter ou réaffecter un prestataire | Accès complet | Interdit |
| F8 | Déclarer un incident résolu | Accès complet | Interdit |
| F9 | Consulter l'historique | Accès complet | Lecture seule |
| F10 | Évaluer un prestataire | Accès complet | Interdit |
| F11 | Tableau de bord | Accès complet | Interdit |

- L'agent signale les pannes et suit les incidents. Le responsable pilote : il affecte, clôture, évalue et supervise.
- F6 : seule la description est modifiable, tant que l'incident n'est pas résolu. Les autres changements passent par F7 et F8.
- Les restrictions sont appliqués côté serveur, sur les méthodes des services, avec `@ReserveAuResponsable`. Elles protègent donc à la fois l'API et les pages.
- Un refus renvoie 403 avec un message dédié

### Sécurité

- Spring Security : connexion par formulaire, sessions, mots de passe hachés en BCrypt.
- Toutes les pages et l'API demandent d'être connecté. Sinon, redirection vers `/connexion`.
- L'API accepte aussi le HTTP Basic, pour être appelée hors navigateur.
- Protection active sur les pages : chaque formulaire Thymeleaf (`th:action`) contient un jeton, et un formulaire envoyé sans jeton est refusé (403). Seule l'API REST (`/incident/**`) en est exemptée, car ses clients (Postman, `requetes.http`) s'authentifient en HTTP Basic et n'ont pas de jeton
- Aucun secret dans le dépôt : identifiants de la base par variables d'environnement

### Exceptions

- Choix : `@RestControllerAdvice` (classe `GestionnaireExceptions`).
- Raison : toute la traduction exception --> code HTTP est au même endroit ;
- Exceptions personnalisées, avec messages en français :
  - `RessourceIntrouvableException` (abstraite) et ses sous-classes `IncidentIntrouvableException`, `EquipementIntrouvableException`, `StationIntrouvableException`, `PrestataireIntrouvableException` --> 404 ;
  - `TransitionIncidentInterditeException`, `EvaluationImpossibleException` → 409.
- Données invalides --> 400 ; action interdite au profil --> 403 ; erreur inattendue --> 500 avec un message générique, sans trace technique.
- `ResponseEntity` est utilisé une seule fois, pour renvoyer 201 à la création d'un incident.

### SOLID

- **S — Responsabilité unique (appliqué) :**
  - `CalculateurNiveauAttention` ne fait que calculer la durée et le niveau d'attention. Les seuils de 48 h et 72 h n'existent qu'à cet endroit.
  - Les mappers convertissent entités et DTO ; les contrôleurs ne font que recevoir et répondre.
  - Le journal métier est écrit par `JournalListener`, pas par `IncidentService`.
  - Effet : chaque règle se modifie et se teste à un seul endroit.
- **O — Ouvert/fermé (partiellement) :** `IncidentService` publie un événement à chaque changement. Ajouter une nouvelle réaction (par exemple une notification) se fait avec un nouvel écouteur, sans modifier le service.
- **L — Substitution de Liskov (peu pertinent) :** il y a très peu d'héritage. Le seul cas, les sous-classes de `RessourceIntrouvableException`, est traité de la même façon partout (404).
- **I — Ségrégation des interfaces (peu pertinent) :** nous n'avons pas créé nos propres interfaces. Les repositories sont déjà petits et propres à une entité.
- **D — Inversion des dépendances (appliqué) :**
- Les services reçoivent leurs dépendances par le constructeur : repositories et horloge `Clock`.
- Effet : dans les tests, on remplace la base par des mocks et l'heure par une horloge figée.

### Requête SQL native

- Où : `IncidentRepository.compterIncidentsActifsParStation()`, pour l'indicateur "incidents actifs par station" du tableau de bord.
- Avantages : SQL lisible, contrôle total du `GROUP BY`, résultat directement projeté dans `IncidentsActifsParStation`.
- Inconvénients :
  - elle dépend des noms de tables et de colonnes, pas des entités : renommer un champ ne la casse qu'à l'exécution ;
  - le statut `'RESOLU'` est écrit en dur, alors qu'on l'a déjà dans l'enum `StatutIncident` ;
  - elle peut dépendre d'une base précise (ici le SQL est standard, il fonctionne aussi dans les tests).

### @Transactional

- **Écritures** (`creerIncident`, `modifierDescription`, `affecterPrestataire`, `resoudre`, `evaluer`) : l'incident et l'événement du journal sont enregistrés ensemble
- **Lectures** (`IncidentService`, `EquipementService`) : la transaction garde la session ouverte pendant la construction des DTO, qui lisent les liens (équipement, station, prestataire)
- **Pas de transaction** dans les contrôleurs, ni dans `TableauDeBordService` qui ne fait qu'appeler des méthodes déjà transactionnelles.

### Horloge injectée

- Le niveau d'attention est calculé à chaque lecture à partir de l'heure courante. Il n'est jamais stocké en base.
- L'heure vient d'un bean `Clock` (`HorlogeConfig`), ce qui permet de la figer dans les tests.

## LOT ALPHA

### Fonctionnalité transverse 1 : aide à l'affectation (3)

- **Besoin :** aider le responsable à choisir le bon prestataire pour un incident.
- **Périmètre :** proposer les 3 prestataires les plus adaptés, avec des arguments.
- **Choix :**
  - chaque prestataire a des compétences (types d'équipements) ;
  - on garde ceux qui sont compétents et disponibles (moins de 3 incidents en cours) ;
  - on les trie par note moyenne sur ce type d'équipement ;
  - chaque proposition donne ses arguments : compétence, note, nombre d'interventions résolues, incidents en cours.
- **Ce qui fonctionne :** les propositions s'affichent dans la page d'un incident non résolu, et le responsable peut affecter un prestataire en un clic. Aussi disponible par l'API : `GET /incident/{id}/recommandation`, réservé au responsable, refusé (409) pour un incident résolu.
- **Difficultés :** calculer la note sur un type d'équipement précis (requête JPQL avec jointures) ; placer les prestataires jamais évalués après les autres.
- **Limites :** les compétences des prestataires ne se modifient que dans la base, et le seuil de 3 incidents en cours est une constante du code.

### Fonctionnalité transverse 2 : journal métier (5)

- **Besoin :** savoir qui a changé quoi, et quand, sur un incident.
- **Périmètre :** déclaration, modification de la description, affectation, résolution, évaluation.
- **Choix :**
  - une entité `EvenementIncident` (type, détails, auteur, date) ;
  - les services publient un événement Spring (`IncidentModifieEvenement`) ;
  - `JournalListener` l'enregistre. Il est synchrone : l'événement est écrit dans la même transaction que l'action
- **Ce qui fonctionne :** l'historique des changements s'affiche dans la page de chaque incident, du plus ancien au plus récent. L'auteur est le login de l'utilisateur connecté. Aussi disponible par l'API : `GET /incident/{id}/journal`.
- **Limites :** les détails de chaque événement sont enregistrés sous forme de texte. On peut les lire, mais pas filtrer ni faire de statistiques dessus.

## BILAN

- **Contribution de chacun :**
  - Elias : F1, F2, F3, les pages Thymeleaf, la documentation Swagger, le script `db/create.sql` et les deux fonctionnalités du lot alpha.
  - Ashley : F4 à F11 côté serveur (cycle de vie des incidents, évaluation, tableau de bord), les exceptions personnalisées et les tests des incidents.
- **Plus grande difficulté :** la gestion des exceptions, et la mise en place des tests unitaires car nous ne sommes pas habitués
- **Fonctionnalités non réalisées et pourquoi :** toutes les fonctionnalités ont été réalisées
- **Trois points à retenir :**
  1. Séparer les couches : les règles métier dans les services, jamais dans les contrôleurs.
  2. Un code testable se conçoit dès le départ (injection de dépendances, horloge injectée).
  3. La sécurité se vérifie côté serveur : masquer un bouton ne suffit pas.
