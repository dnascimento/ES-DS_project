package pt.ist.anacom.shared.exceptions;

import java.io.Serializable;


public class OperatorException extends AnaComException implements Serializable{
	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public OperatorException(){
		super();
	}
	public OperatorException(String msg){
		super(msg);
	}

	public OperatorException(String msg, long seqNum) {
		super(msg, seqNum);
	}
}
