/* **************************************************
Noms : Jean-Philippe Lemay & Juan Manuel Gallego
CIPs : lemj0601 & galj1704
FICHIER : Client.java
DESCRIPTION : Fonction main du client
 ************************************************** */
import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void main(String[] args) throws IOException {

        ArrayList<String> trames = new ArrayList<String>();
        Transport transport = new Transport();
        LiaisonDeDonnees liaison = new LiaisonDeDonnees();

        Scanner in= new Scanner(System.in);
        System.out.print("Adresse ip du serveur : " );
        String serverIP = in.next();
        System.out.print("Nom du fichier à transférer : ");
        String filePath = in.next();

        String fileContent="";
        File file = new File(filePath);

        try {
            // LECTURE DU FICHIER
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                fileContent = fileContent + data+"\n";
            }
            reader.close();

            // COUCHE TRANSPORT
            trames = transport.transportFromApplication(file.getName(), fileContent);

            // COUCHE LIAISON DE DONNÉES
            for(int i=0; i<trames.size(); i++)
            {
                trames.set(i, liaison.liaisonDeDonneesFromTransport(trames.get(i)));
            }

            // ENVOIE DES TRAMES
            DatagramSocket socket = new DatagramSocket();
            byte[] buf = new byte[256];
            InetAddress address = InetAddress.getByName(serverIP);

            for(int i =0; i < trames.size(); i++)
            if( i !=2 && i !=5 && i !=8) {// Utiliser pour simuler retransmissison de paquets
            {
                buf = trames.get(i).getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 25555);
                socket.send(packet);

                // RÉPONSE (ACK OU DEMANDE DE RETRANSMISSION
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Réponse reçue : " + received);

                //REMTRANSMISSION D'UN PAQUET
                if(received.substring(0, 8).equals("00000000"))
                {
                    int manquantNum = Integer.parseInt(received.substring(9,16),2);
                    buf = trames.get(manquantNum).getBytes();
                    DatagramPacket packet2 = new DatagramPacket(buf, buf.length, address, 25555);
                    socket.send(packet2);
                }
            }
            }
            socket.close();

        } catch (FileNotFoundException e) {
            System.out.println("Fichier introuvable.");
        }
    }
}