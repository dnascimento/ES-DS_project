package pt.ist.anacom.service.bridge;

import java.util.concurrent.ExecutionException;

import pt.ist.anacom.shared.dto.CommunicationDTO;
import pt.ist.anacom.shared.dto.SMSDTO;
import pt.ist.anacom.shared.dto.SMSListDTO;
import pt.ist.anacom.shared.dto.VoiceDTO;
import pt.ist.anacom.shared.dto.operator.OperatorBonusDTO;
import pt.ist.anacom.shared.dto.operator.OperatorDetailedDTO;
import pt.ist.anacom.shared.dto.operator.OperatorSimpleDTO;
import pt.ist.anacom.shared.dto.phone.PhoneDetailedDTO;
import pt.ist.anacom.shared.dto.phone.PhoneListDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumModeDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumValueDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumberDTO;
import pt.ist.anacom.shared.exceptions.AlreadyExistOperatorException;
import pt.ist.anacom.shared.exceptions.AnaComException;
import pt.ist.anacom.shared.exceptions.CellPhoneAlreadyExistsException;
import pt.ist.anacom.shared.exceptions.CellPhoneNotAvailableException;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;
import pt.ist.anacom.shared.exceptions.InvalidBalanceException;
import pt.ist.anacom.shared.exceptions.InvalidCommunicationException;
import pt.ist.anacom.shared.exceptions.InvalidNumberException;
import pt.ist.anacom.shared.exceptions.InvalidOperatorException;
import pt.ist.anacom.shared.exceptions.InvalidOperatorPrefixException;
import pt.ist.anacom.shared.exceptions.InvalidParametersException;
import pt.ist.anacom.shared.exceptions.NotEnoughBalanceException;
import pt.ist.anacom.shared.exceptions.OperatorAlreadyExistsException;
import pt.ist.anacom.shared.exceptions.OperatorException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;

/**
 * The Interface ApplicationServerBridge.
 */
public interface ApplicationServerBridge {

    /**
     * Adds the balance.
     * 
     * @param dto the dto
     * @throws InvalidBalanceException the invalid balance exception
     * @throws InvalidOperatorException
     * @throws CellPhoneNotExistsException
     * @throws OperatorNotFoundException
     */
    void addBalance(PhoneNumValueDTO dto) throws InvalidBalanceException, InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException;

    /**
     * Adds the cell phone.
     * 
     * @param dto the dto
     * @throws CellPhoneAlreadyExistsException the cell phone already exists exception
     * @throws InvalidNumberException the invalid number exception
     */
    void addCellPhone(PhoneDetailedDTO dto) throws CellPhoneAlreadyExistsException, InvalidNumberException, InvalidOperatorException;

    /**
     * Creates the operator.
     * 
     * @param dto the dto
     * @throws OperatorAlreadyExistsException the operator already exists exception
     * @throws AlreadyExistOperatorException
     * @throws InvalidOperatorPrefixException
     * @throws OperatorException
     */
    void createOperator(OperatorDetailedDTO dto) throws OperatorAlreadyExistsException, OperatorException, InvalidOperatorPrefixException, AlreadyExistOperatorException;

    /**
     * Removes the cell phone.
     * 
     * @param dto the dto
     * @throws CellPhoneNotExistsException the cell phone not exists exception
     * @throws InvalidNumberException
     * @throws OperatorNotFoundException_Exception
     * @throws CellPhoneNotExistsException_Exception
     * @throws InvalidOperatorException
     * @throws OperatorNotFoundException
     */
    void removeCellPhone(PhoneNumberDTO dto) throws CellPhoneNotExistsException, InvalidNumberException, InvalidOperatorException, OperatorNotFoundException;

    /**
     * Gets the balance.
     * 
     * @param dto the dto
     * @return the balance
     * @throws InterruptedException 
     * @throws ExecutionException 
     * @throws InvalidParametersException 
     * @throws AnaComException
     */
    PhoneNumValueDTO getBalance(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException, InvalidParametersException;

    /**
     * Gets the cell phone list.
     * 
     * @param dto the dto
     * @return the cell phone list
     * @throws InvalidOperatorException
     */
    PhoneListDTO getCellPhoneList(OperatorSimpleDTO dto) throws InvalidOperatorException;

    /**
     * Process sms.
     * 
     * @param dto the dto
     * @throws CellPhoneNotExistsException
     * @throws InvalidOperatorException
     * @throws OperatorNotFoundException
     * @throws NotEnoughBalanceException
     * @throws IncompatibleModeException
     * @throws CellPhoneNotAvailableException
     */
    void processSMS(SMSDTO dto) throws CellPhoneNotExistsException,
            InvalidOperatorException,
            OperatorNotFoundException,
            NotEnoughBalanceException,
            IncompatibleModeException,
            CellPhoneNotAvailableException;

    /**
     * Change cell phone mode.
     * 
     * @param dto the dto
     * @throws InvalidOperatorException
     * @throws CellPhoneNotExistsException
     * @throws OperatorNotFoundException
     */
    void changeCellPhoneMode(PhoneNumModeDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException, IncompatibleModeException;

    /**
     * Get cell phone mode.
     * 
     * @param dto the dto with Phone Number
     * @return GetCellPhoneModeDTO, Dto com o modo do telemovel
     * @throws CellPhoneNotExistsException
     * @throws OperatorNotFoundException
     */
    public PhoneNumModeDTO getCellPhoneMode(PhoneNumberDTO _dto) throws CellPhoneNotExistsException, OperatorNotFoundException;

    public SMSListDTO getReceivedSMS(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException;

    public CommunicationDTO getLastMadeCommunication(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException, InvalidCommunicationException;

    public void initiateVoiceCommunication(VoiceDTO dto) throws CellPhoneNotAvailableException, OperatorNotFoundException, CellPhoneNotExistsException, NotEnoughBalanceException, IncompatibleModeException;

    public void endVoiceCommunication(VoiceDTO dto) throws CellPhoneNotAvailableException, OperatorNotFoundException, CellPhoneNotExistsException, NotEnoughBalanceException, IncompatibleModeException;
    
    
}
