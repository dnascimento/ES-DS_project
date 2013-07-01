package pt.ist.anacom.shared.dto.operator;

import java.io.Serializable;

public class OperatorSimpleDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _name;
	private long _seqNumber;
	
	public OperatorSimpleDTO(){
		
	}
	
	public OperatorSimpleDTO(String name){
		this._name = name;
	}
	
	public OperatorSimpleDTO(String name, long seqNumber){
		this._name = name;
		this._seqNumber = seqNumber;
	}
	
	public String get_name() {
		return _name;
	}
	public void set_name(String _name) {
		this._name = _name;
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
		return " Name:"+this.get_name();
	}
}
