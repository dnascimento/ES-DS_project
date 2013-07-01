package pt.ist.anacom.shared.dto.operator;

import java.io.Serializable;

public class OperatorDetailedDTO extends
        OperatorSimpleDTO
        implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private String _prefix;
    int _smsTrf, _videoTrf, _voiceTrf;
    double _extraTrf, _bonus;
	private long _seqNumber;


    public OperatorDetailedDTO() {

    }

    public OperatorDetailedDTO(String name, String prefix, int smsTrf, int voiceTrf, int videoTrf, double extraTrf, double bonus) {
        super(name);
        this._prefix = prefix;
        this._smsTrf = smsTrf;
        this._videoTrf = videoTrf;
        this._voiceTrf = voiceTrf;
        this._extraTrf = extraTrf;
        this._bonus = bonus;
    }
    
    public OperatorDetailedDTO(String name, String prefix, int smsTrf, int voiceTrf, int videoTrf, double extraTrf, double bonus, long seqNumber) {
        super(name);
        this._prefix = prefix;
        this._smsTrf = smsTrf;
        this._videoTrf = videoTrf;
        this._voiceTrf = voiceTrf;
        this._extraTrf = extraTrf;
        this._bonus = bonus;
        this._seqNumber = seqNumber;
    }

    public String get_prefix() {
        return _prefix;
    }

    public int get_smsTrf() {
        return _smsTrf;
    }

    public int get_videoTrf() {
        return _videoTrf;
    }

    public int get_voiceTrf() {
        return _voiceTrf;
    }

    public double get_extraTrf() {
        return _extraTrf;
    }

    public double get_bonus() {
        return _bonus;
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
		return "Class:"+super.toString()+" Bonus:"+this.get_bonus()+" Extra_Tarif:"+this.get_extraTrf()+" Name:"+this.get_name()+" Prefix:"+this.get_prefix()+" SMS_Tarif:"+this.get_smsTrf()+" Video_Tarif:"+this.get_videoTrf()+" Voice_Tarif:"+this.get_voiceTrf();
	}
}
