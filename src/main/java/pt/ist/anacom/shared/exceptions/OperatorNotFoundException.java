package pt.ist.anacom.shared.exceptions;

import java.io.Serializable;


public class OperatorNotFoundException extends AnaComException implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OperatorNotFoundException(){
		
	}
	
	public OperatorNotFoundException(String msg) {
		super(msg);
	}
	
	public OperatorNotFoundException(String msg, long seqNum) {
		super(msg, seqNum);
	}


}
