package client;

import java.io.IOException;
import java.util.Scanner;

/**
 * Application cliente d'emprunt (borne en médiathèque).
 * Usage : java client.ClientEmprunt [hote] [port]
 * Par défaut : localhost 2001
 *
 * Protocole bttp2.0 : envoie "EMPRUNTE <numAbonne> <idDoc>"
 */
public class ClientEmprunt {

    public static void main(String[] args) {
        String hote = args.length >= 1 ? args[0] : "localhost";
        int port = args.length >= 2 ? Integer.parseInt(args[1]) : 2001;

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Borne d'Emprunt Médiathèque ===");
        System.out.println("Connexion à " + hote + ":" + port);
        System.out.print("Numéro d'abonné : ");
        String numAbonne = scanner.nextLine().trim();

        System.out.print("Identifiant du document à emprunter : ");
        String idDoc = scanner.nextLine().trim();

        String requete = "EMPRUNTE " + numAbonne + " " + idDoc;
        ClientBttp client = new ClientBttp(hote, port);

        try {
            String reponse = client.envoyer(requete);
            System.out.println("Réponse : " + reponse);
        } catch (IOException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
        }

        scanner.close();
    }
}
