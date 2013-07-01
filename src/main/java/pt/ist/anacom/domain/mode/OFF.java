package pt.ist.anacom.domain.mode;

import pt.ist.anacom.shared.enumerados.CellPhoneMode;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;


/** Mode OFF: Se desligado, não pode iniciar ou receber chamadas nem enviar SMS */
public class OFF extends
        OFF_Base {

    public OFF() {
        super();
    }

    // Receive Methods
    /**
     * @return boolean
     */
    public boolean ableToReceiveVideo() {
        return false;
    }

    /**
     * @return boolean
     */
    public boolean ableToReceiveSms() {
        return true;
    }

    /**
     * @return boolean
     */
    public boolean ableToReceiveVoice() {
        return false;
    }

    // Send Methods
    /**
     * @return boolean
     */
    public boolean ableToSendVideo() {
        return false;
    }

    /**
     * @return boolean
     */
    public boolean ableToSendSms() {
        return false;
    }

    /**
     * @return boolean
     */
    public boolean ableToSendVoice() {
        return false;
    }

    @Override
    public CellPhoneMode getModeEnum() {
        return CellPhoneMode.OFF;
    }

    @Override
    public void turnOff() throws IncompatibleModeException {
        throw new IncompatibleModeException("Already Off", this.getCellPhone().getOperator().getSeqNumber());
    }

    @Override
    public void turnOn() throws IncompatibleModeException {
        this.getCellPhone().setMode(new ON());

    }

    @Override
    public void turnBusy() throws IncompatibleModeException {
        this.getCellPhone().setMode(new Busy());

    }

    @Override
    public void turnSilence() throws IncompatibleModeException {
        this.getCellPhone().setMode(new Silence());

    }

    @Override
    public String toString() {
        return "OFF";
    }

}
