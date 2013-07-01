package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.shared.dto.operator.OperatorSimpleDTO;
import pt.ist.anacom.shared.dto.phone.PhoneListDTO;
import pt.ist.anacom.shared.exceptions.InvalidOperatorException;
import pt.ist.fenixframework.FenixFramework;

public class GetCellPhoneListService extends
        AnaComService {

    private OperatorSimpleDTO _dto; // nome do operador a listar

    private PhoneListDTO _result;

    public GetCellPhoneListService(OperatorSimpleDTO dto) {
        this._dto = dto;
    }

    @Atomic
    public void execute() throws InvalidOperatorException {
        dispatch();
    }

    /**
     * Obter a lista de telemoveis de um dado operador
     * 
     * @throws InvalidOperatorException
     */
    @Override
    public void dispatch() throws InvalidOperatorException {
        NetworkManager nm = FenixFramework.getRoot();
        Operator op = nm.getOperatorByName(_dto.get_name());
        this._result = new PhoneListDTO(op.getCellPhoneList(), op.getSeqNumber());
    }

    public PhoneListDTO getList() {
        return this._result;
    }

}
