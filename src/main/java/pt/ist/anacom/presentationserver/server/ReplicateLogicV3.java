package pt.ist.anacom.presentationserver.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.http.HTTPException;

import pt.ist.anacom.shared.dto.CommunicationDTO;
import pt.ist.anacom.shared.dto.SMSDTO;
import pt.ist.anacom.shared.dto.SMSListDTO;
import pt.ist.anacom.shared.dto.phone.InitiateCommunicationDTO;
import pt.ist.anacom.shared.dto.phone.PhoneListDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumModeDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumValueDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumberDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneAlreadyExistsException;
import pt.ist.anacom.shared.exceptions.CellPhoneNotAvailableException;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;
import pt.ist.anacom.shared.exceptions.InvalidBalanceException;
import pt.ist.anacom.shared.exceptions.InvalidCommunicationException;
import pt.ist.anacom.shared.exceptions.InvalidNumberException;
import pt.ist.anacom.shared.exceptions.InvalidOperatorException;
import pt.ist.anacom.shared.exceptions.NotEnoughBalanceException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;
import pt.ist.anacom.shared.exceptions.ReplicateException;
import pt.ist.anacom.shared.stubs.AnacomApplicationServerPortType;
import pt.ist.anacom.shared.stubs.CommunicationDTOType;
import pt.ist.anacom.shared.stubs.InitiateCommunicationDTOType;
import pt.ist.anacom.shared.stubs.OperatorSimpleDTOType;
import pt.ist.anacom.shared.stubs.PhoneDetailedDTOElem;
import pt.ist.anacom.shared.stubs.PhoneListDTOType;
import pt.ist.anacom.shared.stubs.PhoneNumModeDTOType;
import pt.ist.anacom.shared.stubs.PhoneNumValueDTOType;
import pt.ist.anacom.shared.stubs.PhoneNumberDTOType;
import pt.ist.anacom.shared.stubs.SMSListDTOType;
import pt.ist.anacom.shared.stubs.SmsDTOType;
import pt.ist.anacom.shared.stubs.VoiceDTOType;


public class ReplicateLogicV3 {

	private static int NUMERO_REPLICAS = 4;
	private static boolean DEBUG = false;

	public ReplicateLogicV3(){
		
	}

	// ---------------------------------------------------------------------------
	// ------------------------ Função de Leitura  -------------------------------
	// ---------------------------------------------------------------------------


	public PhoneNumValueDTOType getBalance(ArrayList<AnacomApplicationServerPortType> portList, 
			PhoneNumberDTOType msg) throws Throwable{

		PhoneNumValueDTOType retorno = null;
		ArrayList<Response<PhoneNumValueDTOType>> r = new ArrayList<Response<PhoneNumValueDTOType>>();

		//Popular o Array
		for(AnacomApplicationServerPortType port : portList){
			r.add(port.getBalanceAsync(msg));
		}

		int[] done = waitForN(r, NUMERO_REPLICAS-1);
		long[] tStamp = new long[NUMERO_REPLICAS];
		String[] respValuesToString = new String[NUMERO_REPLICAS];
		Object[] respValuesToObject = new Object[NUMERO_REPLICAS];
		int missing = -1;

		initValues(tStamp, respValuesToString);

		//prencher o vector de objectos com os objectos recebidos
		for(int l = 0; l < NUMERO_REPLICAS; l++){

			if(done[l] == 1){
				try{
					respValuesToObject[l] = new PhoneNumValueDTO(r.get(l).get().getNumber(), r.get(l).get().getBalance());
					tStamp[l] = r.get(l).get().getSequenceNumber();
				}catch(ExecutionException e){

					//devolve o objecto que coresponde a exepçao mandada pelo servidor
					ExceptionObject eo = new ExceptionObject(e);
					respValuesToObject[l] = eo.getResp();
					tStamp[l] = eo.getSeqNum();
				}
				respValuesToString[l] = respValuesToObject[l].getClass()+" "+respValuesToObject[l].toString();

			}else {
				missing = l;
			}
		}

		if(detectBizantine(tStamp, respValuesToString, done, NUMERO_REPLICAS-1)){

			done = waitForN(r, NUMERO_REPLICAS);   

			try{
				respValuesToObject[missing] = new PhoneNumValueDTO(r.get(missing).get().getNumber(), r.get(missing).get().getBalance());
				tStamp[missing] = r.get(missing).get().getSequenceNumber();
			}catch(ExecutionException e){

				ExceptionObject eo = new ExceptionObject(e);
				respValuesToObject[missing] = eo.getResp();
				tStamp[missing] = eo.getSeqNum();
			}
			respValuesToString[missing] = respValuesToObject[missing].getClass()+" "+respValuesToObject[missing].toString();
		}

		printValues(done, tStamp, respValuesToObject);

		int rightAnswer = getAnswer(tStamp,respValuesToString,done);

		try{

			retorno =  r.get(rightAnswer).get();

		} catch(ExecutionException e){
			throw e.getCause();
		}

		return retorno;
	}

	
	


