package pt.ist.anacom.ca;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import pt.ist.anacom.shared.stubs.ca.CAException_Exception;
import pt.ist.anacom.shared.stubs.ca.CAPortType;
import pt.ist.anacom.shared.stubs.ca.CAService;
import sun.security.x509.X500Name;


/** Classe de gestão de chaves e certificados de um servidor. */
public class SecuritySDManager
        implements Runnable {


    /** Tempo de actualização da blacklist em segundos. */
    private static long _updateTime = 5;

    /** _issuer - the name of the CA. */
    private X500Name _issuer;

    private X509CRL _revokedCertificates;

    private PrivateKey _privateKey;
    private PublicKey _publicKey;

    /** Directorio da pasta onde guardar as chaves ex: /tmp/keys/. */
    private String _keysFolderPath;


    private X509Certificate _CACert;

    private X509Certificate _serverCert;

    private CAPortType _CA;


    public SecuritySDManager(String caName, String keyFolderPath) throws Exception {

        setIssuer(new X500Name(caName));
        setKeysFolderPath(keyFolderPath);
        CAService serv = new CAService();
        setCa(serv.getCAApplicationServicePort());


        // carregar as chaves do servidor
        loadServerKeys(false);

        // Carregar o certificado da CA da memória
        loadCACert();

        // Registar-se na CA (assinar o certificado)
        updateOwnCertificate();

        // Obter blacklist
        updateBlackList();

        // Lançar thread para update da blacklist
        // TODO NÃO ESQUECER DE LIGAR O AUTO-UPDATE DE CERTIFICADOS
        Thread thread = new Thread(this, "BlacklistUpdater");
        thread.start();
    }

    /*
     * Constructor para a CA
     */
    public SecuritySDManager(String keyFolderPath) throws Exception {
        setIssuer(new X500Name("CA"));
        setKeysFolderPath(keyFolderPath);
        CAService serv = new CAService();
        setCa(serv.getCAApplicationServicePort());
    }


    /**
     * Método que corre em thread indepente e que de timmer em timmer segundos actualiza a
     * blacklist.
     */
    @Override
    public void run() {
        try {
            // System.out.println("Update blackList");
            while (true) {
                this.updateBlackList();
                Thread.currentThread().sleep(getUpdateTime() * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }


    // ---------------------------------------------------------------------------------------------------
    // GERIR CHAVES DO SERVIDOR
    // ---------------------------------------------------------------------------------------------------
    /**
     * Gera chaves publicas e privada do Servidor, guarda num ficheiro e carrega essas
     * chaves de seguida.
     * 
     * @throws Exception error saving keys
     */
    public void generateKeys() throws Exception {
        KeyPair keys = SecurityUtils.generateKeyPair();
        String keysFolderPath = getKeysFolderPath();
        // guardar chave
        String fileName = getIssuer().getCommonName() + "_rsa";
        SecurityUtils.saveKeys(keys, keysFolderPath + fileName + ".pub", keysFolderPath + fileName + ".priv");
        loadServerKeys(false); // Carrega as Chaves Geradas
    }

    /**
     * Carregar as chaves existentes em ficheiro, se não existirem, cria-as.
     * 
     * @param forceNewKeys: obrigar a gerar novas e substituir chaves existentes
     */
    public void loadServerKeys(boolean forceNewKeys) throws Exception {
        if (forceNewKeys) {
            generateKeys();
        }
        KeyPair keyPair;
        String keysFolderPath = getKeysFolderPath();
        try {
            String fileName = getIssuer().getCommonName() + "_rsa";
            keyPair = SecurityUtils.loadKeys(keysFolderPath + fileName + ".pub", keysFolderPath + fileName + ".priv");
            this.setPrivateKey(keyPair.getPrivate());
            this.setPublicKey(keyPair.getPublic());
        } catch (FileNotFoundException e) {
            // o ficheiro não existe, criar e depois voltará a invocar este método
            generateKeys();
        }
    }


    public PublicKey getCAPubKey() {
        return getCACert().getPublicKey();
    }

    // ---------------------------------------------------------------------------------------------------
    // GERIR Certificados
    // ---------------------------------------------------------------------------------------------------
    /** Carregar o certificado da CA que já vem por omissão em disco, já é pré-conhecido */
    private void loadCACert() throws CertificateException, IOException {
        setCACert(SecurityUtils.loadCertificate(getKeysFolderPath() + "CA.cer"));
    }


    public void updateBlackList() throws Exception {
        String data = getCa().getX509CRL();
        X509CRL crl = SecurityDataConverter.textToCRL(data);
        crl.verify(getCAPubKey()); // verificar assinatura da CA
        setRevokedCertificates(crl);
    }


    /**
     * Registar a chave publica actual do servidor na CA e obter o certificado
     * correspondente.
     */
    public void updateOwnCertificate() throws CAException_Exception, CertificateException, IOException {
        String pk = SecurityDataConverter.keyToText(getPublicKey());
        String certString = getCa().generateCertificate(getIssuer().toString(), pk);
        X509Certificate certificado = SecurityDataConverter.TextToCertificate(certString);
        setServerCert(certificado);

        // System.out.println(certificado.getSerialNumber());
    }

    /**
     * Verificiar se o certificado está válido
     * 
     * @throws CertificateException
     */
    public boolean checkCertificateValidation(X509Certificate cert) throws CertificateException {
        SecurityUtils.certificateValidation(cert, getCAPubKey(), getRevokedCertificates());
        return true;
    }

    /**
     * A nossa chave privada foi comprometida, o hacker pode utilizá-la para assinar
     * coisas nossas. 1º Pedir à CA para revogar o nosso certificado 2º Gerar novas chaves
     * no servidor 3º Registar as chaves navas na CA 4º Actualizar o nosso certificado
     * 
     * @throws Exception
     */
    public void revokeCurrentCertificate() throws Exception {
        askCAtoRevokeCurrentCertificate();
        this.loadServerKeys(true);
        this.updateOwnCertificate();
        this.updateBlackList();
    }

    /** Sub-metodo de revokeCurrentCertificate() - por causa de testes */
    public void askCAtoRevokeCurrentCertificate() throws Exception {
        String certEncoded = SecurityDataConverter.certificateToText(getServerCert());
        GregorianCalendar c = new GregorianCalendar();
        XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        getCa().addCertificateToRevokedList(certEncoded, date);
    }

    // ---------------------------------------------------------------------------------------------------
    // Gets e Sets
    // ---------------------------------------------------------------------------------------------------
    /**
     * @return the issuer
     */
    public X500Name getIssuer() {
        return _issuer;
    }



    /**
     * @param issuer the issuer to set
     */
    public void setIssuer(X500Name issuer) {
        this._issuer = issuer;
    }






    public PrivateKey getPrivateKey() {
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




    public CAPortType getCa() {
        return _CA;
    }




    private void setCa(CAPortType ca) {
        this._CA = ca;
    }




    /**
     * @return the revokedCertificates
     */
    public X509CRL getRevokedCertificates() {
        return _revokedCertificates;
    }




    /**
     * @param revokedCertificates the revokedCertificates to set
     */
    public void setRevokedCertificates(X509CRL revokedCertificates) {
        this._revokedCertificates = revokedCertificates;
    }




    /**
     * @return the serverCert
     */
    public X509Certificate getServerCert() {
        return _serverCert;
    }




    /**
     * @param serverCert the serverCert to set
     */
    public void setServerCert(X509Certificate serverCert) {
        this._serverCert = serverCert;
    }

    /**
     * @return the updateTime
     */
    public static long getUpdateTime() {
        return _updateTime;
    }

    /**
     * @param updateTime the updateTime to set
     */
    public static void setUpdateTime(long updateTime) {
        SecuritySDManager._updateTime = updateTime;
    }
}
