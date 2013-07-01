package pt.ist.anacom.shared.dto.phone;

import java.io.Serializable;

import pt.ist.anacom.shared.enumerados.CellPhoneMode;
import pt.ist.anacom.shared.enumerados.CommunicationType;

public class PhoneNumModeDTO extends PhoneNumberDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private CellPhoneMode _mode;
	

	

	public PhoneNumModeDTO() {

	}

	public PhoneNumModeDTO(String cellPhoneNumber, CellPhoneMode newCellPhoneMode) {
		super(cellPhoneNumber);
		setMode(newCellPhoneMode);
	}

	public PhoneNumModeDTO(String cellPhoneNumber, String newCellPhoneMode) {
		super(cellPhoneNumber);
		if(newCellPhoneMode.equals(CellPhoneMode.ON.toString())){
			setMode(CellPhoneMode.ON);
		} else if(newCellPhoneMode.equals(CellPhoneMode.OFF.toString())){
			setMode(CellPhoneMode.OFF);
		} else if(newCellPhoneMode.equals(CellPhoneMode.BUSY.toString())){
			setMode(CellPhoneMode.BUSY);
		} else if(newCellPhoneMode.equals(CellPhoneMode.SILENCE.toString())){
			setMode(CellPhoneMode.SILENCE);
		} 

	}


	public PhoneNumModeDTO(String cellPhoneNumber, CellPhoneMode newCellPhoneMode, long seqNumber) {
		set_number(cellPhoneNumber);
		setMode(newCellPhoneMode);
		setSeqNumber(seqNumber);
	}

	public CellPhoneMode getMode() {
		return _mode;
	}

	public void setMode(CellPhoneMode mode) {
		this._mode = mode;
	}

	/**
	 * @return string contanining the object values
	 * */
	 @Override
	 public String toString(){
		return "Class:"+super.toString()+" Number:"+this.get_number()+" Mode"+this.getMode();
	 }


}
