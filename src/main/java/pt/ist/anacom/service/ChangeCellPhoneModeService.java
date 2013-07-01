package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.CellPhone;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.shared.dto.phone.PhoneNumModeDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;
import pt.ist.fenixframework.FenixFramework;

/**
 * The Class ChangeCellPhoneModeService.
 */
public class ChangeCellPhoneModeService extends
        AnaComService {

    /** The _dto. */
    private PhoneNumModeDTO _dto;

    /**
     * Instantiates a new change cell phone mode service.
     * 
     * @param dto the dto
     */
    public ChangeCellPhoneModeService(PhoneNumModeDTO dto) {
        _dto = dto;
    }

    @Atomic
    public void execute() throws CellPhoneNotExistsException, OperatorNotFoundException {
        dispatch();
    }



    /**
     * @throws IncompatibleModeException quando o modo para o qual pretende alterar, já é
     *             o modo actual.
     */
    @Override
    public void dispatch() throws CellPhoneNotExistsException, OperatorNotFoundException, IncompatibleModeException {
        final NetworkManager networkManager = FenixFramework.getRoot();
        Operator op = networkManager.getOperatorByPhoneNumber(_dto.get_number());
        CellPhone phone = op.getCellPhoneByNumber(_dto.get_number());
        phone.changeMode(_dto.getMode());
		op.setSeqNumber(_dto.getSeqNumber());
    }

}
