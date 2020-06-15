import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.CRC32;

public class LiaisonDeDonnees
{
    private String trame;
    private int paquetsRecus;
    private int paquetsErreurCRC;
    private int paquetsPerdus;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public LiaisonDeDonnees()
    {
        this.trame="";
        paquetsRecus = 0;
        paquetsErreurCRC = 0;
        paquetsPerdus = 0;
    }

    public String liaisonDeDonneesFromTransport(String trameRecue)
    {
        this.trame = trameRecue;

        CRC32 crc = new CRC32();
        crc.update(trame.getBytes());
        StringBuffer crc2 = new StringBuffer(Long.toBinaryString(crc.getValue()));
        int numZeros = 32 - crc2.length();
        while(numZeros-- > 0) {
            crc2.insert(0, "0");
        }

        trame = crc2.toString() + trame;

        return trame;
    }


    public String liaisonDeDonneesToTransport(String trameRecue)
    {
        try {
            FileWriter writer = new FileWriter("liaisonDeDonnes.log",true);
            String text = dateFormat.format(new Date(System.currentTimeMillis()))+" Paquet reçu\n";
            writer.append(text);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        paquetsRecus++;
        this.trame = trameRecue;
        trame = trame.substring(32);

        CRC32 crc = new CRC32();
        crc.update(trame.getBytes());
        StringBuffer crc2 = new StringBuffer(Long.toBinaryString(crc.getValue()));
        int numZeros = 32 - crc2.length();
        while(numZeros-- > 0) {
            crc2.insert(0, "0");
        }
        if(!crc2.toString().equals(trameRecue.substring(0,32)))
        {
            paquetsErreurCRC++;
            try {
                FileWriter writer = new FileWriter("liaisonDeDonnes.log",true);
                String text = dateFormat.format(new Date(System.currentTimeMillis()))+" Erreur CRC\n";
                writer.append(text);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return trame;
    }

    public int getPaquetsPerdus()
    {
        return paquetsPerdus;
    }

    public void setPaquetsPerdus(int paquetsPerdus)
    {
        this.paquetsPerdus = paquetsPerdus;
    }
}
