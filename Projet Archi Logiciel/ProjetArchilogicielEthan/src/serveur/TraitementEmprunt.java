package serveur;

import exceptions.EmpruntException;
import modele.Abonne;
import modele.DocumentAbstrait;
import modele.Mediatheque;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Traitement d'une demande d'emprunt (port 2001).
 * Protocole bttp2.0 : requête "EMPRUNTE <numAbonne> <idDoc>"
 * Réponse : "OK <message>" ou "KO <raison>"
 */
public class TraitementEmprunt implements Runnable {

    private final Socket socket;

    public TraitementEmprunt(Socket socket) {
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

            String[] parts = ligne.trim().split("\\s+");
            if (parts.length < 3 || !parts[0].equalsIgnoreCase("EMPRUNTE")) {
                out.println("KO Syntaxe invalide. Attendu : EMPRUNTE <numAbonne> <idDoc>");
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

            try {
                doc.emprunt(abonne);
                out.println("OK Emprunt confirmé : \"" + doc.getTitre() + "\" est désormais emprunté par " + abonne.getNom() + ".");
            } catch (EmpruntException e) {
                out.println("KO " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("[Emprunt] Erreur socket : " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
