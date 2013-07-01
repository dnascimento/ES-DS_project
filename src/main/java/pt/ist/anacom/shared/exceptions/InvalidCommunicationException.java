package pt.ist.anacom.shared.exceptions;

import java.io.Serializable;

public class InvalidCommunicationException extends AnaComException implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidCommunicationException() {
		super();
	}
	
	public InvalidCommunicationException(String msg) {
		super(msg);
	}
	
	public InvalidCommunicationException(String msg, long seqNum) {
		super(msg, seqNum);
	}
}
