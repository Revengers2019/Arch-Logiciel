package serveur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;

/**
 * Serveur d'écoute générique sur un port donné.
 * Pour chaque connexion entrante, crée un Runnable via la factory fournie,
 * puis l'exécute dans un nouveau thread.
 *
 * Ce découplage (bibliothèque bserveur) permet de réutiliser ce serveur
 * pour n'importe quel type de traitement.
 */
public class ServeurEcoute implements Runnable {

    private final int port;
    private final String nom;
    private final Function<Socket, Runnable> factory;

    public ServeurEcoute(int port, String nom, Function<Socket, Runnable> factory) {
        this.port = port;
        this.nom = nom;
        this.factory = factory;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[" + nom + "] Serveur en écoute sur le port " + port);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket client = serverSocket.accept();
                    System.out.println("[" + nom + "] Connexion de " + client.getInetAddress());
                    Runnable traitement = factory.apply(client);
                    new Thread(traitement).start();
                } catch (IOException e) {
                    if (!Thread.currentThread().isInterrupted()) {
                        System.err.println("[" + nom + "] Erreur accept : " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[" + nom + "] Impossible d'ouvrir le port " + port + " : " + e.getMessage());
        }
    }
}
