package pt.ist.anacom.shared.exceptions;

import java.io.Serializable;


public class CellPhoneNotAvailableException extends AnaComException implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public CellPhoneNotAvailableException() {
		super();
	}
	public CellPhoneNotAvailableException(String msg) {
		super(msg);
	}
	
	public CellPhoneNotAvailableException(String msg, long seqNum) {
		super(msg, seqNum);
	}

}
