ProjetArchilogicielEthan – Médiathèque
Projet R4.01 Architecture logicielle – BUT2 FI  
Gestion des réservations, emprunts et retours d'une médiathèque.
---
Structure du projet
```
src/
├── exceptions/
│   ├── ReservationException.java
│   ├── EmpruntException.java
│   └── RetourException.java
├── modele/
│   ├── Document.java          (interface contractuelle)
│   ├── DocumentAbstrait.java  (classe abstraite commune)
│   ├── Livre.java
│   ├── DVD.java
│   ├── Abonne.java
│   └── Mediatheque.java       (Singleton – données en dur)
├── serveur/
│   ├── ServeurEcoute.java     (bibliothèque bserveur générique)
│   ├── TraitementReservation.java  (port 2000)
│   ├── TraitementEmprunt.java      (port 2001)
│   ├── TraitementRetour.java       (port 2002)
│   └── AppServeur.java        (main serveur)
└── client/
    ├── ClientBttp.java        (client bttp2.0 générique)
    ├── ClientReservation.java
    ├── ClientEmprunt.java
    └── ClientRetour.java
```
---
Compilation et exécution
```bash
chmod +x compile.sh
./compile.sh

# Démarrer le serveur
java -cp out serveur.AppServeur

# Client réservation (depuis chez l'abonné)
java -cp out client.ClientReservation localhost 2000

# Client emprunt (borne en médiathèque)
java -cp out client.ClientEmprunt localhost 2001

# Client retour (borne en médiathèque)
java -cp out client.ClientRetour localhost 2002
```
---
Protocole bttp2.0
Service	Port	Requête	Réponse
Réservation	2000	`RESERVE <numAbonne> <idDoc>`	`OK ...` / `KO ...`
Emprunt	2001	`EMPRUNTE <numAbonne> <idDoc>`	`OK ...` / `KO ...`
Retour	2002	`RETOUR <idDoc> [DEGRADE]`	`OK ...` / `KO ...`
---
Certifications BretteSoft© implémentées
Géronimo
Retard > 2 semaines lors d'un retour → bannissement 1 mois de l'abonné
Document rendu dégradé (`DEGRADE`) → bannissement 1 mois
Grand Chaman
Lors d'une réservation, si le document est réservé avec ≤ 60s restantes :
le client est mis en attente (concert céleste), puis la réservation est
tentée automatiquement dès expiration.
Sitting Bull
(Optionnel – nécessite javax.mail) Si le document n'est pas disponible,
proposer à l'abonné une alerte email lors du retour.
---
Données en dur (Mediatheque.java)
Abonnés :
n°1 Alice Dupont (adulte)
n°2 Bob Martin (mineur, né 2010)
n°3 Claire Durand (adulte)
n°4 David Bernard (adulte)
n°5 Emma Petit (adulte)
Livres : L001 à L004  
DVDs : D001 à D005 (D002, D004 = adulte +16 ans)
