/* **************************************************
Noms : Jean-Philippe Lemay & Juan Manuel Gallego
CIPs : lemj0601 & galj1704
FICHIER : ServerThread.java
DESCRIPTION : Thread pour le serveur
 ************************************************** */


import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread {

    protected DatagramSocket socket = null;
    private boolean run = true;
    private LiaisonDeDonnees liaison = new LiaisonDeDonnees();
    private Transport transport = new Transport();
    private ArrayList<String> paquets;

    private boolean generateError = true;

    public ServerThread() throws IOException {
        this("ServerThread");
    }

    public ServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(25555);
    }

    public void run() {

        paquets = new ArrayList<String>();
        while (run) {
            try {
                byte[] buf = new byte[256];

                // RÉCEPTION TRAME
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Trame reçue : " + received);

                // FONCTION QUI CHANGE UN BIT POUR SIMULER UNE ERREUR (2e trame)
                if(received.substring(32, 40).equals("00000010") && generateError)
                {
                    int index = received.lastIndexOf('0');
                    received= received.substring(0,index)+'1'+received.substring(index+1);

                    generateError = false;
                }

                // COUCHE LIAISON DE DONNÉES
                received = liaison.liaisonDeDonneesToTransport(received);
                if(received != null)
                {
                    paquets.add(received);
                    Collections.sort(paquets);

                    // VÉRIFIER SI LE PAQUET PRÉCÉDENT ÉTAIT MANQUANT
                    int dernierNum = Integer.parseInt(paquets.get(paquets.size()-1).substring(0,8),2);
                    int avantDenierNum = -1;
                    if(paquets.size()>1) avantDenierNum = Integer.parseInt(paquets.get(paquets.size()-2).substring(0,8),2);

                    if( dernierNum != avantDenierNum+1)
                    {
                        liaison.setPaquetsPerdus(liaison.getPaquetsPerdus()+1);
                        if(liaison.getPaquetsPerdus() >=3 )
                        {
                            throw new TransmissionErrorException();
                        }
                        StringBuffer manquant = new StringBuffer(Integer.toBinaryString(avantDenierNum+1));
                        int numZeros2 = 8 - manquant.length();
                        while(numZeros2-- > 0) {
                            manquant.insert(0, "0");
                        }

                        // ENVOYER DEMANDE DE RETRANSMISSION
                        String dString2 = "00000000" + manquant.toString();
                        buf = dString2.getBytes();
                        InetAddress address2 = packet.getAddress();
                        int port2 = packet.getPort();
                        packet = new DatagramPacket(buf, buf.length, address2, port2);
                        socket.send(packet);
                    }
                    else
                    {
                        // ENVOYER ACK
                        String dString = "11111111" + received.substring(0,8);
                        buf = dString.getBytes();
                        InetAddress address = packet.getAddress();
                        int port = packet.getPort();
                        packet = new DatagramPacket(buf, buf.length, address, port);
                        socket.send(packet);
                    }

                    // FIN DE LA RÉCEPTION (COUCHE TRANSPORT)
                    if(paquets.size()-1 == Integer.parseInt(paquets.get(0).substring(9,16),2) )
                    {
                        String retour[] = transport.transportFromApplication(paquets);
                        try {
                            FileWriter writer = new FileWriter(retour[0]);
                            String text = retour[1];
                            writer.write(text);
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        liaison = new LiaisonDeDonnees();
                        transport = new Transport();
                        paquets = new ArrayList<String>();
                    }
                }
                else
                {
                    // ENVOYER ERREUR
                    String dString2 = "00000001";
                    buf = dString2.getBytes();
                    InetAddress address2 = packet.getAddress();
                    int port2 = packet.getPort();
                    packet = new DatagramPacket(buf, buf.length, address2, port2);
                    socket.send(packet);
                }


            } catch (IOException | TransmissionErrorException e) {
                run = false;
                e.printStackTrace();
            }
        }
    }
}