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
import pt.ist.anacom.shared.exceptions.NotEnoughBalanceException;
import pt.ist.anacom.shared.exceptions.OperatorAlreadyExistsException;
import pt.ist.anacom.shared.exceptions.OperatorException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;
import pt.ist.anacom.shared.stubs.Anacom;
import pt.ist.anacom.shared.stubs.AnacomApplicationServerPortType;
import pt.ist.anacom.shared.stubs.CommunicationDTOType;
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

public class ApplicationServerBridgeDistSoft
implements ApplicationServerBridge {


	/** WebServicesLocator - Resolver o nome ou prefixo do operador em url do servidor. */
	private WebServicesLocator _webServicesLocator = new WebServicesLocator();


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
		return port;
	}


	/**
	 * Obter o porto do servidor apartir do nome do operador.
	 * 
	 * @param operatorName nome do operador
	 * @return AnacomApplicationServerPortType Web Service
	 * @throws InvalidOperatorException Não existe operador com o nome
	 */
	private AnacomApplicationServerPortType getPortByOperatorName(String operatorName) throws InvalidOperatorException {

		ArrayList<String> u = _webServicesLocator.getUrlListByOperatorName(operatorName);

		if (u.size() == 0)
			throw new InvalidOperatorException(operatorName, "WebServicesLocator: There is no operator with this name: " + operatorName);

		if(u.size() > 1){
			throw new InvalidOperatorException(operatorName, "WebServicesLocator: There is more than one Replic of this Server !!!");
		}

		return getPortByUrl(u.get(0).toString());
	}
	
	

	/**
	 * Obter o porto do servidor apartir do prefixo do operador.
	 * 
	 * @param operatorPrefix prefixo do operador
	 * @return AnacomApplicationServerPortType Web Service
	 * @throws InvalidOperatorException não existe operador com este prefixo
	 */
	private AnacomApplicationServerPortType getPortByOperatorPrefix(String operatorPrefix) throws InvalidOperatorException {
		
		ArrayList<String> u = _webServicesLocator.getUrlListByOperatorPrefix(operatorPrefix);

		if (u.size() == 0)
			throw new InvalidOperatorException(operatorPrefix, "WebServicesLocator: There is no operator with this prefix: " + operatorPrefix);

		if(u.size() > 1){
			throw new InvalidOperatorException(operatorPrefix, "WebServicesLocator: There is more than one Replic of this Server !!!");
		}

		return getPortByUrl(u.get(0).toString());
	}

	/**
	 * Obter o porto do servidor apartir do número de telemovel.
	 * 
	 * @param operatorPrefix prefixo do operador
	 * @return AnacomApplicationServerPortType Web Service
	 * @throws InvalidOperatorException não existe operador com este prefixo
	 */
	private AnacomApplicationServerPortType getPortByOperatorPhoneNumber(String mobileNumber) throws InvalidOperatorException {
		return getPortByOperatorPrefix(mobileNumber.substring(0, 2));
	}


	/*
	 * Inicializa o Handler-chain do lado do cliente
	 * 
	 * Nota: Deve ser chamado antes de qq pedido ao servidor!!! (consoante se muda de
	 * port) ex:
	 * 
	 * (...) try {
	 * 
	 * //Handler definido programaticamente initHandlerClient(port);
	 * port.createCellPhone(msg); (...)
	 */
	public void initHandlerClient(AnacomApplicationServerPortType port) throws AnaComException {
		try {

			Binding binding = ((BindingProvider) port).getBinding();
			List<Handler> handlerList = binding.getHandlerChain();

			// Adiciona HandlerClient ao handler chain
			handlerList.add(new HandlerSD("CN=ApplicationServer,OU=Tagus"));
			binding.setHandlerChain(handlerList);
		} catch (Exception e) {
			System.out.println("initHandlerClient exception " + e.getMessage());
		}
	}







	// ---------------------------------------------------------------------------
	// ------------------Implementation of the Bridge Methods---------------------
	// ---------------------------------------------------------------------------


	@Override
	public void addBalance(PhoneNumValueDTO dto) throws InvalidBalanceException, InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException {
		final String number = dto.get_number();

		final AnacomApplicationServerPortType port = getPortByOperatorPhoneNumber(number);

		/* Criar a mensagem SOAP apartir do DTO */
		PhoneNumValueDTOType msg = new PhoneNumValueDTOType();
		msg.setBalance(dto.getBalance());
		msg.setNumber(number);

		try {
			initHandlerClient(port);
			port.addBalance(msg);
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
		} catch (WebServiceException e) {
			throw new OperatorNotFoundException(e.getMessage().toString());
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
	public void addCellPhone(PhoneDetailedDTO dto) throws CellPhoneAlreadyExistsException, InvalidOperatorException, InvalidNumberException {

		// obter o WSDL do operador
		final AnacomApplicationServerPortType port = getPortByOperatorName(dto.get_operator());

		/* Criar a mensagem SOAP apartir do DTO */
		PhoneDetailedDTOElem msg = new PhoneDetailedDTOElem();

		msg.setNumber(dto.get_number());
		msg.setOperator(dto.get_operator());
		msg.setType(dto.get_cellPhoneType().toString());
		msg.setBalance(dto.getBalance());
		try {
			initHandlerClient(port);
			port.createCellPhone(msg);

		} catch (pt.ist.anacom.shared.stubs.InvalidOperatorException_Exception e) {
			pt.ist.anacom.shared.stubs.InvalidOperatorException exc = e.getFaultInfo();
			throw new InvalidOperatorException(exc.getOperatorName(), exc.getError());

		} catch (pt.ist.anacom.shared.stubs.InvalidNumberException e) {
			pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
			throw new InvalidNumberException(exc.getError());

		} catch (pt.ist.anacom.shared.stubs.CellPhoneAlreadyExistsException e) {
			pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
			throw new CellPhoneAlreadyExistsException(exc.getError());

		} catch (WebServiceException e) {
			throw new OperatorNotFoundException(e.getMessage().toString());
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
	public void createOperator(OperatorDetailedDTO dto) throws OperatorAlreadyExistsException, OperatorException, InvalidOperatorPrefixException, AlreadyExistOperatorException {
		throw new RuntimeException("Nao e possivel criar operadores em servidores Remotos");
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
	public void removeCellPhone(PhoneNumberDTO dto) throws CellPhoneNotExistsException, InvalidNumberException, InvalidOperatorException, OperatorNotFoundException {
		String number = dto.get_number();

		AnacomApplicationServerPortType port = getPortByOperatorPhoneNumber(number);

		/* Criar a mensagem SOAP apartir do DTO */
		PhoneNumberDTOType msg = new PhoneNumberDTOType();
		msg.setNumber(number);

		try {
			initHandlerClient(port);
			port.removeCellPhone(msg);
		} catch (pt.ist.anacom.shared.stubs.InvalidNumberException e) {
			pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
			throw new InvalidNumberException(exc.getError());

		} catch (pt.ist.anacom.shared.stubs.CellPhoneNotExistsException e) {
			pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
			throw new CellPhoneNotExistsException(exc.getError());
		} catch (pt.ist.anacom.shared.stubs.OperatorNotFoundException e) {
			pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
			throw new OperatorNotFoundException(exc.getError());
		} catch (WebServiceException e) {
			throw new OperatorNotFoundException(e.getMessage().toString());
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
	 * @throws AnaComException the ana com exception
	 */
	@Override
	public PhoneNumValueDTO getBalance(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException {
		String number = dto.get_number();
		AnacomApplicationServerPortType port = getPortByOperatorPhoneNumber(number);

		/* Criar a mensagem SOAP apartir do DTO */
		PhoneNumberDTOType msg = new PhoneNumberDTOType();
		msg.setNumber(dto.get_number());

		try {


			PhoneNumValueDTOType response = new PhoneNumValueDTOType();
			initHandlerClient(port);
			response = port.getBalance(msg);
			return new PhoneNumValueDTO(response.getNumber(), response.getBalance(), 0);


		} catch (pt.ist.anacom.shared.stubs.CellPhoneNotExistsException e) {
			pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
			throw new CellPhoneNotExistsException(exc.getError());
		} catch (pt.ist.anacom.shared.stubs.OperatorNotFoundException e) {
			pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
			throw new OperatorNotFoundException(exc.getError());
		} catch (WebServiceException e) {
			throw new OperatorNotFoundException(e.getMessage().toString());
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
	public PhoneListDTO getCellPhoneList(OperatorSimpleDTO dto) throws InvalidOperatorException {
		String operatorName = dto.get_name();
		AnacomApplicationServerPortType port = getPortByOperatorName(operatorName);

		OperatorSimpleDTOType requestSoapMsg = new OperatorSimpleDTOType();
		requestSoapMsg.setName(operatorName);

		PhoneListDTOType responseSoapMsg = new PhoneListDTOType();
		try {
			initHandlerClient(port);
			responseSoapMsg = port.getCellPhoneList(requestSoapMsg);

			// cria lista de DTO's com resposta do servidor
			List<PhoneNumValueDTOType> cellPhoneList = responseSoapMsg.getCellPhoneList();
			List<PhoneNumValueDTO> phoneList = new ArrayList<PhoneNumValueDTO>();

			for (PhoneNumValueDTOType c : cellPhoneList) {
				PhoneNumValueDTO d = new PhoneNumValueDTO(c.getNumber(), c.getBalance(), 0);
				phoneList.add(d);
			}
			return new PhoneListDTO(phoneList);
		} catch (pt.ist.anacom.shared.stubs.InvalidOperatorException_Exception e) {
			pt.ist.anacom.shared.stubs.InvalidOperatorException exc = e.getFaultInfo();
			throw new InvalidOperatorException(exc.getOperatorName(), exc.getError());
		} catch (WebServiceException e) {
			throw new OperatorNotFoundException(e.getMessage().toString());
		}
	}


	/**
	 * processSMS
	 * 
	 * @param dto com informação para envio e recepção de uma dad mensagem De notar que é
	 *            aqui que se faz o agulhamento para os operadores do telemovel de destino
	 *            e de origem
	 */
	@Override
	public void processSMS(SMSDTO dto) throws CellPhoneNotExistsException,
	InvalidOperatorException,
	OperatorNotFoundException,
	NotEnoughBalanceException,
	IncompatibleModeException,
	CellPhoneNotAvailableException {
		String srcNumber = dto.getSrcNumber();
		String destNumber = dto.getDestNumber();
		String message = dto.getText();

		// sendPort: operador que irá ENVIAR mensagem
		AnacomApplicationServerPortType sendPort = getPortByOperatorPhoneNumber(srcNumber);
		// sendPort: operador que irá RECEBER mensagem
		AnacomApplicationServerPortType receivePort = getPortByOperatorPhoneNumber(destNumber);

		SmsDTOType receiveMsg = new SmsDTOType();
		SmsDTOType sendMsg = new SmsDTOType();

		receiveMsg.setDest(destNumber);
		receiveMsg.setSrc(srcNumber);
		receiveMsg.setMsg(message);

		sendMsg.setDest(destNumber);
		sendMsg.setSrc(srcNumber);
		sendMsg.setMsg(message);



		try {
			initHandlerClient(sendPort);
			sendPort.sendSMS(sendMsg);
			initHandlerClient(receivePort);
			receivePort.receiveSMS(receiveMsg);
		} catch (pt.ist.anacom.shared.stubs.CellPhoneNotExistsException e) {
			pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
			throw new CellPhoneNotExistsException(exc.getError());

		} catch (pt.ist.anacom.shared.stubs.OperatorNotFoundException e) {
			pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
			throw new OperatorNotFoundException(exc.getError());

		} catch (pt.ist.anacom.shared.stubs.NotEnoughBalanceException e) {
			pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
			throw new NotEnoughBalanceException(exc.getError());
		} catch (pt.ist.anacom.shared.stubs.IncompatibleModeException e) {
			pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
			throw new IncompatibleModeException(exc.getError());
		} catch (WebServiceException e) {
			throw new OperatorNotFoundException(e.getMessage().toString());
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
	public void changeCellPhoneMode(PhoneNumModeDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException, IncompatibleModeException {
		String cellPhoneModeString = null;
		String cellPhoneNumber = dto.get_number();
		CellPhoneMode cellPhoneMode = dto.getMode();

		AnacomApplicationServerPortType port = getPortByOperatorPhoneNumber(cellPhoneNumber);
		// Converte o tipo enumerado para String, para ser passado na SOAP msg (reverso
		// mode no cliente!)
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

		PhoneNumModeDTOType soapMsg = new PhoneNumModeDTOType();

		soapMsg.setNumber(cellPhoneNumber);
		soapMsg.setMode(cellPhoneModeString);

		try {
			initHandlerClient(port);
			port.changeCellPhoneMode(soapMsg);
		} catch (pt.ist.anacom.shared.stubs.CellPhoneNotExistsException e) {
			pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
			throw new CellPhoneNotExistsException(exc.getError());

		} catch (pt.ist.anacom.shared.stubs.OperatorNotFoundException e) {
			pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
			throw new OperatorNotFoundException(exc.getError());

		} catch (pt.ist.anacom.shared.stubs.IncompatibleModeException e) {
			pt.ist.anacom.shared.stubs.SimpleExceptionType exc = e.getFaultInfo();
			throw new IncompatibleModeException(exc.getError());

		} catch (WebServiceException e) {
			throw new OperatorNotFoundException(e.getMessage().toString());
		}
	}

	@Override
	public PhoneNumModeDTO getCellPhoneMode(PhoneNumberDTO _dto) throws CellPhoneNotExistsException, OperatorNotFoundException {
		final PhoneNumberDTOType requestMsg = new PhoneNumberDTOType();
		requestMsg.setNumber(_dto.get_number());
		final AnacomApplicationServerPortType port = getPortByOperatorPhoneNumber(_dto.get_number());

		PhoneNumModeDTOType resposta = new PhoneNumModeDTOType();
		try {
			initHandlerClient(port);
			resposta = port.getCellPhoneMode(requestMsg);

			CellPhoneMode mode = null;
			final String _responseMode = resposta.getMode();
			System.out.println("CONSEGUI");
			System.out.println(_responseMode);
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
		} catch (WebServiceException e) {
			throw new OperatorNotFoundException(e.getMessage().toString());
		}
	}

	@Override
	public SMSListDTO getReceivedSMS(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException {
		String cellPhoneNumber = dto.get_number();

		AnacomApplicationServerPortType port = getPortByOperatorPhoneNumber(cellPhoneNumber);
		PhoneNumberDTOType requestMsg = new PhoneNumberDTOType();
		requestMsg.setNumber(dto.get_number());

		SMSListDTOType responseMsg;
		try {
			initHandlerClient(port);
			responseMsg = port.getReceivedSMS(requestMsg);

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
		} catch (WebServiceException e) {
			throw new OperatorNotFoundException(e.getMessage().toString());
		}
	}

	@Override
	public CommunicationDTO getLastMadeCommunication(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException, InvalidCommunicationException {
		String number = dto.get_number();

		AnacomApplicationServerPortType port = getPortByOperatorPhoneNumber(number);

		PhoneNumberDTOType dtoMsg = new PhoneNumberDTOType();
		dtoMsg.setNumber(number);

		CommunicationDTOType respMsg;
		CommunicationDTO resp;

		try {
			initHandlerClient(port);
			respMsg = port.getLastMadeCommunication(dtoMsg);
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
		} catch (WebServiceException e) {
			throw new OperatorNotFoundException(e.getMessage().toString());
		}
		return resp;
	}

	@Override
	public void initiateVoiceCommunication(VoiceDTO dto) throws CellPhoneNotAvailableException,
	OperatorNotFoundException,
	CellPhoneNotExistsException,
	NotEnoughBalanceException,
	IncompatibleModeException {
		String srcNumber = dto.getSrcNumber();
		String destNumber = dto.getDestNumber();
		int duration = dto.getDuration();

		// sendPort: operador que irá ENVIAR
		AnacomApplicationServerPortType sendPort = getPortByOperatorPhoneNumber(srcNumber);
		// sendPort: operador que irá RECEBER
		AnacomApplicationServerPortType receivePort = getPortByOperatorPhoneNumber(destNumber);

		VoiceDTOType voiceDto = new VoiceDTOType();

		voiceDto.setSrc(srcNumber);
		voiceDto.setDest(destNumber);
		voiceDto.setDuration(duration);
		try {
			initHandlerClient(sendPort);
			sendPort.initCallerVoiceCommunication(voiceDto);
			initHandlerClient(receivePort);
			receivePort.initReceiverVoiceCommunication(voiceDto);

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

		} catch (WebServiceException e) {
			throw new OperatorNotFoundException(e.getMessage().toString());
		}
	}

	@Override
	public void endVoiceCommunication(VoiceDTO dto) throws CellPhoneNotAvailableException, OperatorNotFoundException, CellPhoneNotExistsException, NotEnoughBalanceException, IncompatibleModeException {
		String srcNumber = dto.getSrcNumber();
		String destNumber = dto.getDestNumber();
		int duration = dto.getDuration();

		// sendPort: operador que irá ENVIAR
		AnacomApplicationServerPortType sendPort = getPortByOperatorPhoneNumber(srcNumber);
		// sendPort: operador que irá RECEBER
		AnacomApplicationServerPortType receivePort = getPortByOperatorPhoneNumber(destNumber);

		VoiceDTOType voiceDto = new VoiceDTOType();

		voiceDto.setSrc(srcNumber);
		voiceDto.setDest(destNumber);
		voiceDto.setDuration(duration);

		try {
			initHandlerClient(sendPort);
			sendPort.terminationCallerVoice(voiceDto);
			initHandlerClient(receivePort);
			receivePort.terminationReceiverVoice(voiceDto);
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

		} catch (WebServiceException e) {
			throw new OperatorNotFoundException(e.getMessage().toString());
		}
	}

}
