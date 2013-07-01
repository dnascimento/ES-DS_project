package pt.ist.anacom.presentationserver.server;

import jvstm.Atomic;
import pt.ist.anacom.domain.communication.Voice;
import pt.ist.anacom.service.AddBalanceService;
import pt.ist.anacom.service.InitReceiverVoiceService;
import pt.ist.anacom.service.TerminationCallerVoiceService;
import pt.ist.anacom.service.ChangeCellPhoneModeService;
import pt.ist.anacom.service.CreateOperatorService;
import pt.ist.anacom.service.GetBalanceService;
import pt.ist.anacom.service.GetCellPhoneListService;
import pt.ist.anacom.service.GetCellPhoneModeService;
import pt.ist.anacom.service.GetCellPhoneSMSReceiveListService;
import pt.ist.anacom.service.GetLastMadeCommunicationService;
import pt.ist.anacom.service.ReceiveSMSService;
import pt.ist.anacom.service.TerminationReceiverVoiceService;
import pt.ist.anacom.service.RegisterMobileService;
import pt.ist.anacom.service.RemoveCellPhoneService;
import pt.ist.anacom.service.SendSMSService;
import pt.ist.anacom.service.InitCallerVoiceCommunicationService;
import pt.ist.anacom.service.bridge.ApplicationServerBridge;
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


