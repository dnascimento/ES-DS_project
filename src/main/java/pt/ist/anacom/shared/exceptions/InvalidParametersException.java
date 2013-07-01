package pt.ist.anacom.shared.exceptions;

import java.io.Serializable;


public class InvalidParametersException extends AnaComException implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidParametersException() {
		super();
	}

	public InvalidParametersException(String msg) {
		super(msg);
	}

	public InvalidParametersException(String msg, long seqNum) {
		super(msg, seqNum);
	}
}
