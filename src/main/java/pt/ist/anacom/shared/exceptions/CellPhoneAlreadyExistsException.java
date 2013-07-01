package pt.ist.anacom.shared.exceptions;

import java.io.Serializable;



public class CellPhoneAlreadyExistsException extends AnaComException implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public CellPhoneAlreadyExistsException() {
		super();
	}
	public CellPhoneAlreadyExistsException(String msg) {
		super(msg);
	}
	
	public CellPhoneAlreadyExistsException(String msg, long seqNum) {
		super(msg, seqNum);
	}

}
