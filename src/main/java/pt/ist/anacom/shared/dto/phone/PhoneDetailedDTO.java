/******** Projecto ES - SD - Anacom *********
 **     Grupo ES: 03 Grupo SD: 11          **
 ********************************************/
package pt.ist.anacom.shared.dto.phone;

import java.io.Serializable;

import pt.ist.anacom.shared.enumerados.CellPhoneType;




/**
 * DTO para transporte de todos os dados de um Telemovel.
 * 
 */
public class PhoneDetailedDTO extends PhoneNumValueDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The _cell phone type. */
	private CellPhoneType _cellPhoneType;
	
	/** The _operator. */
	private String _operator;
	private long _seqNumber;



	public PhoneDetailedDTO(){
		super();
	}
	
	/**Instantiates a new phone detailed dto.
	 * @param phone number
	 * @param balance
	 * @param phone type (2G, 3G)
	 * @param operator*/
	public PhoneDetailedDTO(String number,int balance,CellPhoneType type,String operator) {
		super(number,balance);
		this._cellPhoneType = type;
		this._operator = operator;
	}

	/**Instantiates a new phone detailed dto.
	 * @param phone number
	 * @param balance
	 * @param phone type (2G, 3G)
	 * @param operator
	 * @param seqNumber sequence number
	 * */
	public PhoneDetailedDTO(String number,int balance,CellPhoneType type,String operator, long seqNumber) {
		super(number,balance);
		this._cellPhoneType = type;
		this._operator = operator;
		this._seqNumber = seqNumber;
	}
	
	/**Gets the _cell phone type.
	 * @return the _cell phone type
	 */
	public CellPhoneType get_cellPhoneType() {
		return _cellPhoneType;
	}



	/** Sets the _cell phone type.
	 * @param _cellPhoneType*/
	public void set_cellPhoneType(CellPhoneType _cellPhoneType) {
		this._cellPhoneType = _cellPhoneType;
	}



	/**Gets the _operator.
	 * @return the _operator */
	public String get_operator() {
		return _operator;
	}



	/** Sets the _operator.
	 * @param _operator */
	public void set_operator(String _operator) {
		this._operator = _operator;
	}

	/**
	 * @return the _seqNumber
	 */
	public long getSeqNumber(){
		return this._seqNumber;
	}
	
	/**
	 * @return string contanining the object values
	 * */
	@Override
	public String toString(){
		return "Class:"+super.toString()+" Number:"+this.get_number()+" Opertor:"+this.get_operator()+" Balance:"+this.getBalance()+" Type:"+this.get_cellPhoneType().toString();
	}
}
