package pt.ist.anacom.presentationserver.server;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.Handler;

import pt.ist.anacom.applicationserver.handler.HandlerSD;
import pt.ist.anacom.service.bridge.ApplicationServerBridge;
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
import pt.ist.anacom.shared.enumerados.CellPhoneMode;
import pt.ist.anacom.shared.enumerados.CommunicationType;
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
import pt.ist.anacom.shared.exceptions.ReplicateException;
import pt.ist.anacom.shared.stubs.Anacom;
import pt.ist.anacom.shared.stubs.AnacomApplicationServerPortType;
import pt.ist.anacom.shared.stubs.CommunicationDTOType;
import pt.ist.anacom.shared.stubs.InitiateCommunicationDTOType;
import pt.ist.anacom.shared.stubs.OperatorSimpleDTOType;
import pt.ist.anacom.shared.stubs.PhoneDetailedDTOElem;
import pt.ist.anacom.shared.stubs.PhoneListDTOType;
import pt.ist.anacom.shared.stubs.PhoneNumModeDTOType;
import pt.ist.anacom.shared.stubs.PhoneNumValueDTOType;
import pt.ist.anacom.shared.stubs.PhoneNumberDTOType;
import pt.ist.anacom.shared.stubs.SMSListDTOType;
import pt.ist.anacom.shared.stubs.SmsDTOType;
import pt.ist.anacom.shared.stubs.VoiceDTOType;

/**
 * Nesta bridge, em cada método: 1º determinar o port servidor. 2º Preencher os ReqType
 * que é dado pelo stub 3º Invocar a função do stub passando-lhe o ReqType 4º Retornar o
 * resultado
 */

