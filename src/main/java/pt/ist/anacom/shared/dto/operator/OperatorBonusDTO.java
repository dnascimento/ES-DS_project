package pt.ist.anacom.shared.dto.operator;

import java.io.Serializable;

/**
 * DTO que guarda o valor de um bónus
 * para uma Operadora
 * 
 */
public class OperatorBonusDTO  extends OperatorSimpleDTO
implements Serializable {
	
	private double bonus;
	private static final long serialVersionUID = 1L;
	private long _seqNumber;
	
	public  OperatorBonusDTO(){
	}
	
	public  OperatorBonusDTO(String name, double theBonus){
		super(name);
		setBonus(theBonus);		
	}
	
	public  OperatorBonusDTO(String name, double theBonus, long seqNumber){
		super(name);
		setBonus(theBonus);
		this._seqNumber = seqNumber;
	}
	
	public double getBonus() {
		return bonus;
	}
	
	public void setBonus(double newBonus) {
		this.bonus = newBonus;
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
		return super.toString()+" Name:"+this.get_name()+" Bonus:"+this.getBonus();
	}
}
