/********
 * Projecto ES - SD - Anacom ********* Grupo ES: 03 Grupo SD: 11 **
 ********************************************/
package pt.ist.anacom.domain.network;

import pt.ist.anacom.domain.mode.OFF;



/**
 * The Class Network3G.
 */
public class Network3G extends
        Network3G_Base {

    /**
     * Instantiates a new network3 g.
     * 
     * @param number the number
     * @param balance the balance
     */
    public Network3G(String number, int balance) {
        super();
        setBalance(balance);
        setNumber(number);
        setMode(new OFF());
    }
}
