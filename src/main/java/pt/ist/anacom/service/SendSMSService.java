package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.CellPhone;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.domain.communication.SMS;
import pt.ist.anacom.shared.dto.SMSDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;
import pt.ist.anacom.shared.exceptions.NotEnoughBalanceException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;
import pt.ist.fenixframework.FenixFramework;

public class SendSMSService extends
AnaComService {

	SMSDTO _dto;


	public SendSMSService(SMSDTO dto) {
		this._dto = dto;
	}


	@Atomic
	public void execute() throws OperatorNotFoundException, CellPhoneNotExistsException, NotEnoughBalanceException, IncompatibleModeException {
		dispatch();
	}

	@Override
	public void dispatch() throws OperatorNotFoundException, CellPhoneNotExistsException, NotEnoughBalanceException, IncompatibleModeException {

		NetworkManager nm = FenixFramework.getRoot();

		Operator operator = nm.getOperatorByPhoneNumber(_dto.getSrcNumber());
		CellPhone cellphone = operator.getCellPhoneByNumber(_dto.getSrcNumber());

		SMS sms = new SMS(_dto.getSrcNumber(), _dto.getDestNumber(), _dto.getText());

		operator.getPlan().calculateCost(sms);
		cellphone.addMadeSMS(sms);
		operator.setSeqNumber(_dto.getSeqNumber());
	}

}
