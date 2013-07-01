package pt.ist.anacom.shared.exceptions;

import java.io.Serializable;


public class InvalidBalanceException extends AnaComException implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public InvalidBalanceException() {
		super();
	}
	public InvalidBalanceException(String msg) {
		super(msg);
	}
	
	
	public InvalidBalanceException(String msg, long seqNum) {
		super(msg, seqNum);
	}
	
}