	public PhoneListDTOType getCellPhoneList(ArrayList<AnacomApplicationServerPortType> portList, 
			OperatorSimpleDTOType msg) throws Throwable{

		PhoneListDTOType retorno = null;
		ArrayList<Response<PhoneListDTOType>> r = new ArrayList<Response<PhoneListDTOType>>();

		//Popular o Array
		for(AnacomApplicationServerPortType port : portList){
			r.add(port.getCellPhoneListAsync(msg));
		}

		int[] done = waitForN(r, NUMERO_REPLICAS-1);
		long[] tStamp = new long[NUMERO_REPLICAS];
		String[] respValuesToString = new String[NUMERO_REPLICAS];
		Object[] respValuesToObject = new Object[NUMERO_REPLICAS];
		int missing = -1;

		initValues(tStamp, respValuesToString);

		//prencher o vector de objectos com os objectos recebidos
		for(int l = 0; l < NUMERO_REPLICAS; l++){

			if(done[l] == 1){
				try{


					List<PhoneNumValueDTOType> stubList = r.get(l).get().getCellPhoneList();
					List<PhoneNumValueDTO> dtoList = new ArrayList<PhoneNumValueDTO>();

					//converter array de stubs para array de dtos
					for(PhoneNumValueDTOType dtoType : stubList){
						PhoneNumValueDTO dto = new PhoneNumValueDTO(dtoType.getNumber(), dtoType.getBalance());
						dtoList.add(dto);
					}

					respValuesToObject[l] = new PhoneListDTO(dtoList);
					tStamp[l] = r.get(l).get().getSequenceNumber();
					
				}catch(ExecutionException e){

					//devolve o objecto que coresponde a exepçao mandada pelo servidor
					ExceptionObject eo = new ExceptionObject(e);
					respValuesToObject[l] = eo.getResp();
					tStamp[l] = eo.getSeqNum();
				}
				respValuesToString[l] = respValuesToObject[l].getClass()+" "+respValuesToObject[l].toString();

			}else{
				missing = l;
			}
		}

		printValues(done, tStamp, respValuesToString);

		if(detectBizantine(tStamp, respValuesToString, done, NUMERO_REPLICAS-1)){

			done = waitForN(r, NUMERO_REPLICAS);   

			try{
				List<PhoneNumValueDTOType> stubList = r.get(missing).get().getCellPhoneList();
				List<PhoneNumValueDTO> dtoList = new ArrayList<PhoneNumValueDTO>();

				//converter array de stubs para array de dtos
				for(PhoneNumValueDTOType dtoType : stubList){
					PhoneNumValueDTO dto = new PhoneNumValueDTO(dtoType.getNumber(), dtoType.getBalance());
					dtoList.add(dto);
				}

				respValuesToObject[missing] = new PhoneListDTO(dtoList);
				tStamp[missing] = r.get(missing).get().getSequenceNumber();
			}catch(ExecutionException e){

				ExceptionObject eo = new ExceptionObject(e);
				respValuesToObject[missing] = eo.getResp();
				tStamp[missing] = eo.getSeqNum();
			}
			respValuesToString[missing] = respValuesToObject[missing].getClass()+" "+respValuesToObject[missing].toString();
		}

		printValues(done, tStamp, respValuesToObject);

		int rightAnswer = getAnswer(tStamp,respValuesToString,done);

		try{

			retorno =  r.get(rightAnswer).get();

		} catch(ExecutionException e){

			try{
				throw e.getCause();
			}catch(pt.ist.anacom.shared.stubs.InvalidOperatorException_Exception w){
				throw new InvalidOperatorException(w.getFaultInfo().getOperatorName(), w.getMessage());
			}catch(WebServiceException w){
				throw new WebServiceException(w.getMessage());
			} catch (Throwable w) {
				e.printStackTrace();
			}
		}

		return retorno;
	}



	public PhoneNumModeDTOType getCellPhoneMode(ArrayList<AnacomApplicationServerPortType> portList, 
			PhoneNumberDTOType msg) throws Throwable{

		PhoneNumModeDTOType retorno = null;
		ArrayList<Response<PhoneNumModeDTOType>> r = new ArrayList<Response<PhoneNumModeDTOType>>();

		//Popular o Array
		for(AnacomApplicationServerPortType port : portList){
			r.add(port.getCellPhoneModeAsync(msg));
		}

		int[] done = waitForN(r, NUMERO_REPLICAS-1);
		long[] tStamp = new long[NUMERO_REPLICAS];
		String[] respValuesToString = new String[NUMERO_REPLICAS];
		Object[] respValuesToObject = new Object[NUMERO_REPLICAS];
		int missing = -1;

		initValues(tStamp, respValuesToString);

		//prencher o vector de objectos com os objectos recebidos
		for(int l = 0; l < NUMERO_REPLICAS; l++){

			if(done[l] == 1){
				try{
					respValuesToObject[l] = new PhoneNumModeDTO(r.get(l).get().getNumber(), r.get(l).get().getMode());
					tStamp[l] = r.get(l).get().getSequenceNumber();
				}catch(ExecutionException e){

					//devolve o objecto que coresponde a exepçao mandada pelo servidor
					ExceptionObject eo = new ExceptionObject(e);
					respValuesToObject[l] = eo.getResp();
					tStamp[l] = eo.getSeqNum();
				}
				respValuesToString[l] = respValuesToObject[l].getClass()+" "+respValuesToObject[l].toString();

			}else{
				missing = l;
			}
		}

		if(detectBizantine(tStamp, respValuesToString, done, NUMERO_REPLICAS-1)){

			done = waitForN(r, NUMERO_REPLICAS);   

			try{
				respValuesToObject[missing] = new  PhoneNumModeDTO(r.get(missing).get().getNumber(), r.get(missing).get().getMode());
				tStamp[missing] = r.get(missing).get().getSequenceNumber();
			}catch(ExecutionException e){

				ExceptionObject eo = new ExceptionObject(e);
				respValuesToObject[missing] = eo.getResp();
				tStamp[missing] = eo.getSeqNum();
			}
			respValuesToString[missing] = respValuesToObject[missing].getClass()+" "+respValuesToObject[missing].toString();
		}

		printValues(done, tStamp, respValuesToObject);

		int rightAnswer = getAnswer(tStamp,respValuesToString,done);

		try{

			retorno =  r.get(rightAnswer).get();

		} catch(ExecutionException e){

			throw e.getCause();
		}

		return retorno;
	}



