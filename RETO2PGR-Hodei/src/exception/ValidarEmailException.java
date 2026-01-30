package exception;

public class ValidarEmailException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidarEmailException(String mensaje) {
        super(mensaje);
    }
}
