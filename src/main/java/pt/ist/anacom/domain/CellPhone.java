package pt.ist.anacom.domain;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import pt.ist.anacom.domain.communication.Communication;
import pt.ist.anacom.domain.communication.SMS;
import pt.ist.anacom.domain.communication.Video;
import pt.ist.anacom.domain.communication.Voice;
import pt.ist.anacom.domain.mode.Mode;
import pt.ist.anacom.shared.dto.SMSDTO;
import pt.ist.anacom.shared.enumerados.CellPhoneMode;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;
import pt.ist.anacom.shared.exceptions.InvalidBalanceException;
import pt.ist.anacom.shared.exceptions.InvalidCommunicationException;
import pt.ist.anacom.shared.exceptions.NotEnoughBalanceException;

/**
 * The Class CellPhone.
 */
public abstract class CellPhone extends
        CellPhone_Base {

    /**
     * Saldo máximo do telemovel.
     */
    private static final int SALDO_MAX = 10000;

    /**
     * Constructor de CellPhone
     */
    public CellPhone() {
    }

    // **************************************************************************************
    //
    // CellPhone Communication Logic.
    //
    // **************************************************************************************

    /**
     * Adicionar comunicação realizada. Verifica se há saldo suficiente antes de fazer a
     * comunicação. For this cellphone be able to make a communication it needs to: Be in
     * a compatible mode with the the type of communication Have enough Balance
     * 
     * @param sms the made communication
     * @throws NotEnoughBalanceException the not enough balance exception
     * @throws IncompatibleModeException the incompatible mode for communication exception
     */
    @Override
    public void addMadeSMS(SMS sms) throws NotEnoughBalanceException, IncompatibleModeException {
        final boolean ableToCommunicate = getMode().ableToSendSms();
        if (!ableToCommunicate) {
            throw new IncompatibleModeException("Incompatible Mode", this.getOperator().getSeqNumber());
        }

        if (this.getBalance() - sms.getCost() < 0) {
            throw new NotEnoughBalanceException("Erro: Saldo Insuficiente", this.getOperator().getSeqNumber());
        }

        // Ok, update balance and registry sms
        this.setBalance(this.getBalance() - sms.getCost());
        super.addMadeSMS(sms);
        super.setLastComunication(sms);
    }

    /**
     * Adds the received communication.
     * 
     * @param receivedCommunication the received communication
     * @throws IncompatibleModeException the incompatible mode for communication exception
     */
    @Override
    public void addReceivedSMS(SMS receivedCommunication) throws IncompatibleModeException {
        boolean ableToCommunicate = getMode().ableToReceiveSms();
        ;

        if (!ableToCommunicate) {
            throw new IncompatibleModeException("Modo incompatível de comunicação " + getMode().toString(), this.getOperator().getSeqNumber());
        }

        super.addReceivedSMS(receivedCommunication);
    }










    /**
     * Initiates the caller's a voice communication
     * 
     * @param number number on the other side of the communication
     * @throws IncompatibleModeException the incompatible mode for communication exception
     * @throws NotEnoughBalanceException Not Enough Balance Exception
     */
    public void initCallerVoiceCommunication(String receiverNumber) throws IncompatibleModeException, NotEnoughBalanceException {
        final boolean ableToCommunicate = this.getMode().ableToSendVoice();
        if (!ableToCommunicate) {
            throw new IncompatibleModeException("Caller Incompatible Mode, current mode is: " + this.getMode().toString(), this.getOperator().getSeqNumber());
        }

        if (receiverNumber.equals(getNumber())) {
            throw new IncompatibleModeException("Caller and destination are the same", this.getOperator().getSeqNumber());
        }

        if (this.getBalance() <= 0) {
            throw new NotEnoughBalanceException("Saldo Insuficiente, current is: " + this.getBalance(), this.getOperator().getSeqNumber());
        }

        // Guarda estado actual antes der ficar Busy para recuperar no fim
        super.setModeBeforeCall(this.getMode());

        this.changeMode(CellPhoneMode.BUSY);

        // guarda o numero com quem está a comunicar
        this.setSpeakingTo(receiverNumber);

        // guarda a flag de iniciador de chamada
        this.setIsCallInitiator(true);

        // Guarda timeStamp do incio da chamada
        this.setVoiceInitTime(new DateTime());
    }

    /**
     * Initiates the receiver's voice communication
     * 
     * @param number number on the other side of the communication
     * @throws IncompatibleModeException the incompatible mode for communication exception
     */
    public void initReceiverVoiceCommunication(String speakingTo) throws IncompatibleModeException {
        final boolean ableToCommunicate = this.getMode().ableToSendVoice();

        if (!ableToCommunicate) {
            throw new IncompatibleModeException("Receiver Incompatible Mode, current mode is: " + this.getMode().toString(), this.getOperator().getSeqNumber());
        }

        // Guarda estado actual antes der ficar Busy para recuperar no fim
        super.setModeBeforeCall(this.getMode());
        this.changeMode(CellPhoneMode.BUSY);

        // guarda o numero com quem está a comunicar
        this.setSpeakingTo(speakingTo);

        // guarda a flag de receptor de chamada
        this.setIsCallInitiator(false);
    }


    /**
     * Get the duration of current ending voice communication Verifica se foi este o
     * telemovel que iniciou a chamada e se foi, obtem a sua duração e preenche o DTO que
     * lhe é passado.
     * 
     * @param voice
     */
    public Voice endCallerVoice_VerifyAndGetDuration(Voice voice) throws IncompatibleModeException {
        // se está able, é porque não foi ele que iniciou a chamada
        final boolean ableToCommunicate = this.getMode().ableToSendVoice();
        if (ableToCommunicate) {
            throw new IncompatibleModeException("Cannot End Call: No call being made because is able to comunicate", this.getOperator().getSeqNumber());
        }

        if (!(this.getIsCallInitiator())) {
            throw new IncompatibleModeException("Cannot End Call: Not Caller, flag callinitiator not set", this.getOperator().getSeqNumber());
        }
        // se o iniciador foi este numero de telemovel e o receptor o "speakingTo"
        if (voice.getReceiverID().equals(this.getSpeakingTo())) {

            // TimeStamp do fim da chamada
            DateTime endTimeVoice = new DateTime();

            // Calcula a duracao em segundos da chamada
            int sec = Seconds.secondsBetween(super.getVoiceInitTime(), endTimeVoice).getSeconds();

            //System.out.println("A chamada teve duranção: " + sec);
            // Acrescenta a duracao a chamada
            voice.setDuration(sec);
            return voice;
        } else {
            throw new IncompatibleModeException("Cannot End Call: Not same destination", this.getOperator().getSeqNumber());
        }
    }


    /**
     * 1º INVOCAR O endCallerVoice_VerifyAndGetDuration. Ends the caller's voice
     * communication. Call endCallerVoiceGetDuration before to get the duration of call.
     * 
     * @param voice
     * @return voice uptaded voice with the duration of the call
     * @throws IncompatibleModeException the incompatible mode for communication exception
     */
    public Voice endCallerVoiceCommunication(Voice voice) throws IncompatibleModeException {
        this.setBalance(getBalance() - voice.getCost());

        // Guardar a chamada feita
        addMadeVoice(voice);
        // Guardar a ultima chamada
        super.setLastComunication(voice);

        // repõe estado antes da chamada (forca porque o busy nao tolera mudanca de
        // estado)
        this.setMode(this.getModeBeforeCall());

        this.setSpeakingTo(null);

        // retorna a comunicacao actualizada (com duracao)
        return voice;
    }

    /**
     * Ends the receiver's voice communication
     * 
     * @param voice The ending voice communication
     * @throws IncompatibleModeException the incompatible mode for communication exception
     */
    public void endReceiverVoiceCommunication(Voice voice) throws IncompatibleModeException {
        final boolean ableToCommunicate = this.getMode().ableToReceiveVoice();
        System.out.println(voice.getDuration());
        if (ableToCommunicate) {
            throw new IncompatibleModeException("Cannot End Call: No call being received because its able to receive", this.getOperator().getSeqNumber());
        }

        // se o iniciador foi este numero de telemovel e o receptor o "speakingTo"
        if (!this.getIsCallInitiator() && voice.getCallerID().equals(this.getSpeakingTo())) {
            addReceivedVoice(voice);
            // (forca porque o busy nao tolera mudanca de estado)
            this.setMode(this.getModeBeforeCall());
            this.setSpeakingTo(null);

        } else {
            throw new IncompatibleModeException("Cannot End Call: Not Receiver", this.getOperator().getSeqNumber());
        }
    }


    /**
     * Adds a voice communication to the list of made voice communications
     * 
     * @param voice communication made
     */
    @Override
    public void addMadeVoice(Voice voice) {
        super.addMadeVoice(voice);
        super.setLastComunication(voice);
    }

    /**
     * Adds a voice communication to the list of received voice communications
     * 
     * @param voice communication received
     */
    @Override
    public void addReceivedVoice(Voice voice) {
        super.addReceivedVoice(voice);
    }


    /**
     * Adds a video communication to the list of made video communications
     * 
     * @param video communication made
     */
    @Override
    public void addMadeVideo(Video video) {
        super.addMadeVideo(video);
        super.setLastComunication(video);
    }

    /**
     * Adds a video communication to the list of received video communications
     * 
     * @param video communication received
     */
    @Override
    public void addReceivedVideo(Video video) {
        super.addReceivedVideo(video);
    }

    // **************************************************************************************
    //
    // CellPhone Mode Logic
    //
    // **************************************************************************************

    /**
     * Alterar o modo do telemovel: recebe o modo pretendido e pede ao modo actual para
     * mudar.
     * 
     * @param newMode modo para o qual o telemovel deve transitar
     * @throws IncompatibleModeException modo solicitado não existe ou não foi possivel
     *             transitar para esse modo
     */
    public void changeMode(CellPhoneMode newMode) throws IncompatibleModeException {
        Mode currentMode = this.getMode();
        switch (newMode) {
        case ON:
            currentMode.turnOn();
            break;
        case OFF:
            currentMode.turnOff();
            break;
        case SILENCE:
            currentMode.turnSilence();
            break;
        case BUSY:
            currentMode.turnBusy();
            break;
        default:
            throw new IncompatibleModeException("Invalid mode", this.getOperator().getSeqNumber());
        }
    }




    // **************************************************************************************



    /**
     * Carregar o telemovel com "value" valor -> Carregar. nao aceita que o saldo com
     * bonus exceda os 100euros.
     * 
     * @param value : valor a somar ao saldo
     * @throws InvalidBalanceException não é permitido adicionar saldo
     */
    public void addBalance(int value) throws InvalidBalanceException {
        int newBalance = (int) (getBalance() + (1 + this.getOperator().getPlan().getBonus()) * value);

        if (value < 0)
            throw new InvalidBalanceException("Valor negativo não aceite", this.getOperator().getSeqNumber());
        if (newBalance > SALDO_MAX) {
            throw new InvalidBalanceException("Excedeu o valor do saldo", this.getOperator().getSeqNumber());
        }
        this.setBalance(newBalance);
    }

    /**
     * Obtem os 2 primeiros digitos do número.
     * 
     * @return os 2 primeiros digitos
     * @date 9/Mar/2012
     */
    public String getPrefix() {
        return getNumber().substring(0, 2);
    }


    /**
     * Gets the cellphone received SMS list.
     * 
     * @return the cellphone received SMS list
     */
    public List<SMSDTO> getCellPhoneReceivedSMS() {
        final List<SMSDTO> smsDtoList = new ArrayList<SMSDTO>();

        for (SMS sms : this.getReceivedSMS()) {
            smsDtoList.add(new SMSDTO(sms.getCallerID(), sms.getReceiverID(), sms.getMessage()));
        }
        return smsDtoList;
    }

    /**
     * Gets the cellphone sent SMS list.
     * 
     * @return the cellphone sent SMS list
     */
    public List<SMSDTO> getCellPhoneMadeSMS() {
        final List<SMSDTO> smsDtoList = new ArrayList<SMSDTO>();

        for (SMS sms : this.getMadeSMS()) {
            smsDtoList.add(new SMSDTO(sms.getCallerID(), sms.getReceiverID(), sms.getMessage()));
        }
        return smsDtoList;
    }



    /**
     * Checks if 2 mobile are equal.
     * 
     * @param phone the phone
     * @return true if equal
     */
    public boolean equals(CellPhone phone) {
        if (this.getNumber().equals(phone.getNumber())) {
            return true;
        }
        return false;
    }


    /**
     * Caso esteja uma chamada em curso devolve o numero com o qual se está a comunicar.
     * Caso contrario
     * 
     * @return the number to whom the voice/video call is made
     * @exception CellPhoneNotExistsException caso nao esteja a comunicar com outro
     *                telefone
     */
    @Override
    public String getSpeakingTo() throws CellPhoneNotExistsException {
        // if(super.getSpeakingTo().equalsIgnoreCase("")){
        if (super.getSpeakingTo() == null) {
            throw new CellPhoneNotExistsException("Cannot End Call: No call is being made", this.getOperator().getSeqNumber());
        } else {
            return super.getSpeakingTo();
        }
    }


    /**
     * Devolve a ultima comunicacao efectuada a partir deste telemovel
     * 
     * @return com ultima comunicacao efectuada
     * @exception InvalidCommunicationException se nenhuma comunicacao foi iniciada a
     *                partir deste telemovel
     */
    @Override
    public Communication getLastComunication() throws InvalidCommunicationException {

        Communication com = super.getLastComunication();

        if (com == null) {
            throw new InvalidCommunicationException("Não foi efectuada nehuma comunicação a partir deste telemovel", this.getOperator().getSeqNumber());
        }

        return com;
    }
}