	public SMSListDTOType getReceivedSMS(ArrayList<AnacomApplicationServerPortType> portList, 
			PhoneNumberDTOType msg) throws Throwable{

		SMSListDTOType retorno = null;
		ArrayList<Response<SMSListDTOType>> r = new ArrayList<Response<SMSListDTOType>>();

		//Popular o Array
		for(AnacomApplicationServerPortType port : portList){
			r.add(port.getReceivedSMSAsync(msg));
		}

		int[] done = waitForN(r, NUMERO_REPLICAS-1);
		long[] tStamp = new long[NUMERO_REPLICAS];
		String[] respValuesToString = new String[NUMERO_REPLICAS];
		Object[] respValuesToObject = new Object[NUMERO_REPLICAS];
		int missing = -1;

		initValues(tStamp, respValuesToString);

		//prencher o vector de objectos com os objectos recebidos
		for(int l = 0; l < NUMERO_REPLICAS; l++){

			if(done[l] == 1){
				try{


					List<SmsDTOType> stubList = r.get(l).get().getSmsList();
					List<SMSDTO> dtoList = new ArrayList<SMSDTO>();

					//converter array de stubs para array de dtos
					for(SmsDTOType dtoType : stubList){
						SMSDTO dto = new SMSDTO(dtoType.getSrc(), dtoType.getDest(), dtoType.getMsg());
						dtoList.add(dto);
					}

					respValuesToObject[l] = new SMSListDTO(dtoList);
					tStamp[l] = r.get(l).get().getSequenceNumber();
				}catch(ExecutionException e){

					//devolve o objecto que coresponde a exepçao mandada pelo servidor
					ExceptionObject eo = new ExceptionObject(e);
					respValuesToObject[l] = eo.getResp();
					tStamp[l] = eo.getSeqNum();
				}
				respValuesToString[l] = respValuesToObject[l].getClass()+" "+respValuesToObject[l].toString();

			}else{
				missing = l;
			}
		}

		if(detectBizantine(tStamp, respValuesToString, done, NUMERO_REPLICAS-1)){

			done = waitForN(r, NUMERO_REPLICAS);   

			try{
				List<SmsDTOType> stubList = r.get(missing).get().getSmsList();
				List<SMSDTO> dtoList = new ArrayList<SMSDTO>();

				//converter array de stubs para array de dtos
				for(SmsDTOType dtoType : stubList){
					SMSDTO dto = new SMSDTO(dtoType.getSrc(), dtoType.getDest(), dtoType.getMsg());
					dtoList.add(dto);
				}

				respValuesToObject[missing] = new SMSListDTO(dtoList);
				tStamp[missing] = r.get(missing).get().getSequenceNumber();
			}catch(ExecutionException e){

				ExceptionObject eo = new ExceptionObject(e);
				respValuesToObject[missing] = eo.getResp();
				tStamp[missing] = eo.getSeqNum();
			}
			respValuesToString[missing] = respValuesToObject[missing].getClass()+" "+respValuesToObject[missing].toString();
		}

		printValues(done, tStamp, respValuesToObject);

		int rightAnswer = getAnswer(tStamp,respValuesToString,done);

		try{

			retorno =  r.get(rightAnswer).get();

		} catch(ExecutionException e){

			throw e.getCause();
		}

		return retorno;
	}



