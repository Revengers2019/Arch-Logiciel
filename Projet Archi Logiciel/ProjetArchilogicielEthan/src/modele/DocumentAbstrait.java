package modele;

import exceptions.EmpruntException;
import exceptions.ReservationException;
import exceptions.RetourException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe abstraite factorisant le comportement commun à tous les documents
 * (état, réservation, emprunt, retour).
 * Patron de conception : Template Method pour reservation/emprunt/retour.
 */
public abstract class DocumentAbstrait implements Document {

    public enum EtatDocument {
        DISPONIBLE, RESERVE, EMPRUNTE
    }

    private final String id;
    private final String titre;

    private EtatDocument etat;
    private Abonne abonneEnCours;       // abonné qui a réservé ou emprunté
    private LocalDateTime finReservation; // heure limite de réservation (2h)

    // Certification Géronimo : date d'emprunt pour détecter les retards
    private LocalDateTime dateEmprunt;

    protected DocumentAbstrait(String id, String titre) {
        this.id = id;
        this.titre = titre;
        this.etat = EtatDocument.DISPONIBLE;
    }

    @Override
    public String idDoc() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public EtatDocument getEtat() {
        // Vérifier si la réservation a expiré
        if (etat == EtatDocument.RESERVE && LocalDateTime.now().isAfter(finReservation)) {
            etat = EtatDocument.DISPONIBLE;
            abonneEnCours = null;
            finReservation = null;
            System.out.println("[AUTO] Réservation expirée pour le document " + id);
        }
        return etat;
    }

    public Abonne getAbonneEnCours() {
        return abonneEnCours;
    }

    public LocalDateTime getFinReservation() {
        return finReservation;
    }

    /**
     * Retourne les secondes restantes avant expiration de la réservation, ou 0.
     */
    public long secondesRestantesReservation() {
        if (etat != EtatDocument.RESERVE || finReservation == null) return 0;
        long secondes = java.time.Duration.between(LocalDateTime.now(), finReservation).getSeconds();
        return Math.max(0, secondes);
    }

    @Override
    public synchronized void reservation(Abonne ab) throws ReservationException {
        if (ab.estBanni()) {
            throw new ReservationException("Vous êtes banni de la médiathèque jusqu'au "
                    + ab.getDateBannissement() + ". Réservation impossible.");
        }
        EtatDocument etatActuel = getEtat();
        if (etatActuel == EtatDocument.EMPRUNTE) {
            throw new ReservationException("Le document \"" + titre + "\" est déjà emprunté.");
        }
        if (etatActuel == EtatDocument.RESERVE) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH'h'mm");
            throw new ReservationException("Le document \"" + titre + "\" est réservé jusqu'à "
                    + finReservation.format(fmt) + ".");
        }
        // Vérification spécifique au sous-type (ex: age pour DVD adulte)
        verifierConditionsReservation(ab);

        etat = EtatDocument.RESERVE;
        abonneEnCours = ab;
        finReservation = LocalDateTime.now().plusHours(2);
        System.out.println("[OK] Réservation de \"" + titre + "\" par " + ab + " jusqu'à "
                + finReservation.format(DateTimeFormatter.ofPattern("HH'h'mm")));
    }

    @Override
    public synchronized void emprunt(Abonne ab) throws EmpruntException {
        if (ab.estBanni()) {
            throw new EmpruntException("Vous êtes banni de la médiathèque jusqu'au "
                    + ab.getDateBannissement() + ". Emprunt impossible.");
        }
        EtatDocument etatActuel = getEtat();
        if (etatActuel == EtatDocument.EMPRUNTE) {
            throw new EmpruntException("Le document \"" + titre + "\" est déjà emprunté.");
        }
        if (etatActuel == EtatDocument.RESERVE) {
            if (!abonneEnCours.equals(ab)) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH'h'mm");
                throw new EmpruntException("Le document \"" + titre + "\" est réservé pour un autre abonné jusqu'à "
                        + finReservation.format(fmt) + ".");
            }
        }
        // Vérification spécifique au sous-type
        verifierConditionsEmprunt(ab);

        etat = EtatDocument.EMPRUNTE;
        abonneEnCours = ab;
        finReservation = null;
        dateEmprunt = LocalDateTime.now();
        System.out.println("[OK] Emprunt de \"" + titre + "\" par " + ab);
    }

    @Override
    public synchronized void retour() throws RetourException {
        if (etat == EtatDocument.DISPONIBLE) {
            throw new RetourException("Le document \"" + titre + "\" est déjà disponible, impossible de le retourner.");
        }

        // Certification Géronimo : vérifier le retard
        if (etat == EtatDocument.EMPRUNTE && dateEmprunt != null) {
            long joursEmprunte = java.time.Duration.between(dateEmprunt, LocalDateTime.now()).toDays();
            if (joursEmprunte > 14) {
                System.out.println("[GERONIMO] Retard de " + joursEmprunte + " jours détecté pour " + abonneEnCours);
                abonneEnCours.bannir();
            }
        }

        Abonne ancien = abonneEnCours;
        etat = EtatDocument.DISPONIBLE;
        abonneEnCours = null;
        finReservation = null;
        dateEmprunt = null;
        System.out.println("[OK] Retour du document \"" + titre + "\"" + (ancien != null ? " (emprunté par " + ancien + ")" : ""));
    }

    /**
     * Retour avec signalement de dégradation (Certification Géronimo).
     */
    public synchronized void retourAvecDegradation() throws RetourException {
        if (etat == EtatDocument.DISPONIBLE) {
            throw new RetourException("Le document \"" + titre + "\" est déjà disponible.");
        }
        if (abonneEnCours != null) {
            System.out.println("[GERONIMO] Dégradation constatée pour " + abonneEnCours);
            abonneEnCours.bannir();
        }
        retour();
    }

    /**
     * Hook pour vérifications spécifiques à chaque type de document lors d'une réservation.
     */
    protected void verifierConditionsReservation(Abonne ab) throws ReservationException {
        // Par défaut : rien
    }

    /**
     * Hook pour vérifications spécifiques à chaque type de document lors d'un emprunt.
     */
    protected void verifierConditionsEmprunt(Abonne ab) throws EmpruntException {
        // Par défaut : rien
    }

    @Override
    public String toString() {
        return "[" + id + "] " + titre + " (" + getEtat() + ")";
    }
}
