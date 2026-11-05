# Journal - Supervision des équipements (ALTN72, 2026-27)

**Groupe :** Elias EL KHILALI, Ashley HARINAMBININA

## README

- **Prérequis :** Java 21, Maven (ou le wrapper `mvnw` fourni), MySQL 8.
- **Configuration :** les identifiants de la base ne sont pas versionnés. Deux possibilités :
  - variables d'environnement `DB_USERNAME` et `DB_PASSWORD` (et `DB_URL` si la base n'est pas `jdbc:mysql://localhost:3306/altyn72_supervision`) ;
  - ou un fichier `application-local.properties` à la racine du projet (ignoré par Git), à créer en copiant `application-local.properties.example`.
- **Initialiser la base :** exécuter `db/create.sql`, par exemple `mysql -u root -p < db/create.sql` ou depuis MySQL Workbench.
  - Il supprime puis recrée la base `altyn72_supervision`, ses tables et les données de démonstration, en moins d'une seconde.
  - Les dates sont relatives à l'instant présent. Le rejouer juste avant une démonstration pour retrouver les cas limites du niveau d'attention : 47 h (vert), 48 h et 71 h (orange), 72 h (rouge)
- **Lancer :** `./mvnw spring-boot:run`, puis ouvrir http://localhost:8080 (le port peut être changé avec la variable `PORT`)
- **Application déployée :** https://altn722026-27projetspring-production.up.railway.app
- **Comptes de test :**

  | Profil | Login | Mot de passe |
  |---|---|---|
  | Responsable d'exploitation | `responsable` | `responsable123` |
  | Agent de terrain | `agent` | `agent123` |

- **API REST :** même login et mot de passe, en HTTP Basic. `requetes.http` contient une requête par fonctionnalité, avec le code HTTP attendu.
- **Documentation de l'API :** http://localhost:8080/swagger-ui.html, après connexion (lien "API" dans le menu)
- **Limites connues :**
  - Les données de démonstration vieillissent avec le temps : un incident "47 h" devient orange une heure plus tard. Il suffit de rejouer `db/create.sql`.
  - Les stations, équipements, prestataires et utilisateurs ne sont pas modifiables depuis l'application : ils viennent du script.
  - Les indicateurs du tableau de bord portent toujours sur tout le réseau ; les filtres s'appliquent à la liste des incidents.

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
   controleur/   @RestController (API)  et  controleur/page/ @Controller (pages)
   exception/    GestionnaireExceptions : exception → code HTTP (API)
                 GestionnaireExceptionsPages : exception → page lisible
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

- **Packages complémentaires :** `dto/` (données échangées avec l'API), `mapper/` (entité <--> DTO), `config/` (sécurité, horloge, documentation de l'API).
- **Pages et API :** les pages (`/supervision`, `/reseau`, `/incidents/...`) et l'API (`/incident`, `/tableau-de-bord`...) appellent les mêmes services. Les règles métier et les droits ne sont donc écrits qu'une fois.

## TESTS

- **Stratégie :**
  - Tests unitaires des services avec Mockito. L'horloge (`Clock`) est injectée et figée : les seuils de 48 h et 72 h sont testés à la minute près.
  - Tests des contrôleurs avec `@WebMvcTest` et MockMvc : codes HTTP et messages d'erreur.
  - Tests du repository avec `@DataJpaTest` sur une base H2 en mémoire.
  - Tests des droits par profil : les vrais services sont chargés par Spring avec la sécurité active.
  - Tests des pages web avec `@WebMvcTest` : affichage, filtres, page d'accès refusé, page introuvable, action refusée.
  - Les tests qui démarrent toute l'application utilisent une base H2 en mémoire (profil `test`) : `mvn verify` passe sans MySQL, sur n'importe quelle machine.
- **Commande :** `./mvnw verify`
- **Couvert :**
  - Un incident non affecté ouvert depuis 47 h est vert ; à 48 h pile il est orange ; à 72 h pile il est rouge.
  - Un incident affecté a un état distinct (`AFFECTE`).
  - Un incident résolu n'apparaît plus dans les incidents actifs.
  - L'historique est trié du plus récent au plus ancien.
  - Un incident inexistant renvoie 404, une transition interdite 409, une donnée invalide 400, une action interdite au profil 403.
  - Évaluation : uniquement sur un incident résolu et affecté, une seule par incident, note de 1 à 5.
  - Connexion : bon et mauvais mot de passe, redirection vers la connexion, en-tête "Bonjour …", déconnexion.
  - Droits : l'agent ne peut ni affecter, ni résoudre, ni évaluer, ni consulter le tableau de bord ou la recommandation.
  - Lot alpha : recommandation de prestataires et journal métier.
  - Pages web : un filtre laissé sur "Tous" ne filtre rien ; un agent obtient une page d'accès refusé sur le tableau de bord ; une action refusée ramène sur l'incident avec le message.
  - Documentation Swagger générée et protégée comme le reste de l'API.