	public CommunicationDTOType getLastMadeCommunication(ArrayList<AnacomApplicationServerPortType> portList, 
			PhoneNumberDTOType msg) throws Throwable{

		CommunicationDTOType retorno = null;
		ArrayList<Response<CommunicationDTOType>> r = new ArrayList<Response<CommunicationDTOType>>();

		//Popular o Array
		for(AnacomApplicationServerPortType port : portList){
			r.add(port.getLastMadeCommunicationAsync(msg));
		}

		int[] done = waitForN(r, NUMERO_REPLICAS-1);
		long[] tStamp = new long[NUMERO_REPLICAS];
		String[] respValuesToString = new String[NUMERO_REPLICAS];
		Object[] respValuesToObject = new Object[NUMERO_REPLICAS];
		int missing = -1;

		initValues(tStamp, respValuesToString);

		//prencher o vector de objectos com os objectos recebidos
		for(int l = 0; l < NUMERO_REPLICAS; l++){

			if(done[l] == 1){
				try{
					respValuesToObject[l] = new CommunicationDTO(r.get(l).get().getDest(), r.get(l).get().getType(),
							r.get(l).get().getCost(), r.get(l).get().getSize());
					tStamp[l] = r.get(l).get().getSequenceNumber();
				}catch(ExecutionException e){

					//devolve o objecto que coresponde a exepçao mandada pelo servidor
					ExceptionObject eo = new ExceptionObject(e);
					respValuesToObject[l] = eo.getResp();
					tStamp[l] = eo.getSeqNum();
				}
				respValuesToString[l] = respValuesToObject[l].getClass()+" "+respValuesToObject[l].toString();

			}else{
				missing = l;
			}
		}

		if(detectBizantine(tStamp, respValuesToString, done, NUMERO_REPLICAS-1)){

			done = waitForN(r, NUMERO_REPLICAS);   

			try{
				respValuesToObject[missing] = new CommunicationDTO(r.get(missing).get().getDest(), r.get(missing).get().getType(), r.get(missing).get().getCost(), r.get(missing).get().getSize());
				tStamp[missing] = r.get(missing).get().getSequenceNumber();
			}catch(ExecutionException e){

				ExceptionObject eo = new ExceptionObject(e);
				respValuesToObject[missing] = eo.getResp();
				tStamp[missing] = eo.getSeqNum();
			}
			respValuesToString[missing] = respValuesToObject[missing].getClass()+" "+respValuesToObject[missing].toString();
		}

		printValues(done, tStamp, respValuesToObject);

		int rightAnswer = getAnswer(tStamp,respValuesToString,done);

		try{

			retorno =  r.get(rightAnswer).get();

		} catch(ExecutionException e){

			throw e.getCause();
		}

		return retorno;
	}



	public PhoneNumberDTOType getSequenceNumber(ArrayList<AnacomApplicationServerPortType> portList, 
			PhoneNumberDTOType msg) throws Throwable{

		PhoneNumberDTOType retorno = null;
		ArrayList<Response<PhoneNumberDTOType>> r = new ArrayList<Response<PhoneNumberDTOType>>();

		//Popular o Array
		for(AnacomApplicationServerPortType port : portList){
			r.add(port.getSequenceNumberAsync(msg));
		}

		int[] done = waitForN(r, NUMERO_REPLICAS-1);
		long[] tStamp = new long[NUMERO_REPLICAS];
		String[] respValuesToString = new String[NUMERO_REPLICAS];
		Object[] respValuesToObject = new Object[NUMERO_REPLICAS];
		int missing = -1;

		initValues(tStamp, respValuesToString);

		//prencher o vector de objectos com os objectos recebidos
		for(int l = 0; l < NUMERO_REPLICAS; l++){

			if(done[l] == 1){
				try{
					respValuesToObject[l] = new PhoneNumberDTO(r.get(l).get().getNumber(), r.get(l).get().getSequenceNumber());
					tStamp[l] = r.get(l).get().getSequenceNumber();
				}catch(ExecutionException e){

					//devolve o objecto que coresponde a exepçao mandada pelo servidor
					ExceptionObject eo = new ExceptionObject(e);
					respValuesToObject[l] = eo.getResp();
					tStamp[l] = eo.getSeqNum();
				}
				respValuesToString[l] = respValuesToObject[l].getClass()+" "+respValuesToObject[l].toString();

			}else{
				missing = l;
			}
		}

		if(detectBizantine(tStamp, respValuesToString, done, NUMERO_REPLICAS-1)){

			done = waitForN(r, NUMERO_REPLICAS);   

			try{
				respValuesToObject[missing] = new PhoneNumberDTO(r.get(missing).get().getNumber(), r.get(missing).get().getSequenceNumber());
				tStamp[missing] = r.get(missing).get().getSequenceNumber();
			}catch(ExecutionException e){

				//devolve o objecto que coresponde a exepçao mandada pelo servidor
				ExceptionObject eo = new ExceptionObject(e);
				respValuesToObject[missing] = eo.getResp();
				tStamp[missing] = eo.getSeqNum();
			}
			respValuesToString[missing] = respValuesToObject[missing].getClass()+" "+respValuesToObject[missing].toString();
		}

		//printValues(done, tStamp, respValuesToObject);

		int rightAnswer = getAnswer(tStamp,respValuesToString,done);

		try{

			retorno =  r.get(rightAnswer).get();

		} catch(ExecutionException e){

			throw e.getCause();

		}

		return retorno;
	}

	
	
