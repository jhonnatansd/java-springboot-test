package pe.com.test.springboot.app;

import java.math.BigDecimal;
import java.util.Optional;

import pe.com.test.springboot.app.model.Banco;
import pe.com.test.springboot.app.model.Cuenta;

public class Datos {
	
	public static final Cuenta CUENTA_001 = new Cuenta(1L, "Andrés", new BigDecimal("1000"));
	public static final Cuenta CUENTA_002 = new Cuenta(2L, "Jhon", new BigDecimal("2000"));
	public static final Banco BANCO = new Banco(1L, "El Banco Financiero", 0);
	
	public static Optional<Cuenta> crearCuenta001() {
		return Optional.of(new Cuenta(1L, "Andrés", new BigDecimal("1000")));
	}
	
	public static Optional<Cuenta> crearCuenta002() {
		return Optional.of(new Cuenta(2L, "Jhon", new BigDecimal("2000")));
	}
	
	public static Optional<Banco> crearBanco() {
		return Optional.of(new Banco(1L, "El Banco Financiero", 0));
	}
}
