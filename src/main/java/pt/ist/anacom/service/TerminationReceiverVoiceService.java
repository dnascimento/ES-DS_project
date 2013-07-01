package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.CellPhone;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.domain.communication.Voice;
import pt.ist.anacom.shared.dto.VoiceDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneNotAvailableException;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;
import pt.ist.anacom.shared.exceptions.NotEnoughBalanceException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;
import pt.ist.fenixframework.FenixFramework;

public class TerminationReceiverVoiceService extends
        AnaComService {

    private VoiceDTO _dto;

    public TerminationReceiverVoiceService(VoiceDTO dto) {
        this._dto = dto;
    }

    @Atomic
    public void execute() throws CellPhoneNotAvailableException, OperatorNotFoundException, CellPhoneNotExistsException, NotEnoughBalanceException, IncompatibleModeException {
        dispatch();
    }



    @Override
    public void dispatch() throws CellPhoneNotAvailableException, OperatorNotFoundException, CellPhoneNotExistsException, NotEnoughBalanceException, IncompatibleModeException {
        NetworkManager nm = FenixFramework.getRoot();

        String destPhoneNumber = _dto.getDestNumber();

        Operator destOp = nm.getOperatorByPhoneNumber(destPhoneNumber);
        CellPhone destCellPhone = destOp.getCellPhoneByNumber(destPhoneNumber);
        Voice voice = new Voice(_dto.getSrcNumber(), _dto.getDestNumber(), _dto.getDuration());


        destCellPhone.endReceiverVoiceCommunication(voice);
        destOp.setSeqNumber(_dto.getSeqNumber());
    }

}
