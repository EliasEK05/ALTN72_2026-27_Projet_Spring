-- Jeu de données de test : complète la base existante sans rien supprimer.
-- Peut être relancé sans risque (INSERT IGNORE + identifiants fixes).
USE altyn72_supervision;
SET NAMES utf8mb4;
START TRANSACTION;

INSERT IGNORE INTO station (id, nom) VALUES
    (4, 'Station Sud'),
    (5, 'Port Maritime');

INSERT IGNORE INTO equipement (id, reference, libelle, type, etat_fonctionnement, station_id) VALUES
    (6,  'ASC-02',  'Ascenseur Quai B',            'ASCENSEUR',          'FONCTIONNEL', 2),
    (7,  'ASC-03',  'Ascenseur Parking',           'ASCENSEUR',          'FONCTIONNEL', 3),
    (8,  'ESC-03',  'Escalator Quai Sud',          'ESCALIER_MECANIQUE', 'FONCTIONNEL', 4),
    (9,  'PORT-02', 'Portique Sortie Sud',         'PORTIQUE',           'FONCTIONNEL', 4),
    (10, 'DIST-02', 'Distributeur Billets Quai',   'DISTRIBUTEUR',       'FONCTIONNEL', 5),
    (11, 'ECR-03',  'Écran Départs',               'ECRAN',              'FONCTIONNEL', 1),
    (12, 'ESC-04',  'Escalator Terminal',          'ESCALIER_MECANIQUE', 'EN_PANNE',    3),
    (13, 'ASC-04',  'Ascenseur Embarcadère',       'ASCENSEUR',          'EN_PANNE',    5);

INSERT IGNORE INTO prestataire (id, nom, contact) VALUES
    (4, 'AscenseursPlus',       'contact@ascenseursplus.fr - 01 11 22 33 44'),
    (5, 'Multi-Services Gares', 'depannage@multiservices.fr - 01 55 66 77 88'),
    (6, 'AccèsSécurité',        'sav@acces-securite.fr - 02 12 34 56 78'),
    (7, 'TechnoBillet',         'support@technobillet.fr - 03 98 76 54 32'),
    (8, 'RapidElev',            'urgence@rapidelev.fr - 06 12 12 12 12');

INSERT IGNORE INTO prestataire_competence (prestataire_id, type_equipement) VALUES
    (1, 'ASCENSEUR'), (1, 'ESCALIER_MECANIQUE'),
    (2, 'ECRAN'), (2, 'DISTRIBUTEUR'),
    (3, 'ESCALIER_MECANIQUE'),
    (4, 'ASCENSEUR'),
    (5, 'ASCENSEUR'), (5, 'ESCALIER_MECANIQUE'), (5, 'PORTIQUE'), (5, 'DISTRIBUTEUR'), (5, 'ECRAN'),
    (6, 'PORTIQUE'),
    (7, 'DISTRIBUTEUR'), (7, 'ECRAN'),
    (8, 'ASCENSEUR');

