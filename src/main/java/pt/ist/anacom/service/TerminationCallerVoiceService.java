package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.domain.CellPhone;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.domain.communication.Voice;
import pt.ist.anacom.shared.dto.VoiceDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneNotAvailableException;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;
import pt.ist.anacom.shared.exceptions.NotEnoughBalanceException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;
import pt.ist.fenixframework.FenixFramework;

public class TerminationCallerVoiceService extends
        AnaComService {

    private VoiceDTO dto;
    private VoiceDTO _result;

    public TerminationCallerVoiceService(VoiceDTO dto) {
        this.dto = dto;
    }

    @Atomic
    public void execute() throws CellPhoneNotAvailableException, OperatorNotFoundException, CellPhoneNotExistsException, NotEnoughBalanceException, IncompatibleModeException {
        dispatch();
    }

    @Override
    public void dispatch() throws CellPhoneNotAvailableException, OperatorNotFoundException, CellPhoneNotExistsException, NotEnoughBalanceException, IncompatibleModeException {
        NetworkManager nm = FenixFramework.getRoot();

        Operator op = nm.getOperatorByPhoneNumber(dto.getSrcNumber());
        CellPhone cellPhone = op.getCellPhoneByNumber(dto.getSrcNumber());

        // Objecto Voz onde guardaremos os dados
        Voice voice = new Voice(dto.getSrcNumber(), cellPhone.getSpeakingTo(), dto.getDuration());

        /* Calcular a duração da chamada */
        voice = cellPhone.endCallerVoice_VerifyAndGetDuration(voice);

        /* Solicitar ao operador calculo do custo do Voice */
        op.getPlan().calculateCost(voice);

        op.setSeqNumber(dto.getSeqNumber());

        /* Registar o final de chamada */
        voice = cellPhone.endCallerVoiceCommunication(voice);

        setResult(voice);
    }

    private void setResult(Voice voice) {
        VoiceDTO result = new VoiceDTO(voice.getCallerID(), voice.getReceiverID(), voice.getDuration(), voice.getCost());
        this._result = result;
    }

    public VoiceDTO getResult() {
        return this._result;
    }
}
