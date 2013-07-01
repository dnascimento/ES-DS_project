package pt.ist.anacom.ca;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import pt.ist.anacom.shared.stubs.ca.CAException_Exception;
import pt.ist.anacom.shared.stubs.ca.CAPortType;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;

/**
 * Classe de interface de webservice da Certification Autorithy. Aqui é realizado a
 * codificação e descodificação para o formato de texto.
 */
@javax.jws.WebService(endpointInterface = "pt.ist.anacom.shared.stubs.ca.CAPortType", wsdlLocation = "/ca.wsdl", name = "CAPortType", portName = "CAApplicationServicePort", targetNamespace = "http://ca", serviceName = "CAService")
public class CertificationAuthorityWebService
        implements CAPortType {

    private static CertificationAuthority _CA;


    /**
     * iniciar o servidor da CA.
     * 
     * @param caName nome da CA: exemplo: O=TagusSign. Em que: CN->commonName; O
     *            organizationName; OU organizationalUnitName; C countryName
     * @param forceGenerateNewKeys true se quiseremos MESMO que ele gere novas chaves (se
     *            não as encontrar, gera na mesma).
     * @param keysFolderPath directorio onde guardar as novas chaves
     */
    public static void init(String caName, String keysFolderPath) throws Exception {
        CertificationAuthorityWebService.setCA(new CertificationAuthority(caName, keysFolderPath));
    }

    @Override
    public String getX509CRL() throws CAException_Exception {
        // System.out.println("PEDIDO DE GET CRL");
        try {
            X509CRL crl = getCA().getX509CRL(new Date());
            String data = SecurityDataConverter.crlToText(crl);
            // System.out.println(data);
            return data;
        } catch (Exception e) {
            throw new CAException_Exception(e.getMessage());
        }
    }


    /**
     * @param serverX500Name ex: CN=Vodafone OU=Tagus
     */
    @Override
    public String generateCertificate(String serverX500Name, String publicKey) throws CAException_Exception {
        X509Certificate cert;
        try {
            X500Name serverName = new X500Name(serverX500Name);
            PublicKey key = SecurityDataConverter.textToPublicKey(publicKey);
            cert = getCA().generateCertificate(serverName, key);
            return SecurityDataConverter.certificateToText(cert);
        } catch (GeneralSecurityException e) {
            throw new CAException_Exception(e.getMessage());
        } catch (IOException e) {
            throw new CAException_Exception(e.getMessage());
        }
    }






    @Override
    public void addCertificateToRevokedList(String certificate, XMLGregorianCalendar date) throws CAException_Exception {
        try {
            Date revokedDate = date.toGregorianCalendar().getTime();
            X509CertImpl cert = SecurityDataConverter.TextToCertificate(certificate);
            getCA().addCertificateToRevokedList(cert, revokedDate);
        } catch (CertificateException e) {
            throw new CAException_Exception(e.getMessage());
        } catch (IOException e) {
            throw new CAException_Exception(e.getMessage());
        }
    }

    @Override
    public String getCertificate(String serverName) throws CAException_Exception {
        try {
            X509Certificate cert = getCA().getCertificate(serverName);
            return SecurityDataConverter.certificateToText(cert);
        } catch (CertificateEncodingException e) {
            throw new CAException_Exception(e.getMessage());
        }
    }




    private static CertificationAuthority getCA() {
        return _CA;
    }

    private static void setCA(CertificationAuthority cA) {
        _CA = cA;
    }
}
