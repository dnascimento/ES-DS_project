package pt.ist.anacom.shared.enumerados;

/**
 * Classe Enum para diferenciar o tipo de comunicação
 */
public enum CommunicationType {
    SENDSMS, RECEIVESMS, SENDVOICE, RECEIVEVOICE, SENDVIDEO, RECEIVEVIDEO;

    public String value() {
        return name();
    }

    public static CommunicationType fromString(String v) {
        return valueOf(v);
    }
}
