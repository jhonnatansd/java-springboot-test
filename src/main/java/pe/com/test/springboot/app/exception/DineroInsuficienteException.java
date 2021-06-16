package pe.com.test.springboot.app.exception;

public class DineroInsuficienteException extends RuntimeException {
	
	public DineroInsuficienteException(String mensaje) {
		super(mensaje);
	}
	
}
