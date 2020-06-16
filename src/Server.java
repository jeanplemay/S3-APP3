/* **************************************************
Noms : Jean-Philippe Lemay & Juan Manuel Gallego
CIPs : lemj0601 & galj1704
FICHIER : Server.java
DESCRIPTION : Fonction main du serveur
 ************************************************** */

import java.io.*;

/**
 * Programme serveur
 */
public class Server {
    /**
     * Fonction main du client
     * @param args Arguments de l'application
     * @throws IOException Exception
     */
    public static void main(String[] args) throws IOException {
        new ServerThread().start();
    }
}