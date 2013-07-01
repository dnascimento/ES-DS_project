package pt.ist.anacom.shared.exceptions;

import java.io.Serializable;


public class IncompatibleModeException extends AnaComException implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public IncompatibleModeException() {
		super();
	}
	public IncompatibleModeException(String msg) {
		super(msg);
	}
	
	public IncompatibleModeException(String msg, long seqNum) {
		super(msg, seqNum);
	}
	

}
