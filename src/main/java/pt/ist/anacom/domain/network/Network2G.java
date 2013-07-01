/********
 * Projecto ES - SD - Anacom ********* Grupo ES: 03 Grupo SD: 11 **
 ********************************************/
package pt.ist.anacom.domain.network;

import pt.ist.anacom.domain.mode.OFF;


/**
 * The Class Network2G.
 */
public class Network2G extends
        Network2G_Base {

    /**
     * Instantiates a new network2 g.
     * 
     * @param number the number
     * @param balance the balance
     */
    public Network2G(String number, int balance) {
        super();
        setBalance(balance);
        setNumber(number);
        setMode(new OFF());
    }
}
