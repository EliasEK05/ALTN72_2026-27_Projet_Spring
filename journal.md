# Journal — Supervision des équipements (ALTN72, 2026-27)

**Groupe :** Elias EL KHILALI, Ashley HARINAMBININA

## README

- **Prérequis :** Java 21, Maven (ou le wrapper `mvnw` fourni), MySQL 8.
- **Configuration :** les identifiants de la base ne sont pas versionnés. Deux possibilités :
  - variables d'environnement `DB_USERNAME` et `DB_PASSWORD` (et `DB_URL` si la base n'est pas `jdbc:mysql://localhost:3306/altyn72_supervision`) ;
  - ou un fichier `application-local.properties` à la racine du projet (ignoré par Git) :
    ```properties
    spring.datasource.username=...
    spring.datasource.password=...
    ```
- **Initialiser la base :**
  1. Créer la base : `CREATE DATABASE altyn72_supervision;`
  2. Lancer l'application une première fois : Hibernate crée les tables (`ddl-auto=update`).
  3. Exécuter `jeu-de-donnees.sql` : stations, équipements, prestataires, incidents (dates relatives à l'heure courante) et comptes de test.
- **Lancer :** `./mvnw spring-boot:run`, puis ouvrir http://localhost:8080
- **Application déployée :** …
- **Comptes de test :**

  | Profil | Login | Mot de passe |
  |---|---|---|
  | Responsable d'exploitation | `responsable` | `responsable123` |
  | Agent de terrain | `agent` | `agent123` |

- **API REST :** même login et mot de passe, en HTTP Basic (par exemple avec `requetes.http` ou Postman).
- **Limites connues :**
  - `jeu-de-donnees.sql` complète une base qui contient déjà les stations 1 à 3, les équipements 1 à 5 et les prestataires 1 à 3. Il doit encore être rendu autonome.
  - Les pages web ne couvrent pour l'instant que la connexion et l'accueil. Les autres fonctionnalités sont disponibles par l'API REST.
  - L'API n'est pas encore documentée avec Swagger / OpenAPI.
  - L'application n'est pas encore déployée.

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
      │                  └──► evenement/  IncidentModifieEvenement
      │                                   → JournalListener (journal métier)
      ▼
   repository/   Spring Data JPA (requêtes dérivées, JPQL, 1 native)
                         ▼
   modele/       entités JPA  ──►  MySQL
```

- **Packages complémentaires :** `dto/` (données échangées avec l'API), `mapper/` (entité ↔ DTO), `config/` (sécurité, horloge).

## TESTS

- **Stratégie :**
  - Tests unitaires des services avec Mockito. L'horloge (`Clock`) est injectée et figée : les seuils de 48 h et 72 h sont testés à la minute près.
  - Tests des contrôleurs avec `@WebMvcTest` et MockMvc : codes HTTP et messages d'erreur.
  - Tests du repository avec `@DataJpaTest` sur une base H2 en mémoire.
  - Tests des droits par profil : les vrais services sont chargés par Spring avec la sécurité active.
- **Commande :** `./mvnw test`
- **Couvert :**
  - Un incident non affecté ouvert depuis 47 h est vert ; à 48 h pile il est orange ; à 72 h pile il est rouge.
  - Un incident affecté a un état distinct (`AFFECTE`).
  - Un incident résolu n'apparaît plus dans les incidents actifs.
  - L'historique est trié du plus récent au plus ancien.
  - Un incident inexistant renvoie 404, une transition interdite 409, une donnée invalide 400, une action interdite au profil 403.
  - Évaluation : uniquement sur un incident résolu et affecté, une seule par incident, note de 1 à 5.
  - Connexion : bon et mauvais mot de passe, redirection vers la connexion, en-tête « Bonjour … », déconnexion.
  - Droits : l'agent ne peut ni affecter, ni résoudre, ni évaluer, ni consulter le tableau de bord ou la recommandation.
  - Lot alpha : recommandation de prestataires et journal métier.
- **Non couvert :**
  - `TpFilRougeApplicationTests` démarre toute l'application et a besoin de MySQL. Il échoue sans base.
  - Les pages web autres que la connexion et l'accueil (pas encore réalisées).
  - La date de résolution antérieure à l'ouverture (RG5) et le corps JSON mal formé n'ont pas de test dédié.

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
- Les restrictions sont appliquées côté serveur (RG11), sur les méthodes des services, avec `@ReserveAuResponsable` (un `@PreAuthorize`). Elles protègent donc à la fois l'API et les pages.
- Un refus renvoie 403 avec un message en français.

### Sécurité

- Spring Security : connexion par formulaire, sessions, mots de passe hachés en BCrypt.
- Toutes les pages et l'API demandent d'être connecté. Sinon, redirection vers `/connexion`.
- L'API accepte aussi le HTTP Basic, pour être appelée hors navigateur.
- Protection CSRF désactivée : les clients REST n'ont pas de jeton CSRF. C'est une simplification, acceptable ici car l'application est un projet pédagogique.
- Aucun secret dans le dépôt : identifiants de la base par variables d'environnement ou fichier local ignoré par Git.

### Exceptions

- Choix : `@RestControllerAdvice` (classe `GestionnaireExceptions`).
- Pourquoi :
  - toute la traduction exception → code HTTP est au même endroit ;
  - les services lèvent des exceptions métier sans rien savoir de HTTP. `ResponseStatusException` aurait mis du HTTP dans les services.
- Exceptions personnalisées, avec messages en français :
  - `RessourceIntrouvableException` (abstraite) et ses sous-classes `IncidentIntrouvableException`, `EquipementIntrouvableException`, `StationIntrouvableException`, `PrestataireIntrouvableException` → 404 ;
  - `TransitionIncidentInterditeException`, `EvaluationImpossibleException` → 409.
- Données invalides → 400 ; action interdite au profil → 403 ; erreur inattendue → 500 avec un message générique, sans trace technique.
- `ResponseEntity` est utilisé une seule fois, pour renvoyer 201 à la création d'un incident.

### SOLID

- **S — Responsabilité unique (appliqué) :**
  - `CalculateurNiveauAttention` ne fait que calculer la durée et le niveau d'attention. Les seuils de 48 h et 72 h n'existent qu'à cet endroit.
  - Les mappers convertissent entités et DTO ; les contrôleurs ne font que recevoir et répondre.
  - Le journal métier est écrit par `JournalListener`, pas par `IncidentService`.
  - Effet : chaque règle se modifie et se teste à un seul endroit.
- **D — Inversion des dépendances (appliqué) :**
  - Les services reçoivent leurs dépendances par le constructeur : repositories (interfaces Spring Data) et horloge `Clock`.
  - Effet : dans les tests, on remplace la base par des mocks et l'heure par une horloge figée.
- **O — Ouvert/fermé (en partie) :** `IncidentService` publie un événement à chaque changement. Ajouter une nouvelle réaction (par exemple une notification) se fait avec un nouvel écouteur, sans modifier le service.
- **L — Substitution de Liskov (peu pertinent) :** il y a très peu d'héritage. Le seul cas, les sous-classes de `RessourceIntrouvableException`, est traité de la même façon partout (404).
- **I — Ségrégation des interfaces (peu pertinent) :** nous n'avons pas créé nos propres interfaces. Les repositories sont déjà petits et propres à une entité.

### Requête SQL native

- Où : `IncidentRepository.compterIncidentsActifsParStation()`, pour l'indicateur « incidents actifs par station » du tableau de bord (F11).
- Avantages : SQL lisible, contrôle total du `GROUP BY`, résultat directement projeté dans `IncidentsActifsParStation`.
- Inconvénients :
  - elle dépend des noms de tables et de colonnes, pas des entités : renommer un champ ne la casse qu'à l'exécution ;
  - le statut `'RESOLU'` est écrit en dur, alors qu'il existe déjà dans l'enum `StatutIncident` ;
  - elle peut dépendre d'une base précise (ici le SQL est standard, il fonctionne aussi sur H2 dans les tests).
- Conclusion : une requête JPQL aurait suffi. La native n'est utile que pour du SQL que JPQL ne sait pas exprimer.

### @Transactional

- **Écritures** (`creerIncident`, `modifierDescription`, `affecterPrestataire`, `resoudre`, `evaluer`) : l'incident et l'événement du journal sont enregistrés ensemble, ou pas du tout (RG10).
- **Lectures** (`IncidentService`, `EquipementService`) : la transaction garde la session ouverte pendant la construction des DTO, qui lisent les liens paresseux (équipement, station, prestataire). Elles devraient passer en `readOnly = true`, comme dans `RecommandationService`.
- **Pas de transaction** dans les contrôleurs, ni dans `TableauDeBordService` qui ne fait qu'appeler des méthodes déjà transactionnelles.

### Horloge injectée

- Le niveau d'attention est calculé à chaque lecture à partir de l'heure courante. Il n'est jamais stocké en base.
- L'heure vient d'un bean `Clock` (`HorlogeConfig`), ce qui permet de la figer dans les tests.

## LOT ALPHA

### Fonctionnalité transverse 1 : aide à l'affectation (piste 3)

- **Besoin :** aider le responsable à choisir le bon prestataire pour un incident.
- **Périmètre :** proposer les 3 prestataires les plus adaptés, avec des arguments.
- **Choix :**
  - chaque prestataire a des compétences (types d'équipements) ;
  - on garde ceux qui sont compétents et disponibles (moins de 3 incidents en cours) ;
  - on les trie par note moyenne sur ce type d'équipement ;
  - chaque proposition donne ses arguments : compétence, note, nombre d'interventions résolues, incidents en cours.
- **Ce qui fonctionne :** `GET /incident/{id}/recommandation`, réservé au responsable. Refusé (409) pour un incident résolu.
- **Difficultés :** calculer la note sur un type d'équipement précis (requête JPQL avec jointures) ; placer les prestataires jamais évalués après les autres.
- **Reste à faire :** afficher la recommandation dans la page d'un incident.

### Fonctionnalité transverse 2 : journal métier (piste 5)

- **Besoin :** savoir qui a changé quoi, et quand, sur un incident.
- **Périmètre :** déclaration, modification de la description, affectation, résolution, évaluation.
- **Choix :**
  - une entité `EvenementIncident` (type, détails, auteur, date) ;
  - les services publient un événement Spring (`IncidentModifieEvenement`) ;
  - `JournalListener` l'enregistre. Il est synchrone : l'événement est écrit dans la même transaction que l'action (RG10).
- **Ce qui fonctionne :** `GET /incident/{id}/journal` renvoie les événements dans l'ordre chronologique. L'auteur est le login de l'utilisateur connecté.
- **Difficultés :** l'auteur valait « Système » tant que la connexion (F1) n'existait pas. Seul `UtilisateurCourant` a changé ensuite.
- **Reste à faire :** afficher le journal dans la page d'un incident.

## BILAN

- **Contribution de chacun :**
  - FN1, FN2, FN3, FNT3, FNT4 : Elias
  - FN5, FN6, FN7, FN8, FN9, FN10, FN11 : Ashley
- **Plus grande difficulté :** le niveau d'attention dépend de l'heure courante. Il était difficile à tester et à démontrer. Solution : injecter une horloge (`Clock`), la figer dans les tests, et générer les dates de démonstration par rapport à l'instant présent.
- **Fonctionnalités non réalisées et pourquoi :** …
- **Trois points à retenir :**
  1. Séparer les couches : les règles métier dans les services, jamais dans les contrôleurs.
  2. Un code testable se conçoit dès le départ (injection de dépendances, horloge injectée).
  3. La sécurité se vérifie côté serveur : masquer un bouton ne suffit pas.
