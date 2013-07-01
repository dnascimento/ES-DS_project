package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.CellPhone;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.shared.dto.phone.InitiateCommunicationDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.InvalidCommunicationException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;
import pt.ist.fenixframework.FenixFramework;

public class AbleToCommunicateService extends
        AnaComService {



    private InitiateCommunicationDTO dtoIN;

    private InitiateCommunicationDTO dtoResult;

    public AbleToCommunicateService(InitiateCommunicationDTO dto) {
        setDtoIN(dto);
    }

    @Atomic
    public void execute() throws OperatorNotFoundException, CellPhoneNotExistsException, InvalidCommunicationException {
        dispatch();
    }

    @Override
    public void dispatch() throws OperatorNotFoundException, CellPhoneNotExistsException, InvalidCommunicationException {
        NetworkManager nm = FenixFramework.getRoot();
        Operator operator = nm.getOperatorByPhoneNumber(getDtoIN().getCellPhoneNumber());
        CellPhone cellphone = operator.getCellPhoneByNumber(getDtoIN().getCellPhoneNumber());


        InitiateCommunicationDTO result = new InitiateCommunicationDTO(getDtoIN().getCellPhoneNumber(), getDtoIN().getCommunicationBeingMade(), getDtoIN().getSeqNumber());

        switch (getDtoIN().getCommunicationBeingMade()) {
        case SENDSMS:
            result.setAbleToInitiateCommunication(cellphone.getMode().ableToSendSms());
            break;
        case RECEIVESMS:
            result.setAbleToInitiateCommunication(cellphone.getMode().ableToReceiveSms());
            break;
        case SENDVOICE:
            result.setAbleToInitiateCommunication(cellphone.getMode().ableToSendVoice());
            break;
        case RECEIVEVOICE:
            result.setAbleToInitiateCommunication(cellphone.getMode().ableToReceiveVoice());
            break;
        default:
            throw new InvalidCommunicationException("Tipo de comunicação inválido");
        }
        result.setSeqNumber(operator.getSeqNumber());
        setDtoResult(result);

    }

    private InitiateCommunicationDTO getDtoIN() {
        return dtoIN;
    }

    private void setDtoIN(InitiateCommunicationDTO dtoIN) {
        this.dtoIN = dtoIN;
    }

    public InitiateCommunicationDTO getDtoResult() {
        return dtoResult;
    }

    private void setDtoResult(InitiateCommunicationDTO dtoResult) {
        this.dtoResult = dtoResult;
    }
}
