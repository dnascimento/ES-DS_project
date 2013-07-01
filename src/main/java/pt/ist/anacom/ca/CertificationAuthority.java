package pt.ist.anacom.ca;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CRLEntryImpl;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;



/**
 * This Class contains the Certification Authority Logic. - As chaves e certificado
 * próprios e a blacklist são percistentes em disco
 */
@SuppressWarnings(value = { "all" })
public class CertificationAuthority {

    /**
     * _issuer - the name of the CA
     */
    private X500Name _issuer;

    /**
     * The ArrayList of revokedCertificates
     */
    private List<X509CRLEntry> _revokedCertificates;


    /**
     * The HashMap of approved Certificates String - This string is the subjectName - the
     * X.509 Distinguished Name, eg "CN=Test, L=London, C=GB" X509CertImpl - The
     * Certificate the corresponds to the subjectName
     */
    private HashMap<String, X509CertImpl> _approvedCertificates;


    private X509Certificate _CACert;


    long _CRLExpirationTimer = 30; // in minutes
    long _CertificateExpirationTimer = 30; // in minutes

    /** Chave privada do servidor. */
    private PrivateKey _privateKey;

    /** Chave publica do servidor */
    private PublicKey _publicKey;


    /** Directório onde são guardadas as chaves do servidor */
    private String _keysFolderPath;






    /**
     * CertificationAuthority Constructor
     * 
     * @param caName nome da CA: exemplo: O=TagusSign. Em que: CN->commonName; O
     *            organizationName; OU organizationalUnitName; C countryName
     * @throws Exception
     */
    public CertificationAuthority(String caName, String keysFolderPath) throws Exception {
        setIssuer(new X500Name(caName));
        setKeysFolderPath(keysFolderPath);
        loadRevokedList();
        setApprovedCertificates(new HashMap<String, X509CertImpl>());
        loadCAKeys();
    }


    // ---------------------------------------------------------------------------------------------------
    // GERIR CHAVES
    // ---------------------------------------------------------------------------------------------------
    /**
     * Gera chaves publicas e privada da CA, guarda num ficheiro e carrega essas chaves de
     * seguida
     * 
     * @throws Exception error saving keys
     */
    public void generateCAKeys() throws Exception {
        KeyPair keys = SecurityUtils.generateKeyPair();
        String keysFolderPath = getKeysFolderPath();
        // guardar chave
        String fileName = getIssuer().getCommonName() + "_rsa";
        SecurityUtils.saveKeys(keys, keysFolderPath + getIssuer().getCommonName() + ".pub", keysFolderPath + getIssuer().getCommonName() + ".priv");
        loadCAKeys(); // Carrega as Chaves Geradas
        loadCACertificate();
    }

    /** Carregar as chaves existentes em ficheiro, se não existirem, cria-as */
    public void loadCAKeys() throws Exception {
        KeyPair keyPair;
        String keysFolderPath = getKeysFolderPath();
        try {
            String fileName = getIssuer().getCommonName() + "_rsa";
            keyPair = SecurityUtils.loadKeys(keysFolderPath + getIssuer().getCommonName() + ".pub", keysFolderPath + getIssuer().getCommonName() + ".priv");
            this.setPrivateKey(keyPair.getPrivate());
            this.setPublicKey(keyPair.getPublic());
        } catch (FileNotFoundException e) {
            // o ficheiro não existe, criar e depois voltará a invocar este método
            generateCAKeys();
        }
    }



    // ---------------------------------------------------------------------------------------------------
    //
    // GERIR CERTIFICADOS
    //
    // ---------------------------------------------------------------------------------------------------
    /**
     * Gerar certificado da CA e guardar para distribuição.
     * 
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public void loadCACertificate() throws IOException, GeneralSecurityException {
        try {
            setCACert(SecurityUtils.loadCertificate(getKeysFolderPath() + getIssuer().getCommonName() + ".cer"));
        } catch (Exception e) {
            createCACertificate();
        }
    }

    public void createCACertificate() throws IOException, GeneralSecurityException {
        X509Certificate cert = generateCertificate(getIssuer(), getPublicKey());
        // guardar em disco o certificado gerado pela CA
        SecurityUtils.saveCertificate(cert, getKeysFolderPath(), getIssuer().getCommonName());
        loadCACertificate();
    }


    /**
     * Create a self-signed X.509 Certificate.
     * 
     * @param subjectName the X.509 Distinguished Name, eg "CN=Test, L=London, C=GB"
     * @param pair the KeyPair
     * @param minutes how many days from now the Certificate is valid for
     * @param algorithm the signing algorithm, eg "SHA1withRSA" or "md5WithRSA"
     */
    public X509Certificate generateCertificate(X500Name owner, PublicKey publicKey) throws GeneralSecurityException, IOException {
        X509CertInfo info = new X509CertInfo(); // 1 segundo = 1000 | 1 minuto = 60
        Date from = new Date(); // data actual
        // Durante quantos minutos e que o certificado e x valido
        Date to = new Date(from.getTime() + getCertificateExpirationTimer() * 60000l);
        // Validade do certificado
        CertificateValidity interval = new CertificateValidity(from, to);

        // Número serie do certificado
        BigInteger serialnumber = new BigInteger(64, new SecureRandom());


        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(serialnumber));
        info.set(X509CertInfo.ISSUER, new CertificateIssuerName(_issuer));
        info.set(X509CertInfo.VALIDITY, interval);