- **Non couvert :**
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
- Côté pages, un second `@ControllerAdvice` (`GestionnaireExceptionsPages`), limité au package `controleur.page`, traite les mêmes exceptions :
  - ressource introuvable, accès refusé, paramètre invalide → page `erreur` avec un message en français et le bon code HTTP ;
  - action refusée sur un incident (résoudre deux fois, évaluer deux fois) --> retour sur la page de l'incident avec le message ;
  - toute autre erreur (adresse inconnue...) --> page `error` générique. Aucune trace technique n'est affichée.

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
- **Lectures** (`IncidentService`, `EquipementService`, `RecommandationService`) : `@Transactional(readOnly = true)`. La transaction garde la session ouverte pendant la construction des DTO, qui lisent les liens paresseux (équipement, station, prestataire). `readOnly` indique qu'aucune écriture n'est attendue : Hibernate ne surveille pas les modifications des entités chargées.
- **Pas de transaction** dans les contrôleurs, ni dans `TableauDeBordService` qui ne fait qu'appeler des méthodes déjà transactionnelles.

### Horloge injectée

- Le niveau d'attention est calculé à chaque lecture à partir de l'heure courante. Il n'est jamais stocké en base.
- L'heure vient d'un bean `Clock` (`HorlogeConfig`), ce qui permet de la figer dans les tests.
- Hibernate enregistre les dates (`Instant`) en UTC. Le script utilise donc `UTC_TIMESTAMP()` et non `NOW()`, qui donne l'heure locale du serveur MySQL. L'affichage, lui, se fait à l'heure de Paris (fuseau `Europe/Paris` fixé dans le `Dockerfile` pour le serveur déployé).

### Déploiement

- `Dockerfile` en deux étapes : construction du jar avec Maven, puis image Java 21 sans Maven ni code source.
- Le port vient de la variable `PORT` imposée par l'hébergeur ; les identifiants de la base viennent de `DB_URL`, `DB_USERNAME` et `DB_PASSWORD`. Aucun secret n'est dans l'image (`application-local.properties` est exclu par `.dockerignore`).

## LOT ALPHA

### Fonctionnalité transverse 1 : aide à l'affectation (piste 3)

