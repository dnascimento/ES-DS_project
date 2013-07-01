package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.domain.network.Network2G;
import pt.ist.anacom.domain.network.Network3G;
import pt.ist.anacom.shared.dto.phone.PhoneDetailedDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneAlreadyExistsException;
import pt.ist.anacom.shared.exceptions.InvalidNumberException;
import pt.ist.anacom.shared.exceptions.InvalidOperatorException;
import pt.ist.fenixframework.FenixFramework;


/**
 * The Class RegisterMobileService.
 */
public class RegisterMobileService extends
AnaComService {

	/** The _dto. */
	private PhoneDetailedDTO dto;


	@Atomic
	public void execute() throws InvalidOperatorException,
	CellPhoneAlreadyExistsException,
	InvalidNumberException {
		dispatch();
	}

	/**
	 * Instantiates a new register mobile service.
	 * 
	 * @param dto the dto
	 */
	public RegisterMobileService(PhoneDetailedDTO dto) {
		this.dto = dto;
	}




	@Override
	public void dispatch() throws InvalidOperatorException,
	CellPhoneAlreadyExistsException,
	InvalidNumberException {
		final NetworkManager nm = FenixFramework.getRoot();
		final Operator op = nm.getOperatorByName(dto.get_operator());

		switch (dto.get_cellPhoneType()) {
		case PHONE2G:
			op.addCellPhone(new Network2G(dto.get_number(), dto.getBalance()));
			break;
		case PHONE3G:
			op.addCellPhone(new Network3G(dto.get_number(), dto.getBalance()));
			break;
		}
		op.setSeqNumber(dto.getSeqNumber());
	}
}
