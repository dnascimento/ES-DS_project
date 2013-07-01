package pt.ist.anacom.shared.exceptions;

import java.io.Serializable;


public class OperatorAlreadyExistsException extends AnaComException implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public OperatorAlreadyExistsException(){
		super();
	}
	public OperatorAlreadyExistsException(String msg){
		super(msg);
	}

	public OperatorAlreadyExistsException(String msg, long seqNum) {
		super(msg, seqNum);
	}
	
}
