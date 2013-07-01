package pt.ist.anacom.test.services;

import pt.ist.anacom.service.AddBalanceService;
import pt.ist.anacom.shared.dto.phone.PhoneNumValueDTO;
import pt.ist.anacom.shared.enumerados.CellPhoneType;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.InvalidBalanceException;

/**
 * Testes ao adicionar saldo.
 */

public class AddBalanceTestCase extends
        FFServiceTestCase {

    /** The O p1. */
    private static String OP1 = "OPERATOR_1";

    /** The PREVIEW: PREFIX OP1. */
    private static String PREFIX_OP1 = "12";

    /** The PREVIEW: EXISTING PHONE NUMBER1. */
    private static String EXISTING_PHONE_NUMBER1 = "123456789";

    /** The PREVIEW: EXISTING PHONE NUMBER2. */
    private static String EXISTING_PHONE_NUMBER2 = "123456780";

    /** The PREVIEW: INEXISTING PHONE NUMBER1. */
    private static String INEXISTING_PHONE_NUMBER1 = "127654321";

    /** The PREVIEW: INITIAL BALANCE DEFAULT. */
    private static int INITIAL_BALANCE_DEFAULT = 0;

    /** The PREVIEW: INITIAL BALANCE. */
    private static int INITIAL_BALANCE = 2000; // 20 euros

    /** The PREVIEW: EXISTING PHONE NUMBER1 3G. */
    private static String EXISTING_PHONE_NUMBER1_3G = "129876543";

    /** The PREVIEW: EXISTING PHONE NUMBER2 3G. */
    private static String EXISTING_PHONE_NUMBER2_3G = "129876542";

    /*
     * (non-Javadoc)
     * 
     * @see pt.ist.anacom.test.FFServiceTestCase#setUp()
     */
    @Override
    public void setUp() {
        super.setUp();
        addOperatorSimple(OP1, PREFIX_OP1);
        addCellPhone(OP1, EXISTING_PHONE_NUMBER1, CellPhoneType.PHONE2G, INITIAL_BALANCE_DEFAULT);
        addCellPhone(OP1, EXISTING_PHONE_NUMBER2, CellPhoneType.PHONE2G, INITIAL_BALANCE);

        addCellPhone(OP1, EXISTING_PHONE_NUMBER1_3G, CellPhoneType.PHONE3G, INITIAL_BALANCE_DEFAULT);
        addCellPhone(OP1, EXISTING_PHONE_NUMBER2_3G, CellPhoneType.PHONE3G, INITIAL_BALANCE);
    }


    /**
     * - - - - - - - - - - - TESTES 2G - - - - - - - - - - -.
     */

    /**
     * Carregar telemovel com 1000 centimos (10 euros) correctamente, com saldo inicial a
     * 0
     */
    public void testAddBalance() {
        // Arrange
        PhoneNumValueDTO dto = new PhoneNumValueDTO(EXISTING_PHONE_NUMBER1, 1000);
        AddBalanceService abs = new AddBalanceService(dto);

        // Act
        try {
            abs.execute();
        } catch (Exception e) {
            fail("Balance should be added without throwing an exception.");
        }
        ;

        // Assert
        assertEquals("The phone balance should increase to 1000 down from 0", 1000, getPhoneBalance(EXISTING_PHONE_NUMBER1));
    }

    /**
     * Testa se adiciona saldo a um telemovel correctamente, com saldo inicial diferente
     * de 0 Que soma e nao substitui (salfo inicial 2000 (20 euros) saoldo final tem que
     * ser 3000 (30 euros)).
     */
    public void test2AddBalance() {
        // Arrange
        PhoneNumValueDTO dto = new PhoneNumValueDTO(EXISTING_PHONE_NUMBER2, 1000);
        AddBalanceService abs = new AddBalanceService(dto);

        // Act
        try {
            abs.execute();
        } catch (Exception e) {
            fail("Balance should be added without throwing an exception.");
        }

        // Assert
        assertEquals("The phone balance should increase to 3000 down from 1000", 3000, getPhoneBalance(EXISTING_PHONE_NUMBER2));
    }

    /**
     * Testa se ocorre uma excepcao quando se carrega um telemovel acima do limite maximo
     * Limite maximo 10000 (100 euros) tem que mandar excepcao quando se tenta caregar com
     * 101 euros.
     */
    public void testOverLimit() {
        // Arrange
        PhoneNumValueDTO dto = new PhoneNumValueDTO(EXISTING_PHONE_NUMBER1, 10100);
        AddBalanceService abs = new AddBalanceService(dto);

        // Act
        try {
            abs.execute();
            fail("Didnt catch exception");
        } catch (InvalidBalanceException e) { // lançou excepção correcta
        } catch (Exception e) {
            fail("A Excepção " + e.getMessage() + " não deveria estar a ser mandada");
        }

        // Assert
        assertEquals("Balance should be the same as before, balance = 0", 0, getPhoneBalance(EXISTING_PHONE_NUMBER1));

    }

    /**
     * Testa se ocorre uma excepcao quando se tenta carregar um telemovel que nao existe.
     */
    public void testWrongNumber() {
        PhoneNumValueDTO dto = new PhoneNumValueDTO(INEXISTING_PHONE_NUMBER1, 1000);
        AddBalanceService abs = new AddBalanceService(dto);
        // Act
        try {
            abs.execute();
        } catch (CellPhoneNotExistsException e) { // lançou excepção correcta
        } catch (Exception e) {
            fail("A Excepção " + e.getMessage() + " não deveria estar a ser mandada");
        }
        // Assert
    }

    /**
     * - - - - - - - - - - - TESTES 3G - - - - - - - - - - -.
     */

    /**
     * Carregar telemovel com 1000 centimos (10 euros) correctamente, com saldo inicial a
     * 0
     */
    public void testAddBalance3G() {
        // Arrange
        PhoneNumValueDTO dto = new PhoneNumValueDTO(EXISTING_PHONE_NUMBER1_3G, 1000);
        AddBalanceService abs = new AddBalanceService(dto);

        // Act
        try {
            abs.execute();
        } catch (Exception e) {
            fail("Balance should be added without throwing an exception.");
        }
        ;

        // Assert
        assertEquals("The phone balance should increase to 1000 down from 0", 1000, getPhoneBalance(EXISTING_PHONE_NUMBER1_3G));
    }

    /**
     * Testa se adiciona saldo a um telemovel correctamente, com saldo inicial diferente
     * de 0 Que soma e nao substitui (salfo inicial 2000 (20 euros) saoldo final tem que
     * ser 3000 (30 euros)).
     */
    public void test2AddBalance3G() {
        // Arrange
        PhoneNumValueDTO dto = new PhoneNumValueDTO(EXISTING_PHONE_NUMBER2_3G, 1000);
        AddBalanceService abs = new AddBalanceService(dto);

        // Act
        try {
            abs.execute();
        } catch (Exception e) {
            fail("Balance should be added without throwing an exception.");
        }

        // Assert
        assertEquals("The phone balance should increase to 3000 down from 1000", 3000, getPhoneBalance(EXISTING_PHONE_NUMBER2_3G));
    }

    /**
     * Testa se ocorre uma excepcao quando se carrega um telemovel acima do limite maximo
     * Limite maximo 10000 (100 euros) tem que mandar excepcao quando se tenta caregar com
     * 101 euros.
     */
    public void testOverLimit3G() {
        // Arrange
        PhoneNumValueDTO dto = new PhoneNumValueDTO(EXISTING_PHONE_NUMBER1_3G, 10100);
        AddBalanceService abs = new AddBalanceService(dto);

        // Act
        try {
            abs.execute();
        } catch (InvalidBalanceException e) {
        } catch (Exception e) {
            fail("A Excepção " + e.getMessage() + " não deveria estar a ser mandada");
        }
        // Assert
        assertEquals("Balance should be the same as before, balance = 0", 0, getPhoneBalance(EXISTING_PHONE_NUMBER1_3G));

    }


    // TODO Testar se carregar com quantias negativas

}
