package pt.ist.anacom.shared.dto;

import java.io.Serializable;

public class VoiceDTO
        implements Serializable {


    private static final long serialVersionUID = 1L;
    /**
	 * 
	 */

    private String _srcNumber;
    private String _destNumber;
    private int _duration;
    private long _seqNumber;
    private double _cost;

    public VoiceDTO() {
    }

    // DTO para iniciar voiceCall (sem duração)
    public VoiceDTO(String srcNumber, String destNumber) {
        super();
        this._srcNumber = srcNumber;
        this._destNumber = destNumber;
        this._duration = 0;
    }

    // DTO para iniciar voiceCall (sem duração)
    public VoiceDTO(String srcNumber, String destNumber, long seqNumber) {
        super();
        this._srcNumber = srcNumber;
        this._destNumber = destNumber;
        this._seqNumber = seqNumber;
    }

    // DTO para finalizar voiceCall (com duração)
    public VoiceDTO(String srcNumber, String destNumber, int duration) {
        super();
        this._srcNumber = srcNumber;
        this._destNumber = destNumber;
        this._duration = duration;
    }

    // DTO para finalizar voiceCall (com duração e custo)
    public VoiceDTO(String srcNumber, String destNumber, int duration, double cost) {
        super();
        this._srcNumber = srcNumber;
        this._destNumber = destNumber;
        this._duration = duration;
        this._cost = cost;
    }


    public double getCost() {
        return _cost;
    }

    public void setCost(double _cost) {
        this._cost = _cost;
    }

    // DTO para finalizar voiceCall (com duração)
    public VoiceDTO(String srcNumber, String destNumber, int duration, long seqNumber) {
        super();
        this._srcNumber = srcNumber;
        this._destNumber = destNumber;
        this._duration = duration;
        this._seqNumber = seqNumber;
    }


    public VoiceDTO(String srcNumber) {
        this._srcNumber = srcNumber;
    }

    public String getSrcNumber() {
        return _srcNumber;
    }

    public String getDestNumber() {
        return _destNumber;
    }

    public int getDuration() {
        return _duration;
    }

    /**
     * @return the _seqNumber
     */
    public long getSeqNumber() {
        return this._seqNumber;
    }

    /**
     * @return string contanining the object values
     */
    @Override
    public String toString() {
        return " Dest_Number:" + this.getDestNumber() + " Duration:" + this.getDuration() + " Src_Number:" + this.getSrcNumber();
    }
}
