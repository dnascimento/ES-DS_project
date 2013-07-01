package pt.ist.anacom.applicationserver.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Set;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.Node;

import pt.ist.anacom.applicationserver.ApplicationServerInitListener;
import pt.ist.anacom.ca.SecurityDataConverter;
import pt.ist.anacom.ca.SecuritySDManager;
import pt.ist.anacom.ca.SecurityUtils;
import sun.misc.BASE64Encoder;

public class BinzantineHandler
        implements SOAPHandler<SOAPMessageContext> {

    // Strings constantes:
    String TNS = "http://anacom";
    String PREFIX = "bn";
    String SIGN = "sign";
    String CERT = "cert";
    String EXCEPTION_PREFIX = "tns";


    /**
     * O gestor de Segurança é a classe/módulo que dentro do servidor é responsável por
     * tratar das chaves, certificados, etc.
     */
    private static SecuritySDManager securityManager;
    private static boolean _securityManagerInitialized = false;

    private static boolean _twoServerDelayed = false;
    private static boolean _threeResponseForOthers = false;
    private static boolean _oneResponseForOthers = false;

    private static boolean _revokeUsedCertificate = false;

    private static boolean _revogate = false;


    private static long _delay = 10000;

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

        if (!_securityManagerInitialized) {
            initializeSecurityManager();
        }


        if (outbound && _twoServerDelayed) {
            delayTwoSOAPMessages();
        }

        if (outbound && _threeResponseForOthers) {
            threeResponsesForOthers(context);
        }

        if (outbound && _oneResponseForOthers) {
            oneResponseForOthers(context);
        }

        if (outbound && _revokeUsedCertificate) {
            revokeUsedCertificate(context);
        }

        return true;
    }


    /*
     * Método que impõe atraso a réplica Vodafone-R1 e Vodafone-R2
     */
    private void delayTwoSOAPMessages() {

        if (ApplicationServerInitListener.getServerSecurityName().equals("CN=Vodafone-R1, O=Tagus") || ApplicationServerInitListener.getServerSecurityName().equals("CN=Vodafone-R2, O=Tagus")) {

            System.out.println("----> Waiting in " + ApplicationServerInitListener.getServerSecurityName());

            try {
                Thread.sleep(_delay);
            } catch (InterruptedException e) {
                System.out.println("AttackHandler Exception: " + e.getMessage());
            }
        }
    }


    /**
     * TESTE PARA REVOGAÇÃO DE CERTIFICADO. Saco o certificado da mensagem e peço
     * directamente à CA para o revogar. Faço delay da resposta para dar tempo de todos os
     * servidores actualizarem a lista de revogação. Depois envio. O servidor recebe, vê
     * que expirou/revogou e descarta. A falha é bizantina na leitura dos timestamps, por
     * isso apenas limita-se a aguardar pela resposta do 4º servidor e tolera-a. Depois, o
     * meu servidor recebe o novo pedido e ao responder, percebe que o seu próprio
     * certificado foi revogado externamente. Por isso, pede à CA para gerar novo e
     * guarda-o.
     * 
     * @param context
     */
    private void revokeUsedCertificate(SOAPMessageContext context) {
        SOAPMessage message = context.getMessage();
        if (ApplicationServerInitListener.getServerSecurityName().equals("CN=Vodafone-R1, O=Tagus")) {

            X509Certificate certDaMensagem = null;
            try {
                certDaMensagem = getCertificate(message);
                System.out.println("Certificado da mensagem actual: " + certDaMensagem);
            } catch (Exception e) {
                System.out.println("Erro ao obter certificado da mensagem: " + e.getMessage());
            }

            if (_revogate == false) {
                try {
                    // forcar a CA a utilizar o certificado do handler normal.
                    getSecurityManager().setServerCert(certDaMensagem);
                    // Revogar agora o certificado original
                    getSecurityManager().askCAtoRevokeCurrentCertificate();
                    Thread.sleep(10000);
                    _revogate = true;
                } catch (Exception e) {
                    System.out.println("AttackHandler Exception: " + e.getMessage());
                }
            }

        }


    }


    /*
     * Método em que Vodafone-R1,R2 e R3 respondem por Vodafone-R4
     */
    private void threeResponsesForOthers(SOAPMessageContext context) {

        try {
            if (ApplicationServerInitListener.getServerSecurityName().equals("CN=Vodafone-R1, O=Tagus") || ApplicationServerInitListener.getServerSecurityName().equals("CN=Vodafone-R2, O=Tagus")
                    || ApplicationServerInitListener.getServerSecurityName().equals("CN=Vodafone-R3, O=Tagus")) {

                // obtem certificado Vodafone-R2
                String vodafoneR4certString = securityManager.getCa().getCertificate("Vodafone-R4");

                // remove assinatura da mensagem
                removeSignMessage(context);

                // pões certificado da Vodafone-r2 no header
                putCertHeader(context, vodafoneR4certString);

                addFakeSignMessage(context);

            }

        } catch (Exception e) {
            System.out.println("BinzantineHandler - not supposed to happen Exception (responseForOthers)");
        }

    }


    /*
     * Método em que Vodafone-R1 responde por Vodafone-R4
     */
    private void oneResponseForOthers(SOAPMessageContext context) {

        try {
            if (ApplicationServerInitListener.getServerSecurityName().equals("CN=Vodafone-R1, O=Tagus")) {

                // obtem certificado Vodafone-R4
                String vodafoneR4certString = securityManager.getCa().getCertificate("Vodafone-R4");

                // remove assinatura da mensagem
                removeSignMessage(context);

                // pões certificado da Vodafone-r2 no header
                putCertHeader(context, vodafoneR4certString);

                addFakeSignMessage(context);

            }

        } catch (Exception e) {
            System.out.println("BinzantineHandler - not supposed to happen Exception (responseForOthers)");
        }

    }



    /**
     * /** Assinar toda a mensagem SOAP com certificado de Vodafone-R2
     * 
     * @param message the message
     * @throws Exception the exception
     */
    private void addFakeSignMessage(SOAPMessageContext context) {
        try {
            SOAPMessage message = context.getMessage();
            SOAPPart soapPart = message.getSOAPPart();
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
            SOAPHeader soapHeader = soapEnvelope.getHeader();

            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            message.writeTo(byteOut);
            byte[] data = byteOut.toByteArray();

            // Assinar TODA A MENSAGEM
            byte[] sign = SecurityUtils.makeDigitalSignature(data, getSecurityManager().getPrivateKey());

            // Converter para texto
            BASE64Encoder encoder = new BASE64Encoder();
            String encodedSign = encoder.encode(sign);

            // Colocar no header
            if (soapHeader == null) {
                soapHeader = soapEnvelope.addHeader();
            }

            Name name = soapEnvelope.createName(SIGN, PREFIX, TNS);
            SOAPElement element = soapHeader.addChildElement(name);
            element.addTextNode(encodedSign);

        } catch (Exception e) {
            System.out.println("BINZANTINE - not supposed to happen Exception (responseForOthers)");
        }

    }




    private void removeSignMessage(SOAPMessageContext context) {

        try {
            SOAPMessage message = context.getMessage();
            SOAPPart soapPart = message.getSOAPPart();
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
            SOAPHeader soapHeader = soapEnvelope.getHeader();

            // Obtém content da assinatura
            Iterator<Node> it = soapHeader.getChildElements();
            Node headerNode = null;
            while (it.hasNext()) {
                headerNode = it.next();

                if (headerNode.getNodeName().equals(PREFIX + ":" + SIGN)) {
                    break;
                }

                // Retirar o header com assinatura
                soapHeader.removeChild(headerNode);
            }

            // Validar a assinatura
            // Retirar o header com assinatura
            soapHeader.removeChild(headerNode);
        } catch (Exception e) {
            System.out.println("Binzantine Exception: " + e.getMessage());
        }

    }



    /**
     * Adiciona certificado do sender ao header da mensagem.
     * 
     * @param context the context
     * @param serverCert the server cert
     * @throws Exception
     */
    private void putCertHeader(SOAPMessageContext context, String serverCert) throws Exception {

        SOAPMessage message = context.getMessage();
        SOAPPart soapPart = message.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.getHeader();

        if (soapHeader == null) {
            soapHeader = soapEnvelope.addHeader();
        }

        // cria novo elemento para header (com certificado)
        Name name = soapEnvelope.createName(CERT, PREFIX, TNS);
        SOAPElement element = soapHeader.addChildElement(name);

        element.addTextNode(serverCert);
    }


    /**
     * Excepções de dominio são tratadas como todas as mensagens SOAP *
     */
    @Override
    public boolean handleFault(SOAPMessageContext context) {
        handleMessage(context);
        return true;

    }

    public X509Certificate getCertificate(SOAPMessage message) throws SOAPException, CertificateException, IOException {
        SOAPPart soapPart = message.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.getHeader();

        // Obter a assinatura codificada
        if (soapHeader == null) {
            throw new WebServiceException("ClientHandler - getCertificate");

        }

        // Obtém content da assinatura
        Iterator<Node> it = soapHeader.getChildElements();
        Node node;
        while (it.hasNext()) {
            node = it.next();
            if (node.getNodeName().equals(PREFIX + ":" + CERT)) {
                String certText = node.getTextContent();
                X509Certificate cert = SecurityDataConverter.TextToCertificate(certText);
                return cert;
            }
        }
        throw new CertificateException("Não consegui extrair o certificado da mensagem");
    }


    private void initializeSecurityManager() {
        try {
            String serverName = ApplicationServerInitListener.getServerSecurityName();
            serverName = serverName + "_BizantineHanlder";
            // System.out.println("HandlerINIT do servidor: " + serverName);
            String keysFolderPath = "/tmp/keys/";
            setSecurityManager(new SecuritySDManager(serverName, keysFolderPath));
            _securityManagerInitialized = true;
        } catch (Exception e) {
            System.out.println("AttackHandler - not supposed to happen Exception (initializeSecurityManager)");
        }
    }

    /**
     * Sets the security manager.
     * 
     * @param securityManager the securityManager to set
     */
    public static void setSecurityManager(SecuritySDManager securityManager) {
        BinzantineHandler.securityManager = securityManager;
    }

    /**
     * Gets the security manager.
     * 
     * @param securityManager the securityManager to set
     * @return
     */
    public static SecuritySDManager getSecurityManager() {
        return securityManager;
    }
}
