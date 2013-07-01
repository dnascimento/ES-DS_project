package pt.ist.anacom.shared.dto;
import java.io.Serializable;

public class CommunicationDTO implements Serializable{
	

	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	private String _destNumber;
	private String _type;
	private Integer _cost;
	private Integer _size;
	private long _seqNumber;
	
	public CommunicationDTO(){}

	public CommunicationDTO(String destNumber, String type, Integer cost, Integer size){
		super();
		this._destNumber = destNumber;
		this._type = type;
		this._cost = cost;
		this._size = size;
	}
	
	public CommunicationDTO(String destNumber, String type, Integer cost, Integer size, long seqNumber){
		super();
		this._destNumber = destNumber;
		this._type = type;
		this._cost = cost;
		this._size = size;
		this._seqNumber = seqNumber;
	}
	
	
	public String get_destNumber() {
		return _destNumber;
	}

	public String get_type() {
		return _type;
	}

	public Integer get_cost() {
		return _cost;
	}

	public Integer get_size() {
		return _size;
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
		return " Des_Number:"+this.get_destNumber()+" Type"+this.get_type()+" Cost:"+this.get_cost()+" Lenght:"+this.get_size();
	}

}

