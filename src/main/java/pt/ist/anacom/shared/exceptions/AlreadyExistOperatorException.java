package pt.ist.anacom.shared.exceptions;

import java.io.Serializable;


public class AlreadyExistOperatorException extends AnaComException implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _opName;

	public AlreadyExistOperatorException() {
		super();
	}
	
	public AlreadyExistOperatorException(String operatorName, String msg) {
		super(msg);
		_opName = operatorName;

	}
	
	public AlreadyExistOperatorException(String msg, long seqNum) {
		super(msg, seqNum);
	}

	
	public String get_opName() {
		return _opName;
	}
	
	



}
