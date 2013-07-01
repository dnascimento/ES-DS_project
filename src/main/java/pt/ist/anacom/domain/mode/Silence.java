package pt.ist.anacom.domain.mode;

import pt.ist.anacom.shared.enumerados.CellPhoneMode;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;

/** Modo Silence: Pode iniciar qualquer comunicação mas só pode receber sms */
public class Silence extends
        Silence_Base {

    public Silence() {
        super();
    }

    // Receive Methods
    public boolean ableToReceiveVideo() {
        return false;
    }

    public boolean ableToReceiveSms() {
        return true;
    }

    public boolean ableToReceiveVoice() {
        return false;
    }

    // Send Methods
    public boolean ableToSendVideo() {
        return true;
    }

    public boolean ableToSendSms() {
        return true;
    }

    public boolean ableToSendVoice() {
        return true;
    }

    @Override
    public CellPhoneMode getModeEnum() {
        return CellPhoneMode.SILENCE;
    }


    @Override
    public void turnOff() throws IncompatibleModeException {
        this.getCellPhone().setMode(new OFF());
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
        throw new IncompatibleModeException("Already Silence", this.getCellPhone().getOperator().getSeqNumber());

    }

    @Override
    public String toString() {
        return "Silence";
    }
}
