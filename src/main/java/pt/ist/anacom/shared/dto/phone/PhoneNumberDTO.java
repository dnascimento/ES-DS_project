/******** Projecto ES - SD - Anacom *********
 **     Grupo ES: 03 Grupo SD: 11          **
 ********************************************/
package pt.ist.anacom.shared.dto.phone;

import java.io.Serializable;

import pt.ist.anacom.shared.enumerados.CommunicationType;


/** DTO para transporte do Numero de Telemovel*/
public class PhoneNumberDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _cellPhoneNumber;
	

	
	private long _seqNumber;
	
	public PhoneNumberDTO(){
		
	}
	
	/** Instantiates a new removes the cell phone dto.
	 * @param numero de telefone*/
	public PhoneNumberDTO(String _number) {
		this._cellPhoneNumber = _number;
	}

	/** Instantiates a new cell phone dto.
	 * @param numero de telefone
	 * @param seqNumber sequence number
	 * */
	public PhoneNumberDTO(String _number, long seqNumber) {
		this._cellPhoneNumber = _number;
	    this._seqNumber = seqNumber;
	}

	
	/** Gets the _number.
	 * @return the _number*/
	public String get_number() {
		return _cellPhoneNumber;
	}

	/**Sets the _number.
	 * @param _number*/
	public void set_number(String _number) {
		this._cellPhoneNumber = _number;
	}

	/**
	/**
	 * @return the _seqNumber
	 */
	public long getSeqNumber(){
		return this._seqNumber;
	}
	

	public void setSeqNumber(long seqNumber) {
		_seqNumber = seqNumber;
	}
	
	
	/**
	 * @return string contanining the object values
	 * */
	@Override
	public String toString(){
		return "Number"+this.get_number();
	}


}
