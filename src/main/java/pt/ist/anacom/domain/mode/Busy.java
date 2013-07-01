package pt.ist.anacom.domain.mode;

import pt.ist.anacom.shared.enumerados.CellPhoneMode;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;

/**
 * The Class Busy.
 */
public class Busy extends
        Busy_Base {

    /**
     * Instantiates a new busy.
     */
    public Busy() {
        super();
    }

    // Receive Methods
    /*
     * (non-Javadoc)
     * 
     * @see pt.ist.anacom.domain.mode.Mode#ableToReceiveVideo()
     */
    public boolean ableToReceiveVideo() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.ist.anacom.domain.mode.Mode#ableToReceiveSms()
     */
    public boolean ableToReceiveSms() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.ist.anacom.domain.mode.Mode#ableToReceiveVoice()
     */
    public boolean ableToReceiveVoice() {
        return false;
    }

    // Send Methods
    /*
     * (non-Javadoc)
     * 
     * @see pt.ist.anacom.domain.mode.Mode#ableToSendVideo()
     */
    public boolean ableToSendVideo() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.ist.anacom.domain.mode.Mode#ableToSendSms()
     */
    public boolean ableToSendSms() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.ist.anacom.domain.mode.Mode#ableToSendVoice()
     */
    public boolean ableToSendVoice() {
        return false;
    }

    @Override
    public CellPhoneMode getModeEnum() {
        return CellPhoneMode.BUSY;
    }

    @Override
    public void turnOff() throws IncompatibleModeException {
        throw new IncompatibleModeException("I'm Busy bro, sorry, end the communication first", this.getCellPhone().getOperator().getSeqNumber());
    }

    @Override
    public void turnOn() throws IncompatibleModeException {
        throw new IncompatibleModeException("I'm Busy bro, sorry, end the communication first", this.getCellPhone().getOperator().getSeqNumber());
    }

    @Override
    public void turnBusy() throws IncompatibleModeException {
        throw new IncompatibleModeException("Already Busy", this.getCellPhone().getOperator().getSeqNumber());
    }

    @Override
    public void turnSilence() throws IncompatibleModeException {
        throw new IncompatibleModeException("I'm Busy bro, sorry, end the communication first", this.getCellPhone().getOperator().getSeqNumber());

    }

    @Override
    public String toString() {
        return "Busy";
    }

}
