package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.domain.Plan;
import pt.ist.anacom.shared.dto.operator.OperatorDetailedDTO;
import pt.ist.anacom.shared.exceptions.AlreadyExistOperatorException;
import pt.ist.anacom.shared.exceptions.InvalidOperatorPrefixException;
import pt.ist.anacom.shared.exceptions.OperatorException;
import pt.ist.fenixframework.FenixFramework;

/**
 * @author Carl Kristensen
 */
public class CreateOperatorService extends
        AnaComService {

    private OperatorDetailedDTO _dto;

    public CreateOperatorService(OperatorDetailedDTO dto) {
        _dto = dto;
    }


    @Atomic
    public void execute() throws OperatorException, InvalidOperatorPrefixException, AlreadyExistOperatorException {
        dispatch();
    }

    /**
     * Add new operator
     * 
     * @throws AlreadyExistOperatorException
     * @throws InvalidOperatorPrefixException
     * @exception OperatorException
     */
    @Override
    public void dispatch() throws OperatorException, InvalidOperatorPrefixException, AlreadyExistOperatorException {
        NetworkManager nm = FenixFramework.getRoot();

        nm.addOperator(new Operator(_dto.get_name(), _dto.get_prefix(), 
    			new Plan(_dto.get_smsTrf(), _dto.get_voiceTrf(), _dto.get_videoTrf(),
    						_dto.get_extraTrf(), _dto.get_bonus())));}


}