- **Besoin :** aider le responsable à choisir le bon prestataire pour un incident.
- **Périmètre :** proposer les 3 prestataires les plus adaptés, avec des arguments.
- **Choix :**
  - chaque prestataire a des compétences (types d'équipements) ;
  - on garde ceux qui sont compétents et disponibles (moins de 3 incidents en cours) ;
  - on les trie par note moyenne sur ce type d'équipement ;
  - chaque proposition donne ses arguments : compétence, note, nombre d'interventions résolues, incidents en cours.
- **Ce qui fonctionne :**
  - `GET /incident/{id}/recommandation`, réservé au responsable. Refusé (409) pour un incident résolu.
  - Sur la page d'un incident non résolu, le responsable voit les prestataires recommandés et leurs arguments, et peut affecter l'un d'eux en un clic.
- **Difficultés :** calculer la note sur un type d'équipement précis (requête JPQL avec jointures) ; placer les prestataires jamais évalués après les autres.

### Fonctionnalité transverse 2 : journal métier (piste 5)

- **Besoin :** savoir qui a changé quoi, et quand, sur un incident.
- **Périmètre :** déclaration, modification de la description, affectation, résolution, évaluation.
- **Choix :**
  - une entité `EvenementIncident` (type, détails, auteur, date) ;
  - les services publient un événement Spring (`IncidentModifieEvenement`) ;
  - `JournalListener` l'enregistre. Il est synchrone : l'événement est écrit dans la même transaction que l'action (RG10).
- **Ce qui fonctionne :**
  - `GET /incident/{id}/journal` renvoie les événements dans l'ordre chronologique. L'auteur est le login de l'utilisateur connecté.
  - Le journal est affiché sur la page de chaque incident. Une réaffectation indique l'ancien et le nouveau prestataire ; une modification de description garde l'ancienne et la nouvelle valeur.
  - Une action refusée n'est pas journalisée.
- **Difficultés :** l'auteur valait « Système » tant que la connexion (F1) n'existait pas. Seul `UtilisateurCourant` a changé ensuite.
- **Reste à faire :** filtrer le journal par type d'événement ou par auteur.

## BILAN

- **Contribution de chacun :**
  - FN1, FN2, FN3, FNT3, FNT5 : Elias
  - Pages web (F2 à F11), F12 (documentation Swagger), script `db/create.sql`, déploiement : Elias
  - FN5, FN6, FN7, FN8, FN9, FN10, FN11 : Ashley
- **Plus grande difficulté :** le niveau d'attention dépend de l'heure courante. Il était difficile à tester et à démontrer.
  - Solution : injecter une horloge (`Clock`), la figer dans les tests, et générer les dates de démonstration par rapport à l'instant présent.
  - Un piège est apparu ensuite : tous les niveaux avaient une heure de retard (un incident de 72 h restait orange). Les dates étaient écrites en heure locale par le script, mais lues en UTC par Hibernate. Utiliser `UTC_TIMESTAMP()` dans le script a corrigé le problème.
- **Fonctionnalités non réalisées et pourquoi :** le socle (F1 à F12) et deux pistes du lot alpha (3 et 5) sont réalisés. Les autres pistes du lot alpha n'ont pas été retenues, faute de temps.
- **Trois points à retenir :**
  1. Séparer les couches : les règles métier dans les services, jamais dans les contrôleurs.
  2. Un code testable se conçoit dès le départ (injection de dépendances, horloge injectée).
  3. La sécurité se vérifie côté serveur : masquer un bouton ne suffit pas.

## Évolution séance finale

### Description : équipement fragile

- **Besoin :** repérer un équipement qui tombe souvent en panne. Il est fragile s'il a eu au moins 3 incidents ouverts durant les 30 derniers jours, quel que soit leur statut.
- **Où dans le code :**
  - `service/DetecteurEquipementFragile` : la règle (EF1 à EF5). Le seuil (3) et la fenêtre (30 jours) sont deux constantes de cette classe, et nulle part ailleurs.
  - `repository/IncidentRepository` : `countByEquipementIdAndDateOuvertureGreaterThanEqual`, qui compte les incidents d'un équipement ouverts depuis une date.
  - `service/EquipementService` et `service/IncidentService` : la fiche (`FicheEquipement.fragile`) et chaque incident (`IncidentDto.equipementFragile`) portent le résultat.
  - `templates/fragments/incident.html` : le fragment `fragile`, badge "Équipement fragile", réutilisé par `fiche-equipement.html` et `supervision.html`.
  - `controleur/page/IncidentPageControleur` : le message de confirmation d'une déclaration contient « fragile » si l'équipement l'est devenu.

### Choix de conception

- **Un composant à part, comme `CalculateurNiveauAttention` :** la règle vit dans la couche service (EF6). Les contrôleurs et les vues ne font que lire un booléen déjà calculé.
- **Rien en base (EF4) :** le caractère fragile est recalculé à chaque consultation, à partir de l'horloge injectée (`Clock`). La fenêtre glisse donc toute seule.
- **Le comptage est fait par la base :** une requête `count` avec `date_ouverture >= maintenant - 30 jours`, sans filtre sur le statut (EF2). La borne est incluse : un incident ouvert il y a 30 jours pile compte encore (EF1).
- **Même information pour l'API :** `IncidentDto` porte `equipementFragile`, donc l'API REST et les pages l'exposent sans code supplémentaire.
- **Raccourci pris :** le calcul est fait incident par incident (une requête `count` par ligne du tableau de bord et de la fiche). C'est simple et suffisant pour le volume de la démonstration, mais pas optimisé : une seule requête groupée par équipement serait préférable sur un gros réseau.

### Tests ajoutés et limites connues

- **Tests ajoutés :**
  - `DetecteurEquipementFragileTest` (`@DataJpaTest`, base H2, horloge figée) : les 7 cas limites de l'énoncé, plus le cas « 30 jours pile » :
    - 2 incidents dans la fenêtre : non fragile ; 3 incidents : fragile ; 3 dont 1 résolu : fragile ;
    - un incident ouvert il y a 30 jours et 1 minute n'est pas compté ; il y a 29 jours 23 h 59, il l'est ; il y a 30 jours pile, il l'est aussi ;
    - 2 incidents récents et 5 vieux de 45 jours : non fragile ;
    - l'équipement voisin, avec un seul incident, reste non fragile.
  - `EquipementServiceTest` et `IncidentServiceTest` : la fiche et l'incident reprennent le résultat du détecteur.
  - `PagesWebTest` : le badge apparaît sur la fiche et sur le tableau de bord si et seulement si l'équipement est fragile ; le message de déclaration contient "fragile" seulement dans ce cas.
- **Limites connues :**
  - Une requête `count` par incident affiché
  - Le seuil et la fenêtre sont des constantes : les changer demande une nouvelle version de l'application, pas un simple paramétrage.
  - Les données de `db/create.sql` ne contiennent aucun équipement fragile. Pour la démonstration, `ASC-03` (Ascenseur Parking) a 2 incidents récents et un troisième ouvert il y a 30 jours pile au moment du script : il sort de la fenêtre juste après (cas limite 4). Déclarer un incident sur `ASC-03` ou `ASC-02` le rend fragile : le message de confirmation le signale et le badge apparaît.
