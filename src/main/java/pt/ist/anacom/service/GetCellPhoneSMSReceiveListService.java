package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.CellPhone;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.shared.dto.SMSListDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumberDTO;
import pt.ist.anacom.shared.exceptions.InvalidOperatorException;
import pt.ist.fenixframework.FenixFramework;

public class GetCellPhoneSMSReceiveListService extends
        AnaComService {

    private PhoneNumberDTO _dto;
    private SMSListDTO _result;

    public GetCellPhoneSMSReceiveListService(PhoneNumberDTO dto) {
        this._dto = dto;
    }

    @Atomic
    public void execute() throws InvalidOperatorException {
        dispatch();
    }

    /**
     * @author dn & david dias Preenche o dto com uma lista de CellPhones
     * @throws InvalidOperatorException
     */
    @Override
    public void dispatch() throws InvalidOperatorException {
        NetworkManager nm = FenixFramework.getRoot();
        Operator op = nm.getOperatorByPhoneNumber(_dto.get_number());
        CellPhone phone = op.getCellPhoneByNumber(_dto.get_number());
        this._result = new SMSListDTO(phone.getCellPhoneReceivedSMS(), op.getSeqNumber());
    }

    public SMSListDTO getList() {
        return this._result;
    }

}