	public InitiateCommunicationDTOType ableToComunicate(
			ArrayList<AnacomApplicationServerPortType> portList,
			InitiateCommunicationDTOType msg) throws Throwable{
		
		
		InitiateCommunicationDTOType retorno = null;
		ArrayList<Response<InitiateCommunicationDTOType>> r = new ArrayList<Response<InitiateCommunicationDTOType>>();

		//Popular o Array
		for(AnacomApplicationServerPortType port : portList){
			r.add(port.ableToCommunicateAsync(msg));
		}

		int[] done = waitForN(r, NUMERO_REPLICAS-1);
		long[] tStamp = new long[NUMERO_REPLICAS];
		String[] respValuesToString = new String[NUMERO_REPLICAS];
		Object[] respValuesToObject = new Object[NUMERO_REPLICAS];
		int missing = -1;

		initValues(tStamp, respValuesToString);

		//prencher o vector de objectos com os objectos recebidos
		for(int l = 0; l < NUMERO_REPLICAS; l++){

			if(done[l] == 1){
				try{
					respValuesToObject[l] = new InitiateCommunicationDTO(r.get(l).get().getCellPhoneNumber(), r.get(l).get().isAbleToCommunicate(), r.get(l).get().getSequenceNumber());
					tStamp[l] = r.get(l).get().getSequenceNumber();
				}catch(ExecutionException e){

					//devolve o objecto que coresponde a exepçao mandada pelo servidor
					ExceptionObject eo = new ExceptionObject(e);
					respValuesToObject[l] = eo.getResp();
					tStamp[l] = eo.getSeqNum();
				}
				respValuesToString[l] = respValuesToObject[l].getClass()+" "+respValuesToObject[l].toString();

			}else{
				missing = l;
			}
		}

		if(detectBizantine(tStamp, respValuesToString, done, NUMERO_REPLICAS-1)){

			done = waitForN(r, NUMERO_REPLICAS);   

			try{
				respValuesToObject[missing] = new InitiateCommunicationDTO(r.get(missing).get().getCellPhoneNumber(), r.get(missing).get().isAbleToCommunicate(), r.get(missing).get().getSequenceNumber());
				tStamp[missing] = r.get(missing).get().getSequenceNumber();
			}catch(ExecutionException e){

				//devolve o objecto que coresponde a exepçao mandada pelo servidor
				ExceptionObject eo = new ExceptionObject(e);
				respValuesToObject[missing] = eo.getResp();
				tStamp[missing] = eo.getSeqNum();
			}
			respValuesToString[missing] = respValuesToObject[missing].getClass()+" "+respValuesToObject[missing].toString();
		}

		printValues(done, tStamp, respValuesToObject);

		int rightAnswer = getAnswer(tStamp,respValuesToString,done);

		try{

			retorno =  r.get(rightAnswer).get();

		} catch(ExecutionException e){

			throw e.getCause();

		}

		return retorno;
	}

	
	
	
	


	// ---------------------------------------------------------------------------
	// ------------------------ Função de Escrita   ------------------------------
	// ---------------------------------------------------------------------------



	public void addBalance(ArrayList<AnacomApplicationServerPortType> portList, 
			PhoneNumValueDTOType msg) throws Throwable{

		ArrayList<Response<?>> r = new ArrayList<Response<?>>();
		long seqNumber = auxGetSequenceNumber(portList, msg.getNumber());
		msg.setSequenceNumber(seqNumber+1);

		//Escreve nos servidores
		for(AnacomApplicationServerPortType port : portList){
			r.add(port.addBalanceAsync(msg));
		}

		getAssincAck(r);

	}



	public void addCellPhone(ArrayList<AnacomApplicationServerPortType> portList,
			PhoneDetailedDTOElem msg) throws Throwable{

		ArrayList<Response<?>> r = new ArrayList<Response<?>>();
		long seqNumber = auxGetSequenceNumber(portList , msg.getNumber());
		msg.setSequenceNumber(seqNumber+1);

		//Escreve nos servidores
		for(AnacomApplicationServerPortType port : portList){
			r.add(port.createCellPhoneAsync(msg));
		}

		getAssincAck(r);

	}



	public void removeCell(ArrayList<AnacomApplicationServerPortType> portList,
			PhoneNumberDTOType msg) throws Throwable{

		ArrayList<Response<?>> r = new ArrayList<Response<?>>();
		long seqNumber = auxGetSequenceNumber(portList, msg.getNumber());
		msg.setSequenceNumber(seqNumber+1);

		//Escreve nos servidores
		for(AnacomApplicationServerPortType port : portList){
			r.add(port.removeCellPhoneAsync(msg));
		}

		getAssincAck(r);

	}



	public void sendSMS(ArrayList<AnacomApplicationServerPortType> portList,
			SmsDTOType msg) throws Throwable{

		ArrayList<Response<?>> r = new ArrayList<Response<?>>();
		long seqNumber = auxGetSequenceNumber(portList, msg.getDest());
		msg.setSequenceNumber(seqNumber+1);

		//Escreve nos servidores
		for(AnacomApplicationServerPortType port : portList){
			r.add(port.sendSMSAsync(msg));
		}

		getAssincAck(r);

	}



