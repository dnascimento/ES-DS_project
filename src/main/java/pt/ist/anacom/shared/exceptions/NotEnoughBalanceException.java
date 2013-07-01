package pt.ist.anacom.shared.exceptions;

import java.io.Serializable;


public class NotEnoughBalanceException extends AnaComException implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public NotEnoughBalanceException(){
		super();
	}
	public NotEnoughBalanceException(String msg){
		super(msg);
	}
	
	public NotEnoughBalanceException(String msg, long seqNum) {
		super(msg, seqNum);
	}

}
