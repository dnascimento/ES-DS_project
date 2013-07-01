package pt.ist.anacom.test.services;

import pt.ist.anacom.service.ReceiveSMSService;
import pt.ist.anacom.service.SendSMSService;
import pt.ist.anacom.shared.dto.SMSDTO;
import pt.ist.anacom.shared.enumerados.CellPhoneMode;
import pt.ist.anacom.shared.enumerados.CellPhoneType;
import pt.ist.anacom.shared.exceptions.AnaComException;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;
import pt.ist.anacom.shared.exceptions.NotEnoughBalanceException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;
import pt.ist.fenixframework.pstm.Transaction;

/**
 * Testes ao Envio de SMS. 1) Testes ao cálculo de envio do SMS; 2) Testes aos registos
 * dos SMS's nos telemóveis (enviados e recebidos); 3) Teste ao envio de SMS's sem saldo
 * suficiente; 4) Testes ao envio de SMS's com modos inválidos (Busy e OFF).
 */

public class SendSMSTestCase extends
        FFServiceTestCase {

    // Operators ID's
    /** The PREVIEW: OPERATOR 1 NAME. */
    private static String OPERATOR_1_NAME = "TagusPark";

    /** The PREVIEW: OPERATOR 1 PREFIX. */
    private static String OPERATOR_1_PREFIX = "99";

    /** The PREVIEW: OPERATOR 2 NAME. */
    private static String OPERATOR_2_NAME = "Alameda";

    /** The PREVIEW: OPERATOR 2 PREFIX. */
    private static String OPERATOR_2_PREFIX = "88";

    // Operator Tariffs
    /** The EXTRACHARGETARIFF. */
    private static double EXTRACHARGETARIFF = 1.2;

    /** The SMSTARIF f1. */
    private static int SMSTARIFF1 = 10;

    /** The SMSTARIF f2. */
    private static int SMSTARIFF2 = 15;

    /** The VOICETARIFF. */
    private static int VOICETARIFF = 0;

    /** The VIDEOTARIFF. */
    private static int VIDEOTARIFF = 0;

    /** The BONUSTARIFF. */
    private static int BONUSTARIFF = 0;

    // CellPhone Constants
    /** The PREVIEW: CELLPHONE BALANCE BROKE ON 2G. */
    private static String CELLPHONE_BALANCE_BROKE_ON_2G = "992200051";

    /** The PREVIEW: CELLPHONE BALANCE BROKE. */
    private static String CELLPHONE_BALANCE_BROKE = "992200051";

    /** The CELLPHON e1. */
    private static String CELLPHONE1 = "992200011";

    /** The CELLPHON e2. */
    private static String CELLPHONE2 = "882200011";

    /** The CELLPHON e3. */
    private static String CELLPHONE3 = "992200012";

    /** The CELLPHON e4. */
    private static String CELLPHONEWITHOUTOPERATOR = "922200012";

    /** The CELLPHON e5. */
    private static String CELLPHONENOTEXISTS = "992201212";

    // Constantes varias
    // Nota: Não mudar a frase se não estraga os testes!!
    /** The MSG. */
    private static final String MSG = "Foi na loja do mestre André que eu comprei um computadorzito tirolilolilo,  um computadorzito... ";

    /** The BROKE BALANCE. */
    private static final int BROKE_BALANCE = 0;

    /** The EXPECTED BALANCE. */
    private static final int EXPECTED_BALANCE = 140;

    /** The EXPECTED BALANCE2. */
    private static final int EXPECTED_BALANCE2 = 132;

    /** The INITIAL BALANCE. */
    private static final int INITIAL_BALANCE = 150;


    /**
     * Instantiates a new send sms test.
     */
    public SendSMSTestCase() {
        super();
    }

    /**
     * Instantiates a new send sms test.
     * 
     * @param msg the msg
     */
    public SendSMSTestCase(String msg) {
        super(msg);
    }

    /**
     * 
     */
    @Override
    public void setUp() {
        super.setUp();

        addOperator(OPERATOR_1_NAME, OPERATOR_1_PREFIX, SMSTARIFF1, VOICETARIFF, VIDEOTARIFF, EXTRACHARGETARIFF, BONUSTARIFF);
        addOperator(OPERATOR_2_NAME, OPERATOR_2_PREFIX, SMSTARIFF2, VOICETARIFF, VIDEOTARIFF, EXTRACHARGETARIFF, BONUSTARIFF);

        addCellPhone(OPERATOR_1_NAME, CELLPHONE_BALANCE_BROKE, CellPhoneType.PHONE2G, 0);
        changeCellPhoneMode(OPERATOR_1_NAME, CELLPHONE_BALANCE_BROKE, CellPhoneMode.ON);

        addCellPhone(OPERATOR_1_NAME, CELLPHONE1, CellPhoneType.PHONE2G, INITIAL_BALANCE);
        changeCellPhoneMode(OPERATOR_1_NAME, CELLPHONE1, CellPhoneMode.ON);


        addCellPhone(OPERATOR_2_NAME, CELLPHONE2, CellPhoneType.PHONE2G, INITIAL_BALANCE);
        changeCellPhoneMode(OPERATOR_2_NAME, CELLPHONE2, CellPhoneMode.ON);

        addCellPhone(OPERATOR_1_NAME, CELLPHONE3, CellPhoneType.PHONE2G, INITIAL_BALANCE);
        changeCellPhoneMode(OPERATOR_1_NAME, CELLPHONE3, CellPhoneMode.ON);


    }

    /*
     * testSMSCost - Testa se o calculo do envio de SMS é bem feito
     */
    public void testSMSCost() {
        // Arrange
        SMSDTO dto = new SMSDTO(CELLPHONE2, CELLPHONE1, MSG);
        SendSMSService sendService = new SendSMSService(dto);
        ReceiveSMSService receiveService = new ReceiveSMSService(dto);

        boolean committed = false;

        SMSDTO dto2 = new SMSDTO(CELLPHONE3, CELLPHONE1, MSG);
        SendSMSService sendService2 = new SendSMSService(dto2);
        ReceiveSMSService receiveService2 = new ReceiveSMSService(dto2);

        // Act
        try {
            Transaction.begin();

            // envio de SMS inter-operadoras
            sendService.dispatch();
            receiveService.dispatch();
            // envio de SMS intra-operadoras
            sendService2.dispatch();
            receiveService2.dispatch();

            Transaction.commit();
            committed = true;
        } catch (AnaComException e) {
            fail("Saldos devem ser calculados sem lançar excepção");
        }
        if (!committed) {
            Transaction.abort();
        }

        // Assert
        assertEquals("Custo de envio de SMS entre duas operadoras diferentes mal calculado", EXPECTED_BALANCE2, getPhoneBalance(CELLPHONE2));

        assertEquals("Custo de envio de SMS entre a mesma operadora mal calculado", EXPECTED_BALANCE, getPhoneBalance(CELLPHONE3));
    }

    /*
     * testeSMSLogs - Testa se os registos do telemóveis(emissor e receptor) são bem
     * actualizados => Envia 3 SMS's e verifica os registos 2 SMS enviados por CELLPHONE1
     * 1 SMS enviados por CELLPHONE2 0 SMS enviados por CELLPHONE3
     * 
     * 0 SMS recebidos por CELLPHONE1 1 SMS recebidos por CELLPHONE2 2 SMS recebidos por
     * CELLPHONE3
     */
    /**
     * Teste sms logs.
     */
    public void testSMSLogs() {
        // Arrange
        SMSDTO dto1 = new SMSDTO(CELLPHONE1, CELLPHONE2, MSG);
        SendSMSService sendService1 = new SendSMSService(dto1);
        ReceiveSMSService receiveService1 = new ReceiveSMSService(dto1);

        SMSDTO dto2 = new SMSDTO(CELLPHONE1, CELLPHONE3, MSG);
        SendSMSService sendService2 = new SendSMSService(dto2);
        ReceiveSMSService receiveService2 = new ReceiveSMSService(dto2);

        SMSDTO dto3 = new SMSDTO(CELLPHONE2, CELLPHONE3, MSG);
        SendSMSService sendService3 = new SendSMSService(dto3);
        ReceiveSMSService receiveService3 = new ReceiveSMSService(dto3);

        boolean committed = false;

        // Act
        try {
            Transaction.begin();

            sendService1.dispatch();
            receiveService1.dispatch();

            sendService2.dispatch();
            receiveService2.dispatch();

            sendService3.dispatch();
            receiveService3.dispatch();

            Transaction.commit();
            committed = true;
        } catch (AnaComException e) {
            fail("SMS's devem ser enviadas sem lançar excepção");
        }
        if (!committed) {
            Transaction.abort();
        }
        // Assert
        assertEquals("Registo de SMS enviados não está a funcionar bem", 2, checkMadeCommunication(OPERATOR_1_NAME, CELLPHONE1));

        assertEquals("Registo de SMS enviados não está a funcionar bem", 1, checkMadeCommunication(OPERATOR_2_NAME, CELLPHONE2));

        assertEquals("Registo de SMS enviados não está a funcionar bem", 0, checkMadeCommunication(OPERATOR_1_NAME, CELLPHONE3));

        assertEquals("Registo de SMS recebidos não está a funcionar bem", 0, checkReceivedCommunication(OPERATOR_1_NAME, CELLPHONE1));

        assertEquals("Registo de SMS recebidos não está a funcionar bem", 1, checkReceivedCommunication(OPERATOR_2_NAME, CELLPHONE2));

        assertEquals("Registo de SMS recebidos não está a funcionar bem", 2, checkReceivedCommunication(OPERATOR_1_NAME, CELLPHONE3));

    }


    /*
     * testWithoutBalance - Testa se telemovel sem saldo suficiente consegue enviar SMS
     */
    /**
     * Test without balance.
     */
    public void testWithoutBalance() {

        // Arrange
        SMSDTO dto = new SMSDTO(CELLPHONE_BALANCE_BROKE_ON_2G, CELLPHONE1, MSG);
        SendSMSService sendService = new SendSMSService(dto);
        ReceiveSMSService receiveService = new ReceiveSMSService(dto);

        boolean committed = false;

        // Act
        try {
            Transaction.begin();

            sendService.dispatch();
            receiveService.dispatch();

            Transaction.commit();
            committed = true;
        } catch (NotEnoughBalanceException e) {

        } catch (AnaComException e) {
            fail("A Excepção " + e.getMessage() + " não deveria estar a ser mandada");
        }

        if (!committed) {
            Transaction.abort();
        }

        // Assert
        assertEquals("Comunicação fica registada no Telefone de origem", 0, checkMadeCommunication(OPERATOR_1_NAME, CELLPHONE_BALANCE_BROKE_ON_2G));


        assertEquals("Comunicação fica registada no Telefone de destino", 0, checkReceivedCommunication(OPERATOR_1_NAME, CELLPHONE1));

        assertEquals("Saldo do telemóvel de origem foi modificado", BROKE_BALANCE, getPhoneBalance(CELLPHONE_BALANCE_BROKE_ON_2G));

        // assertEquals("Excepção não está a ser bem lançada",
        // new NotEnoughBalanceException("Saldo Insuficiente"), ex);

    }


    /*
     * testModes - Testa se verificações "able to make communication" estão a funcionar
     * 
     * Condições de teste: O telemóvel que envia mensagem está: 1) Ligado; 2) Não está
     * ocupado; 3) Tem saldo suficiente (já verificado)
     */

    /**
     * Test mode busy.
     */
    public void testModeBUSY() {
        SMSDTO dto = new SMSDTO(CELLPHONE1, CELLPHONE2, MSG);
        SendSMSService sendService = new SendSMSService(dto);
        ReceiveSMSService receiveService = new ReceiveSMSService(dto);

        boolean committed = false;

        // Act
        try {
            changeCellPhoneMode(OPERATOR_1_NAME, CELLPHONE1, CellPhoneMode.BUSY);

            Transaction.begin();

            sendService.dispatch();
            receiveService.dispatch();

            Transaction.commit();
            committed = true;
        } catch (IncompatibleModeException e) {

        } catch (Exception e) {
            fail("A Excepção " + e.getMessage() + " não deveria estar a ser mandada");
        }

        if (!committed) {
            Transaction.abort();
        }
        // Assert

        assertEquals("Comunicação fica registada no Telefone de origem", 0, checkMadeCommunication(OPERATOR_1_NAME, CELLPHONE1));

        assertEquals("Comunicação fica registada no Telefone de destino", 0, checkReceivedCommunication(OPERATOR_2_NAME, CELLPHONE2));

        assertEquals("Saldo do telemóvel de origem foi modificado", INITIAL_BALANCE, getPhoneBalance(CELLPHONE1));

        // assertEquals("Excepção não está a ser bem lançada",
        // new IncompatibleModeForCommunicationException(), ex);
    }


    /**
     * Test source mobile with mode off.
     */
    public void testModeOFFSource() {
        // Arrange
        SMSDTO dto = new SMSDTO(CELLPHONE1, CELLPHONE2, MSG);
        SendSMSService sendService = new SendSMSService(dto);
        ReceiveSMSService receiveService = new ReceiveSMSService(dto);

        boolean committed = false;

        // Act
        try {
            changeCellPhoneMode(OPERATOR_1_NAME, CELLPHONE1, CellPhoneMode.OFF);

            Transaction.begin();

            sendService.dispatch();
            receiveService.dispatch();

            Transaction.commit();
            committed = true;
        } catch (IncompatibleModeException e) {
            // Lançou a excepção correcta
        } catch (Exception e) {
            fail("A Excepção " + e.getMessage() + " não deveria estar a ser mandada");
        }
        if (!committed) {
            Transaction.abort();
        }

        // Assert
        assertEquals("Comunicação fica registada no Telefone de origem", 0, checkMadeCommunication(OPERATOR_1_NAME, CELLPHONE1));
        assertEquals("Comunicação fica registada no Telefone de destino", 0, checkReceivedCommunication(OPERATOR_2_NAME, CELLPHONE2));
        assertEquals("Saldo do telemóvel de origem foi modificado", INITIAL_BALANCE, getPhoneBalance(CELLPHONE1));
    }

    /**
     * Test source mobile with mode off.
     */
    public void testModeOFFDestination() {
        // Arrange
        SMSDTO dto = new SMSDTO(CELLPHONE2, CELLPHONE1, MSG);
        SendSMSService sendService = new SendSMSService(dto);
        ReceiveSMSService receiveService = new ReceiveSMSService(dto);

        boolean committed = false;

        // Act
        try {
            Transaction.begin();

            sendService.dispatch();
            receiveService.dispatch();

            Transaction.commit();
            committed = true;
        } catch (Exception e) {
            fail("A Excepção " + e.getMessage() + " não deveria estar a ser mandada");
        }
        if (!committed) {
            Transaction.abort();
        }

        // Assert
        assertEquals("Comunicação nãofica registada no Telefone de destino", 1, checkMadeCommunication(OPERATOR_2_NAME, CELLPHONE2));
        assertEquals("Comunicação não fica registada no Telefone de origem", 1, checkReceivedCommunication(OPERATOR_1_NAME, CELLPHONE1));

        assertEquals("Saldo do telemóvel de origem não foi modificado", EXPECTED_BALANCE2, getPhoneBalance(CELLPHONE2));
    }

    /** Testar se o operador de destino não existe */
    public void testDestinationOperatorNotExists() {
        // Arrange
        // Destino é um número em operador não existente
        final SMSDTO dto = new SMSDTO(CELLPHONE1, CELLPHONEWITHOUTOPERATOR, MSG);
        SendSMSService sendService = new SendSMSService(dto);
        ReceiveSMSService receiveService = new ReceiveSMSService(dto);

        boolean committed = false;

        // Act
        try {
            Transaction.begin();

            sendService.dispatch();
            receiveService.dispatch();

            Transaction.commit();
            committed = true;
        } catch (OperatorNotFoundException e) {
            // excepção enviada correctamente
        } catch (Exception e) {
            fail("A Excepção " + e.getMessage() + " não deveria estar a ser mandada");
        }
        if (!committed) {
            Transaction.abort();
        }
        // Assert
        assertEquals("Comunicação fica registada no Telefone de origem", 0, checkMadeCommunication(OPERATOR_1_NAME, CELLPHONE1));

        assertEquals("Saldo do telemóvel de origem foi modificado", INITIAL_BALANCE, getPhoneBalance(CELLPHONE1));

    }



    /** Testar se o telemovel de destino não existe */
    public void testDestinationPhoneNotExists() {
        // Arrange
        // Destino é um número em operador válido mas sem este número
        final SMSDTO dto = new SMSDTO(CELLPHONE1, CELLPHONENOTEXISTS, MSG);
        final SendSMSService sendService = new SendSMSService(dto);
        final ReceiveSMSService receiveService = new ReceiveSMSService(dto);

        boolean committed = false;

        // Act
        try {
            Transaction.begin();

            sendService.dispatch();
            receiveService.dispatch();

            Transaction.commit();
            committed = true;

        } catch (CellPhoneNotExistsException e) {
            // lançou a excepção correctamente
        } catch (Exception e) {
            fail("A Excepção " + e.getMessage() + " não deveria estar a ser mandada");
        }
        if (!committed) {
            Transaction.abort();
        }

        // Assert
        assertEquals("Comunicação fica registada no Telefone de origem", 0, checkMadeCommunication(OPERATOR_1_NAME, CELLPHONE1));
        assertEquals("Saldo do telemóvel de origem foi modificado", INITIAL_BALANCE, getPhoneBalance(CELLPHONE1));
    }



}
