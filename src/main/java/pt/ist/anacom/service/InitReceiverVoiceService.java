package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.CellPhone;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.shared.dto.VoiceDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumberDTO;
import pt.ist.anacom.shared.exceptions.AnaComException;
import pt.ist.anacom.shared.exceptions.CellPhoneNotAvailableException;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;
import pt.ist.anacom.shared.exceptions.NotEnoughBalanceException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;
import pt.ist.fenixframework.FenixFramework;

public class InitReceiverVoiceService extends AnaComService {

	private VoiceDTO _dto;

	public InitReceiverVoiceService(VoiceDTO dto) {
		this._dto = dto;
	}

	@Atomic
	public void execute() throws CellPhoneNotAvailableException, OperatorNotFoundException, CellPhoneNotExistsException, NotEnoughBalanceException, IncompatibleModeException {
		dispatch();
	}

	@Override
	public void dispatch() throws CellPhoneNotAvailableException, OperatorNotFoundException, CellPhoneNotExistsException, NotEnoughBalanceException, IncompatibleModeException {
		NetworkManager nm = FenixFramework.getRoot();

		Operator op = nm.getOperatorByPhoneNumber(_dto.getDestNumber());
		CellPhone cellPhone = op.getCellPhoneByNumber(_dto.getDestNumber());

		cellPhone.initReceiverVoiceCommunication(_dto.getSrcNumber());
		op.setSeqNumber(_dto.getSeqNumber());

	}

}
