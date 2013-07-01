package pt.ist.anacom.applicationserver.handler;

import java.security.cert.X509Certificate;
import java.util.Set;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import pt.ist.anacom.applicationserver.ApplicationServerInitListener;
import pt.ist.anacom.ca.SecuritySDManager;
import pt.ist.anacom.shared.stubs.ca.CAException_Exception;

public class AttackHandler
implements SOAPHandler<SOAPMessageContext> {

	/**
	 * Controlo de teste de ataque
	 */
	private static boolean _serverIsAttacked = false;
	private static boolean _clientIsAttacked = false;

	@Override
	public void close(MessageContext messageContext) {
		return;
	}


	@Override
	public Set getHeaders() {
		return null;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (outbound && _serverIsAttacked) {
			serverAttack(context);}

		if (!outbound && _clientIsAttacked) {
			clientAttack(context);}



		return true;
	}



	private void clientAttack(SOAPMessageContext context) {
		setInvalidSign(context);
	}


	private void serverAttack(SOAPMessageContext context) {
		setInvalidSign(context);
	}


	/**
	 * Excepções de dominio são tratadas como todas as mensagens SOAP	 * 
	 */
	@Override
	public boolean handleFault(SOAPMessageContext context){	
		handleMessage(context);
		return true;

	}





	/**
	 * Ataque à integridade da mensagem
	 */
	private void setInvalidSign(SOAPMessageContext context) {

		System.out.println("[ATTACK] Set the message invalid");

		try {
			SOAPMessage message = context.getMessage();
			SOAPPart soapPart = message.getSOAPPart();
			SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

			SOAPHeader soapHeader = soapEnvelope.getHeader();

			if (soapHeader == null) {
				soapHeader = soapEnvelope.addHeader();
			}

			// cria novo elemento para header (timeStamp)
			Name name = soapEnvelope.createName("rrr", "rrrr", "rrrrr");
			SOAPElement element = soapHeader.addChildElement(name);

			// adiciona novo elemento
			element.addTextNode("rrrrrr");
		}

		catch (Exception e) {
			System.out.println("AttackHandler Exception " + e.getMessage());
		}

	}
}




