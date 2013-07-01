package pt.ist.anacom.shared.dto.phone;

import java.io.Serializable;

import pt.ist.anacom.shared.enumerados.CommunicationType;

public class InitiateCommunicationDTO
        implements Serializable {


    /**
     * Serial version
     */
    private static final long serialVersionUID = 1L;

    /**
     * The type of communication that will be initiated
     */
    private CommunicationType _communicationBeingMade;

    /**
     * Boolean that identifies is the CellPhone is apt to initiate the Communication
     */
    private boolean _ableToInitiateCommunication;

    /**
     * The CellPhone Number being questioned
     */
    private String _cellPhoneNumber;

    /**
     * The timestamp/seqNumber of the DTO
     */
    private long _seqNumber;


    /**
     * InitiateCommunicationDTO Constructor
     * 
     * @param cellPhoneNumber the cellphone number being questioned
     * @param communicationBeingMade the type communication being made
     */
    public InitiateCommunicationDTO(String cellPhoneNumber, CommunicationType communicationBeingMade, long seqNumber) {
        setCellPhoneNumber(cellPhoneNumber);
        setCommunicationBeingMade(communicationBeingMade);
        setSeqNumber(seqNumber);
        setAbleToInitiateCommunication(false);
    }




    public InitiateCommunicationDTO(String cellPhoneNumber, boolean ableToCommunicate, long seqNumber) {
        setCellPhoneNumber(cellPhoneNumber);
        setSeqNumber(seqNumber);
        setAbleToInitiateCommunication(ableToCommunicate);
    }




    /**
     * getCommunicationBeingMade
     * 
     * @return the type of the communication being made
     */
    public CommunicationType getCommunicationBeingMade() {
        return _communicationBeingMade;
    }

    /**
     * setCommunicationBeingMade
     * 
     * @param communicationBeingMade the type of communication that will be initiated
     */
    private void setCommunicationBeingMade(CommunicationType communicationBeingMade) {
        _communicationBeingMade = communicationBeingMade;
    }

    /**
     * isAbleToInitiateCommunication Method
     * 
     * @return true if it is, false if it is not
     */
    public boolean isAbleToInitiateCommunication() {
        return _ableToInitiateCommunication;
    }


    /**
     * Sets the state of being able to communicate
     * 
     * @param _ableToInitiateCommunication true if it is, false if it is not
     */
    public void setAbleToInitiateCommunication(boolean _ableToInitiateCommunication) {
        this._ableToInitiateCommunication = _ableToInitiateCommunication;
    }




    /**
     * @return the _cellPhoneNumber
     */
    public String getCellPhoneNumber() {
        return _cellPhoneNumber;
    }



    /**
     * @param _cellPhoneNumber the _cellPhoneNumber to set
     */
    public void setCellPhoneNumber(String cellPhoneNumber) {
        this._cellPhoneNumber = cellPhoneNumber;
    }



    /**
     * @return the _seqNumber
     */
    public long getSeqNumber() {
        return _seqNumber;
    }


    /**
     * @param _seqNumber the _seqNumber to set
     */
    public void setSeqNumber(long _seqNumber) {
        this._seqNumber = _seqNumber;
    }

    /**
     * toString Method
     */
    public String toString() {
        String toStringMessage = getCellPhoneNumber();

        if (isAbleToInitiateCommunication()) {
            toStringMessage = toStringMessage + " is able to ";
        } else {
            toStringMessage = toStringMessage + " is not able to ";
        }

        toStringMessage = toStringMessage + getCommunicationBeingMade();

        return toStringMessage;

    }
}
