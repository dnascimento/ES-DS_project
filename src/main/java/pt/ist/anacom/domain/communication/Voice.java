package pt.ist.anacom.domain.communication;


/**
 * The Class Voice.
 */
public class Voice extends
        Voice_Base {

    /**
     * Instantiates a new voice.
     * 
     * @param cllerID the cller id
     * @param receiverID the receiver id
     * @param duration the duration
     */
    public Voice(String cllerID, String receiverID, int duration) {
        super();
        setCallerID(cllerID);
        setReceiverID(receiverID);
        setDuration(duration);
    }

    public String toString() {
        return "Voice";
    }
}