/** Bridge para engenharia de software em que toda a aplicação é executada localmente. */
public class ApplicationServerBridgeSoftEng
        implements ApplicationServerBridge {


    /**
     * createOperator - Serviço que cria operador.
     * 
     * @param dto - data unit object com dados importantes dos dados a serem transportados
     *            entre domínios
     * @throws AlreadyExistOperatorException
     * @throws InvalidOperatorPrefixException
     * @throws OperatorException
     */
    @Atomic
    public void createOperator(OperatorDetailedDTO dto) throws OperatorAlreadyExistsException, OperatorException, InvalidOperatorPrefixException, AlreadyExistOperatorException {
        CreateOperatorService rms = new CreateOperatorService(dto);
        rms.dispatch();
    }


    /**
     * addCellPhone - Serviço que adiciona telemóvel á rede
     * 
     * @param dto - data unit object com dados importantes dos dados a serem transportados
     *            entre domínios
     * @throws InvalidOperatorException
     */
    @Atomic
    public void addCellPhone(PhoneDetailedDTO dto) throws CellPhoneAlreadyExistsException, InvalidNumberException, InvalidOperatorException {
        RegisterMobileService service = new RegisterMobileService(dto);
        service.dispatch();
    }

    /**
     * addBalance - Serviço muda saldo de um dado telemóvel.
     * 
     * @param dto - data unit object com dados importantes dos dados a serem transportados
     *            entre domínios
     * @throws CellPhoneNotExistsException
     * @throws OperatorNotFoundException
     * @throws InvalidOperatorException
     */
    @Atomic
    public void addBalance(PhoneNumValueDTO dto) throws InvalidBalanceException, InvalidOperatorException, OperatorNotFoundException, CellPhoneNotExistsException {
        AddBalanceService service = new AddBalanceService(dto);
        service.dispatch();
    }

    /**
     * removeCellPhone - Serviço remove um dado telemóvel.
     * 
     * @param dto - data unit object com dados importantes dos dados a serem transportados
     *            entre domínios
     * @throws InvalidNumberException
     * @throws OperatorNotFoundException
     */
    @Atomic
    public void removeCellPhone(PhoneNumberDTO dto) throws CellPhoneNotExistsException, OperatorNotFoundException, InvalidNumberException {
        RemoveCellPhoneService service = new RemoveCellPhoneService(dto);
        service.dispatch();
    }


    /**
     * getBalance - Serviço que acede ao saldo de um telemóvel.
     * 
     * @param dto - data unit object com dados importantes dos dados a serem transportados
     *            entre domínios
     * @throws OperatorNotFoundException
     */
    @Atomic
    public PhoneNumValueDTO getBalance(PhoneNumberDTO dto) throws CellPhoneNotExistsException, OperatorNotFoundException {
        GetBalanceService service = new GetBalanceService(dto);
        service.dispatch();
        return service.getResult();
    }

    /**
     * getCellPhoneList - Serviço que vai buscar a lista de telemóveis de um dado operador
     * 
     * @param dto - data unit object com dados importantes dos dados a serem transportados
     *            entre domínios
     * @throws InvalidOperatorException
     */
    @Atomic
    public PhoneListDTO getCellPhoneList(OperatorSimpleDTO dto) throws InvalidOperatorException {
        GetCellPhoneListService service = new GetCellPhoneListService(dto);
        service.dispatch();
        return service.getList();
    }

    /**
     * processSMS - Serviço que envia mensagem entre telemóveis. Consiste em chamar dois
     * (envio e ecepção de SMS) serviços para que solução possa ser distrbuida.
     * 
     * @param dto - data unit object com dados importantes dos dados a serem transportados
     *            entre domínios
     * @throws IncompatibleModeException
     * @throws NotEnoughBalanceException
     * @throws CellPhoneNotExistsException
     * @throws OperatorNotFoundException
     */
    @Atomic
    public void processSMS(SMSDTO dto) throws CellPhoneNotAvailableException, OperatorNotFoundException, CellPhoneNotExistsException, NotEnoughBalanceException, IncompatibleModeException {
        ReceiveSMSService receiveSMSService = new ReceiveSMSService(dto);
        receiveSMSService.dispatch();

        SendSMSService sendSMSService = new SendSMSService(dto);
        sendSMSService.dispatch();


    }

    /**
     * changeCellPhoneMode - Serviço que modifica modo de um telemovel.
     * 
     * @param dto - data unit object com dados importantes dos dados a serem transportados
     *            entre domínios
     * @throws CellPhoneNotExistsException the cell phone not exists exception
     * @throws OperatorNotFoundException the operator not found exception
     * @throws IncompatibleModeException the incompatible mode exception
     */
    @Atomic
    public void changeCellPhoneMode(PhoneNumModeDTO dto) throws CellPhoneNotExistsException, OperatorNotFoundException, IncompatibleModeException {
        final ChangeCellPhoneModeService changeCellPhoneModeService = new ChangeCellPhoneModeService(dto);
        changeCellPhoneModeService.dispatch();
    }
    
    /**
     * getReceivedSMS - Serviço que obtem Lista de SMS Recebidos.
     * 
     * @param dto - data unit object com dados importantes dos dados a serem transportados
     *            entre domínios
     * @throws CellPhoneNotExistsException the cell phone not exists exception
     * @throws OperatorNotFoundException the operator not found exception
     * @throws IncompatibleModeException the incompatible mode exception
     */
    @Atomic
    public SMSListDTO getReceivedSMS(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException {
        GetCellPhoneSMSReceiveListService getCellPhoneSMSReceiveListService = new GetCellPhoneSMSReceiveListService(dto);
        getCellPhoneSMSReceiveListService.dispatch();
        return getCellPhoneSMSReceiveListService.getList();
    }

    /**
     * getCellPhoneMode - Serviço que obtem dto com o modo do telemovel.
     * 
     * @param dto - data unit object com dados importantes dos dados a serem transportados
     *            entre domínios
     * @throws CellPhoneNotExistsException the cell phone not exists exception
     * @throws OperatorNotFoundException the operator not found exception
     * @throws IncompatibleModeException the incompatible mode exception
     */
    @Atomic
    public PhoneNumModeDTO getCellPhoneMode(PhoneNumberDTO dto) throws CellPhoneNotExistsException, OperatorNotFoundException {
        final GetCellPhoneModeService getCellPhoneModeService = new GetCellPhoneModeService(dto);
        getCellPhoneModeService.dispatch();

        return getCellPhoneModeService.getCellPhoneModeDTO();
    }
    /**
     * getLastMadeCommunication - Serviço que obtem Ultima Comunicacao Recebida.
     * 
     * @param dto - data unit object com dados importantes dos dados a serem transportados
     *            entre domínios
     * @throws CellPhoneNotExistsException the cell phone not exists exception
     * @throws OperatorNotFoundException the operator not found exception
     * @throws IncompatibleModeException the incompatible mode exception
     */

    @Atomic
    public CommunicationDTO getLastMadeCommunication(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException, InvalidCommunicationException {
        final GetLastMadeCommunicationService service = new GetLastMadeCommunicationService(dto);
        service.dispatch();
        return service.getResult();
    }
       
    /**
     * 
     * */
    @Atomic
    public void initiateVoiceCommunication(VoiceDTO dto) throws CellPhoneNotAvailableException, OperatorNotFoundException, CellPhoneNotExistsException, NotEnoughBalanceException, IncompatibleModeException{

    	InitCallerVoiceCommunicationService srcInitService = new InitCallerVoiceCommunicationService(dto);
    	srcInitService.dispatch();
    	
    	InitReceiverVoiceService destInitService = new InitReceiverVoiceService(dto);
    	destInitService.dispatch();
    }

    /**
     * 
     * */
	@Atomic
    public void endVoiceCommunication(VoiceDTO dto)throws CellPhoneNotAvailableException, OperatorNotFoundException, CellPhoneNotExistsException, NotEnoughBalanceException, IncompatibleModeException {
		
		TerminationCallerVoiceService clrService = new TerminationCallerVoiceService(dto);
		clrService.dispatch();
		
		//DTO actualizado com informacao do tempo
		VoiceDTO forReceiverDTO = clrService.getResult(); 
		
		TerminationReceiverVoiceService rcvService = new TerminationReceiverVoiceService(forReceiverDTO);
		rcvService.dispatch();

	
    }

}
