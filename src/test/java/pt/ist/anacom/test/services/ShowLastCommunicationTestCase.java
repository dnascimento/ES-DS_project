package pt.ist.anacom.test.services;

import pt.ist.anacom.service.GetLastMadeCommunicationService;
import pt.ist.anacom.service.InitCallerVoiceCommunicationService;
import pt.ist.anacom.service.InitReceiverVoiceService;
import pt.ist.anacom.service.ReceiveSMSService;
import pt.ist.anacom.service.SendSMSService;
import pt.ist.anacom.service.TerminationCallerVoiceService;
import pt.ist.anacom.service.TerminationReceiverVoiceService;
import pt.ist.anacom.shared.dto.SMSDTO;
import pt.ist.anacom.shared.dto.VoiceDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumberDTO;
import pt.ist.anacom.shared.enumerados.CellPhoneMode;
import pt.ist.anacom.shared.enumerados.CellPhoneType;
import pt.ist.anacom.shared.exceptions.AnaComException;

public class ShowLastCommunicationTestCase extends
        FFServiceTestCase {



    // Operators ID's
    /** OPERATOR NAME. */
    private static String OPERATOR_NAME = "TagusPark";

    /** OPERATOR PREFIX. */
    private static String OPERATOR_PREFIX = "99";

    // Operator Tariffs
    /** The EXTRACHARGETARIFF. */
    private static double EXTRACHARGETARIFF = 1.2;

    /** The SMSTARIFF */
    private static int SMSTARIFF = 10;

    /** The VOICETARIFF. */
    private static int VOICETARIFF = 2;

    /** The VIDEOTARIFF. */
    private static int VIDEOTARIFF = 2;

    /** The BONUSTARIFF. */
    private static int BONUSTARIFF = 2;


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


    public void testShowLastCommunicationForSMS() {
        String message = "O Balão do João voa voa sem parar";
        SMSDTO SMSDTO = new SMSDTO(CELLPHONE1, CELLPHONE2, message);
        SendSMSService sendService = new SendSMSService(SMSDTO);
        ReceiveSMSService receiveService = new ReceiveSMSService(SMSDTO);

        try {
            sendService.execute();
            receiveService.execute();
        } catch (AnaComException e) {
            fail("The communication shouldn't fail");
        }

        PhoneNumberDTO phoneDTO = new PhoneNumberDTO(CELLPHONE1);
        GetLastMadeCommunicationService showService = new GetLastMadeCommunicationService(phoneDTO);

        try {
            showService.execute();
        } catch (AnaComException e) {
            fail("The communication shouldn't fail");
        }

        assertEquals(CELLPHONE2, showService.getResult().get_destNumber());


    }


    public void testShowLastCommunicationForVoice() {

        VoiceDTO dtoForInitiation = new VoiceDTO(CELLPHONE1, CELLPHONE2);
        InitCallerVoiceCommunicationService senderInitCall = new InitCallerVoiceCommunicationService(dtoForInitiation);
        InitReceiverVoiceService receiverInitCall = new InitReceiverVoiceService(dtoForInitiation);


        try {
            senderInitCall.execute();
            receiverInitCall.execute();
        } catch (AnaComException e) {
            fail("The Call initiation should be executed correctly");
        }

        // espera 2 segundos
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        VoiceDTO dtoForTermination = new VoiceDTO(CELLPHONE1, CELLPHONE2, 5);
        TerminationCallerVoiceService senderTermCall = new TerminationCallerVoiceService(dtoForTermination);
        TerminationReceiverVoiceService receiverTermCall = new TerminationReceiverVoiceService(dtoForTermination);


        try {
            senderTermCall.execute();
            receiverTermCall.execute();
        } catch (AnaComException e) {
            fail("The Call termination should be executed correctly");
        }

        PhoneNumberDTO phoneDTO = new PhoneNumberDTO(CELLPHONE1);
        GetLastMadeCommunicationService showService = new GetLastMadeCommunicationService(phoneDTO);

        try {
            showService.execute();
        } catch (AnaComException e) {
            fail("The communication shouldn't fail");
        }

        assertEquals(CELLPHONE2, showService.getResult().get_destNumber());

    }

}
