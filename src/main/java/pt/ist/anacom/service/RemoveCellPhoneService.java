package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.CellPhone;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.domain.network.Network2G;
import pt.ist.anacom.shared.dto.phone.PhoneNumberDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.InvalidNumberException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;
import pt.ist.fenixframework.FenixFramework;

public class RemoveCellPhoneService extends
AnaComService {

	private PhoneNumberDTO _dto;

	public RemoveCellPhoneService(PhoneNumberDTO dto) {
		super();
		this._dto = dto;
	}

	@Atomic
	public void execute() throws OperatorNotFoundException,
	CellPhoneNotExistsException,
	InvalidNumberException {
		dispatch();
	}


	@Override
	public void dispatch() throws OperatorNotFoundException,
	CellPhoneNotExistsException,
	InvalidNumberException {
		NetworkManager nm = FenixFramework.getRoot();
		Operator op = nm.getOperatorByPhoneNumber(this._dto.get_number());
		CellPhone cp = new Network2G(this._dto.get_number(), 0);
		op.removeCellPhone(cp);
		op.setSeqNumber(_dto.getSeqNumber());
	}
}