	public void receiveSMS(ArrayList<AnacomApplicationServerPortType> portList,
			SmsDTOType msg) throws Throwable{

		ArrayList<Response<?>> r = new ArrayList<Response<?>>();
		long seqNumber = auxGetSequenceNumber(portList, msg.getSrc());
		msg.setSequenceNumber(seqNumber+1);

		//Escreve nos servidores
		for(AnacomApplicationServerPortType port : portList){
			r.add(port.receiveSMSAsync(msg));
		}

		getAssincAck(r);

	}



	public void changeCellPhoneMode(ArrayList<AnacomApplicationServerPortType> portList,
			PhoneNumModeDTOType msg) throws Throwable{

		ArrayList<Response<?>> r = new ArrayList<Response<?>>();
		long seqNumber = auxGetSequenceNumber(portList, msg.getNumber());
		msg.setSequenceNumber(seqNumber+1);

		//Escreve nos servidores
		for(AnacomApplicationServerPortType port : portList){
			r.add(port.changeCellPhoneModeAsync(msg));
		}

		getAssincAck(r);

	}



	public void initCallerVoiceCommunication(ArrayList<AnacomApplicationServerPortType> portList,
			VoiceDTOType msg) throws Throwable{

		ArrayList<Response<?>> r = new ArrayList<Response<?>>();
		long seqNumber = auxGetSequenceNumber(portList, msg.getSrc());
		msg.setSequenceNumber(seqNumber+1);

		//Escreve nos servidores
		for(AnacomApplicationServerPortType port : portList){
			r.add(port.initCallerVoiceCommunicationAsync(msg));
		}

		getAssincAck(r);

	}



	public void initReceiverVoiceCommunication(ArrayList<AnacomApplicationServerPortType> portList,
			VoiceDTOType msg) throws Throwable{

		ArrayList<Response<?>> r = new ArrayList<Response<?>>();
		long seqNumber = auxGetSequenceNumber(portList, msg.getDest());
		msg.setSequenceNumber(seqNumber+1);

		//Escreve nos servidores
		for(AnacomApplicationServerPortType port : portList){
			r.add(port.initReceiverVoiceCommunicationAsync(msg));
		}

		getAssincAck(r);

	}



	public void terminationCallerVoiceCommunication(ArrayList<AnacomApplicationServerPortType> portList,
			VoiceDTOType msg) throws Throwable{

		ArrayList<Response<?>> r = new ArrayList<Response<?>>();
		long seqNumber = auxGetSequenceNumber(portList, msg.getSrc());
		msg.setSequenceNumber(seqNumber+1);

		//Escreve nos servidores
		for(AnacomApplicationServerPortType port : portList){
			r.add(port.terminationCallerVoiceAsync(msg));
		}

		getAssincAck(r);

	}



	public void terminationReceiverVoiceCommunication(ArrayList<AnacomApplicationServerPortType> portList,
			VoiceDTOType msg) throws Throwable{

		ArrayList<Response<?>> r = new ArrayList<Response<?>>();
		long seqNumber = auxGetSequenceNumber(portList, msg.getDest());
		msg.setSequenceNumber(seqNumber+1);

		//Escreve nos servidores
		for(AnacomApplicationServerPortType port : portList){
			r.add(port.terminationReceiverVoiceAsync(msg));
		}

		getAssincAck(r);

	}




	// ---------------------------------------------------------------------------
	// ------------------------FUNÇOES INTERNAS-----------------------------------
	// ---------------------------------------------------------------------------



	/**
	 * Método para esperar pelos ack's de 3 servidores após executado uma
	 * 
	 * @param r Lista de responstas assincronas
	 * @throws Throwable manda a excepção lançada pelo metodo invocado assincronamente
	 */
	private void getAssincAck(ArrayList<Response<?>> r)
			throws Throwable{

		int[] done = waitForN(r, NUMERO_REPLICAS-1);
		long[] tStamp = new long[NUMERO_REPLICAS];
		String[] respValuesToString = new String[NUMERO_REPLICAS];
		Object[] respValuesToObject = new Object[NUMERO_REPLICAS];
		int missing = -1;
		int ack = 0;

		initValues(tStamp, respValuesToString);

		//prencher o vector de objectos com os objectos recebidos
		for(int l = 0; l < NUMERO_REPLICAS; l++){

			if(done[l] == 1){
				try{

					try{
						r.get(l).get();
					}catch(java.lang.NullPointerException e){
						ack++;
					}

					respValuesToObject[l] = new String(r.get(l).getClass().toString());
					tStamp[l] = -1;
				}catch(ExecutionException e){

					//devolve o objecto que coresponde a exepçao mandada pelo servidor
					ExceptionObject eo = new ExceptionObject(e);
					respValuesToObject[l] = eo.getResp();
					tStamp[l] = -1;
				}
				respValuesToString[l] = respValuesToObject[l].getClass()+" "+respValuesToObject[l].toString();

			}else{
				missing = l;
			}
		}

		if(detectBizantine(tStamp, respValuesToString, done, NUMERO_REPLICAS-1)){

			done = waitForN(r, NUMERO_REPLICAS);   

			try{
				try{
					r.get(missing).get();
				}catch(java.lang.NullPointerException e){
					ack++;
				}
				respValuesToObject[missing] = new String(r.get(missing).getClass().toString());
				tStamp[missing] = -1;
			}catch(ExecutionException e){

				//devolve o objecto que coresponde a exepçao mandada pelo servidor
				ExceptionObject eo = new ExceptionObject(e);
				respValuesToObject[missing] = eo.getResp();
				tStamp[missing] = -1;
			}
			respValuesToString[missing] = respValuesToObject[missing].getClass()+" "+respValuesToObject[missing].toString();
		}

		if(DEBUG){
			System.out.println(ack+" Ack`s Receved \n \n");
			if(ack != 3){
				System.out.println("Not 3 Ack Because:");
				printValues(done, tStamp, respValuesToObject);
			}
		}

		int rightAnswer = getAnswer(tStamp,respValuesToString,done);

		try{

			try{
				r.get(rightAnswer).get();
			}catch(java.lang.NullPointerException e){
				ack++;
			}

		} catch(ExecutionException e){

			throw e.getCause();

		}
	}

