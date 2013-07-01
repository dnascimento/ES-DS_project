/**
 * Teste Blackbox: funcionalidade dos serviços da CA.
 */
package pt.ist.anacom.test.security;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509CRL;
import java.util.Date;

import junit.framework.TestCase;
import pt.ist.anacom.ca.CertificationAuthority;
import pt.ist.anacom.ca.SecurityUtils;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;

public class CATestCase extends
        TestCase {


    private static CertificationAuthority CA;
    private static String CAName = "CN=CA, O=Tagus";
    private static String KEYDIR = "/tmp/keys_test/";


    private static String subjectOneName;
    private static String subjectTwoName;
    private static String subjectThreeName;
    private static X500Name subjectOneNameX;
    private static KeyPair subjectOneKeys;
    private static X500Name subjectTwoNameX;
    private static KeyPair subjectTwoKeys;
    private static X500Name subjectThreeNameX;
    private static KeyPair subjectThreeKeys;


    @Override
    public void setUp() {
        try {
            CA = new CertificationAuthority(CAName, KEYDIR);
            subjectOneNameX = new X500Name("CN=subjectOneAKAMirtilho");
            subjectTwoNameX = new X500Name("CN=subjectTwoAKAMedronho");
            subjectThreeNameX = new X500Name("CN=subjectThreeAKAMetronomo");
            subjectOneName = subjectOneNameX.getCommonName();
            subjectTwoName = subjectTwoNameX.getCommonName();
            subjectThreeName = subjectThreeNameX.getCommonName();

        } catch (IOException e) {
            fail("Erro no setUp: " + e.getMessage());
        } catch (Exception e) {
            fail("Erro no setUp: " + e.getMessage());
        }
        subjectOneKeys = SecurityUtils.generateKeyPair();
        subjectTwoKeys = SecurityUtils.generateKeyPair();
        subjectThreeKeys = SecurityUtils.generateKeyPair();
    }




    /**
     * Teste à criação de Certificados. Verificar se a CA cria os certificados de modo
     * correcto.
     */
    public void testGenerateCertificates() {
        // 1 Gerar 3 Certificados
        // 2 Lookup dos 3 Certificados

        X509CertImpl SubjectOneCAcert = null;
        X509CertImpl SubjectTwoCAcert = null;
        X509CertImpl SubjectThreeCAcert = null;

        try {
            SubjectOneCAcert = (X509CertImpl) CA.generateCertificate(subjectOneNameX, subjectOneKeys.getPublic());
            SubjectTwoCAcert = (X509CertImpl) CA.generateCertificate(subjectTwoNameX, subjectTwoKeys.getPublic());
            SubjectThreeCAcert = (X509CertImpl) CA.generateCertificate(subjectThreeNameX, subjectThreeKeys.getPublic());
        } catch (GeneralSecurityException e) {
            fail("Erro na Criação do Certificado - General Security Exception: " + e.getMessage());
        } catch (IOException e) {
            fail("Erro na Criação do Certificado - IO Exception: " + e.getMessage());
        }

        assertEquals(CA.getCertificate(subjectOneName).getIssuerDN().getName(), CAName);
        assertEquals(CA.getCertificate(subjectTwoName).getIssuerDN().getName(), CAName);
        assertEquals(CA.getCertificate(subjectThreeName).getIssuerDN().getName(), CAName);

        assertEquals(SubjectOneCAcert.getSerialNumber(), CA.getCertificate(subjectOneName).getSerialNumber());
        assertEquals(SubjectTwoCAcert.getSerialNumber(), CA.getCertificate(subjectTwoName).getSerialNumber());
        assertEquals(SubjectThreeCAcert.getSerialNumber(), CA.getCertificate(subjectThreeName).getSerialNumber());

    }

    /**
     * Os certificados emitidos pela CA expiram temporalmente?.
     */
    public void testValidateCertificate() {
        // 1 Gerar dois certificados com validades diferentes
        // 2 Lookup do Certificado
        // 3 Validade Temporal

        X509CertImpl SubjectOneCAcert = null;
        X509CertImpl SubjectTwoCAcert = null;

        try {
            CA.setCertificateExpirationTimer(30000); // - 30000 minutes expire date

            SubjectOneCAcert = (X509CertImpl) CA.generateCertificate(subjectOneNameX, subjectOneKeys.getPublic());

            CA.setCertificateExpirationTimer(1); // 1 minute expire date
            SubjectTwoCAcert = (X509CertImpl) CA.generateCertificate(subjectTwoNameX, subjectTwoKeys.getPublic());
        } catch (GeneralSecurityException e) {
            fail("Erro na Criação do Certificado - General Security Exception: " + e.getMessage());
        } catch (IOException e) {
            fail("Erro na Criação do Certificado - IO Exception: " + e.getMessage());
        }

        try {
            CA.getCertificate(subjectOneName).checkValidity();
        } catch (CertificateExpiredException e) {
            fail("The certificate expiration date has passed");
        } catch (CertificateNotYetValidException e) {
            fail("The certificate isn't valid yet: " + e.getMessage());
        }

        try {
            Thread.sleep(60000 * 1, 5); // Sleep 1:30 minutes (in miliseconds)
        } catch (InterruptedException e1) {
            fail("Sleep shouldn't fail: ");
        }

        try {
            CA.getCertificate(subjectTwoName).checkValidity();
            fail("This certification should be expired  now: ");
        } catch (CertificateExpiredException e) {
        } catch (CertificateNotYetValidException e) {
            fail("The certificate isn't valid yet: " + e.getMessage());
        }

    }

    /**
     * Se pedirmos à CA para revogar um certificado, este passa a constar na CRL e é
     * declado como revogado?.
     */
    public void testRevokeCertificate() {
        // 1 Gerar um certificado
        // 2 Revogar o Certificado
        // 3 Get da Black List
        // 4 Confirmar que se trata do mesmo Certificado

        X509CertImpl SubjectOneCAcert = null;


        try {
            SubjectOneCAcert = (X509CertImpl) CA.generateCertificate(subjectOneNameX, subjectOneKeys.getPublic());
        } catch (GeneralSecurityException e) {
            fail("Erro na Criação do Certificado - General Security Exception " + e.getMessage());
        } catch (IOException e) {
            fail("Erro na Criação do Certificado - IO Exception: " + e.getMessage());
        }

        try {
            CA.addCertificateToRevokedList(SubjectOneCAcert, new Date());
        } catch (IOException e1) {
            fail("Failed to Add the Certificate to the RevokedList: " + e1);
        }

        X509CRL CRL = null;

        try {
            CRL = CA.getX509CRL(new Date());
            assertTrue(CRL.isRevoked(SubjectOneCAcert));
        } catch (Exception e) {
            fail("Error fetching the X509CRL: " + e.getMessage());
        }
    }
}
