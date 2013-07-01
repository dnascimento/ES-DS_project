package pt.ist.anacom.applicationserver;

import java.util.concurrent.Future;

import javax.activity.InvalidActivityException;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.service.AbleToCommunicateService;
import pt.ist.anacom.service.AddBalanceService;
import pt.ist.anacom.service.ChangeCellPhoneModeService;
import pt.ist.anacom.service.CreateOperatorService;
import pt.ist.anacom.service.GetBalanceService;
import pt.ist.anacom.service.GetCellPhoneListService;
import pt.ist.anacom.service.GetCellPhoneModeService;
import pt.ist.anacom.service.GetCellPhoneSMSReceiveListService;
import pt.ist.anacom.service.GetLastMadeCommunicationService;
import pt.ist.anacom.service.GetSequenceNumberService;
import pt.ist.anacom.service.InitCallerVoiceCommunicationService;
import pt.ist.anacom.service.InitReceiverVoiceService;
import pt.ist.anacom.service.ReceiveSMSService;
import pt.ist.anacom.service.RegisterMobileService;
import pt.ist.anacom.service.RemoveCellPhoneService;
import pt.ist.anacom.service.SendSMSService;
import pt.ist.anacom.service.TerminationCallerVoiceService;
import pt.ist.anacom.service.TerminationReceiverVoiceService;
import pt.ist.anacom.shared.dto.CommunicationDTO;
import pt.ist.anacom.shared.dto.SMSDTO;
import pt.ist.anacom.shared.dto.SMSListDTO;
import pt.ist.anacom.shared.dto.VoiceDTO;
import pt.ist.anacom.shared.dto.operator.OperatorDetailedDTO;
import pt.ist.anacom.shared.dto.operator.OperatorSimpleDTO;
import pt.ist.anacom.shared.dto.phone.InitiateCommunicationDTO;
import pt.ist.anacom.shared.dto.phone.PhoneDetailedDTO;
import pt.ist.anacom.shared.dto.phone.PhoneListDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumModeDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumValueDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumberDTO;
import pt.ist.anacom.shared.enumerados.CellPhoneMode;
import pt.ist.anacom.shared.enumerados.CellPhoneType;
import pt.ist.anacom.shared.enumerados.CommunicationType;
import pt.ist.anacom.shared.stubs.AnacomApplicationServerPortType;
import pt.ist.anacom.shared.stubs.CellPhoneAlreadyExistsException;
import pt.ist.anacom.shared.stubs.CellPhoneNotAvailableException;
import pt.ist.anacom.shared.stubs.CellPhoneNotExistsException;
import pt.ist.anacom.shared.stubs.CommunicationDTOType;
import pt.ist.anacom.shared.stubs.IncompatibleModeException;
import pt.ist.anacom.shared.stubs.InitiateCommunicationDTOType;
import pt.ist.anacom.shared.stubs.InvalidBalanceException;
import pt.ist.anacom.shared.stubs.InvalidCommunicationException;
import pt.ist.anacom.shared.stubs.InvalidNumberException;
import pt.ist.anacom.shared.stubs.InvalidOperatorException;
import pt.ist.anacom.shared.stubs.InvalidOperatorException_Exception;
import pt.ist.anacom.shared.stubs.NotEnoughBalanceException;
import pt.ist.anacom.shared.stubs.OperatorNotFoundException;
import pt.ist.anacom.shared.stubs.OperatorSimpleDTOType;
import pt.ist.anacom.shared.stubs.PhoneDetailedDTOElem;
import pt.ist.anacom.shared.stubs.PhoneListDTOType;
import pt.ist.anacom.shared.stubs.PhoneNumModeDTOType;
import pt.ist.anacom.shared.stubs.PhoneNumValueDTOType;
import pt.ist.anacom.shared.stubs.PhoneNumberDTOType;
import pt.ist.anacom.shared.stubs.SMSListDTOType;
import pt.ist.anacom.shared.stubs.SimpleExceptionType;
import pt.ist.anacom.shared.stubs.SmsDTOType;
import pt.ist.anacom.shared.stubs.VoiceDTOType;
import pt.ist.fenixframework.Config;
import pt.ist.fenixframework.Config.RepositoryType;
import pt.ist.fenixframework.FenixFramework;




/**
 * Applicação do servidor onde se localizam todos os webservices, aqui as mensagens SOAP
 * são recebidas, convertidas em DTO's e invoca-se o respectivo serviço.
 */
