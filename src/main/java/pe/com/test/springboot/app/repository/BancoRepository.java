package pe.com.test.springboot.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.com.test.springboot.app.model.Banco;

public interface BancoRepository extends JpaRepository<Banco, Long>{
	
	//List<Banco> findAll();
	
	//Banco findById(Long id);

	//void update(Banco banco);
}
