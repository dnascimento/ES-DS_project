package pt.ist.anacom.domain;

import pt.ist.anacom.shared.exceptions.AlreadyExistOperatorException;
import pt.ist.anacom.shared.exceptions.InvalidOperatorException;
import pt.ist.anacom.shared.exceptions.InvalidOperatorPrefixException;
import pt.ist.anacom.shared.exceptions.OperatorException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;

/** Classe que gere um cojunto de operadores. */
public class NetworkManager extends
        NetworkManager_Base {

    /**
     * Instantiates a new network manager.
     */
    public NetworkManager() {
        super();
    }

    /**
     * Cria Novo Operador se o name e prefix ainda nao existirem
     * 
     * @param name nome do operador
     * @param prefix prefixo do operador (Ex: 96 ou 92)
     * @throws OperatorException
     */
    @Override
    public void addOperator(Operator oprt) throws OperatorException, InvalidOperatorPrefixException, AlreadyExistOperatorException {
        String prefix = oprt.getPrefix();

        if (prefix.length() != 2) {
            throw new InvalidOperatorPrefixException(prefix.length());
        }

        if (existOperator(oprt.getName(), prefix)) {
            throw new AlreadyExistOperatorException(oprt.getName(), "Operator name or prefix already exists!");
        }

        super.addOperator(oprt);
    }


    /**
     * Obter o operador do número de telefone indicado.
     * 
     * @param number num. telefone completo
     * @return Operator: operador do numero, null se não existe
     * @throws InvalidOperatorException
     * @date 9/Mar/2012
     */
    public Operator getOperatorByPhoneNumber(String number) throws OperatorNotFoundException {
        String prefix = number.substring(0, 2);
        for (Operator op : super.getOperator()) {
            if (op.getPrefix().compareTo(prefix) == 0) {
                return op;
            }
        }
        throw new OperatorNotFoundException("ERROR: getOperatorByPhoneNumber: no operator to this number: " + number);
    }


    /**
     * Obter o operador do prefixo indicado.
     * 
     * @param prefix prefixo do operador
     * @return Operator: operador do numero, null se não existe
     * @throws OperatorNotFoundException o operador não foi detectado
     * @date 9/Mar/2012
     */
    public Operator getOperatorByPrefix(String prefix) throws OperatorNotFoundException {
        if (prefix.length() != 2) {
            throw new OperatorNotFoundException("ERROR: getOperatorByPrefix: no operator with this prefix");
        }
        for (Operator op : super.getOperator()) {
            if (op.getPrefix().compareTo(prefix) == 0) {
                return op;
            }
        }
        throw new OperatorNotFoundException("ERROR: getOperatorByPrefix: no operator with this prefix");
    }



    /**
     * Obter o operador por nome.
     * 
     * @param name nome do operador
     * @return Operator operador do numero, null se não existe
     * @throws InvalidOperatorException operador não é valido
     */
    public Operator getOperatorByName(String name) throws InvalidOperatorException {
        for (Operator op : super.getOperator()) {
            if (op.getName().equals(name)) {
                return op;
            }
        }
        throw new InvalidOperatorException(name, "ERROR: getOperatorByName: no operator with this name: " + name);
    }

    private boolean existOperator(String name, String prefix) {
        boolean exist = false;
        for (Operator op : super.getOperatorSet()) {
            if (op.getName().equals(name) || op.getPrefix().equals(prefix)) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    @Override
    public boolean hasOperator(Operator obj) {
        for (Operator op : super.getOperator()) {
            if (op.equals(obj)) {
                return true;
            }
        }
        return false;
    }


}
