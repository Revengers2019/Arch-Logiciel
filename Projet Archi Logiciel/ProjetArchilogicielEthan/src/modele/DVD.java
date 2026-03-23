package modele;

import exceptions.EmpruntException;
import exceptions.ReservationException;

/**
 * Représente un DVD de la médiathèque.
 * Le champ adulte (true) signifie réservé aux plus de 16 ans.
 */
public class DVD extends DocumentAbstrait {
    private final boolean adulte;

    public DVD(String id, String titre, boolean adulte) {
        super(id, titre);
        this.adulte = adulte;
    }

    public boolean isAdulte() {
        return adulte;
    }

    @Override
    protected void verifierConditionsReservation(Abonne ab) throws ReservationException {
        if (adulte && ab.getAge() < 16) {
            throw new ReservationException(
                "Vous devez avoir au moins 16 ans pour réserver ce DVD (\"" + getTitre() + "\"). "
                + "Votre âge : " + ab.getAge() + " ans.");
        }
    }

    @Override
    protected void verifierConditionsEmprunt(Abonne ab) throws EmpruntException {
        if (adulte && ab.getAge() < 16) {
            throw new EmpruntException(
                "Vous devez avoir au moins 16 ans pour emprunter ce DVD (\"" + getTitre() + "\"). "
                + "Votre âge : " + ab.getAge() + " ans.");
        }
    }

    @Override
    public String toString() {
        return "DVD " + super.toString() + (adulte ? " [+16]" : " [Tout public]");
    }
}
