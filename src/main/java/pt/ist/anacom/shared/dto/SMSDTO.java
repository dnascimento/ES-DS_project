package pt.ist.anacom.shared.dto;

import java.io.Serializable;

public class SMSDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _src;
	private String _dest;
	private String _msg;
	private long _seqNumber;
	
	public SMSDTO(){
		
	}
	
	public SMSDTO(String src, String dest, String msg){
		this._src = src;
		this._dest = dest;
		this._msg = msg;
	}
	
	public SMSDTO(String src, String dest, String msg, long seqNumber){
		this._src = src;
		this._dest = dest;
		this._msg = msg;
        this._seqNumber = seqNumber;
	}

	public String getSrcNumber() {
		return _src;
	}

	public String getDestNumber() {
		return _dest;
	}

	public String getText() {
		return _msg;
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
		return " Dest_Number:"+this.getDestNumber()+" Src_Number:"+this.getSrcNumber()+" Text:"+this.getText();
	}
	
	
}
