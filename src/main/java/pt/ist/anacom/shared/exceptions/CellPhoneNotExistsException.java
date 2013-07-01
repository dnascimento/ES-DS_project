package pt.ist.anacom.shared.exceptions;

import java.io.Serializable;


public class CellPhoneNotExistsException extends AnaComException implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public CellPhoneNotExistsException() {
		super();
	}
	public CellPhoneNotExistsException(String msg) {
		super(msg);
	}
	
	
	public CellPhoneNotExistsException(String msg, long seqNum) {
		super(msg, seqNum);
	}
	

}
