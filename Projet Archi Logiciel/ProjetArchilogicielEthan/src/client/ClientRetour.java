package client;

import java.io.IOException;
import java.util.Scanner;

/**
 * Application cliente de retour (borne en médiathèque).
 * Usage : java client.ClientRetour [hote] [port]
 * Par défaut : localhost 2002
 *
 * Protocole bttp2.0 : envoie "RETOUR <idDoc> [DEGRADE]"
 * L'option DEGRADE signale une dégradation du document (Certification Géronimo).
 */
public class ClientRetour {

    public static void main(String[] args) {
        String hote = args.length >= 1 ? args[0] : "localhost";
        int port = args.length >= 2 ? Integer.parseInt(args[1]) : 2002;

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Borne de Retour Médiathèque ===");
        System.out.println("Connexion à " + hote + ":" + port);
        System.out.print("Identifiant du document à rendre : ");
        String idDoc = scanner.nextLine().trim();

        System.out.print("Document dégradé ? (o/n) : ");
        String rep = scanner.nextLine().trim();
        boolean degrade = rep.equalsIgnoreCase("o") || rep.equalsIgnoreCase("oui");

        String requete = "RETOUR " + idDoc + (degrade ? " DEGRADE" : "");
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
