package pt.ist.anacom.test.services;

import pt.ist.anacom.service.CreateOperatorService;
import pt.ist.anacom.shared.dto.operator.OperatorDetailedDTO;
import pt.ist.anacom.shared.exceptions.AlreadyExistOperatorException;
import pt.ist.anacom.shared.exceptions.InvalidOperatorPrefixException;

/**
 * Testes à criação de novos Operadores. 1) Teste à criação de um operador válido; 2)
 * Testes à criação de operadores com prefixos inválidos; 3) Criação de um operador com um
 * nome já existente;
 */

public class CreateOperatorWithTariffServiceTestCase extends
        FFServiceTestCase {

    // Valid
    /** The PREVIEW: OPERATOR NAME. */
    private static String OPERATOR_NAME = "IST_TELECOM";

    /** The PREVIEW: NEW VALID OPERATOR PREFIX. */
    private static String NEW_VALID_OPERATOR_PREFIX = "77";

    // Invalid
    /** The PREVIEW: NEW INVALID OPERATOR PREFIX A. */
    private static String NEW_INVALID_OPERATOR_PREFIX_A = "789";

    /** The PREVIEW: NEW INVALID OPERATOR PREFIX B. */
    private static String NEW_INVALID_OPERATOR_PREFIX_B = "1";

    /** The PREVIEW: NEW INVALID OPERATOR PREFIX C. */
    private static String NEW_INVALID_OPERATOR_PREFIX_C = "";

    // Tariff values
    /** The PREVIEW: SMS TARIFF PRICE. */
    private static int SMS_TARIFF_PRICE = 1;

    /** The PREVIEW: VOICE TARIFF PRICE. */
    private static int VOICE_TARIFF_PRICE = 35;

    /** The PREVIEW: VIDEO TARIFF PRICE. */
    private static int VIDEO_TARIFF_PRICE = 67;

    /** The PREVIEW: EXTRA TARIFF PRICE. */
    private static int EXTRA_TARIFF_PRICE = 2;

    /** The PREVIEW: BONUS TARIFF PRICE. */
    private static int BONUS_TARIFF_PRICE = 0;

    /**
     * Instantiates a new creates the operator with tariff service test.
     * 
     * @param msg the msg
     */
    public CreateOperatorWithTariffServiceTestCase(String msg) {
        super(msg);
    }

    /**
     * Instantiates a new creates the operator with tariff service test.
     */
    public CreateOperatorWithTariffServiceTestCase() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.ist.anacom.test.FFServiceTestCase#setUp()
     */
    @Override
    public void setUp() {
        super.setUp();
    }

    /**
     * Teste válido a operaçao nao tem que mandar exepçao.
     */
    public void testValidOperator() {
        // Arrange
        OperatorDetailedDTO dto = new OperatorDetailedDTO(OPERATOR_NAME, NEW_VALID_OPERATOR_PREFIX, SMS_TARIFF_PRICE, VOICE_TARIFF_PRICE, VIDEO_TARIFF_PRICE, EXTRA_TARIFF_PRICE, BONUS_TARIFF_PRICE);
        CreateOperatorService service = new CreateOperatorService(dto);

        // Act
        try {
            service.execute();
        } catch (Exception e) {
            fail("Operator should be added without throwing an exception.");
            System.out.println("Teste JUnit CreateOperator - validOperator: " + e.getMessage());
        }

        // Assert
        assertTrue("Operador válido não está a ser guardado na rede", checkOperatorByName(OPERATOR_NAME));
    }

    /**
     * Testa com un prefixo de operador invalido.
     */
    public void testInValidOperator() {

        // Arrange
        OperatorDetailedDTO dto = new OperatorDetailedDTO(OPERATOR_NAME, NEW_INVALID_OPERATOR_PREFIX_A, SMS_TARIFF_PRICE, VOICE_TARIFF_PRICE, VIDEO_TARIFF_PRICE, EXTRA_TARIFF_PRICE,
                BONUS_TARIFF_PRICE);
        CreateOperatorService service1 = new CreateOperatorService(dto);

        OperatorDetailedDTO dto2 = new OperatorDetailedDTO(OPERATOR_NAME, NEW_INVALID_OPERATOR_PREFIX_B, SMS_TARIFF_PRICE, VOICE_TARIFF_PRICE, VIDEO_TARIFF_PRICE, EXTRA_TARIFF_PRICE,
                BONUS_TARIFF_PRICE);
        CreateOperatorService service2 = new CreateOperatorService(dto2);

        OperatorDetailedDTO dto3 = new OperatorDetailedDTO(OPERATOR_NAME, NEW_INVALID_OPERATOR_PREFIX_C, SMS_TARIFF_PRICE, VOICE_TARIFF_PRICE, VIDEO_TARIFF_PRICE, EXTRA_TARIFF_PRICE,
                BONUS_TARIFF_PRICE);
        CreateOperatorService service3 = new CreateOperatorService(dto3);

        // Act
        try {
            service1.execute();
        } catch (InvalidOperatorPrefixException e) {
            assertEquals(NEW_INVALID_OPERATOR_PREFIX_A.length(), e.get_prefixLen());
        } catch (Exception x) {
            fail("Nao devia mandar outra excepçao a nao ser InvalidOperatorPrefixException");
        }

        try {
            service2.execute();
        } catch (InvalidOperatorPrefixException e) {
            assertEquals(NEW_INVALID_OPERATOR_PREFIX_B.length(), e.get_prefixLen());
        } catch (Exception x) {
            fail("Nao devia mandar outra excepçao a nao ser InvalidOperatorPrefixException");
        }

        try {
            service3.execute();
        } catch (InvalidOperatorPrefixException e) {
            assertEquals(NEW_INVALID_OPERATOR_PREFIX_C.length(), e.get_prefixLen());
        } catch (Exception x) {
            fail("Nao devia mandar outra excepçao a nao ser InvalidOperatorPrefixException");
        }

        // Assert
        assertFalse("Operador inválido está a ser guardado na rede", checkOperatorByName(OPERATOR_NAME));
    }

    /**
     * Testa se é possível adicionar um operador com o mesmo nome que um já existente.
     */

    public void testRepeatedOperator() {
        // Arrange
        OperatorDetailedDTO dto = new OperatorDetailedDTO(OPERATOR_NAME, NEW_VALID_OPERATOR_PREFIX, SMS_TARIFF_PRICE, VOICE_TARIFF_PRICE, VIDEO_TARIFF_PRICE, EXTRA_TARIFF_PRICE, BONUS_TARIFF_PRICE);
        CreateOperatorService service = new CreateOperatorService(dto);

        CreateOperatorService service2 = new CreateOperatorService(dto);

        // Act
        try {
            service.execute();
        } catch (Exception e) {
            fail("Nao devia ter mandado qualquer tipo de exepçao");
        }

        try {
            service2.execute();
        } catch (AlreadyExistOperatorException a) {
            assertEquals("Operado a tentar ser adicionado pela segunda vez", OPERATOR_NAME, a.get_opName());
        } catch (Exception e) {
            fail("Nao devia mandar outra excepçao a nao ser AlreadyExistOperatorException");
        }
    }


    // TODO Testes individuais: adicionar só com o nome repetido e só com o prefixo
    // repetido
}