	/**
	 * Iremos ver para cada resposta, quantas outras há iguais.
	 * Se houver mais de uma então essa é a resposta certa.
	 * Se todos as respostas têm outra igual ex: (R1,R1,R2,R2) iremos escolher 
	 * o par com maior sequence number.
	 * Se só houver uma resposta duplicada então é essa a resposta certa.
	 * Caso contrário não é possivel determinar a resposta certa.
	 */
	private int getAnswer(long[] tStamp, String[] respostas, int[] done)throws InterruptedException{

		int rightIndex = 0;
		int[] repeted = {0,0,0,0};
		int biggerStamp = 0;

		for(int i = 0; i <= 3; i++){
			for(int j = 0; j <= 3; j++){
				//contar o numero de repostas iguais a esta
				if(done[i]==1&&done[j]==1&&tStamp[i]==tStamp[j]&&respostas[i].equals(respostas[j]))
					repeted[i]++;

				//se houver mais de duas iguais entao essa é a resposta certa
				if(repeted[i]>2){
					rightIndex = i;
					return rightIndex;
				}

				//guardar o maior timestamp (caso empate)
				if(tStamp[i]> tStamp[biggerStamp]) biggerStamp = i;
			}
		}

		//Se há dois pares iguais escolher o que tem maior TS
		if(repeted[0] == 2 && repeted[1] == 2 &&
				repeted[2] == 2 && repeted[3] == 2)
			return biggerStamp;

		//Se não escolher uma posicao que tenha outro igual
		for(int i = 0; i <= 3; i++)
			if(repeted[i]==2){
				rightIndex = i;
				return rightIndex;

			}
		throw new ReplicateException("Error: Invalid Answer from all servers.");
	}


	/**
	 * Podemos concluir que existe uma resposta bizantina se:
	 * Todas as respostas são diferentes ou
	 * existe uma resposta com timestamp superior ás outras.
	 */
	private boolean detectBizantine(long[] tStamp, String[] respostas, int[] done, int msgRecebidas){

		boolean bizantine = false;
		int equal = 0;
		
		//Caso tenhamos mais de 3 respostas então e possivel saber a certa.
		if(msgRecebidas > 3)
			return false;
		
		for(int i = 0; i <= 3; i++){
			int superior = 0;
			
			if(done[i]==0) continue;
			
			for(int j = 0; j <= 3; j++){
				
				if(done[j]==0) continue;
				
				if(i == j) continue;
				
				//Se houver um obj com timestamp maior que
				//os outros dois entao há bizantina
				if(tStamp[i] > tStamp[j]) superior++;
				
				//Se todos os valores são diferentes entao há bizantina
				//iguais = 1 então quer dizer que há pelo menos 2 obj iguais
				if(tStamp[i] == tStamp[j] && respostas[i].equals(respostas[j]))
					equal = 1;
				
				if(superior >= 2){
					bizantine = true;
					break;		
				}
			}
			if(bizantine) break;
		}

		//se são todos diferentes
		if(equal == 0) bizantine = true;

		if(bizantine && DEBUG)
			System.out.println("Replicate Logic: Bizantine Fault Detected.");

		return bizantine;
	}



	@SuppressWarnings("rawtypes")
	private int[] waitForN(ArrayList<?> r, int servidores) throws InterruptedException{
		//Quais das respostas estao prontas
		int[] n = new int[NUMERO_REPLICAS];

		for(int l = 0; l < NUMERO_REPLICAS; l++)
			n[l] = 0;

		int respostas = 0;
		while (respostas < servidores) {
			for(int index = 0; index < r.size(); index++)
				if(((Future)(r.get(index))).isDone() && n[index]!=1){ 
					n[index]=1;
					respostas++;
					if(respostas == servidores) break;
					continue;
				}
			Thread.sleep(25);
		}
		return n;
	}

	
	private void initValues(long[] tStamp, String[] respValuesToString) {
		for(int l = 0; l < NUMERO_REPLICAS; l++){
			tStamp[l]=-1;
			respValuesToString[l]="";
		}
	}



