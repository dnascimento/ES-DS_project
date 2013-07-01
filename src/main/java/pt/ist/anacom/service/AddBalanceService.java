package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.CellPhone;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.shared.dto.phone.PhoneNumValueDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.InvalidBalanceException;
import pt.ist.anacom.shared.exceptions.InvalidOperatorException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;
import pt.ist.fenixframework.FenixFramework;

/**
 * 
 * Service que incrementa o valor do saldo
 * tendo em conta o valor do bónus.
 * 
 */
public class AddBalanceService extends
        AnaComService {

    private PhoneNumValueDTO _dto;

    public AddBalanceService(PhoneNumValueDTO dto) {
        _dto = dto;
    }

    @Atomic
    public void execute() throws InvalidOperatorException,
            OperatorNotFoundException,
            CellPhoneNotExistsException,
            InvalidBalanceException {
        dispatch();
    }



    @Override
    public void dispatch() throws InvalidOperatorException,
    OperatorNotFoundException,
    CellPhoneNotExistsException,
    InvalidBalanceException {

    	NetworkManager nm = FenixFramework.getRoot();
    	Operator op = nm.getOperatorByPhoneNumber(_dto.get_number());
    	CellPhone cp = op.getCellPhoneByNumber(_dto.get_number());
    	cp.addBalance(_dto.getBalance());
		op.setSeqNumber(_dto.getSeqNumber());
    }

}
