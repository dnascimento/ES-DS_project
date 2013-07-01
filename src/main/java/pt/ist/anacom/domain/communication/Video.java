package pt.ist.anacom.domain.communication;


/**
 * The Class Video.
 */
public class Video extends
        Video_Base {

    /**
     * Instantiates a new video.
     * 
     * @param cllerID the cller id
     * @param receiverID the receiver id
     * @param duration the duration
     */
    public Video(String cllerID, String receiverID, int duration) {
        super();
        setCallerID(cllerID);
        setReceiverID(receiverID);
        setDuration(duration);
    }

    public String toString() {
        return "Video";
    }
}
