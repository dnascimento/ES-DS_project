package pt.ist.anacom.presentationserver.client;

import pt.ist.anacom.shared.dto.CommunicationDTO;
import pt.ist.anacom.shared.dto.SMSDTO;
import pt.ist.anacom.shared.dto.SMSListDTO;
import pt.ist.anacom.shared.dto.VoiceDTO;
import pt.ist.anacom.shared.dto.operator.OperatorDetailedDTO;
import pt.ist.anacom.shared.dto.operator.OperatorSimpleDTO;
import pt.ist.anacom.shared.dto.phone.PhoneDetailedDTO;
import pt.ist.anacom.shared.dto.phone.PhoneListDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumModeDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumValueDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumberDTO;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AnacomServiceAsync {

    public void initBridge(String serverType, AsyncCallback<Void> callback);

    void addBalance(PhoneNumValueDTO dto, AsyncCallback<Void> callback);

    void createOperator(OperatorDetailedDTO dto, AsyncCallback<Void> callback);

    void processSMS(SMSDTO dto, AsyncCallback<Void> callback);

    void getCellPhoneList(OperatorSimpleDTO dto, AsyncCallback<PhoneListDTO> callback);

    void addCellPhone(PhoneDetailedDTO dto, AsyncCallback<Void> callback);

    void removeCellPhone(PhoneNumberDTO dto, AsyncCallback<Void> callback);

    void getBalance(PhoneNumberDTO dto, AsyncCallback<PhoneNumValueDTO> callback);

    void changeCellPhoneMode(PhoneNumModeDTO dto, AsyncCallback<Void> callback);

    void getReceivedSMS(PhoneNumberDTO dto, AsyncCallback<SMSListDTO> callback);

    void getMode(PhoneNumberDTO dto, AsyncCallback<PhoneNumModeDTO> callback);

    void getLastMadeCall(PhoneNumberDTO dto, AsyncCallback<CommunicationDTO> callback);

    void startVoice(VoiceDTO dto, AsyncCallback<Void> callback);

    void endVoice(VoiceDTO dto, AsyncCallback<Void> callback);

}
