package serveur;

import exceptions.RetourException;
import modele.DocumentAbstrait;
import modele.Mediatheque;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Traitement d'une demande de retour (port 2002).
 * Protocole bttp2.0 : requête "RETOUR <idDoc> [DEGRADE]"
 * Le mot-clé DEGRADE déclenche la Certification Géronimo (bannissement).
 * Réponse : "OK <message>" ou "KO <raison>"
 */
public class TraitementRetour implements Runnable {

    private final Socket socket;

    public TraitementRetour(Socket socket) {
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
            if (parts.length < 2 || !parts[0].equalsIgnoreCase("RETOUR")) {
                out.println("KO Syntaxe invalide. Attendu : RETOUR <idDoc> [DEGRADE]");
                return;
            }

            String idDoc = parts[1];
            boolean degrade = parts.length >= 3 && parts[2].equalsIgnoreCase("DEGRADE");

            Mediatheque media = Mediatheque.getInstance();
            DocumentAbstrait doc = media.trouverDocument(idDoc);
            if (doc == null) {
                out.println("KO Document \"" + idDoc + "\" introuvable.");
                return;
            }

            try {
                if (degrade) {
                    doc.retourAvecDegradation();
                    out.println("OK Retour du document \"" + doc.getTitre() + "\" enregistré. Dégradation signalée : l'abonné a été banni 1 mois.");
                } else {
                    doc.retour();
                    out.println("OK Retour du document \"" + doc.getTitre() + "\" enregistré. Merci !");
                }
            } catch (RetourException e) {
                out.println("KO " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("[Retour] Erreur socket : " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
