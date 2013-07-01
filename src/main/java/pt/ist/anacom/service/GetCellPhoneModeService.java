package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.CellPhone;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.shared.dto.phone.PhoneNumModeDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumberDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.InvalidOperatorException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;
import pt.ist.fenixframework.FenixFramework;

public class GetCellPhoneModeService extends
AnaComService {
	private PhoneNumberDTO _dto;
	private PhoneNumModeDTO _result;

	public GetCellPhoneModeService(PhoneNumberDTO dto) {
		this._dto = dto;
	}

	@Atomic
	public void execute() throws InvalidOperatorException {
		dispatch();
	}

	@Override
	public void dispatch() throws OperatorNotFoundException, CellPhoneNotExistsException {
		final NetworkManager networkManager = FenixFramework.getRoot();

		Operator op = networkManager.getOperatorByPhoneNumber(_dto.get_number());
		CellPhone mobile = op.getCellPhoneByNumber(_dto.get_number());

		// Obter o modo, converte-lo para enumerado e guardar no DTO
		_result = new PhoneNumModeDTO(_dto.get_number(), mobile.getMode().getModeEnum(), op.getSeqNumber());
	}

	public PhoneNumModeDTO getCellPhoneModeDTO() {
		return _result;
	}

}
