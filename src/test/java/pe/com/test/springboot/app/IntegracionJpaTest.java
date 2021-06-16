package pe.com.test.springboot.app;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import pe.com.test.springboot.app.model.Cuenta;
import pe.com.test.springboot.app.repository.CuentaRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IntegracionJpaTest {

	@Autowired
	CuentaRepository cuentaRepository;
	
	@Test
	void testFindById() {
		Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
		assertTrue(cuenta.isPresent());
		assertEquals("Andrés", cuenta.orElseThrow().getPersona());
	}
	
	@Test
	void testFindByPerson() {
		Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Andrés");
		assertTrue(cuenta.isPresent());
		assertEquals("Andrés", cuenta.orElseThrow().getPersona());
		assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
	}
	
	@Test
	void testFindByPersonThrowException() {
		Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Andrés2");
		assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
	}
	
	@Test
	void testFindAll() {
		List<Cuenta> cuentas = cuentaRepository.findAll();
		assertFalse(cuentas.isEmpty());
		assertEquals(2, cuentas.size());
	}
	
	@Test
	void testSave() {
		Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		
		Cuenta cuenta = cuentaRepository.save(cuentaPepe);
		
		assertEquals("Pepe", cuenta.getPersona());
		assertEquals("3000", cuenta.getSaldo().toPlainString());
		//assertEquals(3, cuenta.getId());
	}
	
	@Test
	void testUpdate() {
		Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		
		Cuenta cuenta = cuentaRepository.save(cuentaPepe);
		
		assertEquals("Pepe", cuenta.getPersona());
		assertEquals("3000", cuenta.getSaldo().toPlainString());
		
		cuenta.setSaldo(new BigDecimal("3800"));
		Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
		
		assertEquals("Pepe", cuentaActualizada.getPersona());
		assertEquals("3800", cuentaActualizada.getSaldo().toPlainString());
	}
	
	@Test
	void testDelete() {
		Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
		assertEquals("John", cuenta.getPersona());
		
		cuentaRepository.delete(cuenta);
		
		assertThrows(NoSuchElementException.class, ()-> cuentaRepository.findByPersona("John").orElseThrow());
	}
}
