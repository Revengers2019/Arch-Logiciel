package serveur;

import exceptions.ReservationException;
import modele.Abonne;
import modele.DocumentAbstrait;
import modele.Mediatheque;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.format.DateTimeFormatter;

/**
 * Traitement d'une demande de réservation (port 2000).
 * Protocole bttp2.0 : requête sur une ligne "RESERVE <numAbonne> <idDoc>"
 * Réponse : "OK <message>" ou "KO <raison>"
 *
 * Certification Grand Chaman : si le document est réservé avec moins de 60s restantes,
 * on fait patienter l'abonné A jusqu'à expiration de la réservation courante,
 * puis on tente de valider la sienne.
 */
public class TraitementReservation implements Runnable {

    private final Socket socket;

    public TraitementReservation(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String ligne = in.readLine();
            if (ligne == null) {
                out.println("KO Requête vide.");
                return;
            }

            // Protocole bttp2.0 : "RESERVE <numAbonne> <idDoc>"
            String[] parts = ligne.trim().split("\\s+");
            if (parts.length < 3 || !parts[0].equalsIgnoreCase("RESERVE")) {
                out.println("KO Syntaxe invalide. Attendu : RESERVE <numAbonne> <idDoc>");
                return;
            }

            int numAbonne;
            try {
                numAbonne = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                out.println("KO Numéro d'abonné invalide : " + parts[1]);
                return;
            }
            String idDoc = parts[2];

            Mediatheque media = Mediatheque.getInstance();
            Abonne abonne = media.trouverAbonne(numAbonne);
            if (abonne == null) {
                out.println("KO Abonné n°" + numAbonne + " introuvable.");
                return;
            }

            DocumentAbstrait doc = media.trouverDocument(idDoc);
            if (doc == null) {
                out.println("KO Document \"" + idDoc + "\" introuvable.");
                return;
            }

            // Certification Grand Chaman : attente si réservation faible (<= 60s)
            if (doc.getEtat() == DocumentAbstrait.EtatDocument.RESERVE) {
                long secondes = doc.secondesRestantesReservation();
                if (secondes > 0 && secondes <= 60) {
                    out.println("INFO Concert céleste en cours... Patientez " + secondes + "s, nous tentons de valider votre réservation.");
                    try {
                        Thread.sleep(secondes * 1000 + 500); // attente + marge
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    // Retenter après expiration
                }
            }

            try {
                doc.reservation(abonne);
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH'h'mm");
                out.println("OK Réservation confirmée pour \"" + doc.getTitre()
                        + "\" jusqu'à " + doc.getFinReservation().format(fmt)
                        + ". Venez l'emprunter dans les 2h !");
            } catch (ReservationException e) {
                out.println("KO " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("[Réservation] Erreur socket : " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
