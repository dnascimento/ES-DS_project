package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.CellPhone;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.shared.dto.VoiceDTO;
import pt.ist.anacom.shared.exceptions.AnaComException;
import pt.ist.anacom.shared.exceptions.CellPhoneNotAvailableException;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;
import pt.ist.anacom.shared.exceptions.NotEnoughBalanceException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;
import pt.ist.fenixframework.FenixFramework;

public class InitCallerVoiceCommunicationService extends AnaComService {

	private VoiceDTO _dto;

	public InitCallerVoiceCommunicationService(VoiceDTO dto) {
		this._dto = dto;
	}

	@Atomic
	public void execute()  throws CellPhoneNotAvailableException, OperatorNotFoundException, CellPhoneNotExistsException, NotEnoughBalanceException, IncompatibleModeException {
		dispatch();
	}

	@Override
	public void dispatch()  throws CellPhoneNotAvailableException, OperatorNotFoundException, CellPhoneNotExistsException, NotEnoughBalanceException, IncompatibleModeException {
		NetworkManager nm = FenixFramework.getRoot();

		Operator op = nm.getOperatorByPhoneNumber(_dto.getSrcNumber());
		CellPhone cellPhone = op.getCellPhoneByNumber(_dto.getSrcNumber());

		cellPhone.initCallerVoiceCommunication(_dto.getDestNumber());
		op.setSeqNumber(_dto.getSeqNumber());
	}

}
