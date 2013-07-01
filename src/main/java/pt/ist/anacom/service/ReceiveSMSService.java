package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.CellPhone;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.domain.communication.SMS;
import pt.ist.anacom.shared.dto.SMSDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;
import pt.ist.fenixframework.FenixFramework;

public class ReceiveSMSService extends
AnaComService {

	SMSDTO _dto;

	public ReceiveSMSService(SMSDTO dto) {
		this._dto = dto;
	}

	@Atomic
	public void execute() throws OperatorNotFoundException, CellPhoneNotExistsException, IncompatibleModeException {
		dispatch();
	}

	@Override
	public void dispatch() throws OperatorNotFoundException, CellPhoneNotExistsException, IncompatibleModeException {
		final NetworkManager nm = FenixFramework.getRoot();

		final Operator op = nm.getOperatorByPhoneNumber(_dto.getDestNumber());
		final CellPhone cp = op.getCellPhoneByNumber(_dto.getDestNumber());

		cp.addReceivedSMS(new SMS(_dto.getSrcNumber(), _dto.getDestNumber(), _dto.getText()));
		op.setSeqNumber(_dto.getSeqNumber());
	}

}
