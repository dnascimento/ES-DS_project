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
import pt.ist.anacom.shared.exceptions.AlreadyExistOperatorException;
import pt.ist.anacom.shared.exceptions.CellPhoneAlreadyExistsException;
import pt.ist.anacom.shared.exceptions.CellPhoneNotAvailableException;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;
import pt.ist.anacom.shared.exceptions.InvalidBalanceException;
import pt.ist.anacom.shared.exceptions.InvalidCommunicationException;
import pt.ist.anacom.shared.exceptions.InvalidNumberException;
import pt.ist.anacom.shared.exceptions.InvalidOperatorException;
import pt.ist.anacom.shared.exceptions.InvalidOperatorPrefixException;
import pt.ist.anacom.shared.exceptions.NotEnoughBalanceException;
import pt.ist.anacom.shared.exceptions.OperatorAlreadyExistsException;
import pt.ist.anacom.shared.exceptions.OperatorException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service")
public interface AnacomService
        extends RemoteService {

    public void initBridge(String serverType);

    void addBalance(PhoneNumValueDTO dto) throws InvalidBalanceException, InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException;

    void createOperator(OperatorDetailedDTO dto) throws OperatorAlreadyExistsException, OperatorException, InvalidOperatorPrefixException, AlreadyExistOperatorException;

    void processSMS(SMSDTO dto) throws CellPhoneNotExistsException,
            InvalidOperatorException,
            OperatorNotFoundException,
            NotEnoughBalanceException,
            IncompatibleModeException,
            CellPhoneNotAvailableException;

    PhoneListDTO getCellPhoneList(OperatorSimpleDTO dto) throws InvalidOperatorException;

    void addCellPhone(PhoneDetailedDTO dto) throws CellPhoneAlreadyExistsException, InvalidNumberException, InvalidOperatorException;

    void removeCellPhone(PhoneNumberDTO dto) throws CellPhoneNotExistsException, InvalidNumberException, InvalidOperatorException, OperatorNotFoundException;

    PhoneNumValueDTO getBalance(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException;

    void changeCellPhoneMode(PhoneNumModeDTO dto) throws IncompatibleModeException, CellPhoneNotExistsException, OperatorNotFoundException;

    SMSListDTO getReceivedSMS(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException;

    PhoneNumModeDTO getMode(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException;

    CommunicationDTO getLastMadeCall(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException, InvalidCommunicationException;

    void startVoice(VoiceDTO dto) throws CellPhoneNotExistsException, InvalidOperatorException, OperatorNotFoundException, NotEnoughBalanceException, IncompatibleModeException, CellPhoneNotAvailableException;

    void endVoice(VoiceDTO dto) throws CellPhoneNotExistsException, InvalidOperatorException, OperatorNotFoundException, NotEnoughBalanceException, IncompatibleModeException, CellPhoneNotAvailableException;

}
