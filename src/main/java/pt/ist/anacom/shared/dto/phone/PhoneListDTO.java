package pt.ist.anacom.shared.dto.phone;

import java.io.Serializable;
import java.util.List;

public class PhoneListDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<PhoneNumValueDTO> phoneList;
	private long _seqNumber;


	public PhoneListDTO(){
		
	}
	
	public PhoneListDTO(List<PhoneNumValueDTO> list){
		this.phoneList = list;
	}
	
	public PhoneListDTO(List<PhoneNumValueDTO> list, long seqNumber){
		this.phoneList = list;
		this._seqNumber = seqNumber;
	}

	public List<PhoneNumValueDTO> getPhones(){
		return this.phoneList;
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
		String ret= new String();
		for(PhoneNumValueDTO dto : this.getPhones()){
			ret=ret+ " " +dto.toString();
		}
		return ret;
	}
}
