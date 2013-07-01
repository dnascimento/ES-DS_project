package pt.ist.anacom.shared.exceptions;

import java.io.Serializable;


/**
 * The Class AnaComException.
 */

public abstract class AnaComException extends
        RuntimeException implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The _msg. */
    private String _msg;
    private long _sequenceNumber;

    /**
	 * @return the _sequenceNumber
	 */
	public long get_sequenceNumber() {
		return _sequenceNumber;
	}

	/**
	 * @param _sequenceNumber the _sequenceNumber to set
	 */
	public void set_sequenceNumber(long _sequenceNumber) {
		this._sequenceNumber = _sequenceNumber;
	}

	/**
     * Instantiates a new ana com exception.
     */
    public AnaComException() {
        super();
    }

    /**
     * Instantiates a new ana com exception.
     * 
     * @param msg the msg
     */
    public AnaComException(String msg) {
        _msg = msg;
    }

    
    /**
     * Instantiates a new anacom exception.
     * 
     * @param msg the msg
     */
    public AnaComException(String msg, long sequenceNumber) {
        _msg = msg;
        _sequenceNumber = sequenceNumber;
    }
    
    
    /**
     * Sets the msg.
     * 
     * @param msg the new msg
     */
    public void setMsg(String msg) {
        _msg = msg;
    }

    /**
     * Gets the msg.
     * 
     * @return the msg
     */
    public String getMsg() {
        return _msg;
    }

	/**
	 * @return string contanining the object values
	 * */
	@Override
	public String toString(){
		return this.getMsg();
	}
}
