/********
 * Projecto ES - SD - Anacom ********* Grupo ES: 03 Grupo SD: 11 **
 ********************************************/
package pt.ist.anacom.domain;

import pt.ist.anacom.domain.communication.SMS;
import pt.ist.anacom.domain.communication.Video;
import pt.ist.anacom.domain.communication.Voice;


/** Classe tarifa para calculo de custo das comunicações. */
public class Plan extends
        Plan_Base {

    /**
     * Tarifário da operadora.
     * 
     * @param smsTariff - Taxa de 100 chars de sms
     * @param voiceTariff - Taxa de 1 minuto de voz
     * @param videoTariff - Taxa de 1 minuto de voz
     * @param extraChargeTariff - Taxa: percentagem em decimal: 1.1 -> 10% extra
     * @param bonus - Bonus para os carregamentos de telemóvel
     */
    public Plan(int smsTariff, int voiceTariff, int videoTariff, double extraChargeTariff, double bonus) {
        setSMSTariff(smsTariff);
        setVideoTariff(videoTariff);
        setVoiceTariff(voiceTariff);
        setExtraChargeTariff(extraChargeTariff);
        setBonus(bonus);
    }

    /**
     * calculateSMSCost calcula o custo de uma SMS.
     * 
     * @param sms the sms
     */
    public void calculateCost(SMS sms) {
        int finalCost = (getSMSTariff() * (((sms.getLenghtSMS() - 1) / 100) + 1));
        finalCost = extraTax(sms.getCallerID(), sms.getReceiverID(), finalCost);
        sms.setCost(finalCost);
    }

    /**
     * calculateVoiceCost calcula o custo de uma chamada de VOZ.
     * 
     * @param voice the cm
     */
    public void calculateCost(Voice voice) {
        int finalCost = getVoiceTariff() * voice.getDuration();
        finalCost = extraTax(voice.getCallerID(), voice.getReceiverID(), finalCost);
        voice.setCost(finalCost);
        System.out.println("Tarifa: " + getVoiceTariff() + ", duração: " + voice.getDuration() + ", Custo da chamada: " + finalCost);

    }

    /**
     * calculateVideoCost calcula o custo de uma chamada de VOZ.
     * 
     * @param video the cm
     */
    public void calculateCost(Video video) {
        int finalCost = getVideoTariff() * video.getDuration();
        finalCost = extraTax(video.getCallerID(), video.getReceiverID(), finalCost);
        video.setCost(finalCost);
    }

    /**
     * Calcula a taxa extra sobre o custo final da comunicação caso a origem e destino
     * sejam distintos.
     * 
     * @param caller the caller number
     * @param receiver the receiver number
     * @param finalCost the final cost of communication
     * @return the value with extraTax included
     */
    private int extraTax(String caller, String receiver, int finalCost) {
        final String opCall = caller.substring(0, 2);
        final String opReceive = receiver.substring(0, 2);
        if (!opCall.equals(opReceive)) {
            // System.out.println("Extra tarif using: " + getExtraChargeTariff());
            return (int) (finalCost * (getExtraChargeTariff()));
        } else {
            return finalCost;
        }
    }

}
