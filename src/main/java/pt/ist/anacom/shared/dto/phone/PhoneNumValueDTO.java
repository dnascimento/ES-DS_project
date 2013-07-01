/******** Projecto ES - SD - Anacom *********
 **     Grupo ES: 03 Grupo SD: 11          **
 ********************************************/
package pt.ist.anacom.shared.dto.phone;

import java.io.Serializable;


/**
 * DTO para transporte do Numero de Telemovel.
 * 
 */
public class PhoneNumValueDTO extends PhoneNumberDTO implements
Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The balance. */
	private int balance;
	private long _seqNumber;
	
	public PhoneNumValueDTO() {
		
	}
	
	/** Instantiates a new removes the cell phone dto.
	 * @param phone number
	 * @param balance*/
	public PhoneNumValueDTO(String number, int balance) {
		super(number);
		this.balance = balance;
	}

	/** Instantiates a new PhoneNumValueDTO.
	 * @param seqNymber
	 * @param phone number
	 * @param balance*/
	public PhoneNumValueDTO(String number, int balance, long seqNumber) {
		super(number);
		this.balance = balance;
		this._seqNumber = seqNumber;
	}


	/** Gets the balance.
	 * @return the balance*/
	public int getBalance() {
		return balance;
	}

	/**Sets the balance.
	 * @param balance*/
	public void setBalance(int balance) {
		this.balance = balance;
	}

	/**
	 * @return the _seqNumber
	 */
	public long getSeqNumber(){
		return this._seqNumber;
	}

	@Override
	public String toString(){
		return "Number:"+this.get_number()+" Balance:"+this.getBalance();
	}
	
}
