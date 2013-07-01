package pt.ist.anacom.domain;

import java.util.ArrayList;
import java.util.List;

import pt.ist.anacom.shared.dto.phone.PhoneNumValueDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneAlreadyExistsException;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.InvalidNumberException;

/**
 * The Class Operator.
 */
public class Operator extends
        Operator_Base {
    /** Número de digitos de um telemovel da operadora. */
    private static final int NUMERO_DIGITOS = 9;



    /**
     * Instantiates a new operator.
     * 
     * @param name nome do operador
     * @param prefix prefixo de operador
     * @param pl tarifario do operador
     */
    public Operator(String name, String prefix, Plan pl) {
        super();
        this.setName(name);
        this.setPrefix(prefix);
        this.setPlan(pl);
        this.setSeqNumber(0);
    }

    /**
     * addCellPhone - regista um telemóvel no sistema no operador.
     * 
     * @param cp phone
     * @throws CellPhoneAlreadyExistsException ,InvalidNumberException,
     * @throws InvalidNumberException o numero não existe
     * @date 16/Mar/2012
     */
    @Override
    public void addCellPhone(CellPhone cp) throws CellPhoneAlreadyExistsException, InvalidNumberException {
        // faz a validaçao do CellPhone
        cellPhoneValidator(cp);

        // Já existe este numero ?
        if (hasCellPhone(cp)) {
            throw new CellPhoneAlreadyExistsException("addCellPhone: number already exits in " + this.getName(), this.getSeqNumber());
        }
        super.addCellPhone(cp);
    }


    /**
     * Verificar que o telefone pertence a este operador.
     * 
     * @param cp telefone a verificar
     * @throws InvalidNumberException número não pertencente ao operador
     */
    private void cellPhoneValidator(CellPhone cp) throws InvalidNumberException {
        final String number = cp.getNumber();
        final String nrPrefix = number.substring(0, 2);

        try {
            Integer.parseInt(number); // verifica se nao tem letras
        } catch (NumberFormatException e) {
            throw new InvalidNumberException("cellPhoneValidator: Invalid number, Not A Number", this.getSeqNumber());
        }
        if (number.length() != NUMERO_DIGITOS) {
            throw new InvalidNumberException("cellPhoneValidator: Invalid number", this.getSeqNumber());
        }

        if (!nrPrefix.equals(this.getPrefix())) {
            throw new InvalidNumberException("cellPhoneValidator: incompatible prefix with " + this.getName(), this.getSeqNumber());
        }
    }

    /**
     * Removes the cell phone with that number.
     * 
     * @param cp to delete
     * @throws CellPhoneNotExistsException the cell phone not exists exception
     * @throws InvalidNumberException the invalid number exception
     * @date 16/Mar/2012
     */
    @Override
    public void removeCellPhone(CellPhone cp) throws CellPhoneNotExistsException, InvalidNumberException {

        cellPhoneValidator(cp);
        final String number = cp.getNumber();
        final CellPhone phone = getCellPhoneByNumber(number);

        super.removeCellPhone(phone);
    }


    /**
     * Gets the cell phone by its number.
     * 
     * @param number the cell phone number to get
     * @return the cell phone with the received number
     * @throws CellPhoneNotExistsException cell phone doesn't exists at corrent operator
     */
    public CellPhone getCellPhoneByNumber(String number) throws CellPhoneNotExistsException {
        for (CellPhone c : this.getCellPhoneSet()) {
            if (c.getNumber().equals(number)) {
                return c;
            }
        }
        throw new CellPhoneNotExistsException("Operator: no mobile with this number in " + this.getName(), this.getSeqNumber());
    }

    /**
     * Checks for cell phone.
     * 
     * @param phone the phone
     * @return true if a CellPhone exists in this Operator
     */
    @Override
    public boolean hasCellPhone(CellPhone phone) {
        for (CellPhone c : this.getCellPhoneSet()) {
            if (c.getNumber().equals(phone.getNumber())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Verifica se os operadores têm mesmos parametros.
     * 
     * @param op operador a validar
     * @return true se mesmo prefixo e nome
     */
    public boolean equals(Operator op) {
        if (op.getName().equals(this.getName()) || op.getPrefix().equals(this.getPrefix())) {
            return true;
        }
        return false;
    }


    /**
     * Gets the cell phone list.
     * 
     * @return the cell phone list
     */
    public List<PhoneNumValueDTO> getCellPhoneList() {
        final List<PhoneNumValueDTO> cellDtoList = new ArrayList<PhoneNumValueDTO>();

        for (CellPhone c : this.getCellPhoneSet()) {
            cellDtoList.add(new PhoneNumValueDTO(c.getNumber(), c.getBalance()));
        }
        return cellDtoList;
    }

    /**
     * Funcao que altera o valor do bonus oferecido pelo Operador.
     * terá que ser valor positivo ou zero para ser aplicado.
     *
     * @param double (valor do novo bonus)
     * @return void
     */
    public void changeBonus(double _bonus){
    	if(_bonus >= 0)
    		this.getPlan().setBonus(_bonus);
    	
    }
    
//    /**
//     * Incrementa o numero de sequência
//     * */
//    public void incSeqNumber(){
//    	this.setSeqNumber(this.getSeqNumber()+1);
//    }
}
