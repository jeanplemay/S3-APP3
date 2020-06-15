import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void main(String[] args) throws IOException {

        Scanner in= new Scanner(System.in);
        System.out.print("Adresse ip du serveur :" );
        String serverIP = in.next();
        System.out.print("Nom du fichier à transférer : ");
        String file = in.next();

        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();

        // send request
        byte[] buf = new byte[256];
        InetAddress address = InetAddress.getByName(serverIP);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 25555);
        socket.send(packet);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Quote of the Moment: " + received);

        socket.close();
    }
}