package pt.ist.anacom.shared.exceptions;

/**
 * @author Camels
 * Excepção mandada quando existe mais de uma falha 
 * nos servidores
 *
 */
public class ReplicateException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReplicateException(){
		
	}
	
	public ReplicateException(String msg) {
		super(msg);
	}


}