        info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(owner));
        info.set(X509CertInfo.KEY, new CertificateX509Key(publicKey));

        // By default, the encription algorithm is md5WithRSA
        String algorithm = new String("md5WithRSA");

        AlgorithmId algorithmID = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid); // MD5+RSA
        info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algorithmID));

        // Sign the cert to identify the algorithm that's used.
        X509CertImpl cert = new X509CertImpl(info);
        cert.sign(getPrivateKey(), algorithm);

        // Update the algorith, and resign.
        algorithmID = (AlgorithmId) cert.get(X509CertImpl.SIG_ALG);
        info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algorithmID);
        cert = new X509CertImpl(info);


        cert.sign(getPrivateKey(), algorithm);
        _approvedCertificates.put(owner.getCommonName(), cert); // Add the certificate to
                                                                // the approved hashmap
        return cert;
    }



    /**
     * getCertificate method.
     * 
     * @param subjectName
     * @return
     */
    public X509Certificate getCertificate(String subjectName) {
        return _approvedCertificates.get(subjectName);
    }




    // ---------------------------------------------------------------------------------------------------
    //
    // GERIR CERTIFICADOS Revogados
    //
    // ---------------------------------------------------------------------------------------------------
    /**
     * . This method returns the X509CRL ready to be sent to whom it may concern
     * thisUpdate - the Date of this issue. nextUpdate - the Date of the next CRL.
     * 
     * @param thisDate : data actual
     * @throws CRLException
     * @throws GeneralSecurityException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public X509CRL getX509CRL(Date thisDate) throws CRLException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, GeneralSecurityException {
        Date nextDate = new Date(thisDate.getTime() + getCRLExpirationTimer() * 60000l);
        X509CRLImpl crl = new X509CRLImpl(_issuer, thisDate, nextDate, (X509CRLEntry[]) getRevokedCertificates().toArray(new X509CRLEntry[] {}));
        // assinar a CRL para garantir integridade e autentiticidade
        crl.sign(getPrivateKey(), "MD5withRSA");
        return crl;
    }



    /**
     * Carregar a lista de certificados revogados apartir do disco
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void loadRevokedList() {
        // try {
        // List<X509CRLEntry> crl = SecurityUtils.loadCRL(getKeysFolderPath());
        // setRevokedCertificates(crl);
        // } catch (IOException e) {
        // // Originou erro porque o ficheiro não existe, criar um novo
        // setRevokedCertificates(new ArrayList<X509CRLEntry>());
        // } catch (ClassNotFoundException e) {
        setRevokedCertificates(new ArrayList<X509CRLEntry>());
        // }
    }

    /**
     * Adds a Certificate to revoked list
     * 
     * @param certificate the certificate that was revoked
     * @param revokedDate the date that the certificate was revoked
     * @throws IOException
     */
    public void addCertificateToRevokedList(X509CertImpl certificate, Date revokedDate) throws IOException {
        getRevokedCertificates().add(new X509CRLEntryImpl(certificate.getSerialNumber(), revokedDate));
        // SecurityUtils.saveCRL(getRevokedCertificates(), getKeysFolderPath());
    }








    public long getCRLExpirationTimer() {
        return _CRLExpirationTimer;
    }



    public void setCRLExpirationTimer(long cRLExpirationTimer) {
        _CRLExpirationTimer = cRLExpirationTimer;
    }



    public long getCertificateExpirationTimer() {
        return _CertificateExpirationTimer;
    }



    public void setCertificateExpirationTimer(long certificateExpirationTimer) {
        _CertificateExpirationTimer = certificateExpirationTimer;
    }





    private PrivateKey getPrivateKey() {
        return _privateKey;
    }


    private void setPrivateKey(PrivateKey privateKey) {
        this._privateKey = privateKey;
    }


    public PublicKey getPublicKey() {
        return _publicKey;
    }


    private void setPublicKey(PublicKey publicKey) {
        this._publicKey = publicKey;
    }


    public X500Name getIssuer() {
        return _issuer;
    }


    private void setIssuer(X500Name issuer) {
        this._issuer = issuer;
    }


    public List<X509CRLEntry> getRevokedCertificates() {
        return _revokedCertificates;
    }


    private void setRevokedCertificates(List<X509CRLEntry> revokedCertificates) {
        this._revokedCertificates = revokedCertificates;
    }


    public HashMap<String, X509CertImpl> getApprovedCertificates() {
        return _approvedCertificates;
    }


    private void setApprovedCertificates(HashMap<String, X509CertImpl> approvedCertificates) {
        this._approvedCertificates = approvedCertificates;
    }


    private String getKeysFolderPath() {
        return _keysFolderPath;
    }


    private void setKeysFolderPath(String keysFolderPath) {
        this._keysFolderPath = keysFolderPath;
    }


    private X509Certificate getCACert() {
        return _CACert;
    }


    private void setCACert(X509Certificate cACert) {
        _CACert = cACert;
    }
}
