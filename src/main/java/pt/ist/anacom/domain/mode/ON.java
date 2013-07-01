package pt.ist.anacom.domain.mode;

import pt.ist.anacom.shared.enumerados.CellPhoneMode;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;


/** Mode ON: at this mode, the mobile can make and receive all kinds of communications */
public class ON extends
        ON_Base {

    public ON() {
        super();
    }

    // Receive Methods
    public boolean ableToReceiveVideo() {
        return true;
    }

    public boolean ableToReceiveSms() {
        return true;
    }

    public boolean ableToReceiveVoice() {
        return true;
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
        return CellPhoneMode.ON;
    }

    @Override
    public void turnOff() throws IncompatibleModeException {
        this.getCellPhone().setMode(new OFF());
    }

    @Override
    public void turnOn() throws IncompatibleModeException {
        throw new IncompatibleModeException("Already ON", this.getCellPhone().getOperator().getSeqNumber());

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
        return "ON";
    }


}
