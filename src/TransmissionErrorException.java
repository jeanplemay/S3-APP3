/* **************************************************
Noms : Jean-Philippe Lemay & Juan Manuel Gallego
CIPs : lemj0601 & galj1704
FICHIER : TransmissionErrorException.java
DESCRIPTION : Erreur lors de la transmission (trois paquets manquants)
 ************************************************** */

/**
 * Erreur lors de la transmission
 */
public class TransmissionErrorException extends Exception
{

    public TransmissionErrorException()
    {
        super("Erreur lors de la transmission (trois paquets manquants)");
    }
}
