package pt.ist.anacom.shared.dto;

import java.io.Serializable;
import java.util.List;

import pt.ist.anacom.shared.dto.phone.PhoneNumValueDTO;

public class SMSListDTO
        implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private List<SMSDTO> smsList;
    private long _seqNumber;

 
	public SMSListDTO() {

    }

    public SMSListDTO(List<SMSDTO> list) {
        this.smsList = list;
    }
    
    public SMSListDTO(List<SMSDTO> list, long seqNumber) {
        this.smsList = list;
        this._seqNumber = seqNumber;
    }

    public List<SMSDTO> getSMSList() {
        return this.smsList;
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
		String ret = new String();
		for(SMSDTO dto : this.getSMSList()){
			ret=ret+ " " +dto.toString();
		}
		return ret;
	}
	
}
