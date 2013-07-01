package pt.ist.anacom.presentationserver;

import pt.ist.anacom.shared.dto.CommunicationDTO;
import pt.ist.anacom.shared.dto.SMSDTO;
import pt.ist.anacom.shared.dto.SMSListDTO;
import pt.ist.anacom.shared.dto.phone.PhoneDetailedDTO;
import pt.ist.anacom.shared.dto.phone.PhoneListDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumModeDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumValueDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumberDTO;


/** Converte os DTO's em representação textual (String). */
public class DataPresenter {

    /**
     * Apresentar o numero de um telemovel.
     * 
     * @param dto PhoneNumberDTO
     * @return String
     */
    public static String showPhoneNumber(PhoneNumberDTO dto) {
        return dto.get_number();
    }

    /**
     * Apresentar o numero e saldo de um telemovel.
     * 
     * @param dto PhoneNumValueDTO
     * @return String
     */
    public static String showPhoneNumberBalance(PhoneNumValueDTO dto) {
        return dto.get_number() + " : " + (float) dto.getBalance() / 100 + "€";
    }

    /**
     * Apresentar todos os detalhes de um telemovel.
     * 
     * @param dto PhoneDetailedDTO
     * @return String
     */
    public static String showPhoneDetailed(PhoneDetailedDTO dto) {
        return dto.get_number() + " : " + dto.getBalance() + " : " + dto.get_operator() + " : " + dto.get_cellPhoneType();
    }




    /**
     * Mostrar uma lista dos numeros dos telefones do dto.
     * 
     * @param PhoneListDTO
     * @return String
     */
    public static String showPhoneListNumber(PhoneListDTO list) {
        String result = "List of phone numbers:";
        for (PhoneNumberDTO dto : list.getPhones()) {
            result = result + "\n" + dto.get_number();
        }
        return result;
    }

    /**
     * Mostrar uma lista dos numeros e saldos dos telefone do dto.
     * 
     * @param PhoneListDTO
     * @return String
     */
    public static String showPhoneListNumberBalance(PhoneListDTO list) {
        String result = "List of phone number : balance";
        for (PhoneNumberDTO dto : list.getPhones()) {
            result = result + "\n" + showPhoneNumberBalance((PhoneNumValueDTO) dto);
        }
        return result;
    }

    /**
     * Mostrar uma lista dos detalhes dos telefones do dto.
     * 
     * @param PhoneListDTO
     * @return String
     */
    public static String showPhoneListDetailed(PhoneListDTO list) {
        String result = "List of phone number : balance : operator : type";
        for (PhoneNumberDTO dto : list.getPhones()) {
            result = result + "\n" + showPhoneDetailed((PhoneDetailedDTO) dto);
        }
        return result;
    }

    /**
     * Mostrar o modo do cellphone do dto no formato "numero modo" ex: 91233212 ON.
     * 
     * @param mode the mode
     * @return the string
     */
    public static String showGetCellPhoneModeDTO(PhoneNumModeDTO mode) {
        String outMode;

        switch (mode.getMode()) {
        case ON:
            outMode = "ON";
            break;
        case OFF:
            outMode = "OFF";
            break;
        case SILENCE:
            outMode = "SILENCE";
            break;
        case BUSY:
            outMode = "BUSY";
            break;
        default:
            outMode = "Invalid Mode";
            break;
        }
        return mode.get_number() + " " + outMode;

    }
    
    /**
     * Prints a list containing the information of received sms
     * 
     * @param SMSListDTO list of sms
     * @return string containing the info of each sms. [Sender:_ Dest:_ Message:_]
     * */
    public static String showReceivedSMS(SMSListDTO list) {
        String result = "List of received sms: source-destination-message";
        for (SMSDTO dto : list.getSMSList()) {
            result = result + "\n" + "Sender:" + dto.getSrcNumber() + " Dest:" + dto.getDestNumber() + " Message:" + dto.getText();
        }
        return result;
    }
    
    /**
     * Prints the destination number, type, cost and lenght/duration of a given communication
     * 
     *@param cm comunication information
     *@return string with the content of a communication
     * */
    public static String showLastCall(CommunicationDTO cm) {
        String result = "Communication info: " +
        				"Destination: " + cm.get_destNumber() +
        				" | Type: " + cm.get_type() + 
        				" | Cost: " + cm.get_cost() + 
        				" | Lenght/Duration: " + cm.get_size();
        return result;
    }
}
