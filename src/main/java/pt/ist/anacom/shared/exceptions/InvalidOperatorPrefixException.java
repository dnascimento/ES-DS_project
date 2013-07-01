package pt.ist.anacom.shared.exceptions;

import java.io.Serializable;


public class InvalidOperatorPrefixException extends AnaComException implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int _prefixLen;

	public InvalidOperatorPrefixException(){
		
	}
	
	public InvalidOperatorPrefixException(int len) {
		super();
		_prefixLen = len;
	}
	
	public InvalidOperatorPrefixException(String msg, long seqNum) {
		super(msg, seqNum);
	}

	public int get_prefixLen() {
		return _prefixLen;
	}

}
