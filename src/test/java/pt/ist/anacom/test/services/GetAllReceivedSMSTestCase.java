package pt.ist.anacom.test.services;

import pt.ist.anacom.service.GetCellPhoneSMSReceiveListService;
import pt.ist.anacom.service.ReceiveSMSService;
import pt.ist.anacom.service.SendSMSService;
import pt.ist.anacom.shared.dto.SMSDTO;
import pt.ist.anacom.shared.dto.SMSListDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumberDTO;
import pt.ist.anacom.shared.enumerados.CellPhoneMode;
import pt.ist.anacom.shared.enumerados.CellPhoneType;
import pt.ist.anacom.shared.exceptions.AnaComException;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;
import pt.ist.anacom.shared.exceptions.NotEnoughBalanceException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;

public class GetAllReceivedSMSTestCase extends
        FFServiceTestCase {



    /**
     * Bateria de testes à lista de Mensagens SMS Recebidas para um dado Telemóvel Testa
     * os seguintes casos: Stage1: - Operadora não existe; - Telemóvel não existe; Stage2:
     * - Verifica contador de mensagens recebidas; - Verifica conteudo das mensagens da
     * lista; - Verifica srcNumbers e dstNumbers das mensagens da lista - Enviar mensagens
     * com saldo insuficiente e verificar se vai para a lista - Enviar/Receber mensagens
     * com telemóveis desligados
     */

    /**
     * Variáveis globais
     */

    // Operadora
    private static String OPERATOR_1_NAME = "TagusPark";
    private static String OPERATOR_1_PREFIX = "99";

    private static float EXTRACHARGETARIFF = 0.2f;;
    private static int SMSTARIFF = 15;
    private static int VOICETARIFF = 0;
    private static int VIDEOTARIFF = 0;
    private static int BONUSTARIFF = 0;

    // Operadora que não existe

    // Telemóveis
    private static String CELLPHONE1 = "992200011";
    private static String CELLPHONE2 = "992200012";
    private static String CELLPHONE3 = "992200013";
    private static final String CELLPHONE_NOT_EXISTS = "992200014";
    private static final String CELLPHONE_BROKE_BALANCE = "992200015";
    private static final String CELLPHONE_OP_NOT_EXISTS = "982200014";
    private static final int INITIAL_BALANCE = 10000;

    // Mensagens
    private static final String MSG1 = "Mensagem1"; // 9chars
    private static final String MSG2 = "Mensagem2 Mensagem2"; // 19chars
    private static final String MSG3 = "Mensagem3 Mensagem3 Mensagem3"; // 29chars



    // Constructores
    public GetAllReceivedSMSTestCase() {
        super();
    }

    public GetAllReceivedSMSTestCase(String msg) {
        super(msg);
    }

    /**
     * Método de setup - cria objectos a serem testados
     */
    public void setUp() {
        super.setUp();

        addOperator(OPERATOR_1_NAME, OPERATOR_1_PREFIX, SMSTARIFF, VOICETARIFF, VIDEOTARIFF, EXTRACHARGETARIFF, BONUSTARIFF);

        addCellPhone(OPERATOR_1_NAME, CELLPHONE1, CellPhoneType.PHONE2G, INITIAL_BALANCE);
        changeCellPhoneMode(OPERATOR_1_NAME, CELLPHONE1, CellPhoneMode.ON);

        addCellPhone(OPERATOR_1_NAME, CELLPHONE2, CellPhoneType.PHONE2G, INITIAL_BALANCE);
        changeCellPhoneMode(OPERATOR_1_NAME, CELLPHONE2, CellPhoneMode.ON);

        addCellPhone(OPERATOR_1_NAME, CELLPHONE3, CellPhoneType.PHONE2G, INITIAL_BALANCE);
        changeCellPhoneMode(OPERATOR_1_NAME, CELLPHONE3, CellPhoneMode.ON);

        addCellPhone(OPERATOR_1_NAME, CELLPHONE_BROKE_BALANCE, CellPhoneType.PHONE2G, 0);
        changeCellPhoneMode(OPERATOR_1_NAME, CELLPHONE_BROKE_BALANCE, CellPhoneMode.ON);
    }


    /**
     * Bateria de testes
     */

    /**
     * Testa o que acontece quando se tenta aceder a uma lista de SMS's recebidos de uma
     * operadora que não existe
     */
    public void testOperatorNotExists() {

        PhoneNumberDTO dto = new PhoneNumberDTO(CELLPHONE_OP_NOT_EXISTS);
        GetCellPhoneSMSReceiveListService smsReceivedService = new GetCellPhoneSMSReceiveListService(dto);

        try {
            smsReceivedService.execute();

        } catch (OperatorNotFoundException e) {
        } catch (AnaComException e) {
            fail("Excepção enviada no teste caso Operadora não existe não é a esperada " + e);
        }
    }

    /**
     * Testa o que acontece quando se tenta aceder a uma SMS's recebidos de um telemovel
     * que não existe numa dada operadora
     */
    public void testCellPhoneNotExists() {

        PhoneNumberDTO dto = new PhoneNumberDTO(CELLPHONE_NOT_EXISTS);
        GetCellPhoneSMSReceiveListService smsReceivedService = new GetCellPhoneSMSReceiveListService(dto);

        try {
            smsReceivedService.execute();

        } catch (CellPhoneNotExistsException e) {
        } catch (AnaComException e) {
            fail("Excepção enviada no teste caso Telemovel não existir não é a esperada " + e);
        }
    }


    /**
     * Verifica se envio de mensagem de um Telemóvel sme saldo vai para a lista
     */
    public void testNotEnoughBalanceListSMS() {

        SMSDTO dto1 = new SMSDTO(CELLPHONE_BROKE_BALANCE, CELLPHONE1, MSG1);
        SendSMSService sendService = new SendSMSService(dto1);
        ReceiveSMSService receiveService = new ReceiveSMSService(dto1);

        PhoneNumberDTO dto4 = new PhoneNumberDTO(CELLPHONE1);
        GetCellPhoneSMSReceiveListService smsReceivedService = new GetCellPhoneSMSReceiveListService(dto4);

        SMSListDTO listSMS = null;

        try {
            // Envia SMS's
            sendService.execute();
            receiveService.execute();

            // Recebe Lista de sms's recebidos de CELLPHONE1
            smsReceivedService.execute();
            listSMS = smsReceivedService.getList();

        } catch (NotEnoughBalanceException e) {
        } catch (AnaComException e) {
            fail("Não é suposto enviar Excepção em testNumberReceivedSMS" + e);
        }

        assertEquals("Lista de SMS's recebidos não deveria de existir", null, listSMS);

    }

    /**
     * Verifica se envio de mensagem de/apartir um Telemóvel desligado vai para a lista
     */
    public void testCellPhoneOFFReceivedSMSList() {

        // mensagem apartir de telemovel desligado
        SMSDTO dto1 = new SMSDTO(CELLPHONE2, CELLPHONE1, MSG1);
        SendSMSService sendService = new SendSMSService(dto1);
        ReceiveSMSService receiveService = new ReceiveSMSService(dto1);

        // mensagem para de telemovel desligado
        SMSDTO dto2 = new SMSDTO(CELLPHONE1, CELLPHONE2, MSG1);
        SendSMSService sendService2 = new SendSMSService(dto2);
        ReceiveSMSService receiveService2 = new ReceiveSMSService(dto2);

        changeCellPhoneMode(OPERATOR_1_NAME, CELLPHONE2, CellPhoneMode.OFF);

        PhoneNumberDTO dto3 = new PhoneNumberDTO(CELLPHONE1);
        GetCellPhoneSMSReceiveListService smsReceivedService1 = new GetCellPhoneSMSReceiveListService(dto3);

        PhoneNumberDTO dto4 = new PhoneNumberDTO(CELLPHONE2);
        GetCellPhoneSMSReceiveListService smsReceivedService2 = new GetCellPhoneSMSReceiveListService(dto4);

        SMSListDTO listSMS1 = null;
        SMSListDTO listSMS2 = null;

        try {
            // Envia SMS's
            sendService.execute();
            receiveService.execute();

            sendService2.execute();
            receiveService2.execute();

            // Recebe Lista de sms's recebidos de CELLPHONE1
            smsReceivedService1.execute();
            smsReceivedService2.execute();

            listSMS1 = smsReceivedService1.getList();
            listSMS2 = smsReceivedService2.getList();

        } catch (IncompatibleModeException e) {
        } catch (AnaComException e) {
            fail("Não é suposto enviar Excepção em testNumberReceivedSMS" + e);
        }

        assertEquals("Lista de SMS's recebidos não deveria de existir", null, listSMS1);

        assertEquals("Lista de SMS's recebidos não deveria de existir", null, listSMS2);

    }



    /**
     * Verifica se numero de mensagens recebidas da lista é o suposto
     */
    public void testNumberReceivedSMS() {

        SMSDTO dto1 = new SMSDTO(CELLPHONE2, CELLPHONE1, MSG1);
        SendSMSService sendService = new SendSMSService(dto1);
        ReceiveSMSService receiveService = new ReceiveSMSService(dto1);

        SMSDTO dto2 = new SMSDTO(CELLPHONE3, CELLPHONE1, MSG2);
        SendSMSService sendService2 = new SendSMSService(dto2);
        ReceiveSMSService receiveService2 = new ReceiveSMSService(dto2);

        SMSDTO dto3 = new SMSDTO(CELLPHONE3, CELLPHONE1, MSG3);
        SendSMSService sendService3 = new SendSMSService(dto3);
        ReceiveSMSService receiveService3 = new ReceiveSMSService(dto3);

        PhoneNumberDTO dto4 = new PhoneNumberDTO(CELLPHONE1);
        GetCellPhoneSMSReceiveListService smsReceivedService = new GetCellPhoneSMSReceiveListService(dto4);

        SMSListDTO listSMS = null;

        try {
            // Envia SMS's
            sendService.execute();
            receiveService.execute();
            sendService2.execute();
            receiveService2.execute();
            sendService3.execute();
            receiveService3.execute();

            // Recebe Lista de sms's recebidos de CELLPHONE1
            smsReceivedService.execute();
            listSMS = smsReceivedService.getList();

            // Testar envio de exepção outOfBounds (para ter a certeza que não há mais que
            // 3 mensagens na lista)
            listSMS.getSMSList().get(3);

        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        } catch (AnaComException e) {
            fail("Não é suposto enviar Excepção em testNumberReceivedSMS" + e);
        }

        assertEquals("Número de mensagens na lista não é a correcta", 3, listSMS.getSMSList().size());

    }

    /**
     * Verifica se mensagens guardadas na lista são as mesmas que as enviadas.
     */
    public void testReceivedTextSMS() {

        SMSDTO dto1 = new SMSDTO(CELLPHONE2, CELLPHONE1, MSG1);
        SendSMSService sendService = new SendSMSService(dto1);
        ReceiveSMSService receiveService = new ReceiveSMSService(dto1);

        SMSDTO dto2 = new SMSDTO(CELLPHONE3, CELLPHONE1, MSG2);
        SendSMSService sendService2 = new SendSMSService(dto2);
        ReceiveSMSService receiveService2 = new ReceiveSMSService(dto2);

        SMSDTO dto3 = new SMSDTO(CELLPHONE3, CELLPHONE1, MSG3);
        SendSMSService sendService3 = new SendSMSService(dto3);
        ReceiveSMSService receiveService3 = new ReceiveSMSService(dto3);

        PhoneNumberDTO dto4 = new PhoneNumberDTO(CELLPHONE1);
        GetCellPhoneSMSReceiveListService smsReceivedService = new GetCellPhoneSMSReceiveListService(dto4);

        SMSListDTO listSMS = null;

        try {
            // Envia SMS's
            sendService.execute();
            receiveService.execute();
            sendService2.execute();
            receiveService2.execute();
            sendService3.execute();
            receiveService3.execute();

            // Recebe Lista de sms's recebidos de CELLPHONE1
            smsReceivedService.execute();
            listSMS = smsReceivedService.getList();


        } catch (AnaComException e) {
            fail("Não é suposto enviar Excepção em testReceivedTextSMS" + e);
        }

        assertEquals("Mensagem1 não é a correcta", MSG1, listSMS.getSMSList().get(0).getText());

        assertEquals("Mensagem2 não é a correcta", MSG2, listSMS.getSMSList().get(1).getText());

        assertEquals("Mensagem3 não é a correcta", MSG3, listSMS.getSMSList().get(2).getText());

    }

    /**
     * Verifica se mensagens guardadas na lista são as mesmas que as enviadas.
     */
    public void testIDsReceivedSMS() {

        SMSDTO dto1 = new SMSDTO(CELLPHONE2, CELLPHONE1, MSG1);
        SendSMSService sendService = new SendSMSService(dto1);
        ReceiveSMSService receiveService = new ReceiveSMSService(dto1);

        SMSDTO dto2 = new SMSDTO(CELLPHONE3, CELLPHONE1, MSG2);
        SendSMSService sendService2 = new SendSMSService(dto2);
        ReceiveSMSService receiveService2 = new ReceiveSMSService(dto2);

        SMSDTO dto3 = new SMSDTO(CELLPHONE3, CELLPHONE1, MSG3);
        SendSMSService sendService3 = new SendSMSService(dto3);
        ReceiveSMSService receiveService3 = new ReceiveSMSService(dto3);

        PhoneNumberDTO dto4 = new PhoneNumberDTO(CELLPHONE1);
        GetCellPhoneSMSReceiveListService smsReceivedService = new GetCellPhoneSMSReceiveListService(dto4);

        SMSListDTO listSMS = null;

        try {
            // Envia SMS's
            sendService.execute();
            receiveService.execute();
            sendService2.execute();
            receiveService2.execute();
            sendService3.execute();
            receiveService3.execute();

            // Recebe Lista de sms's recebidos de CELLPHONE1
            smsReceivedService.execute();
            listSMS = smsReceivedService.getList();


        } catch (AnaComException e) {
            fail("Não é suposto enviar Excepção em testReceivedTextSMS" + e);
        }

        assertEquals("DestNumber da Mensagem1 não é correcto", CELLPHONE1, listSMS.getSMSList().get(0).getDestNumber());

        assertEquals("SrcNumber da Mensagem1 não é correcto", CELLPHONE2, listSMS.getSMSList().get(0).getSrcNumber());

        assertEquals("DestNumber da Mensagem2 não é correcto", CELLPHONE1, listSMS.getSMSList().get(1).getDestNumber());

        assertEquals("SrcNumber da Mensagem2 não é correcto", CELLPHONE3, listSMS.getSMSList().get(1).getSrcNumber());

        assertEquals("DestNumber da Mensagem3 não é correcto", CELLPHONE1, listSMS.getSMSList().get(2).getDestNumber());

        assertEquals("SrcNumber da Mensagem1 não é correcto", CELLPHONE3, listSMS.getSMSList().get(2).getSrcNumber());

    }



}
