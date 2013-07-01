package pt.ist.anacom.presentationserver.server;

import pt.ist.anacom.applicationserver.DatabaseBootstrap;
import pt.ist.anacom.presentationserver.client.AnacomService;
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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Servidor de apresentação. Receber pediddos dos clientes.
 */
public class AnacomServiceImpl extends
        RemoteServiceServlet
        implements AnacomService {

    private static final long serialVersionUID = 1L;

    private static ApplicationServerBridge bridge = null;
    private static boolean databasedInitialized = false;


    /**
     * Initializes the Bridge.
     * 
     * @param serverType tipo de execução: ES ou SD
     */
    public void initBridge(String serverType) {
        if (serverType.equals("ES+SD")) {
            bridge = new ApplicationServerBridgeDistSoft();
            System.out.println("---- Start bridge SD ----");
        } else if (serverType.equals("SD-RepliKate")) {
            bridge = new ApplicationServerBridgeDistReplicas();
            System.out.println("---- Start bridge SD Replicada ----");
        } else if (serverType.equals("ES-only")) {
            bridge = new ApplicationServerBridgeSoftEng();
            if (!databasedInitialized) {
                DatabaseBootstrap.init();
                DatabaseBootstrap.setup();
                databasedInitialized = true;
            }
        } else {
            throw new RuntimeException("Servlet parameter error: ES+SD nor ES-only");
        }
    }



    /**
     * Returns the bridge currently being used.
     * 
     * @return Devolve a bridge actual.
     */
    public static ApplicationServerBridge getBridge() {
        return bridge;
    }


    // ====================================================================================
    // Available API for the GWT Client
    // ====================================================================================
    /**
     * Adiciona saldo a um numero de telemovel em especifico.
     * 
     * @param dto number and balance to add (cents)
     * @throws OperatorNotFoundException
     * @throws CellPhoneNotExistsException
     * @throws InvalidOperatorException
     * @throws InvalidBalanceException
     * @throws AnaComException o valor nao pode ultrapassar os 100€ nesse envia uma
     *             excepção
     */
    @Override
    public void addBalance(PhoneNumValueDTO dto) throws InvalidBalanceException, InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException {
        getBridge().addBalance(dto);

    }

    /**
     * Cria um operador com o respectivo tarifario que inclui os preços de cada tipo de
     * chamada e ainda um valor extra a pagar se a chamada for para fora da operadora.
     * 
     * @param OperatorDetailedDTO
     * @throws AlreadyExistOperatorException
     * @throws InvalidOperatorPrefixException
     * @throws OperatorException
     * @throws OperatorAlreadyExistsException
     * @throws AnaComException the ana com exception
     */
    @Override
    public void createOperator(OperatorDetailedDTO dto) throws OperatorAlreadyExistsException, OperatorException, InvalidOperatorPrefixException, AlreadyExistOperatorException {
        getBridge().createOperator(dto);
    }

    /**
     * Executa o serviço de enviar um SMS de um telemovel para outro.
     * 
     * @param dto numero origem, numero destino, mensagem a transmitir
     * @throws CellPhoneNotAvailableException
     * @throws IncompatibleModeException
     * @throws NotEnoughBalanceException
     * @throws OperatorNotFoundException
     * @throws InvalidOperatorException
     * @throws CellPhoneNotExistsException
     * @throws AnaComException the ana com exception
     */
    @Override
    public void processSMS(SMSDTO dto) throws CellPhoneNotExistsException,
            InvalidOperatorException,
            OperatorNotFoundException,
            NotEnoughBalanceException,
            IncompatibleModeException,
            CellPhoneNotAvailableException {
        getBridge().processSMS(dto);
    }


    /**
     * Imprime no terminal a lista completa dos telemoveis e seus saldos de uma
     * determinada operadora.
     * 
     * @param OperatorSimpleDTO
     * @return the cell phone list command
     * @throws InvalidOperatorException
     * @throws AnaComException the ana com exception
     */
    @Override
    public PhoneListDTO getCellPhoneList(OperatorSimpleDTO dto) throws InvalidOperatorException {
        System.out.println("------- GET CELLPHONE LIST ----- ");
        return getBridge().getCellPhoneList(dto);
    }


    /**
     * Cria um dado telemovel num determinado operador.
     * 
     * @param PhoneDetailedDTO
     * @throws InvalidOperatorException
     * @throws InvalidNumberException
     * @throws CellPhoneAlreadyExistsException
     * @throws AnaComException the ana com exception
     */
    @Override
    public void addCellPhone(PhoneDetailedDTO dto) throws CellPhoneAlreadyExistsException, InvalidNumberException, InvalidOperatorException {
        getBridge().addCellPhone(dto);
    }

    /**
     * remover um telemovel do seu operador.
     * 
     * @param dto the number to remove
     * @throws OperatorNotFoundException
     * @throws InvalidOperatorException
     * @throws InvalidNumberException
     * @throws CellPhoneNotExistsException
     * @throws AnaComException the ana com exception
     * @date 16/Mar/2012
     */
    @Override
    public void removeCellPhone(PhoneNumberDTO dto) throws CellPhoneNotExistsException, InvalidNumberException, InvalidOperatorException, OperatorNotFoundException {
        getBridge().removeCellPhone(dto);
    }


    /**
     * getBalance - Obtenção do saldo de um dado telemóvel (remoto/distribuido).
     * 
     * @param PhoneNumberDTO
     * @throws AnaComException the ana com exception
     * @throws OperatorNotFoundException
     * @throws CellPhoneNotExistsException
     * @throws InvalidOperatorException
     * @return balance: saldo actual do telemóvel.
     */
    @Override
    public PhoneNumValueDTO getBalance(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException {
        System.out.println("Get balance: " + dto.get_number());
        return getBridge().getBalance(dto);
        // CellPhonePresenter.showPhoneNumberBalance(result);
    }

    /**
     * Changes the CellPhone Mode to a new mode.
     * 
     * @param cellPhoneNumber the cell phone number
     * @param newCellPhoneMode the new cell phone mode
     * @throws AnaComException the ana com exception
     * @author David Dias
     * @throws OperatorNotFoundException
     * @throws CellPhoneNotExistsException
     * @throws InvalidOperatorException
     */
    @Override
    public void changeCellPhoneMode(PhoneNumModeDTO dto) throws CellPhoneNotExistsException, OperatorNotFoundException, IncompatibleModeException {
        System.out.println("------- change CELLPHONE Mode ----- ");
        getBridge().changeCellPhoneMode(dto);
    }


    /**
     * getReceivedSMS - get the sms list off a given cellphone.
     * 
     * @param dto
     * @throws AnaComException the ana com exception
     * @throws OperatorNotFoundException
     * @throws CellPhoneNotExistsException
     * @throws InvalidOperatorException
     */
    @Override
    public SMSListDTO getReceivedSMS(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException {
        return getBridge().getReceivedSMS(dto);
    }


    /**
     * getCellPhoneMode - Obtenção do Modo de um dado telemóvel.
     * 
     * @param dto
     * @throws AnaComException the ana com exception
     * @throws OperatorNotFoundException
     * @throws CellPhoneNotExistsException
     * @throws InvalidOperatorException
     */
    @Override
    public PhoneNumModeDTO getMode(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException {
        System.out.println("------- get CELLPHONE Mode ----- ");
        System.out.println(dto.get_number());
        return getBridge().getCellPhoneMode(dto);
    }



    @Override
    public CommunicationDTO getLastMadeCall(PhoneNumberDTO dto) throws InvalidOperatorException, CellPhoneNotExistsException, OperatorNotFoundException, InvalidCommunicationException {
        return getBridge().getLastMadeCommunication(dto);
    }



    /** Método para inicializar chamadas de voz */
    @Override
    public void startVoice(VoiceDTO dto) throws CellPhoneNotExistsException,
            InvalidOperatorException,
            OperatorNotFoundException,
            NotEnoughBalanceException,
            IncompatibleModeException,
            CellPhoneNotAvailableException {
        getBridge().initiateVoiceCommunication(dto);

    }



    @Override
    public void endVoice(VoiceDTO dto) throws CellPhoneNotExistsException,
            InvalidOperatorException,
            OperatorNotFoundException,
            NotEnoughBalanceException,
            IncompatibleModeException,
            CellPhoneNotAvailableException {
        getBridge().endVoiceCommunication(dto);

    }


}