public class ApplicationServerBridgeDistReplicas
        implements ApplicationServerBridge {


    /** WebServicesLocator - Resolver o nome ou prefixo do operador em url do servidor. */
    private WebServicesLocator _webServicesLocator = new WebServicesLocator();

    private static int NUMERO_REPLICAS = 4;
    private ReplicateLogicV3 replicateLogic = new ReplicateLogicV3();

    // ---------------------------------------------------------------------------
    // ------------------HANDLERS---------------------
    // ---------------------------------------------------------------------------

    /*
     * Inicializa o Handler-chain do lado do cliente //Handler definido programaticamente
     * initHandlerClient(port); port.createCellPhone(msg); (...)
     */
    @SuppressWarnings("rawtypes")
    public void initHandlerClient(AnacomApplicationServerPortType port) throws AnaComException {
        try {

            Binding binding = ((BindingProvider) port).getBinding();
            List<Handler> handlerList = binding.getHandlerChain();

            // Adiciona HandlerClient ao handler chain
            handlerList.add(new HandlerSD("CN=ApplicationServer,OU=Tagus"));
            // handlerList.add(new AttackHandler());
            binding.setHandlerChain(handlerList);
        } catch (Exception e) {
            System.out.println("initHandlerClient exception " + e.getMessage());
        }
    }


    // ---------------------------------------------------------------------------
    // ------------------Implementation of the Bridge Methods---------------------
    // ---------------------------------------------------------------------------


    /**
     * @param PhoneNumValueDTO, dto com os detalhes do telemovel e saldo a incrementar
     * @throws InvalidBalanceException, Caso o saldo ultrapasse o limite ou seja negativo.
     * @throws InvalidOperatorException, Caso o Operador não corresponda ou tlmvl
     * @throws CellPhoneNotExistsException, Caso o telemovel não exista
     * @throws OperatorNotFoundException, Caso o Operador não exista Função que adiciona o
     *             telemovel as Replicas, usando como auxiliar o ReplicationLogic.
     */
    @Override
    public synchronized void addBalance(PhoneNumValueDTO dto) throws InvalidBalanceException, InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException {

        final String number = dto.get_number();
        // final AnacomApplicationServerPortType port =
        // getPortByOperatorPhoneNumber(number);
        ArrayList<AnacomApplicationServerPortType> portList = getPortListByOperatorPhoneNumber(number);
        /* Criar a mensagem SOAP apartir do DTO */
        PhoneNumValueDTOType msg = new PhoneNumValueDTOType();
        msg.setBalance(dto.getBalance());
        msg.setNumber(number);

        checkNumReplicas(portList.size());

        try {
            replicateLogic.addBalance(portList, msg);

        } catch (pt.ist.anacom.shared.stubs.InvalidOperatorException_Exception e) {
            pt.ist.anacom.shared.stubs.InvalidOperatorException exc = e.getFaultInfo();
            throw new InvalidBalanceException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.CellPhoneNotExistsException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new CellPhoneNotExistsException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.OperatorNotFoundException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new OperatorNotFoundException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.InvalidBalanceException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new InvalidBalanceException(exc.getError());

        } catch (ReplicateException e) {
            System.out.println("ReplicationBridge : More than one faulted server detected");
            throw new OperatorNotFoundException("Server Error: More than one faulted server detected.\n" + e.getMessage());
        } catch (WebServiceException e) {
            System.out.println("ReplicationBridge : WebServiceException");
            throw new OperatorNotFoundException(e.getMessage().toString());
        } catch (InterruptedException e) {
            System.out.println("ReplicationBridge : InterruptedException");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        } catch (Throwable e) {
            System.out.println("ReplicationBridge : Throwable");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        }
    }



    /**
     * Adicionar telemovel no servidor remoto, recebe o DTO, desempacota, obtem o operador
     * pelo uddi, cria a mensagem SOAP XML e invoca o WS enviando a mensagem.
     * 
     * @param dto : PhoneDetailedDTO com os detalhes do telemovel a criar
     * @throws CellPhoneAlreadyExistsException the cell phone already exists exception
     * @throws InvalidOperatorException não existe servidor com o operador pretendido
     * @throws InvalidNumberException
     */
    @Override
    public synchronized void addCellPhone(PhoneDetailedDTO dto) throws CellPhoneAlreadyExistsException, InvalidOperatorException, InvalidNumberException {

        // obter o WSDL do operador
        ArrayList<AnacomApplicationServerPortType> portList = getPortListByOperatorName(dto.get_operator());

        /* Criar a mensagem SOAP apartir do DTO */
        PhoneDetailedDTOElem msg = new PhoneDetailedDTOElem();

        msg.setNumber(dto.get_number());
        msg.setOperator(dto.get_operator());
        msg.setType(dto.get_cellPhoneType().toString());
        msg.setBalance(dto.getBalance());

        checkNumReplicas(portList.size());

        try {
            replicateLogic.addCellPhone(portList, msg);

        } catch (pt.ist.anacom.shared.stubs.InvalidOperatorException_Exception e) {
            pt.ist.anacom.shared.stubs.InvalidOperatorException exc = e.getFaultInfo();
            throw new InvalidOperatorException(exc.getOperatorName(), exc.getError());

        } catch (pt.ist.anacom.shared.stubs.InvalidNumberException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new InvalidNumberException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.CellPhoneAlreadyExistsException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new CellPhoneAlreadyExistsException(exc.getError());
        } catch (ReplicateException e) {
            System.out.println("ReplicationBridge : More than one faulted server detected");
            throw new OperatorNotFoundException("Server Error: More than one faulted server detected.\n" + e.getMessage());
        } catch (WebServiceException e) {
            System.out.println("ReplicationBridge : WebServiceException");
            throw new OperatorNotFoundException(e.getMessage().toString());
        } catch (InterruptedException e) {
            System.out.println("ReplicationBridge : InterruptedException");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        } catch (Throwable e) {
            System.out.println("ReplicationBridge : Throwable");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        }

    }



    /**
     * createOperator Este método não faz sentido distribuido uma vez que os operadores já
     * estão criados nos servidores de raiz.
     * 
     * @param dto the dto
     * @throws OperatorAlreadyExistsException the operator already exists exception
     */
    @Override
    public synchronized void createOperator(OperatorDetailedDTO dto) throws OperatorAlreadyExistsException, OperatorException, InvalidOperatorPrefixException, AlreadyExistOperatorException {
        throw new OperatorNotFoundException("Nao e possivel criar operadores em servidores Remotos");
    }



    /**
     * removeCellPhone.
     * 
     * @param dto com o numero do telefone a eliminar
     * @throws CellPhoneNotExistsException the cell phone not exists exception
     * @throws InvalidNumberException the invalid number exception
     * @throws pt.ist.anacom.shared.stubs.OperatorNotFoundException
     * @throws pt.ist.anacom.shared.stubs.CellPhoneNotExistsException
     * @throws InvalidOperatorException
     */
    @Override
    public synchronized void removeCellPhone(PhoneNumberDTO dto) throws CellPhoneNotExistsException, InvalidNumberException, InvalidOperatorException, OperatorNotFoundException {
        String number = dto.get_number();

        ArrayList<AnacomApplicationServerPortType> portList = getPortListByOperatorPhoneNumber(number);

        /* Criar a mensagem SOAP apartir do DTO */
        PhoneNumberDTOType msg = new PhoneNumberDTOType();
        msg.setNumber(number);


        checkNumReplicas(portList.size());

        try {

            replicateLogic.removeCell(portList, msg);

        } catch (pt.ist.anacom.shared.stubs.InvalidNumberException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new InvalidNumberException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.CellPhoneNotExistsException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new CellPhoneNotExistsException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.OperatorNotFoundException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new OperatorNotFoundException(exc.getError());

        } catch (ReplicateException e) {
            System.out.println("ReplicationBridge : More than one faulted server detected");
            throw new OperatorNotFoundException("Server Error: More than one faulted server detected.\n" + e.getMessage());
        } catch (WebServiceException e) {
            System.out.println("ReplicationBridge : WebServiceException");
            throw new OperatorNotFoundException(e.getMessage().toString());
        } catch (InterruptedException e) {
            System.out.println("ReplicationBridge : InterruptedException");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        } catch (Throwable e) {
            System.out.println("ReplicationBridge : Throwable");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        }
    }



    /**
     * PhoneNumValueDTO.
     * 
     * @param dto com número de telemóvel
     * @return PhoneNumValueDTO - resposta do servidor Este método recebe um DTO e faz a
     *         conversão dos parametros para uma mensagem do tipo SOAP para ser enviada
     * @throws CellPhoneNotExistsException
     * @throws OperatorNotFoundException
     * @throws InvalidParametersException
     * @throws AnaComException the ana com exception
     */
    @Override
    public synchronized PhoneNumValueDTO getBalance(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException, InvalidParametersException {

        ArrayList<AnacomApplicationServerPortType> portList = getPortListByOperatorPhoneNumber(dto.get_number());

        /* Criar a mensagem SOAP apartir do DTO */
        PhoneNumberDTOType msg = new PhoneNumberDTOType();
        msg.setNumber(dto.get_number());

        checkNumReplicas(portList.size());

        try {

            PhoneNumValueDTOType response = replicateLogic.getBalance(portList, msg);
            return new PhoneNumValueDTO(response.getNumber(), response.getBalance());

        } catch (pt.ist.anacom.shared.stubs.InvalidOperatorException_Exception e) {
            pt.ist.anacom.shared.stubs.InvalidOperatorException exc = e.getFaultInfo();
            throw new InvalidOperatorException(exc.getOperatorName(), exc.getError());

        } catch (InvalidOperatorException e) {
            throw new InvalidOperatorException();

        } catch (pt.ist.anacom.shared.stubs.CellPhoneNotExistsException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new CellPhoneNotExistsException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.OperatorNotFoundException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new OperatorNotFoundException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.InvalidBalanceException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new InvalidBalanceException(exc.getError());

        } catch (ReplicateException e) {
            throw new OperatorNotFoundException("Server Error: More than one faulted server detected.\n" + e.getMessage());
        } catch (WebServiceException e) {
            System.out.println("ReplicationBridge : WebServiceException");
            throw new OperatorNotFoundException(e.getMessage().toString());
        } catch (InterruptedException e) {
            System.out.println("ReplicationBridge : InterruptedException");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        } catch (Throwable e) {
            System.out.println("ReplicationBridge : Throwable");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        }

    }



    /**
     * getCellPhoneList.
     * 
     * @param dto com número de telemóvel e modo que deve ser mudado Este método recebe um
     *            DTO e faz a conversão dos parametros para uma mensagem do tipo SOAP para
     *            ser enviada
     * @return the cell phone list
     * @throws InvalidOperatorException
     * @throws AnaComException the ana com exception
     */
    @Override
    public synchronized PhoneListDTO getCellPhoneList(OperatorSimpleDTO dto) throws InvalidOperatorException {

        ArrayList<AnacomApplicationServerPortType> portList = getPortListByOperatorName(dto.get_name());

        OperatorSimpleDTOType msg = new OperatorSimpleDTOType();
        msg.setName(dto.get_name());
        PhoneListDTOType response = new PhoneListDTOType();

        checkNumReplicas(portList.size());

        try {

            response = replicateLogic.getCellPhoneList(portList, msg);

        } catch (pt.ist.anacom.shared.stubs.InvalidOperatorException_Exception e) {
            pt.ist.anacom.shared.stubs.InvalidOperatorException exc = e.getFaultInfo();
            throw new InvalidOperatorException(exc.getOperatorName(), exc.getError());

        } catch (ReplicateException e) {
            throw new OperatorNotFoundException("Server Error: More than one faulted server detected.\n" + e.getMessage());
        } catch (WebServiceException e) {
            System.out.println("ReplicationBridge : WebServiceException");
            throw new OperatorNotFoundException(e.getMessage().toString());
        } catch (InterruptedException e) {
            System.out.println("ReplicationBridge : InterruptedException");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        } catch (Throwable e) {
            System.out.println("ReplicationBridge : Throwable");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        }

        List<PhoneNumValueDTOType> cellPhoneList = response.getCellPhoneList();
        List<PhoneNumValueDTO> phoneList = new ArrayList<PhoneNumValueDTO>();

        for (PhoneNumValueDTOType c : cellPhoneList) {
            PhoneNumValueDTO d = new PhoneNumValueDTO(c.getNumber(), c.getBalance());
            phoneList.add(d);
        }
        return new PhoneListDTO(phoneList);
    }



    /**
     * processSMS
     * 
     * @param dto com informação para envio e recepção de uma dad mensagem De notar que é
     *            aqui que se faz o agulhamento para os operadores do telemovel de destino
     *            e de origem
     */
    @Override
    public synchronized void processSMS(SMSDTO dto) throws CellPhoneNotExistsException,
            InvalidOperatorException,
            OperatorNotFoundException,
            NotEnoughBalanceException,
            IncompatibleModeException,
            CellPhoneNotAvailableException {


        String srcNumber = dto.getSrcNumber();
        String destNumber = dto.getDestNumber();
        String message = dto.getText();

        // sendPort: operador que irá ENVIAR mensagem
        ArrayList<AnacomApplicationServerPortType> sendPortList = getPortListByOperatorPhoneNumber(srcNumber);

        // sendPort: operador que irá RECEBER mensagem
        ArrayList<AnacomApplicationServerPortType> receivePortList = getPortListByOperatorPhoneNumber(destNumber);

        // DTO para efectuar as chamadas
        SmsDTOType receiveMsg = new SmsDTOType();
        receiveMsg.setDest(destNumber);
        receiveMsg.setSrc(srcNumber);
        receiveMsg.setMsg(message);

        SmsDTOType sendMsg = new SmsDTOType();
        sendMsg.setDest(destNumber);
        sendMsg.setSrc(srcNumber);
        sendMsg.setMsg(message);



        InitiateCommunicationDTOType iniReceiveMsg = new InitiateCommunicationDTOType();
        iniReceiveMsg.setCellPhoneNumber(destNumber);
        iniReceiveMsg.setCommunicationBeingMade(CommunicationType.RECEIVESMS.toString());

        InitiateCommunicationDTOType iniSendMsg = new InitiateCommunicationDTOType();
        iniSendMsg.setCellPhoneNumber(srcNumber);
        iniSendMsg.setCommunicationBeingMade(CommunicationType.SENDSMS.toString());

        // respostas
        InitiateCommunicationDTOType iniReceiveMsgRes = new InitiateCommunicationDTOType();
        iniReceiveMsgRes.setAbleToCommunicate(false);
        InitiateCommunicationDTOType iniSendMsgRes = new InitiateCommunicationDTOType();
        iniSendMsgRes.setAbleToCommunicate(false);

        // verificar se tem o nº de réplicas necessárias
        checkNumReplicas(sendPortList.size());
        checkNumReplicas(receivePortList.size());
        try {
            // Verificar se a origem e o destino estão em modo correcto
            iniReceiveMsgRes = replicateLogic.ableToComunicate(receivePortList, iniReceiveMsg);
            iniSendMsgRes = replicateLogic.ableToComunicate(receivePortList, iniSendMsg);


            if (!iniSendMsgRes.isAbleToCommunicate()) {
                throw new IncompatibleModeException("Sender CellPhone is in incompatible mode for Comunication");
            }
            if (!iniReceiveMsgRes.isAbleToCommunicate()) {
                throw new IncompatibleModeException("Receiver CellPhone is in incompatible mode for Comunication");
            }
            replicateLogic.sendSMS(sendPortList, sendMsg);
            replicateLogic.receiveSMS(receivePortList, receiveMsg);

        } catch (pt.ist.anacom.shared.stubs.OperatorNotFoundException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new OperatorNotFoundException(exc.getError());
        } catch (pt.ist.anacom.shared.stubs.CellPhoneNotExistsException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new CellPhoneNotExistsException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.IncompatibleModeException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new IncompatibleModeException(exc.getError());
        } catch (pt.ist.anacom.shared.stubs.CellPhoneNotAvailableException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new CellPhoneNotAvailableException(exc.getError());
        } catch (pt.ist.anacom.shared.stubs.NotEnoughBalanceException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new NotEnoughBalanceException(exc.getError());
        } catch (IncompatibleModeException e) {
            throw e;
        } catch (ReplicateException e) {
            throw new OperatorNotFoundException("Server Error: More than one faulted server detected.\n" + e.getMessage());
        } catch (WebServiceException e) {
            System.out.println("ReplicationBridge : WebServiceException");
            throw new OperatorNotFoundException(e.getMessage().toString());
        } catch (InterruptedException e) {
            System.out.println("ReplicationBridge : InterruptedException");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        } catch (Throwable e) {
            System.out.println("ReplicationBridge : Throwable " + e.getClass());
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        }
    }

    /**
     * changeCellPhoneMode.
     * 
     * @param dto com número de telemóvel e modo que deve ser mudado Este método rece um
     *            DTO e faz a conversão dos parametros para uma mensagem do tipo SOAP para
     *            ser enviada
     * @throws InvalidOperatorException
     * @throws CellPhoneNotExistsException
     * @throws OperatorNotFoundException
     * @throws AnaComException the ana com exception
     */
    @Override
    public synchronized void changeCellPhoneMode(PhoneNumModeDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException, IncompatibleModeException {
        String cellPhoneModeString = null;
        String cellPhoneNumber = dto.get_number();
        CellPhoneMode cellPhoneMode = dto.getMode();

        ArrayList<AnacomApplicationServerPortType> portList = getPortListByOperatorPhoneNumber(cellPhoneNumber);

        switch (cellPhoneMode) {
        case ON:
            cellPhoneModeString = "ON";
            break;
        case OFF:
            cellPhoneModeString = "OFF";
            break;
        case SILENCE:
            cellPhoneModeString = "SILENCE";
            break;
        case BUSY:
            cellPhoneModeString = "BUSY";
            break;
        }

        PhoneNumModeDTOType msg = new PhoneNumModeDTOType();

        msg.setNumber(cellPhoneNumber);
        msg.setMode(cellPhoneModeString);


        checkNumReplicas(portList.size());

        try {

            replicateLogic.changeCellPhoneMode(portList, msg);


        } catch (pt.ist.anacom.shared.stubs.CellPhoneNotExistsException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new CellPhoneNotExistsException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.OperatorNotFoundException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new OperatorNotFoundException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.IncompatibleModeException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new IncompatibleModeException(exc.getError());

        } catch (ReplicateException e) {
            throw new OperatorNotFoundException("Server Error: More than one faulted server detected.\n" + e.getMessage());
        } catch (WebServiceException e) {
            System.out.println("ReplicationBridge : WebServiceException");
            throw new OperatorNotFoundException(e.getMessage().toString());
        } catch (InterruptedException e) {
            System.out.println("ReplicationBridge : InterruptedException");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        } catch (Throwable e) {
            System.out.println("ReplicationBridge : Throwable");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        }
    }



    /**
     * getCellPhoneMode
     * 
     * @param PhoneNumberDTO, dto com detalhes do telemovel a ser buscado
     * @throws CellPhoneNotExistsException, caso o telemovel não exista.
     * @throws OperatorNotFoundException, caso o Operador não exista.
     * @return PhoneNumModeDTO, dto com o modo do telemovel
     */
    @Override
    public synchronized PhoneNumModeDTO getCellPhoneMode(PhoneNumberDTO dto) throws CellPhoneNotExistsException, OperatorNotFoundException {

        PhoneNumberDTOType msg = new PhoneNumberDTOType();
        msg.setNumber(dto.get_number());

        ArrayList<AnacomApplicationServerPortType> portList = getPortListByOperatorPhoneNumber(dto.get_number());

        PhoneNumModeDTOType resposta = new PhoneNumModeDTOType();


        checkNumReplicas(portList.size());
        try {
            resposta = replicateLogic.getCellPhoneMode(portList, msg);

            CellPhoneMode mode = null;
            final String _responseMode = resposta.getMode();

            if (_responseMode.equals("ON")) {
                mode = CellPhoneMode.ON;
            } else if (_responseMode.equals("OFF")) {
                mode = CellPhoneMode.OFF;
            } else if (_responseMode.equals("BUSY")) {
                mode = CellPhoneMode.BUSY;
            } else if (_responseMode.equals("SILENCE")) {
                mode = CellPhoneMode.SILENCE;
            } else
                mode = null;
            return new PhoneNumModeDTO(resposta.getNumber(), mode);

        } catch (pt.ist.anacom.shared.stubs.CellPhoneNotExistsException e) {
            final pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new CellPhoneAlreadyExistsException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.OperatorNotFoundException e) {
            final pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new OperatorNotFoundException(exc.getError());

        } catch (ReplicateException e) {
            throw new OperatorNotFoundException("Server Error: More than one faulted server detected.\n" + e.getMessage());
        }

        catch (WebServiceException e) {
            System.out.println("ReplicationBridge : WebServiceException");
            throw new OperatorNotFoundException(e.getMessage().toString());
        } catch (InterruptedException e) {
            System.out.println("ReplicationBridge : InterruptedException");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        } catch (Throwable e) {
            System.out.println("ReplicationBridge : Throwable");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        }
    }



    /**
     * getReceivedSMS
     * 
     * @param PhoneNumberDTO, dto com detalhes do telemovel a ser buscado.
     * @throws CellPhoneNotExistsException, caso o telemovel não exista.
     * @throws InvalidOperatorException, Caso o Operador não corresponda ao telemovel.
     * @throws OperatorNotFoundException, caso o Operador não exista.
     * @return SMSListDTO, dto com o lista dos SMS do telemovel.
     */
    @Override
    public synchronized SMSListDTO getReceivedSMS(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException {

        ArrayList<AnacomApplicationServerPortType> portList = getPortListByOperatorPhoneNumber(dto.get_number());
        PhoneNumberDTOType requestMsg = new PhoneNumberDTOType();
        requestMsg.setNumber(dto.get_number());

        SMSListDTOType responseMsg;
        checkNumReplicas(portList.size());
        try {
            responseMsg = replicateLogic.getReceivedSMS(portList, requestMsg);

            List<SmsDTOType> list = responseMsg.getSmsList();

            List<SMSDTO> smsList = new ArrayList<SMSDTO>();

            for (SmsDTOType sms : list) {
                smsList.add(new SMSDTO(sms.getSrc(), sms.getDest(), sms.getMsg()));
            }
            return new SMSListDTO(smsList);

        } catch (pt.ist.anacom.shared.stubs.InvalidOperatorException_Exception e) {
            final pt.ist.anacom.shared.stubs.InvalidOperatorException exc = e.getFaultInfo();
            throw new InvalidOperatorException(exc.getOperatorName(), exc.getError());

        } catch (pt.ist.anacom.shared.stubs.CellPhoneNotExistsException e) {
            final pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new CellPhoneAlreadyExistsException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.OperatorNotFoundException e) {
            final pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new OperatorNotFoundException(exc.getError());

        } catch (ReplicateException e) {
            throw new OperatorNotFoundException("Server Error: More than one faulted server detected.\n" + e.getMessage());
        }

        catch (WebServiceException e) {
            System.out.println("ReplicationBridge : WebServiceException");
            throw new OperatorNotFoundException(e.getMessage().toString());
        } catch (InterruptedException e) {
            System.out.println("ReplicationBridge : InterruptedException");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        } catch (Throwable e) {
            System.out.println("ReplicationBridge : Throwable");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        }
    }



    /**
     * getLastMadeCommunication
     * 
     * @param PhoneNumberDTO, dto com detalhes do telemovel a ser buscado.
     * @throws CellPhoneNotExistsException, caso o telemovel não exista.
     * @throws InvalidOperatorException, Caso o Operador não corresponda ao telemovel.
     * @throws OperatorNotFoundException, caso o Operador não exista.
     * @throws InvalidCommunicationException, caso o telemovel não tenha efectuado nenhuma
     *             comunicação
     * @return CommunicationDTO, dto com o ultima comunication do telemovel.
     */
    @Override
    public synchronized CommunicationDTO getLastMadeCommunication(PhoneNumberDTO dto) throws InvalidOperatorException,
            CellPhoneNotExistsException,
            OperatorNotFoundException,
            InvalidCommunicationException {
        String number = dto.get_number();

        ArrayList<AnacomApplicationServerPortType> portList = getPortListByOperatorPhoneNumber(number);

        PhoneNumberDTOType dtoMsg = new PhoneNumberDTOType();
        dtoMsg.setNumber(number);

        CommunicationDTOType respMsg;
        CommunicationDTO resp;

        checkNumReplicas(portList.size());

        try {
            respMsg = replicateLogic.getLastMadeCommunication(portList, dtoMsg);

            resp = new CommunicationDTO(respMsg.getDest(), respMsg.getType(), respMsg.getCost(), respMsg.getSize());
        } catch (pt.ist.anacom.shared.stubs.CellPhoneNotExistsException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new CellPhoneNotExistsException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.OperatorNotFoundException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new OperatorNotFoundException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.InvalidCommunicationException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new InvalidCommunicationException(exc.getError());

        } catch (ReplicateException e) {
            throw new OperatorNotFoundException("Server Error: More than one faulted server detected.\n" + e.getMessage());
        }

        catch (WebServiceException e) {
            System.out.println("ReplicationBridge : WebServiceException");
            throw new OperatorNotFoundException(e.getMessage().toString());
        } catch (InterruptedException e) {
            System.out.println("ReplicationBridge : InterruptedException");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        } catch (Throwable e) {
            System.out.println("ReplicationBridge : Throwable");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        }

        return resp;
    }


    /**
     * initiateVoiceCommunication
     * 
     * @rev Dário N. - 15-Maio
     * @param VoiceDTO
     * @throws CellPhoneNotAvailableException, caso o telemovel não esteja disponivel.
     * @throws CellPhoneNotExistsException, Caso o telemovel não exista.
     * @throws OperatorNotFoundException, caso o Operador não exista.
     * @throws IncompatibleModeException, caso o telemovel não esteja num modo compativel
     */
    @Override
    public synchronized void initiateVoiceCommunication(VoiceDTO dto) {
        String srcNumber = dto.getSrcNumber();
        String destNumber = dto.getDestNumber();
        int duration = 0;

        // sendPort: operador que irá ENVIAR
        ArrayList<AnacomApplicationServerPortType> sendPortList = getPortListByOperatorPhoneNumber(srcNumber);
        // sendPort: operador que irá RECEBER
        ArrayList<AnacomApplicationServerPortType> receivePortList = getPortListByOperatorPhoneNumber(destNumber);

        // DTO para efectuar as chamadas
        VoiceDTOType voiceDto = new VoiceDTOType();
        voiceDto.setSrc(srcNumber);
        voiceDto.setDest(destNumber);
        voiceDto.setDuration(duration);

        // DTO para verificar se pode efectuar a chamada
        InitiateCommunicationDTOType iniReceiveMsg = new InitiateCommunicationDTOType();
        iniReceiveMsg.setCellPhoneNumber(destNumber);
        iniReceiveMsg.setCommunicationBeingMade(CommunicationType.RECEIVEVOICE.toString());

        InitiateCommunicationDTOType iniSendMsg = new InitiateCommunicationDTOType();
        iniSendMsg.setCellPhoneNumber(srcNumber);
        iniSendMsg.setCommunicationBeingMade(CommunicationType.SENDVOICE.toString());

        // respostas
        InitiateCommunicationDTOType iniReceiveMsgRes = new InitiateCommunicationDTOType();
        iniReceiveMsgRes.setAbleToCommunicate(false);
        InitiateCommunicationDTOType iniSendMsgRes = new InitiateCommunicationDTOType();
        iniSendMsgRes.setAbleToCommunicate(false);

        // verificar se tem o nº de réplicas necessárias
        checkNumReplicas(sendPortList.size());
        checkNumReplicas(receivePortList.size());
        try {
            // Verificar se a origem e o destino estão em modo correcto
            iniReceiveMsgRes = replicateLogic.ableToComunicate(receivePortList, iniReceiveMsg);
            iniSendMsgRes = replicateLogic.ableToComunicate(receivePortList, iniSendMsg);


            if (!iniSendMsgRes.isAbleToCommunicate()) {
                throw new IncompatibleModeException("Sender CellPhone is in incompatible mode for Comunication");
            }
            if (!iniReceiveMsgRes.isAbleToCommunicate()) {
                throw new IncompatibleModeException("Receiver CellPhone is in incompatible mode for Comunication");
            }
            replicateLogic.initCallerVoiceCommunication(sendPortList, voiceDto);
            replicateLogic.initReceiverVoiceCommunication(receivePortList, voiceDto);

        } catch (pt.ist.anacom.shared.stubs.OperatorNotFoundException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new OperatorNotFoundException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.CellPhoneNotExistsException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new CellPhoneNotExistsException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.IncompatibleModeException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new IncompatibleModeException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.CellPhoneNotAvailableException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new CellPhoneNotAvailableException(exc.getError());
        } catch (pt.ist.anacom.shared.stubs.NotEnoughBalanceException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new IncompatibleModeException(exc.getError());

        } catch (IncompatibleModeException e) {
            throw e;
        } catch (ReplicateException e) {
            throw new OperatorNotFoundException("Server Error: More than one faulted server detected.\n" + e.getMessage());
        } catch (WebServiceException e) {
            System.out.println("ReplicationBridge : WebServiceException");
            throw new OperatorNotFoundException(e.getMessage().toString());
        } catch (InterruptedException e) {
            System.out.println("ReplicationBridge : InterruptedException");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        } catch (Throwable e) {
            System.out.println("ReplicationBridge : Throwable " + e.getClass());
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        }
    }


    /**
     * endVoiceCommunication
     * 
     * @param VoiceDTO
     * @throws CellPhoneNotAvailableException, caso o telemovel não esteja disponivel.
     * @throws CellPhoneNotExistsException, Caso o telemovel não exista.
     * @throws OperatorNotFoundException, caso o Operador não exista.
     * @throws IncompatibleModeException, caso o telemovel não esteja num modo compativel
     * @throws NotEnoughBalanceException
     */
    @Override
    public synchronized void endVoiceCommunication(VoiceDTO dto) throws CellPhoneNotAvailableException,
            OperatorNotFoundException,
            CellPhoneNotExistsException,
            NotEnoughBalanceException,
            IncompatibleModeException {


        String srcNumber = dto.getSrcNumber();
        String destNumber = dto.getDestNumber();
        int duration = dto.getDuration();

        // sendPort: operador que irá ENVIAR
        ArrayList<AnacomApplicationServerPortType> sendPortList = getPortListByOperatorPhoneNumber(srcNumber);
        // sendPort: operador que irá RECEBER
        ArrayList<AnacomApplicationServerPortType> receivePortList = getPortListByOperatorPhoneNumber(destNumber);

        // DTO para guardar os dados da chamada
        VoiceDTOType voiceDto = new VoiceDTOType();
        voiceDto.setSrc(srcNumber);
        voiceDto.setDest(destNumber);
        voiceDto.setDuration(duration); // está a 0

        // DTO para ler a ultima chamada efectuada.
        PhoneNumberDTOType dtoMsg = new PhoneNumberDTOType();
        dtoMsg.setNumber(srcNumber);
        try {

            replicateLogic.terminationCallerVoiceCommunication(sendPortList, voiceDto);

            // pede a duração da ultima chamada (esta) ao iniciador da chamada
            CommunicationDTOType respMsg = replicateLogic.getLastMadeCommunication(sendPortList, dtoMsg);


            VoiceDTOType receiverDto = new VoiceDTOType();
            receiverDto.setSrc(srcNumber);
            receiverDto.setDest(destNumber);
            receiverDto.setDuration(respMsg.getSize());


            // manda informação com a duração da chamada
            replicateLogic.terminationReceiverVoiceCommunication(receivePortList, voiceDto);

        } catch (pt.ist.anacom.shared.stubs.CellPhoneNotExistsException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new CellPhoneNotExistsException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.OperatorNotFoundException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new OperatorNotFoundException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.CellPhoneNotAvailableException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new CellPhoneNotExistsException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.IncompatibleModeException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new IncompatibleModeException(exc.getError());

        } catch (pt.ist.anacom.shared.stubs.NotEnoughBalanceException e) {
            pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
            throw new IncompatibleModeException(exc.getError());

        } catch (ReplicateException e) {
            throw new OperatorNotFoundException("Server Error: More than one faulted server detected.\n" + e.getMessage());
        }

        catch (WebServiceException e) {
            System.out.println("ReplicationBridge : WebServiceException");
            throw new OperatorNotFoundException(e.getMessage().toString());
        } catch (InterruptedException e) {
            System.out.println("ReplicationBridge : InterruptedException");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        } catch (Throwable e) {
            System.out.println("ReplicationBridge : Throwable");
            throw new OperatorNotFoundException("Server Error: Could not get correct response from the server.\n" + e.getMessage());
        }
    }



    /**
     * Obter o porto apartir do url do servidor do operador.
     * 
     * @param endpointURL url do servidor
     * @return porto do web service
     */
    private AnacomApplicationServerPortType getPortByUrl(String endpointURL) {
        final AnacomApplicationServerPortType port = new Anacom().getAnacomApplicationServicePort();
        final BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);
        initHandlerClient(port);
        return port;
    }


    /**
     * Obter os portos do servidor apartir do nome do operador.
     * 
     * @param operatorName nome do operador
     * @return AnacomApplicationServerPortType Web Service List
     * @throws InvalidOperatorException Não existe operador com o nome
     */
    private ArrayList<AnacomApplicationServerPortType> getPortListByOperatorName(String operatorName) throws InvalidOperatorException {

        ArrayList<AnacomApplicationServerPortType> z = new ArrayList<AnacomApplicationServerPortType>();
        for (String u : _webServicesLocator.getUrlListByOperatorName(operatorName)) {
            z.add(getPortByUrl(u));
        }
        if (z.equals(null))
            throw new InvalidOperatorException(operatorName, "WebServicesLocator: There is no operator with this name: " + operatorName);
        return z;
    }


    /**
     * Obter os portos do servidor apartir do prefixo do operador.
     * 
     * @param operatorName nome do operador
     * @return AnacomApplicationServerPortType Web Service List
     * @throws InvalidOperatorException Não existe operador com o nome
     */
    private ArrayList<AnacomApplicationServerPortType> getPortListByOperatorPrefix(String operatorPrefix) throws InvalidOperatorException {

        ArrayList<AnacomApplicationServerPortType> z = new ArrayList<AnacomApplicationServerPortType>();
        for (String u : _webServicesLocator.getUrlListByOperatorPrefix(operatorPrefix)) {
            z.add(getPortByUrl(u));
        }
        if (z.equals(null))
            throw new InvalidOperatorException(operatorPrefix, "Uddi: There is no operator with this prefix: " + operatorPrefix);
        return z;
    }


    /**
     * Obter os portos dos servidores apartir do número de telemovel.
     * 
     * @param operatorPrefix prefixo do operador
     * @return AnacomApplicationServerPortType Web Services List
     * @throws InvalidOperatorException não existe operador com este prefixo
     */
    private ArrayList<AnacomApplicationServerPortType> getPortListByOperatorPhoneNumber(String mobileNumber) throws InvalidOperatorException {
        return getPortListByOperatorPrefix(mobileNumber.substring(0, 2));
    }

    /**
     * Funcao que manda Exepcao se o numero de portas recebidas pelo WebServiceLocator for
     * menor que o numero de portas minimo
     * 
     * @param portSize numero de portas recebidas
     */
    private void checkNumReplicas(int portSize) {

        if (portSize == 0)
            throw new OperatorNotFoundException("Destination/Source Operator Does Not Exists!");


        // Numero de replicas
        if (portSize < NUMERO_REPLICAS - 1)
            throw new OperatorNotFoundException("The number of servers must be at least " + (NUMERO_REPLICAS - 1) + ", you have: " + portSize);
    }


}