	private long auxGetSequenceNumber(
			ArrayList<AnacomApplicationServerPortType> portList,
			String msg) throws Throwable {
		
		if(DEBUG)
			System.out.println("Reading Last TimeStamp:");

		PhoneNumberDTOType auxMsg = new PhoneNumberDTOType();
		auxMsg.setNumber(msg);
		
		long seqNumber = getSequenceNumber(portList, auxMsg).getSequenceNumber();
		
		if(DEBUG)
			System.out.println("Last TimeStamp is: " + seqNumber);
		
		return seqNumber;
	}



	private void printValues(int[] done, long[] tStamp,
			Object[] respValuesToObject) {

		if(DEBUG){
			for(int l = 0; l < NUMERO_REPLICAS; l++){
				if(done[l] == 1)
					System.out.println("Tstamp:"+tStamp[l]+
							" String:"+respValuesToObject[l].getClass()+" "+respValuesToObject[l].toString());
			}
		}
	}


	/**
	 * Esta Classe tem como objectivo apanhar Exepçoes do tipo Throwable e passa-los para
	 *  objectos que depois serao uteis nas comparaceos entre Objetos.
	 */
	class ExceptionObject{

		Object _resp = null;
		long _seqNum = 0;

		public ExceptionObject(ExecutionException e){

			try {
				throw e.getCause();
			}

			catch (pt.ist.anacom.shared.stubs.CellPhoneNotAvailableException x) {
				_resp = new CellPhoneNotAvailableException(x.getFaultInfo().getError(), x.getFaultInfo().getSequenceNumber());
				_seqNum = x.getFaultInfo().getSequenceNumber();

			}catch (pt.ist.anacom.shared.stubs.IncompatibleModeException x) {
				_resp = new IncompatibleModeException(x.getFaultInfo().getError(), x.getFaultInfo().getSequenceNumber());
				_seqNum = x.getFaultInfo().getSequenceNumber();

			}catch (pt.ist.anacom.shared.stubs.NotEnoughBalanceException x) {
				_resp = new NotEnoughBalanceException(x.getFaultInfo().getError(), x.getFaultInfo().getSequenceNumber());
				_seqNum = x.getFaultInfo().getSequenceNumber();

			}catch (pt.ist.anacom.shared.stubs.InvalidCommunicationException x) {
				_resp = new InvalidCommunicationException(x.getFaultInfo().getError(), x.getFaultInfo().getSequenceNumber());
				_seqNum = x.getFaultInfo().getSequenceNumber();

			}catch (pt.ist.anacom.shared.stubs.InvalidNumberException x) {
				_resp = new InvalidNumberException(x.getFaultInfo().getError(), x.getFaultInfo().getSequenceNumber());
				_seqNum = x.getFaultInfo().getSequenceNumber();

			}catch (pt.ist.anacom.shared.stubs.CellPhoneAlreadyExistsException x) {
				_resp = new CellPhoneAlreadyExistsException(x.getFaultInfo().getError(), x.getFaultInfo().getSequenceNumber());
				_seqNum = x.getFaultInfo().getSequenceNumber();		

			}catch (pt.ist.anacom.shared.stubs.CellPhoneNotExistsException x) {
				_resp = new CellPhoneNotExistsException(x.getFaultInfo().getError(), x.getFaultInfo().getSequenceNumber());
				_seqNum = x.getFaultInfo().getSequenceNumber();

			}catch (pt.ist.anacom.shared.stubs.InvalidOperatorException_Exception x) {
				_resp = new InvalidOperatorException(x.getFaultInfo().getOperatorName(), x.getFaultInfo().getError(), x.getFaultInfo().getSequenceNumber());
				_seqNum = x.getFaultInfo().getSequenceNumber();

			}catch (pt.ist.anacom.shared.stubs.OperatorNotFoundException x) {
				_resp = new OperatorNotFoundException(x.getFaultInfo().getError(), x.getFaultInfo().getSequenceNumber());
				_seqNum = x.getFaultInfo().getSequenceNumber();

			}catch (pt.ist.anacom.shared.stubs.InvalidBalanceException x) {
				_resp = new InvalidBalanceException(x.getFaultInfo().getError(), x.getFaultInfo().getSequenceNumber());
				_seqNum = x.getFaultInfo().getSequenceNumber();

			}catch (InterruptedException x) {
				//System.out.println("Error in Replicate Logic: InterruptedException when extracting exeption!");
				_resp = new OperatorNotFoundException("WebServiceException"+x.getMessage(), -1);
				_seqNum = -1;

			}catch (HTTPException x) {
				//System.out.println("Error in Replicate Logic: WebServiceException when extracting exeption!");
				_resp = new OperatorNotFoundException("HTTPException"+x.getMessage(), -1);
				_seqNum = -1;

			}catch (WebServiceException x) {
				//System.out.println("Error in Replicate Logic: WebServiceException when extracting exeption!");
				_resp = new OperatorNotFoundException("WebServiceException"+x.getMessage(), -1);
				_seqNum = -1;

			} catch (Throwable z) {
				//System.out.println("Error in Replicate Logic: Throwable when extracting exeption!");
				_resp = new OperatorNotFoundException("WebServiceException"+z.getMessage(), -1);
				_seqNum = -1;
			}
		}


		public Object getResp() {
			return _resp;
		}

		public long getSeqNum() {
			return _seqNum;
		}
	}
}