package pt.ist.anacom.shared.exceptions;

import java.io.Serializable;


public class InvalidNumberException extends AnaComException implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public InvalidNumberException() {
		super();
	}
	public InvalidNumberException(String msg) {
		super(msg);
	}
	
	public InvalidNumberException(String msg, long seqNum) {
		super(msg, seqNum);
	}
	

}
