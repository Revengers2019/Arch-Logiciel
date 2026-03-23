package client;

import java.io.IOException;
import java.util.Scanner;

/**
 * Application cliente de réservation (installée chez l'abonné).
 * Usage : java client.ClientReservation [hote] [port]
 * Par défaut : localhost 2000
 *
 * Protocole bttp2.0 : envoie "RESERVE <numAbonne> <idDoc>"
 * Reçoit "OK ..." ou "KO ..." ou "INFO ..." (Grand Chaman)
 */
public class ClientReservation {

    public static void main(String[] args) {
        String hote = args.length >= 1 ? args[0] : "localhost";
        int port = args.length >= 2 ? Integer.parseInt(args[1]) : 2000;

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Client Réservation Médiathèque ===");
        System.out.println("Connexion à " + hote + ":" + port);
        System.out.print("Votre numéro d'abonné : ");
        String numAbonne = scanner.nextLine().trim();

        System.out.print("Identifiant du document à réserver : ");
        String idDoc = scanner.nextLine().trim();

        String requete = "RESERVE " + numAbonne + " " + idDoc;
        ClientBttp client = new ClientBttp(hote, port);

        try {
            System.out.println("Envoi de la demande...");
            String reponse = client.envoyer(requete);
            System.out.println("Réponse du serveur : " + reponse);
        } catch (IOException e) {
            System.err.println("Erreur de connexion au serveur : " + e.getMessage());
        }

        scanner.close();
    }
}
