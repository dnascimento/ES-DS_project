/**
 * Teste de funcionalidade à SecurityManager. Verificar se o SM faz o que lhe é pedido.
 */
package pt.ist.anacom.test.security;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;

import junit.framework.TestCase;
import pt.ist.anacom.ca.SecuritySDManager;
import pt.ist.anacom.ca.SecurityUtils;
import pt.ist.anacom.shared.stubs.ca.CAException_Exception;


/**
 * ATENÇÃO: ESTE TESTE NECESSITA QUE UMA NOVA Certification Authority esteja DEPLOYED! Não
 * foram criados stubs para a CA de preposito, pretende-se testar a CA e o SecurityManager
 * em conjunto
 */
public class SecurityTestCase extends
        TestCase {


    String KEYDIR = "/tmp/keys/";

    private static SecuritySDManager securityManager;



    @Override
    public void setUp() {
        try {
            securityManager = new SecuritySDManager("CN=TestServer,OU=ATKSign", KEYDIR);

        } catch (Exception e) {
            fail("setUp Error:  " + e.getMessage());
        }



    }


    /**
     * Garantir que ao receber uma assinatura falsa, isto é, assinada por outro servidor,
     * se usarmos a chave pública do assinante correcto, a assinatura falsa é detectada.
     * Ex: Uma réplica adulterada, envia respostas por todos os servidores mas tem de
     * assinar com a sua privada. Ao receber, o servidor verifica a assinatura com a chave
     * publica correcta (de quem deveria ter assinado). Detecta a autenticidade das
     * mensagens.
     */
    /*
     * Metacódigo: 1 Gerar um Certificado com as Chave Privada que ficam com o Security
     * Manager 2 Gerar uma Mensagem 3 Assinar a Mensagem com a Chave Privada de
     * subjectKeysNotOnCertificate 4 Fazer a Validação da Mensagem e verificar que não
     * está certo 5 Gerar uma nova Mensagem 6 Assinar a nova Mensagem com a Chave Privada
     * de subjectKeysOnCertificate 7 Fazer a Validação da Mensagem e verificar que está
     * certo
     */
    public void testSignatureValidation() {

        KeyPair subjectKeysNotOnCertificate = SecurityUtils.generateKeyPair();

        try {
            securityManager.updateOwnCertificate();
        } catch (CertificateException e) {
            fail("Error Generating the Certificate");
        } catch (CAException_Exception e) {
            fail("Error in Certification Authority");
        } catch (IOException e) {
            fail("Error in Certification Authority");
        }

        String message = new String("SUPER MENSAGEM DE VALIDAÇÃO DE ASSINATURAS");

        byte[] messageByteArray = message.getBytes();
        byte[] signedMessage = null;

        // With the Wrong Sinature
        try {
            signedMessage = SecurityUtils.makeDigitalSignature(messageByteArray, subjectKeysNotOnCertificate.getPrivate());
        } catch (Exception e) {
            fail("Failed Signing Message");
        }

        try {
            assertFalse(SecurityUtils.verifyDigitalSignature(signedMessage, messageByteArray, securityManager.getPublicKey()));
        } catch (Exception e) {
        }


        // With the Right Signature
        try {
            signedMessage = SecurityUtils.makeDigitalSignature(messageByteArray, securityManager.getPrivateKey());
        } catch (Exception e) {
            fail("Failed Signing Message");
        }

        try {
            assertTrue(SecurityUtils.verifyDigitalSignature(signedMessage, messageByteArray, securityManager.getPublicKey()));
        } catch (Exception e) {
            fail("The signature must be verified correctly, we signed it with a correct KeyPair");
        }

    }



    /**
     * Teste para os casos da mensagem ser comprometida ou alterada
     */
    public void testDamagedMsgBody() {

        try {
            securityManager.updateOwnCertificate();
        } catch (CertificateException e) {
            fail("Error Generating the Certificate");
        } catch (CAException_Exception e) {
            fail("Error in Certification Authority");
        } catch (IOException e) {
            fail("Error in Certification Authority");
        }

        String message = new String("SUPER MENSAGEM QUE VAI SER DANIFICADA");

        byte[] messageByteArray = message.getBytes();
        byte[] signedMessage = null;



        try {
            signedMessage = SecurityUtils.makeDigitalSignature(messageByteArray, securityManager.getPrivateKey());
        } catch (Exception e) {
            fail("Failed Signing Message");
        }

        signedMessage[2] = 12;

        try {
            assertFalse(SecurityUtils.verifyDigitalSignature(signedMessage, messageByteArray, securityManager.getPublicKey()));
        } catch (Exception e) {
        }
    }


    /** Verificar se a chave é efectivamente renovada quando solicitado */
    public void testForceNewKeys() {
        try {
            PrivateKey privKey = securityManager.getPrivateKey();
            PublicKey pubKey = securityManager.getPublicKey();
            securityManager.loadServerKeys(true);
            PrivateKey newPrivKey = securityManager.getPrivateKey();
            PublicKey newPubKey = securityManager.getPublicKey();

            assertFalse("A chave privada nova deve ser distinta da antiga", privKey.equals(newPrivKey));
            assertFalse("A chave publica nova deve ser distinta da antiga", pubKey.equals(newPubKey));
        } catch (Exception e) {
            fail("Ocorreu uma excepção: " + e.getMessage());
        }
    }


    /** Testa só se o certificado é bem revogado */
    public void testRevoke() {
        X509CRL crl = securityManager.getRevokedCertificates();
        try {
            crl.verify(securityManager.getCAPubKey());
        } catch (Exception e) {
            fail("ERROR a CRL tem de estar assinada pela CA");
        }
        try {
            securityManager.checkCertificateValidation(securityManager.getServerCert());
        } catch (CertificateException e1) {
            fail("Ainda não devia estar revogado: " + e1.getMessage());
        }

        // Revogado
        try {
            securityManager.askCAtoRevokeCurrentCertificate();
        } catch (Exception e) {
            fail("ERROR revokeCurrentCertificate : " + e.getMessage());
        }
        try {
            securityManager.updateBlackList();
        } catch (Exception e) {
            fail("ERROR updateBlackList : " + e.getMessage());
        }

        crl = securityManager.getRevokedCertificates();

        try {
            crl.verify(securityManager.getCAPubKey());
        } catch (Exception e) {
            fail("ERROR a CRL tem de estar assinada pela CA");

        }
        try {
            securityManager.checkCertificateValidation(securityManager.getServerCert());
            fail("Já devia estar revogado");
        } catch (CertificateException e) {
        }
    }



    // TODO mensagem assinada por chave pública previamente revogada

    public void testMessageSignedWithRevokedCertificate() {
        try {
            securityManager.updateOwnCertificate();
        } catch (CertificateException e) {
            fail("Error Generating the Certificate");
        } catch (CAException_Exception e) {
            fail("Error in Certification Authority");
        } catch (IOException e) {
            fail("Error in Certification Authority");
        }

        String message = new String("SUPER MENSAGEM QUE VAI SER ASSINADA COM UM CERTIFICADO REVOGADO");

        byte[] messageByteArray = message.getBytes();
        byte[] signedMessage = null;

        try {
            securityManager.askCAtoRevokeCurrentCertificate();
            securityManager.updateBlackList();
        } catch (Exception e1) {
            fail("Shouldn't have a problem revoking current certificate");
        }

        try {
            signedMessage = SecurityUtils.makeDigitalSignature(messageByteArray, securityManager.getPrivateKey());
        } catch (Exception e) {
            fail("Failed Signing Message");
        }

        try {
            securityManager.checkCertificateValidation(securityManager.getServerCert());
            SecurityUtils.verifyDigitalSignature(signedMessage, messageByteArray, securityManager.getPublicKey());
            fail("Shouldn't be verified correctly");
        } catch (Exception e) {
        }
    }
}
