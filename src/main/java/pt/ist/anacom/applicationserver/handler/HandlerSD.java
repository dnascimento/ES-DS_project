package pt.ist.anacom.applicationserver.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
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
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Handler do Servidor de Operador Todas as mensagens enviadas são: - Assinadas: gerar
 * assinatura com a PrivKey do servidor - TimeStamped: adicionar um timestamp à mensagem -
 * Nome do servidor para depois ir buscar o certificado Todas as recebidas: - Ir à lista
 * de certificados actual, validar o certificado com a chave da CA, - Sacar a chave
 * publica do sender no certificado - Validar a assinatura com essa chave publica -
 * Verificar o timestamp.
 */
public class HandlerSD
        implements SOAPHandler<SOAPMessageContext> {

    // Strings constantes:
    String TNS = "http://anacom";
    String PREFIX = "bn";
    String SIGN = "sign";
    String CERT = "cert";
    String EXCEPTION_PREFIX = "tns";

    /** O security Manager já foi inicializado? Utilizado em nos ApplicationServer */
    private static boolean _securityManagerInitialized = false;

    /**
     * O gestor de Segurança é a classe/módulo que dentro do servidor é responsável por
     * tratar das chaves, certificados, etc.
     */
    private static SecuritySDManager securityManager;



    /**
     * Criar apenas o gestor de segurança do SERVIDOR, que utiliza handlerschain. Depois
     * inicializar em runtime.
     */

    public HandlerSD() {
        // System.out.println("Handler de servidor construido");
    }


    /**
     * Iniciar o gestor de segurança do SERVIDOR DE APRESENTAÇÃO/CLIENTES.
     * 
     * @param serverName nome do servidor (Formato: "CN=ApplicationServer,OU=Tagus")
     * @throws Exception the exception /** Os clientes de WS instânciam directamente a
     *             classe de server handler, utilizando este construtor para passar o nome
     */
    public HandlerSD(String serverName) throws Exception {
        initializeSecurityManager(serverName);
        // System.out.println("HandlerINIT do cliente: " + serverName);
    }


    /**
     * Inicializar o security manager.
     * 
     * @throws Exception
     */
    public void initializeServerHandler() throws Exception {
        String serverName = ApplicationServerInitListener.getServerSecurityName();
        initializeSecurityManager(serverName);
        // System.out.println("HandlerINIT do servidor: " + serverName);
    }


    /** Iniciar o securityManager. */
    private void initializeSecurityManager(String serverName) throws Exception {
        String keysFolderPath = "/tmp/keys/";
        setSecurityManager(new SecuritySDManager(serverName, keysFolderPath));
        _securityManagerInitialized = true;
    }


    @Override
    public void close(MessageContext messageContext) {
        return;
    }


    @Override
    public Set getHeaders() {
        return null;
    }

    /**
     * Tratamento das mensagens SOAP caso estas sejam referentes a excepções.
     */
    @Override
    public boolean handleFault(SOAPMessageContext context) {
        handleMessage(context);
        return true;
    }



    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        try {
            if (!_securityManagerInitialized) {
                // se for handler de servidor, inicializar agora
                initializeServerHandler();
            }
            if (outbound) {
                // System.out.println("|-------------------------------- OUTclient  --------------------------------------------|");
                outboundMessage(context);
            } else {
                // System.out.println("|-------------------------------- INclient  ---------------------------------------------|");
                inboundMessage(context);

                // System.out.println("lista certificados revogados: "+securityManager.getRevokedCertificates());

            }
        } catch (RuntimeException e) {
            throw new WebServiceException("SecurityFault - " + e.getMessage());
        } catch (Exception w) {
            System.out.println("handleMessage Client (general) Exception " + w.getMessage());
            throw new WebServiceException("SecurityFault - de outro tipo que não ataques, ex: certificado expirado, ect...");
        }
        return true;
    }


    /*---------------------------------------------------------------------------------------------
     *                           Enviar mensagem 
     *------------------------------------------------------------------------------------------- */
    /**
     * Todas as mensagens: 1) Adiciona-se timestamp para replicação 2) Adiciona-se
     * certificado do sender no header 3) Assina a mensagem (e coloca no header) 4)
     * chutá-la para a nuvem....
     * 
     * @param context contexto da mensagem
     * @return true se a mensagem pode ser enviada
     * @throws Exception
     */
    public final boolean outboundMessage(SOAPMessageContext context) throws Exception {

        SOAPMessage message = context.getMessage();

        // Verifica se o nosso certificado continua válido ou não
        X509Certificate cert = getSecurityManager().getServerCert();
        try {
            getSecurityManager().checkCertificateValidation(cert);
        } catch (CertificateException e) {
            System.out.println("O certificado no envio, esta revogado, vou gerar novo certificado");
            getSecurityManager().revokeCurrentCertificate();
            cert = getSecurityManager().getServerCert();
        }
        putCertHeader(context, cert);
        signMessage(message);
        return true;
    }



    /**
     * Adiciona certificado do sender ao header da mensagem.
     * 
     * @param context the context
     * @param serverCert the server cert
     */
    private void putCertHeader(SOAPMessageContext context, X509Certificate serverCert) {
        try {

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


            element.addTextNode(SecurityDataConverter.certificateToText(serverCert));
        } catch (CertificateEncodingException e) {
            System.out.println("ClientHandler putCertHeader exception");
            e.printStackTrace();
        } catch (SOAPException e) {
            System.out.println("ClientHandler putCertHeader exception");
            e.printStackTrace();
        }
    }




    /**
     * /** Assinar toda a mensagem SOAP
     * 
     * @param message the message
     * @throws Exception the exception
     */
    public void signMessage(SOAPMessage message) throws Exception {
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
    }



    /*---------------------------------------------------------------------------------------------
     *                           Receber mensagem 
     *------------------------------------------------------------------------------------------- */
    /**
     * @param context the context
     * @throws CertificateException
     * @throws IOException
     * @throws SOAPException
     */
    public void inboundMessage(SOAPMessageContext context) throws SOAPException, IOException {
        SOAPMessage message = context.getMessage();
        try {
            // retira certificado à mensagem
            X509Certificate cert = getCertificate(message);

            // valida certificado da mensagem
            getSecurityManager().updateBlackList();
            getSecurityManager().checkCertificateValidation(cert);
            // verifica a assinatura
            verifySign(context, cert);
        } catch (CertificateException e) {
            System.out.println("-----> CERTIFICATE ERROR:");
            throw new WebServiceException("ClientHandler - Certificate not valid " + e.getMessage());
        } catch (Exception e) {
            System.out.println("-----> CERTIFICATE ERROR:");
        }
    }


    /**
     * Gets the certificate.
     * 
     * @param message the message
     * @return the certificate
     * @throws SOAPException the sOAP exception
     * @throws CertificateException the certificate exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
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
        return null;
    }


    /*
     * Retira a assinatura do header e verifica-a!
     */
    /**
     * Verify sign.
     * 
     * @param message the message
     * @param sourceCertificate the source certificate
     * @return true, if successful
     */
    public void verifySign(SOAPMessageContext context, X509Certificate sourceCertificate) {
        try {
            SOAPMessage message = context.getMessage();
            SOAPPart soapPart = message.getSOAPPart();
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
            SOAPHeader soapHeader = soapEnvelope.getHeader();

            // Obter a assinatura codificada
            if (soapHeader == null) {
                throw new WebServiceException("ClientHandler-verifySign");
            }


            // Obtém content da assinatura
            Iterator<Node> it = soapHeader.getChildElements();
            Node headerNode = null;
            while (it.hasNext()) {
                headerNode = it.next();

                if (headerNode.getNodeName().equals(PREFIX + ":" + SIGN)) {
                    break;
                }
            }

            if (headerNode == null) {
                throw new WebServiceException("ClientHandler - verifySign");

            }

            String encodedSign = headerNode.getTextContent();

            // Conveter para byte array
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] decoded = decoder.decodeBuffer(encodedSign);

            // Validar a assinatura
            // Retirar o header com assinatura
            soapHeader.removeChild(headerNode);

            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            message.writeTo(byteOut);
            byte[] data = byteOut.toByteArray();


            // verifica assinatura
            if (SecurityUtils.verifyDigitalSignature(decoded, data, sourceCertificate.getPublicKey()) == false) {
                throw new WebServiceException("ClientHandler - verifySign: Assinatura não válida");
            }

        } catch (javax.xml.soap.SOAPException e) {
            throw new WebServiceException("ClientHandler - SOAPMessage error " + e.getMessage());
        } catch (java.io.IOException w) {
            throw new WebServiceException("ClientHandler - decodeBuffer() error " + w.getMessage());
        } catch (java.lang.Exception h) {
            throw new WebServiceException("ClientHandler - verifyDigitalSignature error " + h.getMessage());
        }

    }


    /**
     * Gets the security manager.
     * 
     * @return the securityManager
     */
    public static SecuritySDManager getSecurityManager() {
        return securityManager;
    }


    /**
     * Sets the security manager.
     * 
     * @param securityManager the securityManager to set
     */
    public static void setSecurityManager(SecuritySDManager securityManager) {
        HandlerSD.securityManager = securityManager;
    }

}
