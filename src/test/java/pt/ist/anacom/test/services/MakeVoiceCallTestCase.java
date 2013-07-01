package pt.ist.anacom.test.services;

import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.domain.communication.Voice;
import pt.ist.anacom.service.InitCallerVoiceCommunicationService;
import pt.ist.anacom.service.InitReceiverVoiceService;
import pt.ist.anacom.service.TerminationCallerVoiceService;
import pt.ist.anacom.shared.dto.VoiceDTO;
import pt.ist.anacom.shared.enumerados.CellPhoneMode;
import pt.ist.anacom.shared.enumerados.CellPhoneType;
import pt.ist.anacom.shared.exceptions.AnaComException;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.pstm.Transaction;


/**
 * Testes de Estabelecer uma ligação de voz
 */

public class MakeVoiceCallTestCase extends FFServiceTestCase {

	/*
	 * #1 Testar sem saldo
	 * #2 Testar sender com modo incompatível
	 * #3 Testar receiver com modo incompatível
	 * 
	 */

	// Operators ID's
	/** OPERATOR  NAME. */
	private static String OPERATOR_NAME = "TagusPark";

	/** OPERATOR  PREFIX. */
	private static String OPERATOR_PREFIX = "99";

	// Operator Tariffs
	/** The EXTRACHARGETARIFF. */
	private static double EXTRACHARGETARIFF = 1.2;

	/** The SMSTARIFF  */
	private static int SMSTARIFF = 10;

	/** The VOICETARIFF. */
	private static int VOICETARIFF = 2;

	/** The VIDEOTARIFF. */
	private static int VIDEOTARIFF = 2;

	/** The BONUSTARIFF. */
	private static double BONUSTARIFF = 1.1;


	/** The CELLPHONE 1. */
	private static String CELLPHONE1 = "992200011";

	/** The CELLPHONE 2. */
	private static String CELLPHONE2 = "992200012";

	/** The INITIAL BALANCE 150. */
	private static final int BALANCE = 150;

	/** The INITIAL BALANCE 0. */
	private static final int BROKE = 0;

	@Override
	public void setUp() {
		super.setUp();

		addOperator(OPERATOR_NAME, OPERATOR_PREFIX, SMSTARIFF, VOICETARIFF, VIDEOTARIFF, EXTRACHARGETARIFF, BONUSTARIFF);

		addCellPhone(OPERATOR_NAME, CELLPHONE1, CellPhoneType.PHONE2G, BALANCE);
		changeCellPhoneMode(OPERATOR_NAME, CELLPHONE1, CellPhoneMode.ON);

		addCellPhone(OPERATOR_NAME, CELLPHONE2, CellPhoneType.PHONE2G, BROKE);
		changeCellPhoneMode(OPERATOR_NAME, CELLPHONE2, CellPhoneMode.ON);


	}

	//#1 Testar sem saldo
	public void testWithoutBalance(){
		VoiceDTO dto = new VoiceDTO(CELLPHONE2, CELLPHONE1);
		InitCallerVoiceCommunicationService initCall = new InitCallerVoiceCommunicationService(dto);

		try{
			initCall.execute();
			fail("The CellPhone has no balance(is broke), shouldn't initiate the Call");
		}
		catch(AnaComException e){
		}

	}


	//#2 Testar sender com modo incompatível
	public void testSenderWithIncompatibleMode(){
		changeCellPhoneMode(OPERATOR_NAME, CELLPHONE1, CellPhoneMode.OFF);
		VoiceDTO dto = new VoiceDTO(CELLPHONE1, CELLPHONE2);
		InitCallerVoiceCommunicationService initCall = new InitCallerVoiceCommunicationService(dto);

		try{
			initCall.execute();
			fail("The CellPhone is OFF, shouldn't initiate the Call");
		}
		catch(AnaComException e){
		}

	}


	//#3 Testar receiver com modo incompatível
	public void testReceiverWithIncompatibleMode(){

		changeCellPhoneMode(OPERATOR_NAME, CELLPHONE2, CellPhoneMode.OFF);
		VoiceDTO dto = new VoiceDTO(CELLPHONE1, CELLPHONE2);
		InitReceiverVoiceService initReceiver = new InitReceiverVoiceService(dto);

		try{
			initReceiver.execute();
			fail("The CellPhone is OFF, shouldn't initiate the Call");
		}
		catch(AnaComException e){
		}

		changeCellPhoneMode(OPERATOR_NAME, CELLPHONE2, CellPhoneMode.BUSY);
		dto = new VoiceDTO(CELLPHONE1, CELLPHONE2);
		initReceiver = new InitReceiverVoiceService(dto);

		try{
			initReceiver.execute();
			fail("The CellPhone is BUSY, shouldn't initiate the Call");
		}
		catch(AnaComException e){
		}


	}

	// Como o tempo é medido em tempo real, temos de arranjar uma forma de manipular esse tempo	
	public void testCalculateVoiceCost(){
		int duration = 20;
		double calculatedCost = 0;
		
		Operator op = null;
		boolean committed = false;

		try {
			Transaction.begin();

			NetworkManager nm = FenixFramework.getRoot();
			try {
				op = nm.getOperatorByName(OPERATOR_NAME);
				Voice v = new Voice(CELLPHONE1, CELLPHONE2, duration);
				op.getPlan().calculateCost(v);
				calculatedCost = v.getCost();
			} catch (AnaComException e) {
			}

			Transaction.commit();
			committed = true;
		} finally {
			if (!committed) {
				Transaction.abort();
				fail("Could not get operator: " + OPERATOR_NAME);
			}
		}


		double predictedCost = VOICETARIFF * duration;
		assertEquals(calculatedCost, predictedCost);

	}


}
