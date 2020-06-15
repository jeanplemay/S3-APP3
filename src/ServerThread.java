import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread {

    protected DatagramSocket socket = null;
    private boolean run = true;
    private LiaisonDeDonnees liaison = new LiaisonDeDonnees();
    private Transport transport = new Transport();
    private ArrayList<String> paquets;

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

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Paquet reçu : " + received);

                // FONCTION QUI CHANGE UN BIT POUR SIMULER UNE ERREUR
                if(received.substring(32, 40).equals("00000010"))
                {
                    int index = received.lastIndexOf('0');
                    received= received.substring(0,index)+'1'+received.substring(index+1);
                }

                received = liaison.liaisonDeDonneesToTransport(received);

                paquets.add(received);
                Collections.sort(paquets);

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

                    // Paquet perdu
                    String dString2 = "00000000" + manquant.toString();
                    buf = dString2.getBytes();
                    InetAddress address2 = packet.getAddress();
                    int port2 = packet.getPort();
                    packet = new DatagramPacket(buf, buf.length, address2, port2);
                    socket.send(packet);
                }
                else
                {
                    // ACK
                    String dString = "11111111" + received.substring(0,8);
                    buf = dString.getBytes();
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    packet = new DatagramPacket(buf, buf.length, address, port);
                    socket.send(packet);
                }

                // FIN DE LA RÉCEPTION
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

            } catch (IOException | TransmissionErrorException e) {
                run = false;
                e.printStackTrace();

            }
        }
    }

}