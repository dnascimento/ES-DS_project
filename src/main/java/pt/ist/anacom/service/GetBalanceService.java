package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.CellPhone;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.shared.dto.phone.PhoneNumValueDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumberDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;
import pt.ist.fenixframework.FenixFramework;

public class GetBalanceService extends
        AnaComService {

    private PhoneNumberDTO _dto;

    private PhoneNumValueDTO _result;

    public GetBalanceService(PhoneNumberDTO dto) {
        _dto = dto;
    }


    @Atomic
    public void execute() throws OperatorNotFoundException, CellPhoneNotExistsException {
        dispatch();
    }

    /**
     * Implementa lógica no servidor do operador para obter saldo telemovel. Preenche DTO
     * com saldo telemovel para devolver ao cliente que fez o pedido
     * 
     * @throws OperatorNotFoundException
     * @throws CellPhoneNotExistsException
     */
    @Override
    public void dispatch() throws OperatorNotFoundException, CellPhoneNotExistsException {
        NetworkManager nm = FenixFramework.getRoot();

        Operator op = nm.getOperatorByPhoneNumber(_dto.get_number());
        CellPhone cellPhone = op.getCellPhoneByNumber(_dto.get_number());

        this.set_result(new PhoneNumValueDTO(_dto.get_number(), cellPhone.getBalance(), op.getSeqNumber()));
    }


    private void set_result(PhoneNumValueDTO _result) {
        this._result = _result;
    }

    public PhoneNumValueDTO getResult() {
        return _result;
    }


}