-- Historique : incidents résolus (alimentent les notes et le nombre d'interventions)
INSERT IGNORE INTO incident (id, description, date_ouverture, statut, date_resolution, equipement_id, prestataire_id) VALUES
    (6,  'Ascenseur bloqué entre deux étages',  NOW() - INTERVAL 60 DAY, 'RESOLU', NOW() - INTERVAL 59 DAY, 6,  1),
    (7,  'Bouton d''appel défectueux',          NOW() - INTERVAL 45 DAY, 'RESOLU', NOW() - INTERVAL 44 DAY, 7,  1),
    (8,  'Porte qui se referme trop vite',      NOW() - INTERVAL 40 DAY, 'RESOLU', NOW() - INTERVAL 37 DAY, 6,  4),
    (9,  'Voyant de surcharge allumé en continu', NOW() - INTERVAL 30 DAY, 'RESOLU', NOW() - INTERVAL 28 DAY, 7, 4),
    (10, 'Cabine qui vibre',                    NOW() - INTERVAL 25 DAY, 'RESOLU', NOW() - INTERVAL 24 DAY, 13, 5),
    (11, 'Éclairage cabine en panne',           NOW() - INTERVAL 20 DAY, 'RESOLU', NOW() - INTERVAL 19 DAY, 6,  5),
    (12, 'Arrêt brutal en descente',            NOW() - INTERVAL 15 DAY, 'RESOLU', NOW() - INTERVAL 15 DAY, 7,  8),
    (13, 'Marche cassée',                       NOW() - INTERVAL 50 DAY, 'RESOLU', NOW() - INTERVAL 48 DAY, 8,  3),
    (14, 'Main courante désynchronisée',        NOW() - INTERVAL 35 DAY, 'RESOLU', NOW() - INTERVAL 30 DAY, 8,  1),
    (15, 'Portique qui ne lit plus les badges', NOW() - INTERVAL 22 DAY, 'RESOLU', NOW() - INTERVAL 21 DAY, 9,  6),
    (16, 'Vantail bloqué en position ouverte',  NOW() - INTERVAL 18 DAY, 'RESOLU', NOW() - INTERVAL 16 DAY, 3,  5),
    (17, 'Écran qui clignote',                  NOW() - INTERVAL 28 DAY, 'RESOLU', NOW() - INTERVAL 27 DAY, 11, 7),
    (18, 'Affichage figé',                      NOW() - INTERVAL 12 DAY, 'RESOLU', NOW() - INTERVAL 10 DAY, 5,  2),
    (19, 'Monnayeur bloqué',                    NOW() - INTERVAL 9 DAY,  'RESOLU', NOW() - INTERVAL 8 DAY,  10, 7);

INSERT IGNORE INTO evaluation (note, commentaire, date_evaluation, incident_id) VALUES
    (5, 'Intervention rapide et efficace',        NOW() - INTERVAL 58 DAY, 6),
    (4, 'Bon travail, un peu de retard',          NOW() - INTERVAL 43 DAY, 7),
    (3, 'Problème réglé mais revenu une fois',    NOW() - INTERVAL 36 DAY, 8),
    (3, 'Correct',                                NOW() - INTERVAL 27 DAY, 9),
    (4, 'Technicien compétent',                   NOW() - INTERVAL 23 DAY, 10),
    (5, 'Excellent, très réactif',                NOW() - INTERVAL 14 DAY, 12),
    (4, 'Travail soigné',                         NOW() - INTERVAL 47 DAY, 13),
    (2, 'Intervention longue, pas spécialiste',   NOW() - INTERVAL 29 DAY, 14),
    (5, 'Parfait',                                NOW() - INTERVAL 20 DAY, 15),
    (3, 'Résolu mais délai important',            NOW() - INTERVAL 15 DAY, 16),
    (5, 'Très professionnel',                     NOW() - INTERVAL 26 DAY, 17),
    (2, 'A dû revenir deux fois',                 NOW() - INTERVAL 9 DAY,  18),
    (4, 'Bien',                                   NOW() - INTERVAL 7 DAY,  19);

-- Incidents en cours : RapidElev est débordé (3 incidents), les autres ont différents niveaux d'attention
INSERT IGNORE INTO incident (id, description, date_ouverture, statut, date_resolution, equipement_id, prestataire_id) VALUES
    (20, 'Ascenseur hors service',              NOW() - INTERVAL 2 DAY,   'AFFECTE',     NULL, 13, 8),
    (21, 'Portes qui ne s''ouvrent plus',       NOW() - INTERVAL 1 DAY,   'AFFECTE',     NULL, 6,  8),
    (22, 'Bruit anormal du moteur',             NOW() - INTERVAL 10 HOUR, 'AFFECTE',     NULL, 7,  8),
    (23, 'Escalator à l''arrêt',                NOW() - INTERVAL 5 DAY,   'NON_AFFECTE', NULL, 12, NULL),
    (24, 'Portique qui reste fermé',            NOW() - INTERVAL 60 HOUR, 'NON_AFFECTE', NULL, 9,  NULL),
    (25, 'Distributeur qui refuse les cartes',  NOW() - INTERVAL 6 HOUR,  'NON_AFFECTE', NULL, 10, NULL);

COMMIT;
