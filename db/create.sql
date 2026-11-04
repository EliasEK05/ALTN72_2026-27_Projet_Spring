-- Recrée entièrement la base et ses données de démonstration (§7.6).
-- Les dates sont relatives à l'instant présent : rejouer le script juste avant une démonstration
-- pour retrouver les cas limites du niveau d'attention (47 h vert, 48 h orange, 72 h rouge).
DROP DATABASE IF EXISTS altyn72_supervision;
CREATE DATABASE altyn72_supervision CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE altyn72_supervision;
SET NAMES utf8mb4;

CREATE TABLE station (
    id  BIGINT       NOT NULL AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE prestataire (
    id      BIGINT       NOT NULL AUTO_INCREMENT,
    nom     VARCHAR(100) NOT NULL,
    contact VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE prestataire_competence (
    prestataire_id  BIGINT      NOT NULL,
    type_equipement VARCHAR(50) NOT NULL,
    PRIMARY KEY (prestataire_id, type_equipement),
    CONSTRAINT fk_competence_prestataire FOREIGN KEY (prestataire_id) REFERENCES prestataire (id) ON DELETE CASCADE
);

CREATE TABLE utilisateur (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    login        VARCHAR(50)  NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    prenom       VARCHAR(50)  NOT NULL,
    profil       VARCHAR(50)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_utilisateur_login (login)
);

CREATE TABLE equipement (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    reference           VARCHAR(50)  NOT NULL,
    libelle             VARCHAR(255) NOT NULL,
    type                VARCHAR(50)  NOT NULL,
    etat_fonctionnement VARCHAR(50)  NOT NULL,
    station_id          BIGINT       NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_equipement_reference (reference),
    CONSTRAINT fk_equipement_station FOREIGN KEY (station_id) REFERENCES station (id) ON DELETE CASCADE
);

CREATE TABLE incident (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    description     TEXT        NOT NULL,
    date_ouverture  DATETIME    NOT NULL,
    statut          VARCHAR(50) NOT NULL,
    date_resolution DATETIME    NULL,
    equipement_id   BIGINT      NOT NULL,
    prestataire_id  BIGINT      NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_incident_equipement FOREIGN KEY (equipement_id) REFERENCES equipement (id) ON DELETE CASCADE,
    CONSTRAINT fk_incident_prestataire FOREIGN KEY (prestataire_id) REFERENCES prestataire (id) ON DELETE SET NULL
);

CREATE TABLE evaluation (
    id              BIGINT   NOT NULL AUTO_INCREMENT,
    note            INT      NOT NULL,
    commentaire     TEXT     NULL,
    date_evaluation DATETIME NOT NULL,
    incident_id     BIGINT   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_evaluation_incident (incident_id),
    CONSTRAINT fk_evaluation_incident FOREIGN KEY (incident_id) REFERENCES incident (id) ON DELETE CASCADE,
    CONSTRAINT chk_evaluation_note CHECK (note BETWEEN 1 AND 5)
);

CREATE TABLE evenement_incident (
    id             BIGINT      NOT NULL AUTO_INCREMENT,
    incident_id    BIGINT      NOT NULL,
    type           VARCHAR(50) NOT NULL,
    details        TEXT        NOT NULL,
    auteur         VARCHAR(50) NOT NULL,
    date_evenement DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_evenement_incident FOREIGN KEY (incident_id) REFERENCES incident (id) ON DELETE CASCADE
);

INSERT INTO station (id, nom) VALUES
    (1, 'Gare Centrale'),
    (2, 'Station Nord'),
    (3, 'Aéroport Terminus'),
    (4, 'Station Sud'),
    (5, 'Port Maritime');

INSERT INTO equipement (id, reference, libelle, type, etat_fonctionnement, station_id) VALUES
    (1,  'ASC-01',  'Ascenseur Quai A',            'ASCENSEUR',          'EN_PANNE',    1),
    (2,  'ESC-02',  'Escalator Sortie Principale', 'ESCALIER_MECANIQUE', 'EN_PANNE',    1),
    (3,  'PORT-01', 'Portique Entrée Nord',        'PORTIQUE',           'EN_PANNE',    2),
    (4,  'DIST-01', 'Distributeur Billets Hall',   'DISTRIBUTEUR',       'FONCTIONNEL', 3),
    (5,  'ECR-02',  'Écran Affichage Vols',        'ECRAN',              'EN_PANNE',    3),
    (6,  'ASC-02',  'Ascenseur Quai B',            'ASCENSEUR',          'EN_PANNE',    2),
    (7,  'ASC-03',  'Ascenseur Parking',           'ASCENSEUR',          'EN_PANNE',    3),
    (8,  'ESC-03',  'Escalator Quai Sud',          'ESCALIER_MECANIQUE', 'FONCTIONNEL', 4),
    (9,  'PORT-02', 'Portique Sortie Sud',         'PORTIQUE',           'EN_PANNE',    4),
    (10, 'DIST-02', 'Distributeur Billets Quai',   'DISTRIBUTEUR',       'EN_PANNE',    5),
    (11, 'ECR-03',  'Écran Départs',               'ECRAN',              'FONCTIONNEL', 1),
    (12, 'ESC-04',  'Escalator Terminal',          'ESCALIER_MECANIQUE', 'EN_PANNE',    3),
    (13, 'ASC-04',  'Ascenseur Embarcadère',       'ASCENSEUR',          'EN_PANNE',    5);

INSERT INTO prestataire (id, nom, contact) VALUES
    (1, 'ElevatorTech',         'contact@elevatortech.fr - 01 02 03 04 05'),
    (2, 'ScreenDisplay SARL',   'support@screendisplay.com - 06 00 00 00 00'),
    (3, 'MecaEscalator',        'intervention@mecaescalator.fr - 01 99 88 77 66'),
    (4, 'AscenseursPlus',       'contact@ascenseursplus.fr - 01 11 22 33 44'),
    (5, 'Multi-Services Gares', 'depannage@multiservices.fr - 01 55 66 77 88'),
    (6, 'AccèsSécurité',        'sav@acces-securite.fr - 02 12 34 56 78'),
    (7, 'TechnoBillet',         'support@technobillet.fr - 03 98 76 54 32'),
    (8, 'RapidElev',            'urgence@rapidelev.fr - 06 12 12 12 12');

INSERT INTO prestataire_competence (prestataire_id, type_equipement) VALUES
    (1, 'ASCENSEUR'), (1, 'ESCALIER_MECANIQUE'),
    (2, 'ECRAN'), (2, 'DISTRIBUTEUR'),
    (3, 'ESCALIER_MECANIQUE'),
    (4, 'ASCENSEUR'),
    (5, 'ASCENSEUR'), (5, 'ESCALIER_MECANIQUE'), (5, 'PORTIQUE'), (5, 'DISTRIBUTEUR'), (5, 'ECRAN'),
    (6, 'PORTIQUE'),
    (7, 'DISTRIBUTEUR'), (7, 'ECRAN'),
    (8, 'ASCENSEUR');

-- Mots de passe hachés en BCrypt : responsable123 et agent123
INSERT INTO utilisateur (id, login, mot_de_passe, prenom, profil) VALUES
    (1, 'responsable', '$2b$10$uGq0KfLygoa0K8kB3aLHZOqfoSj3Q6qT/btipg9ryHIg9M3MiULSq', 'Camille', 'RESPONSABLE_EXPLOITATION'),
    (2, 'agent',       '$2b$10$dltAXM3L5rG4ydB39umUWulZ57v6fmZvZNBRRxjkqi.qUo2kfcPlS', 'Lucas',   'AGENT_TERRAIN');

-- Incidents actifs non affectés : cas limites du niveau d'attention (RG3)
INSERT INTO incident (id, description, date_ouverture, statut, date_resolution, equipement_id, prestataire_id) VALUES
    (1, 'Porte bloquée au rez-de-chaussée',    UTC_TIMESTAMP() - INTERVAL 47 HOUR, 'NON_AFFECTE', NULL, 1,  NULL),
    (2, 'Lecteur de carte abîmé',              UTC_TIMESTAMP() - INTERVAL 48 HOUR, 'NON_AFFECTE', NULL, 3,  NULL),
    (3, 'Écran noir, ne s''allume plus',       UTC_TIMESTAMP() - INTERVAL 72 HOUR, 'NON_AFFECTE', NULL, 5,  NULL),
    (4, 'Portique qui reste fermé',            UTC_TIMESTAMP() - INTERVAL 71 HOUR, 'NON_AFFECTE', NULL, 9,  NULL),
    (5, 'Escalator à l''arrêt',                UTC_TIMESTAMP() - INTERVAL 5 DAY,   'NON_AFFECTE', NULL, 12, NULL),
    (6, 'Distributeur qui refuse les cartes',  UTC_TIMESTAMP() - INTERVAL 6 HOUR,  'NON_AFFECTE', NULL, 10, NULL);

-- Incidents actifs affectés (RG4) : RapidElev en a trois, il n'est donc plus proposé par l'aide à l'affectation
INSERT INTO incident (id, description, date_ouverture, statut, date_resolution, equipement_id, prestataire_id) VALUES
    (7,  'Le roulement fait un bruit métallique', UTC_TIMESTAMP() - INTERVAL 30 HOUR, 'AFFECTE', NULL, 2,  3),
    (8,  'Ascenseur hors service',                UTC_TIMESTAMP() - INTERVAL 2 DAY,   'AFFECTE', NULL, 13, 8),
    (9,  'Portes qui ne s''ouvrent plus',         UTC_TIMESTAMP() - INTERVAL 1 DAY,   'AFFECTE', NULL, 6,  8),
    (10, 'Bruit anormal du moteur',               UTC_TIMESTAMP() - INTERVAL 10 HOUR, 'AFFECTE', NULL, 7,  8);

-- Historique : incidents résolus (RG5), évalués ou non
INSERT INTO incident (id, description, date_ouverture, statut, date_resolution, equipement_id, prestataire_id) VALUES
    (11, 'Bourrage papier',                       UTC_TIMESTAMP() - INTERVAL 27 DAY, 'RESOLU', UTC_TIMESTAMP() - INTERVAL 24 DAY, 4,  2),
    (12, 'Ascenseur bloqué entre deux étages',    UTC_TIMESTAMP() - INTERVAL 60 DAY, 'RESOLU', UTC_TIMESTAMP() - INTERVAL 59 DAY, 6,  1),
    (13, 'Bouton d''appel défectueux',            UTC_TIMESTAMP() - INTERVAL 45 DAY, 'RESOLU', UTC_TIMESTAMP() - INTERVAL 44 DAY, 7,  1),
    (14, 'Porte qui se referme trop vite',        UTC_TIMESTAMP() - INTERVAL 40 DAY, 'RESOLU', UTC_TIMESTAMP() - INTERVAL 37 DAY, 6,  4),
    (15, 'Voyant de surcharge allumé en continu', UTC_TIMESTAMP() - INTERVAL 30 DAY, 'RESOLU', UTC_TIMESTAMP() - INTERVAL 28 DAY, 7,  4),
    (16, 'Cabine qui vibre',                      UTC_TIMESTAMP() - INTERVAL 25 DAY, 'RESOLU', UTC_TIMESTAMP() - INTERVAL 24 DAY, 13, 5),
    (17, 'Éclairage cabine en panne',             UTC_TIMESTAMP() - INTERVAL 20 DAY, 'RESOLU', UTC_TIMESTAMP() - INTERVAL 19 DAY, 6,  5),
    (18, 'Arrêt brutal en descente',              UTC_TIMESTAMP() - INTERVAL 16 DAY, 'RESOLU', UTC_TIMESTAMP() - INTERVAL 15 DAY, 7,  8),
    (19, 'Marche cassée',                         UTC_TIMESTAMP() - INTERVAL 50 DAY, 'RESOLU', UTC_TIMESTAMP() - INTERVAL 48 DAY, 8,  3),
    (20, 'Main courante désynchronisée',          UTC_TIMESTAMP() - INTERVAL 35 DAY, 'RESOLU', UTC_TIMESTAMP() - INTERVAL 30 DAY, 8,  1),
    (21, 'Portique qui ne lit plus les badges',   UTC_TIMESTAMP() - INTERVAL 22 DAY, 'RESOLU', UTC_TIMESTAMP() - INTERVAL 21 DAY, 9,  6),
    (22, 'Vantail bloqué en position ouverte',    UTC_TIMESTAMP() - INTERVAL 18 DAY, 'RESOLU', UTC_TIMESTAMP() - INTERVAL 16 DAY, 3,  5),
    (23, 'Écran qui clignote',                    UTC_TIMESTAMP() - INTERVAL 28 DAY, 'RESOLU', UTC_TIMESTAMP() - INTERVAL 27 DAY, 11, 7),
    (24, 'Affichage figé',                        UTC_TIMESTAMP() - INTERVAL 12 DAY, 'RESOLU', UTC_TIMESTAMP() - INTERVAL 10 DAY, 5,  2),
    (25, 'Monnayeur bloqué',                      UTC_TIMESTAMP() - INTERVAL 9 DAY,  'RESOLU', UTC_TIMESTAMP() - INTERVAL 8 DAY,  10, 7);

-- Une évaluation par incident résolu et affecté (RG8) ; l'incident 17 n'est pas encore évalué
INSERT INTO evaluation (note, commentaire, date_evaluation, incident_id) VALUES
    (4, 'Intervention correcte',                  UTC_TIMESTAMP() - INTERVAL 23 DAY, 11),
    (5, 'Intervention rapide et efficace',        UTC_TIMESTAMP() - INTERVAL 58 DAY, 12),
    (4, 'Bon travail, un peu de retard',          UTC_TIMESTAMP() - INTERVAL 43 DAY, 13),
    (3, 'Problème réglé mais revenu une fois',    UTC_TIMESTAMP() - INTERVAL 36 DAY, 14),
    (3, 'Correct',                                UTC_TIMESTAMP() - INTERVAL 27 DAY, 15),
    (4, 'Technicien compétent',                   UTC_TIMESTAMP() - INTERVAL 23 DAY, 16),
    (5, 'Excellent, très réactif',                UTC_TIMESTAMP() - INTERVAL 14 DAY, 18),
    (4, 'Travail soigné',                         UTC_TIMESTAMP() - INTERVAL 47 DAY, 19),
    (2, 'Intervention longue, pas spécialiste',   UTC_TIMESTAMP() - INTERVAL 29 DAY, 20),
    (5, 'Parfait',                                UTC_TIMESTAMP() - INTERVAL 20 DAY, 21),
    (3, 'Résolu mais délai important',            UTC_TIMESTAMP() - INTERVAL 15 DAY, 22),
    (5, 'Très professionnel',                     UTC_TIMESTAMP() - INTERVAL 26 DAY, 23),
    (2, 'A dû revenir deux fois',                 UTC_TIMESTAMP() - INTERVAL 9 DAY,  24),
    (4, 'Bien',                                   UTC_TIMESTAMP() - INTERVAL 7 DAY,  25);

-- Journal métier (FTN5) reconstitué à partir des données ci-dessus
INSERT INTO evenement_incident (incident_id, type, details, auteur, date_evenement)
SELECT id, 'DECLARATION', CONCAT('Incident déclaré : ', description), 'agent', date_ouverture
FROM incident;

INSERT INTO evenement_incident (incident_id, type, details, auteur, date_evenement)
SELECT i.id, 'AFFECTATION', CONCAT('Affecté à ', p.nom), 'responsable', i.date_ouverture + INTERVAL 2 HOUR
FROM incident i JOIN prestataire p ON p.id = i.prestataire_id;

INSERT INTO evenement_incident (incident_id, type, details, auteur, date_evenement)
SELECT id, 'RESOLUTION', 'Incident résolu', 'responsable', date_resolution
FROM incident
WHERE statut = 'RESOLU';

INSERT INTO evenement_incident (incident_id, type, details, auteur, date_evenement)
SELECT e.incident_id, 'EVALUATION', CONCAT(p.nom, ' évalué ', e.note, '/5'), 'responsable', e.date_evaluation
FROM evaluation e
JOIN incident i ON i.id = e.incident_id
JOIN prestataire p ON p.id = i.prestataire_id;
