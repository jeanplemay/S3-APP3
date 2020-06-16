/* **************************************************
Noms : Jean-Philippe Lemay & Juan Manuel Gallego
CIPs : lemj0601 & galj1704
FICHIER : Server.java
DESCRIPTION : Fonction main du serveur
 ************************************************** */

import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        new ServerThread().start();
    }
}