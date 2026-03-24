Projet Archi Logiciel 
Projet R4.01 Architecture logicielle – BUT2   
Ethan - Tanim - Alexandre
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

## 🛰 Protocole BTTP 2.0 (BretteSoft Transport Protocol)

| Service | Port | Requête | Réponse |
| :--- | :--- | :--- | :--- |
| **Réservation** | 2000 | `RESERVE <numAbonne> <idDoc>` | `OK ...` / `KO ...` |
| **Emprunt** | 2001 | `EMPRUNTE <numAbonne> <idDoc>` | `OK ...` / `KO ...` |
| **Retour** | 2002 | `RETOUR <idDoc> [DEGRADE]` | `OK ...` / `KO ...` |

---

## 📋 Données en dur (Mediatheque.java)

**Abonnés :**
* `n°1` Alice Dupont (Adulte)
* `n°2` Bob Martin (Mineur, né en 2010)
* `n°3` Claire Durand (Adulte)
* `n°4` David Bernard (Adulte)
* `n°5` Emma Petit (Adulte)

**Documents :**
* **Livres :** `L001` à `L004`

DVDs : D001 à D005 (D002, D004 = adulte +16 ans)
