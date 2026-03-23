package serveur;

/**
 * Point d'entrée de l'application serveur de la médiathèque.
 * Lance 3 serveurs d'écoute en parallèle :
 *   - Port 2000 : Réservations
 *   - Port 2001 : Emprunts
 *   - Port 2002 : Retours
 */
public class AppServeur {

    public static final int PORT_RESERVATION = 2000;
    public static final int PORT_EMPRUNT     = 2001;
    public static final int PORT_RETOUR      = 2002;

    public static void main(String[] args) {
        System.out.println("=== Médiathèque - Serveur démarré ===");

        // Serveur Réservation (port 2000)
        Thread tReservation = new Thread(
            new ServeurEcoute(PORT_RESERVATION, "Réservation", TraitementReservation::new),
            "Thread-Reservation"
        );

        // Serveur Emprunt (port 2001)
        Thread tEmprunt = new Thread(
            new ServeurEcoute(PORT_EMPRUNT, "Emprunt", TraitementEmprunt::new),
            "Thread-Emprunt"
        );

        // Serveur Retour (port 2002)
        Thread tRetour = new Thread(
            new ServeurEcoute(PORT_RETOUR, "Retour", TraitementRetour::new),
            "Thread-Retour"
        );

        tReservation.setDaemon(false);
        tEmprunt.setDaemon(false);
        tRetour.setDaemon(false);

        tReservation.start();
        tEmprunt.start();
        tRetour.start();

        System.out.println("Serveurs actifs sur les ports 2000 (réservation), 2001 (emprunt), 2002 (retour).");
        System.out.println("Données chargées : " + modele.Mediatheque.getInstance().getAbonnes().size()
                + " abonnés, " + modele.Mediatheque.getInstance().getDocuments().size() + " documents.");
    }
}
