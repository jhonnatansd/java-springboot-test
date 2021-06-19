package pe.com.test.springboot.app.service;

import java.math.BigDecimal;
import java.util.List;

import pe.com.test.springboot.app.model.Cuenta;

public interface CuentaService {
	
	List<Cuenta> findAll();
	
	Cuenta findById(Long id);
	
	Cuenta save(Cuenta save);
	
	void deleteById(Long id);
	
	int revisarTotalTransferencias(Long bancoId);
	
	BigDecimal revisarSaldo(Long cuentaId);
	
	void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto, Long bancoId);

}
