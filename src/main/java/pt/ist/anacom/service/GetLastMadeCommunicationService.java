package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.CellPhone;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.domain.communication.Communication;
import pt.ist.anacom.domain.communication.SMS;
import pt.ist.anacom.domain.communication.Video;
import pt.ist.anacom.domain.communication.Voice;
import pt.ist.anacom.shared.dto.CommunicationDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumberDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.InvalidCommunicationException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;
import pt.ist.fenixframework.FenixFramework;

public class GetLastMadeCommunicationService extends
        AnaComService {

    private PhoneNumberDTO _dto;
    private CommunicationDTO _result;

    public GetLastMadeCommunicationService(PhoneNumberDTO dto) {
        this._dto = dto;
    }

    @Atomic
    public void execute() throws InvalidCommunicationException, CellPhoneNotExistsException, OperatorNotFoundException {
        dispatch();
    }

    @Override
    public void dispatch() throws InvalidCommunicationException, CellPhoneNotExistsException, OperatorNotFoundException {
        final NetworkManager nm = FenixFramework.getRoot();

        final Operator op = nm.getOperatorByPhoneNumber(_dto.get_number());
        final CellPhone cellPhone = op.getCellPhoneByNumber(_dto.get_number());

        final Communication cm = cellPhone.getLastComunication();

        if (cm instanceof SMS) {
            final SMS sms = (SMS) cm;
            final CommunicationDTO dto = new CommunicationDTO(sms.getReceiverID(), cm.toString(), sms.getCost(), sms.getLenghtSMS(), op.getSeqNumber());
            this.setResult(dto);
        } else if (cm instanceof Voice) {
            final Voice voice = (Voice) cm;

            final CommunicationDTO dto = new CommunicationDTO(voice.getReceiverID(), cm.toString(), voice.getCost(), voice.getDuration(), op.getSeqNumber());
            this.setResult(dto);

        } else if (cm instanceof Video) {
            final Video video = (Video) cm;
            final CommunicationDTO dto = new CommunicationDTO(video.getReceiverID(), cm.toString(), video.getCost(), video.getDuration(), op.getSeqNumber());
            this.setResult(dto);

        } else {
            throw new InvalidCommunicationException("Comunicacao nao reconhecida");
        }
    }


    private void setResult(CommunicationDTO dto) {
        this._result = dto;
    }


    public CommunicationDTO getResult() {
        return this._result;
    }

}
