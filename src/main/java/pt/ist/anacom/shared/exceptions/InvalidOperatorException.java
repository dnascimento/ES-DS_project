package pt.ist.anacom.shared.exceptions;

import java.io.Serializable;


public class InvalidOperatorException extends AnaComException implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _operatorName;

	public InvalidOperatorException() {
		super();
	}
	public InvalidOperatorException(String operatorName, String msg) {
		super(msg);
		_operatorName = operatorName;

	}
	
	public InvalidOperatorException(String operatorName, String msg, long seqNum) {
		super(msg, seqNum);
		_operatorName = operatorName;

	}
	
	public InvalidOperatorException(String msg, long seqNum) {
		super(msg, seqNum);
	}
	
	public InvalidOperatorException(String msg) {
		super(msg);
	}
	public String get_OperatorName() {
		return _operatorName;
	}


}
