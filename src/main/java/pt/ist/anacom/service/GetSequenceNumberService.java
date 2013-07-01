package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.CellPhone;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.shared.dto.operator.OperatorSimpleDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumValueDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumberDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;
import pt.ist.fenixframework.FenixFramework;

public class GetSequenceNumberService extends AnaComService {

	private PhoneNumberDTO _dto;

	private PhoneNumberDTO _result;

	public GetSequenceNumberService(PhoneNumberDTO dto) {
		_dto = dto;
	}

	@Atomic
	public void execute() throws OperatorNotFoundException,
			CellPhoneNotExistsException {
		dispatch();
	}

	/**
	 * Implementa lógica no servidor do operador para obter o numero de sequência actual.
	 * Preenche DTO com numero de sequência para devolver ao cliente que fez o
	 * pedido
	 * 
	 * @throws OperatorNotFoundException
	 * @throws CellPhoneNotExistsException
	 */
	@Override
	public void dispatch() throws OperatorNotFoundException, CellPhoneNotExistsException{
		NetworkManager nm = FenixFramework.getRoot();

		Operator op = nm.getOperatorByPhoneNumber(_dto.get_number());		
		this.set_result(new PhoneNumberDTO(_dto.get_number(), op.getSeqNumber()));
	}

	private void set_result(PhoneNumberDTO _result) {
		this._result = _result;
	}

	public PhoneNumberDTO getResult() {
		return _result;
	}

}