@javax.jws.WebService(endpointInterface = "pt.ist.anacom.shared.stubs.AnacomApplicationServerPortType", wsdlLocation = "/anacom.wsdl", name = "AnacomApplicationServerPortType", portName = "AnacomApplicationServicePort", targetNamespace = "http://anacom", serviceName = "anacom")
@javax.jws.HandlerChain(file = "handler/handler-chain.xml")
public class ApplicationServerWebService
        implements AnacomApplicationServerPortType {

    public static void init(
            final String operatorName,
                final String serverName,
                final String operatorPrefix,
                final String smsTariff,
                final String voiceTariff,
                final String videoTariff,
                final String extraTariff,
                final String bonusTariff) throws InvalidActivityException {

        System.out.println(".............STARTING Anacom SERVER......." + operatorName + " : " + operatorPrefix + ".....");

        // initializes the Fenix Framework

        try {
            FenixFramework.initialize(new Config() {
                {
                    dbAlias = "/tmp/db/" + serverName;
                    domainModelPath = "/tmp/anacom.dml";
                    repositoryType = RepositoryType.BERKELEYDB;
                    rootClass = NetworkManager.class;
                }
            });
            createServerOperator(operatorName, operatorPrefix, smsTariff, voiceTariff, videoTariff, extraTariff, bonusTariff);
        } catch (Exception e) {
            System.out.println("Failed to initialize the operator server.\n");
            System.out.println(e.getStackTrace());
        }
    }

    private static void createServerOperator(String operatorName, String operatorPrefix, String smsTariff, String voiceTariff, String videoTariff, String extraTariff, String bonusString) {
        int smsTrf;
        int voiceTrf;
        int videoTrf;
        double extraTrf, bonus;


        try {
            smsTrf = Integer.parseInt(smsTariff);
            voiceTrf = Integer.parseInt(voiceTariff);
            videoTrf = Integer.parseInt(videoTariff);
            extraTrf = Double.parseDouble(extraTariff);
            bonus = Double.parseDouble(bonusString);

            final OperatorDetailedDTO dto = new OperatorDetailedDTO(operatorName, operatorPrefix, smsTrf, voiceTrf, videoTrf, extraTrf, bonus);
            final CreateOperatorService service = new CreateOperatorService(dto);
            service.execute();
        } catch (NumberFormatException e) {
            System.out.println("ERRO: ApplicationServerWebService: Invalid arguments");
        } catch (pt.ist.anacom.shared.exceptions.InvalidOperatorPrefixException e) {
            System.out.println("ERRO: ApplicationServerWebService: Invalid operator prefix");
        } catch (pt.ist.anacom.shared.exceptions.AlreadyExistOperatorException e) {
            System.out.println("ERRO: ApplicationServerWebService: Operator already Exists");
        }
    }

    //
    // ######### Funções do Presentation Server Para o servidor remoto ############
    //

    /**
     * changeCellPhoneMode.
     * 
     * @param parameters Parametros da Mensagem SOAP
     * @throws CellPhoneNotExistsException
     * @throws OperatorNotFoundException
     * @throws IncompatibleModeException
     * @exception WebServiceException Converte os parametros da mensagem SOAP em DTO e
     *                chama o serviço correspondente!
     */
    @Override
    public void changeCellPhoneMode(PhoneNumModeDTOType parameters) throws CellPhoneNotExistsException, OperatorNotFoundException, IncompatibleModeException {

        final String newCellPhoneModeString = parameters.getMode().toString();
        CellPhoneMode newCellPhoneMode = null;

        // Converte o tipo String para enumerado
        if (newCellPhoneModeString.equals("ON")) {
            newCellPhoneMode = CellPhoneMode.ON;
        } else if (newCellPhoneModeString.equals("OFF")) {
            newCellPhoneMode = CellPhoneMode.OFF;
        } else if (newCellPhoneModeString.equals("BUSY")) {
            newCellPhoneMode = CellPhoneMode.BUSY;
        } else { // SILENCE
            newCellPhoneMode = CellPhoneMode.SILENCE;
        }

        PhoneNumModeDTO dto = new PhoneNumModeDTO(parameters.getNumber(), newCellPhoneMode, parameters.getSequenceNumber());
        ChangeCellPhoneModeService service = new ChangeCellPhoneModeService(dto);


        try {
            enforceSequentialExecution(parameters.getSequenceNumber(), parameters.getNumber());
            service.execute();

        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotExistsException("CellPhoneNotExistsException", i);

        } catch (pt.ist.anacom.shared.exceptions.OperatorNotFoundException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new OperatorNotFoundException("OperatorNotFoundException", i);

        } catch (pt.ist.anacom.shared.exceptions.IncompatibleModeException e) {
            final SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new IncompatibleModeException("IncompatibleModeException", i);

        } catch (RuntimeException e) {
            System.out.println("-------------------RUNTIME Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------RUNTIME Exception" + e.getMessage());
            throw new OperatorNotFoundException("RUNTIME Exception", i);

        } catch (Exception e) {
            System.out.println("-------------------Exception Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------Exception Exception" + e.getMessage());
            throw new OperatorNotFoundException("Exception", i);

        } finally {
            synchronized (this) {
                notifyAll();
            }
        }

    }


    /**
     * removeCellPhone.
     * 
     * @param parameters Parametros da Mensagem SOAP Converte os parametros da mensagem
     *            SOAP em DTO e chama o serviço correspondente!
     * @exception WebServiceException
     * @throws InvalidNumberException_Exception
     * @throws OperatorNotFoundException
     * @throws CellPhoneNotExistsException
     */
    @Override
    public void removeCellPhone(PhoneNumberDTOType parameters) throws CellPhoneNotExistsException, OperatorNotFoundException, InvalidNumberException {

        PhoneNumberDTO dto = new PhoneNumberDTO(parameters.getNumber(), parameters.getSequenceNumber());
        RemoveCellPhoneService service = new RemoveCellPhoneService(dto);

        try {
            enforceSequentialExecution(parameters.getSequenceNumber(), parameters.getNumber());
            service.execute();

        } catch (pt.ist.anacom.shared.exceptions.OperatorNotFoundException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new OperatorNotFoundException("OperatorNotFoundException", i);

        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotExistsException("CellPhoneNotExistsException", i);

        } catch (pt.ist.anacom.shared.exceptions.InvalidNumberException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new InvalidNumberException("InvalidNumberException", i);

        } catch (RuntimeException e) {
            System.out.println("-------------------RUNTIME Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------RUNTIME Exception" + e.getMessage());
            throw new OperatorNotFoundException("RUNTIME Exception", i);

        } catch (Exception e) {
            System.out.println("-------------------Exception Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------Exception Exception" + e.getMessage());
            throw new OperatorNotFoundException("Exception", i);

        } finally {
            synchronized (this) {
                notifyAll();
            }
        }
    }



    /**
     * getBalance.
     * 
     * @param parameters Parametros da Mensagem SOAP Converte os parametros da mensagem
     *            SOAP em DTO e chama o serviço correspondente!
     * @exception WebServiceException
     * @return GetBalanceMsgResponseType (mensagem SOAP com resposta do servidor)
     * @throws OperatorNotFoundException
     * @throws CellPhoneNotExistsException
     */
    @Override
    public PhoneNumValueDTOType getBalance(PhoneNumberDTOType parameters) throws CellPhoneNotExistsException, OperatorNotFoundException {

        PhoneNumberDTO dto = new PhoneNumberDTO(parameters.getNumber(), parameters.getSequenceNumber());
        PhoneNumValueDTO resDto;

        PhoneNumValueDTOType resMsg = new PhoneNumValueDTOType();
        GetBalanceService service = new GetBalanceService(dto);

        try {

            service.execute();


        } catch (pt.ist.anacom.shared.exceptions.OperatorNotFoundException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new OperatorNotFoundException("OperatorNotFoundException", i);
        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotExistsException("CellPhoneNotExistsException", i);

        } catch (RuntimeException e) {
            System.out.println("-------------------RUNTIME Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------RUNTIME Exception" + e.getMessage());
            throw new OperatorNotFoundException("RUNTIME Exception", i);

        } catch (Exception e) {
            System.out.println("-------------------Exception Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------Exception Exception" + e.getMessage());
            throw new OperatorNotFoundException("Exception", i);

        } finally {
            synchronized (this) {
                notifyAll();
            }
        }


        resDto = service.getResult();
        resMsg.setNumber(parameters.getNumber());
        resMsg.setBalance(resDto.getBalance());
        resMsg.setSequenceNumber(resDto.getSeqNumber());
        return resMsg;
    }



    /**
     * receiveSMSMsgType.
     * 
     * @param parameters Parametros da Mensagem SOAP
     * @throws OperatorNotFoundException
     * @throws CellPhoneNotExistsException
     * @throws IncompatibleModeException
     * @exception WebServiceException Converte os parametros da mensagem SOAP em DTO e
     *                chama o serviço correspondente!
     */
    @Override
    public void receiveSMS(SmsDTOType parameters) throws CellPhoneNotExistsException, OperatorNotFoundException, IncompatibleModeException {
        final String destCellPhone = parameters.getDest();
        final String srcCellPhone = parameters.getSrc();
        final String msg = parameters.getMsg();
        final long seqN = parameters.getSequenceNumber();

        final SMSDTO dto = new SMSDTO(srcCellPhone, destCellPhone, msg, seqN);


        final ReceiveSMSService receiveSMSService = new ReceiveSMSService(dto);

        try {
            enforceSequentialExecution(parameters.getSequenceNumber(), parameters.getDest());
            receiveSMSService.execute();

        } catch (pt.ist.anacom.shared.exceptions.OperatorNotFoundException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new OperatorNotFoundException("OperatorNotFoundException", i);

        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotExistsException("CellPhoneNotExistsException", i);

        } catch (pt.ist.anacom.shared.exceptions.IncompatibleModeException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new IncompatibleModeException("IncompatibleModeException", i);

        } catch (RuntimeException e) {
            System.out.println("-------------------RUNTIME Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------RUNTIME Exception" + e.getMessage());
            throw new OperatorNotFoundException("RUNTIME Exception", i);

        } catch (Exception e) {
            System.out.println("-------------------Exception Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------Exception Exception" + e.getMessage());
            throw new OperatorNotFoundException("Exception", i);

        } finally {
            synchronized (this) {
                notifyAll();
            }
        }
    }



    @Override
    public SMSListDTOType getReceivedSMS(PhoneNumberDTOType parameters) throws CellPhoneNotExistsException, InvalidOperatorException_Exception, OperatorNotFoundException {

        PhoneNumberDTO dto = new PhoneNumberDTO(parameters.getNumber(), parameters.getSequenceNumber());
        SMSListDTO resDto;

        SMSListDTOType resMsg = new SMSListDTOType();
        GetCellPhoneSMSReceiveListService service = new GetCellPhoneSMSReceiveListService(dto);

        try {
            service.execute();
            resDto = service.getList();
            // Converter os DTO's do servico para elementos de mensagem e adicionar à
            // lista da mensagem de resposta
            for (SMSDTO smsDTO : service.getList().getSMSList()) {
                SmsDTOType cp = new SmsDTOType();
                cp.setSrc(smsDTO.getSrcNumber());
                cp.setDest(smsDTO.getDestNumber());
                cp.setMsg(smsDTO.getText());
                cp.setSequenceNumber(smsDTO.getSeqNumber());
                resMsg.getSmsList().add(cp);
            }

            resMsg.setSequenceNumber(resDto.getSeqNumber());
            return resMsg;

        } catch (pt.ist.anacom.shared.exceptions.OperatorNotFoundException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new OperatorNotFoundException("OperatorNotFoundException", i);
        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotExistsException("CellPhoneNotExistsException", i);
        } catch (pt.ist.anacom.shared.exceptions.InvalidOperatorException e) {
            InvalidOperatorException i = new InvalidOperatorException();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            i.setOperatorName(e.get_OperatorName());
            throw new InvalidOperatorException_Exception("InvalidOperatorException", i);
        }
    }


    /**
     * @param PhoneDetailedDTOElem, DTO com os dados do telefone a ser criado
     * @throws InvalidOperatorException_Exception caso O operador não seja o mesmo do DTO
     * @throws CellPhoneAlreadyExistsException Caso o telemovel já exista no Operador
     * @throws InvalidNumberException Caso o numero seja inválido
     * @throws Todos as exceções mandadas têm um InvalidOperatorException dentro que
     *             contêm o sequence number e nome do Operador
     */
    @Override
    public void createCellPhone(PhoneDetailedDTOElem parameters) throws InvalidOperatorException_Exception, InvalidNumberException, CellPhoneAlreadyExistsException {

        PhoneDetailedDTO dto = null;

        final String number = parameters.getNumber();
        final int balance = parameters.getBalance();
        final String type = parameters.getType().toString();
        final String operator = parameters.getOperator();
        final long seqNumber = parameters.getSequenceNumber();

        if (CellPhoneType.PHONE2G.toString().equals(type)) {
            dto = new PhoneDetailedDTO(number, balance, CellPhoneType.PHONE2G, operator, seqNumber);
        }

        if (CellPhoneType.PHONE3G.toString().equals(type)) {
            dto = new PhoneDetailedDTO(number, balance, CellPhoneType.PHONE3G, operator, seqNumber);
        }
        RegisterMobileService s = new RegisterMobileService(dto);


        try {
            enforceSequentialExecution(parameters.getSequenceNumber(), parameters.getNumber());
            s.execute();

        } catch (pt.ist.anacom.shared.exceptions.InvalidOperatorException e) {
            InvalidOperatorException i = new InvalidOperatorException();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            i.setOperatorName(e.get_OperatorName());
            throw new InvalidOperatorException_Exception("InvalidOperatorException", i);

        } catch (pt.ist.anacom.shared.exceptions.CellPhoneAlreadyExistsException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneAlreadyExistsException("CellPhoneAlreadyExistsException", i);

        } catch (pt.ist.anacom.shared.exceptions.InvalidNumberException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new InvalidNumberException("InvalidNumberException", i);

        } catch (pt.ist.anacom.shared.exceptions.OperatorNotFoundException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new InvalidNumberException("InvalidNumberException", i);

        } catch (RuntimeException e) {
            System.out.println("-------------------RUNTIME Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------RUNTIME Exception" + e.getMessage());
            throw new InvalidNumberException("RUNTIME Exception", i);

        } catch (Exception e) {
            System.out.println("-------------------Exception Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------Exception Exception" + e.getMessage());
            throw new InvalidNumberException("Exception", i);

        } finally {
            synchronized (this) {
                notifyAll();
            }
        }
    }



    /**
     * getCellPhoneList
     * 
     * @param Parametros da Mensagem SOAP Converte os parametros da mensagem SOAP em DTO e
     *            chama o serviço correspondente!
     * @exception WebServiceException .
     * @return GetCellPhoneListMsgResponseType (mensagem SOAP com resposta do servidor)
     * @throws InvalidOperatorException_Exception
     */
    @Override
    public PhoneListDTOType getCellPhoneList(OperatorSimpleDTOType parameters) throws InvalidOperatorException_Exception {

        String name = parameters.getName();
        OperatorSimpleDTO dto = new OperatorSimpleDTO(name);
        GetCellPhoneListService service = new GetCellPhoneListService(dto);

        PhoneListDTOType resMsg = new PhoneListDTOType();
        try {
            service.execute();
            PhoneListDTO resDto = service.getList();

            for (PhoneNumValueDTO phoneDTO : service.getList().getPhones()) {
                PhoneNumValueDTOType cp = new PhoneNumValueDTOType();
                cp.setNumber(phoneDTO.get_number());
                cp.setBalance(phoneDTO.getBalance());
                cp.setSequenceNumber(resDto.getSeqNumber());
                resMsg.getCellPhoneList().add(cp);
            }
            resMsg.setSequenceNumber(resDto.getSeqNumber());
            return resMsg;
        } catch (pt.ist.anacom.shared.exceptions.InvalidOperatorException e) {
            InvalidOperatorException i = new InvalidOperatorException();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            i.setOperatorName(e.get_OperatorName());
            throw new InvalidOperatorException_Exception("InvalidOperatorException", i);
        }
    }



    /**
     * SendSMSMsgType.
     * 
     * @param Parametros da Mensagem SOAP
     * @throws CellPhoneNotExistsException
     * @throws OperatorNotFoundException
     * @throws NotEnoughBalanceException_Exception
     * @throws IncompatibleModeException
     * @exception WebServiceException Converte os parametros da mensagem SOAP em DTO e
     *                chama o serviço correspondente!
     */
    @Override
    public void sendSMS(SmsDTOType parameters) throws CellPhoneNotExistsException, OperatorNotFoundException, NotEnoughBalanceException, IncompatibleModeException {
        final String destCellPhone = parameters.getDest();
        final String srcCellPhone = parameters.getSrc();
        final String msg = parameters.getMsg();
        final long seqN = parameters.getSequenceNumber();

        final SMSDTO dto = new SMSDTO(srcCellPhone, destCellPhone, msg, seqN);
        final SendSMSService sendSMSService = new SendSMSService(dto);

        try {
            enforceSequentialExecution(parameters.getSequenceNumber(), parameters.getSrc());
            sendSMSService.execute();

        } catch (pt.ist.anacom.shared.exceptions.OperatorNotFoundException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new OperatorNotFoundException("OperatorNotFoundException", i);

        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotExistsException("CellPhoneNotExistsException", i);

        } catch (pt.ist.anacom.shared.exceptions.NotEnoughBalanceException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new NotEnoughBalanceException("NotEnoughBalanceException", i);

        } catch (pt.ist.anacom.shared.exceptions.IncompatibleModeException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new IncompatibleModeException("IncompatibleModeException", i);
        } catch (RuntimeException e) {
            System.out.println("-------------------RUNTIME Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------RUNTIME Exception" + e.getMessage());
            throw new OperatorNotFoundException("RUNTIME Exception", i);

        } catch (Exception e) {
            System.out.println("-------------------Exception Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------Exception Exception" + e.getMessage());
            throw new OperatorNotFoundException("Exception", i);

        } finally {
            synchronized (this) {
                notifyAll();
            }
        }
    }


    /**
     * @param PhoneNumberDTOType, detalhes sobre o telemovel que queremos obter o modo
     * @throws OperatorNotFoundException, CellPhoneNotExistsException ambos têm um
     *             SimpleExceptionType que contem informação acerca de sequence Number e
     *             Operador
     */
    @Override
    public PhoneNumModeDTOType getCellPhoneMode(PhoneNumberDTOType parameters) throws CellPhoneNotExistsException, OperatorNotFoundException {

        PhoneNumberDTO dto = new PhoneNumberDTO(parameters.getNumber(), parameters.getSequenceNumber());

        final PhoneNumModeDTOType resMsg = new PhoneNumModeDTOType();
        final GetCellPhoneModeService service = new GetCellPhoneModeService(dto);

        try {
            service.execute();
        } catch (pt.ist.anacom.shared.exceptions.OperatorNotFoundException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new OperatorNotFoundException("OperatorNotFoundException", i);
        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotExistsException("CellPhoneNotExistsException", i);
        }


        PhoneNumModeDTO resDto = service.getCellPhoneModeDTO();
        resMsg.setNumber(resDto.get_number());
        resMsg.setMode(resDto.getMode().toString());
        resMsg.setSequenceNumber(resDto.getSeqNumber());
        return resMsg;
    }


    /**
     * addBalance
     * 
     * @param Parametros da Mensagem SOAP Converte os parametros da mensagem SOAP em DTO e
     *            chama o serviço correspondente!
     * @throws InvalidOperatorException_Exception
     * @throws OperatorNotFoundException
     * @throws CellPhoneNotExistsException
     * @throws InvalidBalanceException
     */
    @Override
    public void addBalance(PhoneNumValueDTOType parameters) throws CellPhoneNotExistsException, InvalidOperatorException_Exception, OperatorNotFoundException, InvalidBalanceException {
        int balance = parameters.getBalance();
        String number = parameters.getNumber();
        long seqNumber = parameters.getSequenceNumber();

        PhoneNumValueDTO dto = new PhoneNumValueDTO(number, balance, seqNumber);
        AddBalanceService service = new AddBalanceService(dto);


        try {
            enforceSequentialExecution(parameters.getSequenceNumber(), parameters.getNumber());
            service.execute();

        } catch (pt.ist.anacom.shared.exceptions.InvalidOperatorException e) {
            InvalidOperatorException i = new InvalidOperatorException();
            i.setError(e.getMsg());
            i.setOperatorName(e.get_OperatorName());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new InvalidOperatorException_Exception("InvalidOperatorException", i);

        } catch (pt.ist.anacom.shared.exceptions.OperatorNotFoundException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new OperatorNotFoundException("OperatorNotFoundException", i);

        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotExistsException("CellPhoneNotExistsException", i);

        } catch (pt.ist.anacom.shared.exceptions.InvalidBalanceException e) {

            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new InvalidBalanceException("InvalidBalanceException", i);

        } catch (RuntimeException e) {
            System.out.println("-------------------RUNTIME Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------RUNTIME Exception" + e.getMessage());
            throw new OperatorNotFoundException("RUNTIME Exception", i);

        } catch (Exception e) {
            System.out.println("-------------------Exception Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------Exception Exception" + e.getMessage());
            throw new OperatorNotFoundException("Exception", i);

        } finally {
            synchronized (this) {
                notifyAll();
            }
        }
    }

    @Override
    public void terminationReceiverVoice(VoiceDTOType parameters) throws CellPhoneNotAvailableException,
            OperatorNotFoundException,
            CellPhoneNotExistsException,
            NotEnoughBalanceException,
            IncompatibleModeException {
        VoiceDTO dto = new VoiceDTO(parameters.getSrc(), parameters.getDest(), parameters.getDuration(), parameters.getSequenceNumber());
        TerminationReceiverVoiceService service = new TerminationReceiverVoiceService(dto);

        try {
            // Sincronizar as escritas
            enforceSequentialExecution(parameters.getSequenceNumber(), parameters.getDest());
            service.execute();

        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotAvailableException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotAvailableException("CellPhoneNotAvailableException", i);

        } catch (pt.ist.anacom.shared.exceptions.OperatorNotFoundException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new OperatorNotFoundException("OperatorNotFoundException", i);

        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotExistsException("CellPhoneNotExistsException", i);

        } catch (pt.ist.anacom.shared.exceptions.NotEnoughBalanceException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new NotEnoughBalanceException("NotEnoughBalanceException", i);

        } catch (pt.ist.anacom.shared.exceptions.IncompatibleModeException e) {
            final SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new IncompatibleModeException("IncompatibleModeException", i);

        } catch (RuntimeException e) {
            System.out.println("-------------------RUNTIME Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------RUNTIME Exception" + e.getMessage());
            throw new OperatorNotFoundException("RUNTIME Exception", i);

        } catch (Exception e) {
            System.out.println("-------------------Exception Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------Exception Exception" + e.getMessage());
            throw new OperatorNotFoundException("Exception", i);

        } finally {
            synchronized (this) {
                notifyAll();
            }

        }
    }


    @Override
    public void terminationCallerVoice(VoiceDTOType parameters) throws CellPhoneNotAvailableException,
            OperatorNotFoundException,
            CellPhoneNotExistsException,
            NotEnoughBalanceException,
            IncompatibleModeException {


        VoiceDTO dto = new VoiceDTO(parameters.getSrc(), parameters.getDest(), parameters.getDuration(), parameters.getSequenceNumber());
        TerminationCallerVoiceService service = new TerminationCallerVoiceService(dto);

        try {
            // Sincronizar as escritas
            enforceSequentialExecution(parameters.getSequenceNumber(), parameters.getSrc());
            service.execute();

        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotAvailableException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotAvailableException("CellPhoneNotAvailableException", i);

        } catch (pt.ist.anacom.shared.exceptions.OperatorNotFoundException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new OperatorNotFoundException("OperatorNotFoundException", i);

        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotExistsException("CellPhoneNotExistsException", i);

        } catch (pt.ist.anacom.shared.exceptions.NotEnoughBalanceException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new NotEnoughBalanceException("NotEnoughBalanceException", i);

        } catch (pt.ist.anacom.shared.exceptions.IncompatibleModeException e) {
            final SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new IncompatibleModeException("IncompatibleModeException", i);

        } catch (RuntimeException e) {
            System.out.println("-------------------RUNTIME Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------RUNTIME Exception" + e.getMessage());
            throw new OperatorNotFoundException("RUNTIME Exception", i);
        } catch (Exception e) {
            System.out.println("-------------------Exception Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------Exception Exception" + e.getMessage());
            throw new OperatorNotFoundException("Exception", i);
        } finally {
            synchronized (this) {
                notifyAll();
            }

        }
    }

    @Override
    public void initReceiverVoiceCommunication(VoiceDTOType parameters) throws CellPhoneNotAvailableException,
            OperatorNotFoundException,
            CellPhoneNotExistsException,
            NotEnoughBalanceException,
            IncompatibleModeException {
        VoiceDTO dto = new VoiceDTO(parameters.getSrc(), parameters.getDest(), parameters.getDuration(), parameters.getSequenceNumber());
        InitReceiverVoiceService service = new InitReceiverVoiceService(dto);

        try {
            enforceSequentialExecution(parameters.getSequenceNumber(), parameters.getDest());
            service.execute();

        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotAvailableException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotAvailableException("CellPhoneNotAvailableException", i);

        } catch (pt.ist.anacom.shared.exceptions.OperatorNotFoundException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new OperatorNotFoundException("OperatorNotFoundException", i);

        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotExistsException("CellPhoneNotExistsException", i);

        } catch (pt.ist.anacom.shared.exceptions.NotEnoughBalanceException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new NotEnoughBalanceException("NotEnoughBalanceException", i);

        } catch (pt.ist.anacom.shared.exceptions.IncompatibleModeException e) {
            final SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new IncompatibleModeException("IncompatibleModeException", i);

        } catch (RuntimeException e) {
            System.out.println("-------------------RUNTIME Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------RUNTIME Exception" + e.getMessage());
            throw new OperatorNotFoundException("RUNTIME Exception", i);

        } catch (Exception e) {
            System.out.println("-------------------Exception Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------Exception Exception" + e.getMessage());
            throw new OperatorNotFoundException("Exception", i);

        } finally {
            synchronized (this) {
                notifyAll();
            }

        }
    }



    @Override
    public void initCallerVoiceCommunication(VoiceDTOType parameters) throws CellPhoneNotAvailableException,
            OperatorNotFoundException,
            CellPhoneNotExistsException,
            NotEnoughBalanceException,
            IncompatibleModeException {
        VoiceDTO dto = new VoiceDTO(parameters.getSrc(), parameters.getDest(), parameters.getDuration(), parameters.getSequenceNumber());
        InitCallerVoiceCommunicationService service = new InitCallerVoiceCommunicationService(dto);

        try {
            enforceSequentialExecution(parameters.getSequenceNumber(), parameters.getSrc());
            service.execute();

        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotAvailableException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotAvailableException("CellPhoneNotAvailableException", i);

        } catch (pt.ist.anacom.shared.exceptions.OperatorNotFoundException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new OperatorNotFoundException("OperatorNotFoundException", i);

        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotExistsException("CellPhoneNotExistsException", i);

        } catch (pt.ist.anacom.shared.exceptions.NotEnoughBalanceException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new NotEnoughBalanceException("NotEnoughBalanceException", i);

        } catch (pt.ist.anacom.shared.exceptions.IncompatibleModeException e) {
            final SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new IncompatibleModeException("IncompatibleModeException", i);

        } catch (RuntimeException e) {
            System.out.println("-------------------RUNTIME Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------RUNTIME Exception" + e.getMessage());
            throw new OperatorNotFoundException("RUNTIME Exception", i);

        } catch (Exception e) {
            System.out.println("-------------------Exception Exception");
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError("-------------------Exception Exception" + e.getMessage());
            throw new OperatorNotFoundException("Exception", i);

        } finally {
            synchronized (this) {
                notifyAll();
            }

        }
    }



    @Override
    public CommunicationDTOType getLastMadeCommunication(PhoneNumberDTOType parameters) throws CellPhoneNotExistsException, OperatorNotFoundException, InvalidCommunicationException {
        PhoneNumberDTO dto = new PhoneNumberDTO(parameters.getNumber(), parameters.getSequenceNumber());
        GetLastMadeCommunicationService service = new GetLastMadeCommunicationService(dto);
        CommunicationDTOType response = new CommunicationDTOType();
        try {
            service.execute();
        } catch (pt.ist.anacom.shared.exceptions.OperatorNotFoundException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new OperatorNotFoundException("OperatorNotFoundException", i);
        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotExistsException("CellPhoneNotExistsException", i);
        } catch (pt.ist.anacom.shared.exceptions.InvalidCommunicationException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new InvalidCommunicationException("InvalidCommunicationException", i);
        } catch (Exception e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMessage());
            i.setSequenceNumber((long) -1);
            throw new OperatorNotFoundException("Exception: OperatorNotFoundException", i);
        }
        CommunicationDTO resp = service.getResult();

        response.setCost(resp.get_cost());
        response.setDest(resp.get_destNumber());
        response.setSize(resp.get_size());
        response.setType(resp.get_type());
        response.setSequenceNumber(resp.getSeqNumber());
        return response;
    }


    /**
     * @param parameters Parametros da Mensagem SOAP Converte os parametros da mensagem
     *            SOAP em DTO e chama o serviço correspondente!
     * @exception WebServiceException
     * @return GetSequenceNumberMsgResponseType (mensagem SOAP com resposta do servidor)
     * @throws OperatorNotFoundException
     */
    @Override
    public PhoneNumberDTOType getSequenceNumber(PhoneNumberDTOType parameters) throws OperatorNotFoundException {

        PhoneNumberDTO dto = new PhoneNumberDTO(parameters.getNumber(), parameters.getSequenceNumber());
        PhoneNumberDTO resDto;

        PhoneNumberDTOType resMsg = new PhoneNumberDTOType();
        GetSequenceNumberService service = new GetSequenceNumberService(dto);

        try {
            service.execute();

        } catch (pt.ist.anacom.shared.exceptions.OperatorNotFoundException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new OperatorNotFoundException("OperatorNotFoundException", i);
        }

        resDto = service.getResult();
        resMsg.setNumber(resDto.get_number());
        resMsg.setSequenceNumber(resDto.getSeqNumber());
        return resMsg;
    }

    @Override
    public InitiateCommunicationDTOType ableToCommunicate(InitiateCommunicationDTOType parameters) throws OperatorNotFoundException, CellPhoneNotExistsException, InvalidCommunicationException {
        final String comTypeString = parameters.getCommunicationBeingMade();
        CommunicationType comType = CommunicationType.fromString(comTypeString);

        InitiateCommunicationDTO dtoIN = new InitiateCommunicationDTO(parameters.getCellPhoneNumber(), comType, 0);
        InitiateCommunicationDTO dtoResult;
        InitiateCommunicationDTOType responseMessage = new InitiateCommunicationDTOType();

        AbleToCommunicateService service = new AbleToCommunicateService(dtoIN);

        try {
            service.execute();
        } catch (pt.ist.anacom.shared.exceptions.OperatorNotFoundException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new OperatorNotFoundException("InitiateCommunication: OperatorNotFoundException", i);
        } catch (pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new CellPhoneNotExistsException("InitiateCommunication: CellPhoneNotExistsException", i);
        } catch (pt.ist.anacom.shared.exceptions.InvalidCommunicationException e) {
            SimpleExceptionType i = new SimpleExceptionType();
            i.setError(e.getMsg());
            i.setSequenceNumber(e.get_sequenceNumber());
            throw new InvalidCommunicationException("InitiateCommunication: Incom", i);
        }

        dtoResult = service.getDtoResult();
        responseMessage.setAbleToCommunicate(dtoResult.isAbleToInitiateCommunication());
        responseMessage.setCellPhoneNumber(dtoResult.getCellPhoneNumber());
        responseMessage.setSequenceNumber(dtoResult.getSeqNumber());

        return responseMessage;

    }




    // Realização Contract-First não permitiu usar os stubs para os métodos assyncronos.
    // Código seguinte é necessário para compilação correcta.

    @Override
    public Response<?> changeCellPhoneModeAsync(PhoneNumModeDTOType parameters) {

        return null;
    }



    @Override
    public Future<?> changeCellPhoneModeAsync(PhoneNumModeDTOType parameters, AsyncHandler<?> asyncHandler) {

        return null;
    }



    @Override
    public Response<?> terminationReceiverVoiceAsync(VoiceDTOType parameters) {

        return null;
    }



    @Override
    public Future<?> terminationReceiverVoiceAsync(VoiceDTOType parameters, AsyncHandler<?> asyncHandler) {

        return null;
    }



    @Override
    public Response<?> removeCellPhoneAsync(PhoneNumberDTOType parametersRemove) {

        return null;
    }



    @Override
    public Future<?> removeCellPhoneAsync(PhoneNumberDTOType parametersRemove, AsyncHandler<?> asyncHandler) {

        return null;
    }



    @Override
    public Response<PhoneNumValueDTOType> getBalanceAsync(PhoneNumberDTOType parameters) {

        return null;
    }



    @Override
    public Future<?> getBalanceAsync(PhoneNumberDTOType parameters, AsyncHandler<PhoneNumValueDTOType> asyncHandler) {

        return null;
    }



    @Override
    public Response<?> receiveSMSAsync(SmsDTOType parameters) {

        return null;
    }



    @Override
    public Future<?> receiveSMSAsync(SmsDTOType parameters, AsyncHandler<?> asyncHandler) {

        return null;
    }



    @Override
    public Response<SMSListDTOType> getReceivedSMSAsync(PhoneNumberDTOType parameters) {

        return null;
    }



    @Override
    public Future<?> getReceivedSMSAsync(PhoneNumberDTOType parameters, AsyncHandler<SMSListDTOType> asyncHandler) {

        return null;
    }



    @Override
    public Response<?> createCellPhoneAsync(PhoneDetailedDTOElem parameters) {

        return null;
    }



    @Override
    public Future<?> createCellPhoneAsync(PhoneDetailedDTOElem parameters, AsyncHandler<?> asyncHandler) {

        return null;
    }



    @Override
    public Response<PhoneListDTOType> getCellPhoneListAsync(OperatorSimpleDTOType parameters) {

        return null;
    }



    @Override
    public Future<?> getCellPhoneListAsync(OperatorSimpleDTOType parameters, AsyncHandler<PhoneListDTOType> asyncHandler) {

        return null;
    }



    @Override
    public Response<?> terminationCallerVoiceAsync(VoiceDTOType parameters) {

        return null;
    }



    @Override
    public Future<?> terminationCallerVoiceAsync(VoiceDTOType parameters, AsyncHandler<?> asyncHandler) {

        return null;
    }



    @Override
    public Response<?> initReceiverVoiceCommunicationAsync(VoiceDTOType parameters) {

        return null;
    }



    @Override
    public Future<?> initReceiverVoiceCommunicationAsync(VoiceDTOType parameters, AsyncHandler<?> asyncHandler) {

        return null;
    }



    @Override
    public Response<?> sendSMSAsync(SmsDTOType parameters) {

        return null;
    }



    @Override
    public Future<?> sendSMSAsync(SmsDTOType parameters, AsyncHandler<?> asyncHandler) {

        return null;
    }



    @Override
    public Response<?> initCallerVoiceCommunicationAsync(VoiceDTOType parameters) {

        return null;
    }



    @Override
    public Future<?> initCallerVoiceCommunicationAsync(VoiceDTOType parameters, AsyncHandler<?> asyncHandler) {

        return null;
    }



    @Override
    public Response<?> addBalanceAsync(PhoneNumValueDTOType parameters) {
        return null;
    }



    @Override
    public Future<?> addBalanceAsync(PhoneNumValueDTOType parameters, AsyncHandler<?> asyncHandler) {

        return null;
    }



    @Override
    public Response<CommunicationDTOType> getLastMadeCommunicationAsync(PhoneNumberDTOType parameters) {

        return null;
    }



    @Override
    public Future<?> getLastMadeCommunicationAsync(PhoneNumberDTOType parameters, AsyncHandler<CommunicationDTOType> asyncHandler) {

        return null;
    }



    @Override
    public Response<PhoneNumModeDTOType> getCellPhoneModeAsync(PhoneNumberDTOType parameters) {

        return null;
    }



    @Override
    public Future<?> getCellPhoneModeAsync(PhoneNumberDTOType parameters, AsyncHandler<PhoneNumModeDTOType> asyncHandler) {

        return null;
    }


    @Override
    public Response<PhoneNumberDTOType> getSequenceNumberAsync(PhoneNumberDTOType parameters) {

        return null;
    }

    @Override
    public Future<?> getSequenceNumberAsync(PhoneNumberDTOType parameters, AsyncHandler<PhoneNumberDTOType> asyncHandler) {

        return null;
    }


    /**
     * Enquanto o pedido não tiver o sequence number correcto, a thread deve aguardar para
     * ser atendidada.
     * 
     * @param sequenceCheck
     * @param Number
     * @throws OperatorNotFoundException
     */
    private synchronized void enforceSequentialExecution(long sequenceCheck, String Number) throws OperatorNotFoundException {
        PhoneNumberDTOType check = new PhoneNumberDTOType();
        check.setNumber(Number);
        check.setSequenceNumber(sequenceCheck);

        while (sequenceCheck > (getSequenceNumber(check).getSequenceNumber() + 1)) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Error: Method Interrupted due to sequence number check.");
            }
        }
    }

    @Override
    public Response<InitiateCommunicationDTOType> ableToCommunicateAsync(InitiateCommunicationDTOType parameters) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<?> ableToCommunicateAsync(InitiateCommunicationDTOType parameters, AsyncHandler<InitiateCommunicationDTOType> asyncHandler) {
        // TODO Auto-generated method stub
        return null;
    }







}
