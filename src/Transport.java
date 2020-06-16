/* **************************************************
Noms : Jean-Philippe Lemay & Juan Manuel Gallego
CIPs : lemj0601 & galj1704
FICHIER : Transport.java
DESCRIPTION : Classe de la couche de transport
 ************************************************** */

import java.util.ArrayList;

/**
 * Couche transport
 */
public class Transport
{
    /**
     * Couche Application vers couche Transport
     * @param fileName Nom du fichier
     * @param fileContent Contenu du fichier
     * @return Array liste des trames à envoyer
     */
    public ArrayList<String> transportFromApplication(String fileName, String fileContent)
    {
        ArrayList<String> paquets = new ArrayList<String>();
        ArrayList<String> trames = new ArrayList<String>();

        paquets.add(fileName);

        for(int i =0; i < Math.ceil(fileContent.length()/200.0);i++)
        {
            if(fileContent.length() >= ((i+1)*200)-1)
            {
                paquets.add(fileContent.substring(i*200, ((i+1)*200)-1));
            }
            else
            {
                paquets.add(fileContent.substring(i*200));
            }
        }

        StringBuffer tailleBuffer = new StringBuffer(Integer.toBinaryString(paquets.size()-1));
        int numZeros2 = 8 - tailleBuffer.length();
        while(numZeros2-- > 0) {
            tailleBuffer.insert(0, "0");
        }
        String taille = tailleBuffer.toString();

        for (int i=0; i <paquets.size(); i++)
        {
            StringBuffer num = new StringBuffer(Integer.toBinaryString(i));
            int numZeros = 8 - num.length();
            while(numZeros-- > 0) {
                num.insert(0, "0");
            }
            trames.add(num.toString() + taille + paquets.get(i));
        }
        return trames;
    }

    /**
     * Couche Transport vers couche Application
     * @param paquets Array liste des trames
     * @return Tableau de 2 String. Index 0 : nom du fichier, Index 1 : Contenu du fichier
     */
    public String[] transportToApplication(ArrayList<String> paquets)
    {
        String fileName = paquets.get(0).substring(16);
        String fileContent="";

        for (int i=1; i<paquets.size(); i++)
        {
            fileContent = fileContent + paquets.get(i).substring(16);
        }

        String retour[] ={fileName, fileContent};
        return retour;
    }
}
