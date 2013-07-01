package pt.ist.anacom.ca;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;

/**
 * Biblioteca com os métodos necessários à implementação da segurança no sistema
 * distribuido
 */
final public class SecurityUtils {

    // ########### GESTÃO DE CHAVES PRIVADAS E PUBLICAS ###############
    public static KeyPair generateKeyPair() {
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024); // CHAVE RSA-1024
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            // Excepção lançada apenas em erro de implementação, apanhamos já
            System.out.println("SecurityUtils ERROR: " + e.getMessage());
            return null;
        }
    }


    /**
     * Guardar chaves publicas em ficheiro de sistema.
     * 
     * @param key par de chaves a guardar em ficheiro
     * @param publicKeyPath onde guardar a chave publica
     * @param privateKeyPath onde guardar a chave privada
     */
    public static void saveKeys(KeyPair key, String publicKeyPath, String privateKeyPath) throws Exception {
        byte[] pubEncoded = key.getPublic().getEncoded();

        FileOutputStream fout = new FileOutputStream(publicKeyPath);
        fout.write(pubEncoded);
        fout.flush();
        fout.close();

        byte[] privEncoded = key.getPrivate().getEncoded();

        fout = new FileOutputStream(privateKeyPath);
        fout.write(privEncoded);
        fout.flush();
        fout.close();

        System.out.println("Novas chaves RSA Guardadas em disco");
    }


    /**
     * Lêr chaves de um ficheiro e transformá-las em objectos.
     * 
     * @param publicKeyPath localização da chave publica: DO FICHEIRO
     * @param privateKeyPath localização da chave privada: DO FICHEIRO
     */
    public static KeyPair loadKeys(String publicKeyPath, String privateKeyPath) throws FileNotFoundException, Exception {
        // System.out.println("Reading public key from file " + publicKeyPath + " ...");
        FileInputStream fin = new FileInputStream(publicKeyPath);
        byte[] pubEncoded = new byte[fin.available()];
        fin.read(pubEncoded);
        fin.close();

        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubEncoded);
        KeyFactory keyFacPub = KeyFactory.getInstance("RSA");

        PublicKey pub = keyFacPub.generatePublic(pubSpec);

        // System.out.println(pub);
        // System.out.println("---");
        // System.out.println("Reading private key from file " + privateKeyPath + " ...");
        //
        fin = new FileInputStream(privateKeyPath);
        byte[] privEncoded = new byte[fin.available()];
        fin.read(privEncoded);
        fin.close();

        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privEncoded);
        KeyFactory keyFacPriv = KeyFactory.getInstance("RSA");

        PrivateKey priv = keyFacPriv.generatePrivate(privSpec);

        // System.out.println(priv);
        // System.out.println("---");

        return new KeyPair(pub, priv);
    }


    // ##################### GERIR CERTIFICADOS ############################

    /**
     * Verificar se um certificado ainda é válido: não está na blacklist, ainda tem data
     * válida e a CA é que assinou o certificado
     * 
     * @param certificate - certificado a validar
     * @param blackList - lista de certificados revogados
     * @return true se válido, false se inválido
     */
    public static boolean certificateValidation(X509Certificate certificate, PublicKey caPublicKey, X509CRL blackList) throws CertificateException {
        try {
            certificate.checkValidity(new Date());
            if (blackList.isRevoked(certificate)) {
                System.out.println("Certificado Revogado!!!");
                throw new CertificateException("Certificado revogado");
            }
            certificate.verify(caPublicKey); // a chave que assinou o cert é da CA
        } catch (CertificateExpiredException e) {
            throw new CertificateException("Certificado expirou");
        } catch (CertificateNotYetValidException e) {
            throw new CertificateException("Certificado ainda não é válido");
        } catch (InvalidKeyException e) {
            throw new CertificateException("Não é possivel verificar certificado");
        } catch (NoSuchAlgorithmException e) {
            throw new CertificateException("Não é possivel verificar certificado");
        } catch (NoSuchProviderException e) {
            throw new CertificateException("Não é possivel verificar certificado");
        } catch (SignatureException e) {
            throw new CertificateException("Assinatura do certificado não é válida");
        }
        return true;
    }


    /**
     * Guardar certificado em ficheiro de sistema. IMPORTANTE: O nome do ficheiro é
     * determinado nesta lib para possibilitar a leitura de todos os certificados de um
     * directório.
     * 
     * @param certificate: certificado a guardar
     * @param directoryPath: directório onde vai guardar o ficheiro ex: /tmp/cert/ (Acabar
     *            em /)
     * @param issuerName : nome da issure associado ao certificado para identificar o
     *            certificado guardado
     * @throws CertificateEncodingException : falha ao codificar
     * @throws IOException: falha ao guardar o ficheiro
     */
    public static void saveCertificate(X509Certificate certificate, String directoryPath, String issuerName) throws CertificateEncodingException, IOException {
        // System.out.println("Writing certificate to: '" + directoryPath + issureName +
        // ".cer");
        byte[] certEncoded = certificate.getEncoded();
        FileOutputStream fout = new FileOutputStream(directoryPath + issuerName + ".cer");
        fout.write(certEncoded);
        fout.flush();
        fout.close();
    }




    public static X509Certificate loadCertificate(String filePath) throws IOException, CertificateException {
        X509Certificate cert;
        FileInputStream fin;
        fin = new FileInputStream(filePath);

        CertificateFactory cf = CertificateFactory.getInstance("X509");
        cert = (X509Certificate) cf.generateCertificate(fin);
        fin.close();
        return cert;
    }

    // ##################### GERIR CRL ############################

    /** Guardar a CRL List dinamica em disco, é guardada a dinamica e não uma X509CRL. */
    public static void saveCRL(List<X509CRLEntry> crl, String directoryPath) throws IOException {
        String filePath = directoryPath + "blacklist";
        FileOutputStream fileOut = new FileOutputStream(filePath);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(crl);
        out.close();
        fileOut.close();
    }

    /**
     * Carregar a CRL Blacklist
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static List<X509CRLEntry> loadCRL(String directoryPath) throws IOException, ClassNotFoundException {
        String filePath = directoryPath + "blacklist";
        FileInputStream fin = new FileInputStream(filePath);
        ObjectInputStream in = new ObjectInputStream(fin);
        List<X509CRLEntry> list = (List<X509CRLEntry>) in.readObject();
        in.close();
        fin.close();
        return list;
    }

    // ##################### GERIR ASSINATURAS ############################
    /**
     * Criar assinatura digital.
     * 
     * @param text - seq. binária a cifrar
     * @param privateKey - chave privada a utilizar para cifra
     * @return seq. binária cifrada que constitui a assinatura
     */
    public static byte[] makeDigitalSignature(byte[] text, PrivateKey privateKey) throws Exception {

        // get a message digest object using the MD5 algorithm
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        //
        // calculate the digest and print it out
        messageDigest.update(text);
        byte[] digest = messageDigest.digest();
        // System.out.println("\nDigest: " + writeByteArray(digest));

        //
        // get an RSA cipher object and print the provider
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        //
        // encrypt the plaintext using the private key
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] cipherDigest = cipher.doFinal(digest);
        // System.out.println("Finish encryption: ");
        // System.out.println("\nCipherDigest: " + writeByteArray(cipherDigest));

        return cipherDigest;
    }



    /**
     * Verifica a assinatura digital comparando-a com o texto limpo.
     * 
     * @param cipherDigest - seq. binária cifrada da assinatura
     * @param text - texto limpo apartir do qual foi gerada a assinatura
     * @param keyPair - chave publica do assinante
     * @return true - se a assinatura é válida, false se a assinatura é inválida
     */
    public static boolean verifyDigitalSignature(byte[] cipherDigest, byte[] text, PublicKey publicKey) {

        try {

            // get a message digest object using the MD5 algorithm
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");


            // calculate the digest and print it out
            messageDigest.update(text);
            byte[] digest = messageDigest.digest();
            // System.out.println("\nNew digest: " + writeByteArray(digest));

            //
            // get an RSA cipher object and print the provider
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

            //
            // decrypt the ciphered digest using the public key
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] decipheredDigest = cipher.doFinal(cipherDigest);
            // System.out.println("Finish decryption: ");
            // System.out.println("\nDecipheredDigest: " +
            // writeByteArray(decipheredDigest));

            //
            // compare digests
            if (digest.length != decipheredDigest.length) {
                return false;
            }

            for (int i = 0; i < digest.length; i++) {
                if (digest[i] != decipheredDigest[i]) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }

    }






    // ##################### UTILS ############################



    /** Writes a byte array in hexadecimal text format. */
    public static String writeByteArray(byte[] data) {
        int loc;
        int end;

        StringBuffer sb = new StringBuffer();
        int len = data.length;
        int off = 0;

        // Print a hexadecimal dump of 'data[off...off+len-1]'
        if (off >= data.length)
            off = data.length;

        end = off + len;
        if (end >= data.length)
            end = data.length;

        len = end - off;
        if (len <= 0)
            return null;

        loc = (off / 0x10) * 0x10;

        for (int i = loc; i < len; i += 0x10, loc += 0x10) {
            int j = 0;

            for (; j < 0x10 && i + j < end; j++) {
                int ch;
                int d;

                if (j == 0x08)
                    sb.append(' ');

                ch = data[i + j] & 0xFF;

                d = (ch >>> 4);
                d = (d < 0xA ? d + '0' : d - 0xA + 'A');
                sb.append((char) d);

                d = (ch & 0x0F);
                d = (d < 0xA ? d + '0' : d - 0xA + 'A');
                sb.append((char) d);

                sb.append(' ');
            }
        }
        return "[ " + sb.toString() + "], length=" + sb.length() / 3;
    }


}
