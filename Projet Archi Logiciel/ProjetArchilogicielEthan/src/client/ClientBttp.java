package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Client bttp2.0 générique.
 * Envoie une requête sur l'hôte/port donné et retourne la réponse du serveur.
 * Le numéro de port peut être passé en argument du main des clients.
 */
public class ClientBttp {

    private final String hote;
    private final int port;

    public ClientBttp(String hote, int port) {
        this.hote = hote;
        this.port = port;
    }

    /**
     * Envoie une requête et retourne la réponse.
     */
    public String envoyer(String requete) throws IOException {
        try (Socket socket = new Socket(hote, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(requete);
            return in.readLine();
        }
    }
}
