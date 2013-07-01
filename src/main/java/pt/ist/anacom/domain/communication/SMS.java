package pt.ist.anacom.domain.communication;

/**
 * The Class SMS.
 */
public class SMS extends
        SMS_Base {

    /**
     * Instantiates a new sMS.
     * 
     * @param cllerID the cller id
     * @param receiverID the receiver id
     * @param message the message
     */
    public SMS(String cllerID, String receiverID, String message) {
        super();
        setCallerID(cllerID);
        setReceiverID(receiverID);
        setMessage(message);
    }

    /**
     * Gets the lenght sms.
     * 
     * @return the lenght sms
     */
    public int getLenghtSMS() {
        return this.getMessage().length();
    }

    public String toString() {
        return "SMS";
    }
}
