package pt.ist.anacom.domain.mode;

import pt.ist.anacom.shared.enumerados.CellPhoneMode;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;


/**
 * The Class Mode.
 */
public abstract class Mode extends
        Mode_Base {

    /**
     * Instantiates a new mode.
     */
    public Mode() {
        super();
    }

    // Receive Methods
    /**
     * Able to receive video.
     * 
     * @return boolean
     */
    public abstract boolean ableToReceiveVideo();

    /**
     * Able to receive sms.
     * 
     * @return boolean
     */
    public abstract boolean ableToReceiveSms();

    /**
     * Able to receive voice.
     * 
     * @return boolean
     */
    public abstract boolean ableToReceiveVoice();

    // Send Methods
    /**
     * Able to send video.
     * 
     * @return boolean
     */
    public abstract boolean ableToSendVideo();

    /**
     * Able to send sms.
     * 
     * @return boolean
     */
    public abstract boolean ableToSendSms();

    /**
     * Able to send voice.
     * 
     * @return boolean
     */
    public abstract boolean ableToSendVoice();

    public abstract CellPhoneMode getModeEnum();


    /* Change State Methods */

    /**
     * Turn off.
     */
    public abstract void turnOff() throws IncompatibleModeException;

    public abstract void turnOn() throws IncompatibleModeException;

    public abstract void turnBusy() throws IncompatibleModeException;

    public abstract void turnSilence() throws IncompatibleModeException;

    public abstract String toString();

}
