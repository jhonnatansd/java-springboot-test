package pe.com.test.springboot.app.controller;

import org.assertj.core.error.ShouldHaveSameSizeAs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pe.com.test.springboot.app.model.Cuenta;
import pe.com.test.springboot.app.model.TransaccionDTO;
import pe.com.test.springboot.app.service.CuentaService;

import static org.mockito.Mockito.*;
import static pe.com.test.springboot.app.Datos.*;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuentaController.class)
public class CuentaControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private CuentaService cuentaService;
	
	ObjectMapper objectMapper;
	
	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
	}
	
	@Test
	void testDetalle() throws Exception {
		when(cuentaService.findById(1L)).thenReturn(crearCuenta001().orElseThrow());
		
		mvc.perform(get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.persona").value("Andrés"))
			.andExpect(jsonPath("$.saldo").value("1000"));
		
		verify(cuentaService).findById(1L);
	}
	
	@Test
	void testTransferir() throws JsonProcessingException, Exception {
		TransaccionDTO dto = new TransaccionDTO();
		dto.setCuentaOrigenId(1L);
		dto.setCuentaDestinoId(2L);
		dto.setMonto(new BigDecimal("100"));
		dto.setBancoId(1L);
		
		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "OK");
		response.put("mensaje", "Transferencia realizada con éxito");
		response.put("transaccion", dto);
		
		mvc.perform(post("/api/cuentas/transferir").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
				.andExpect(jsonPath("$.mensaje").value("Transferencia realizada con éxito"))
				.andExpect(jsonPath("$.transaccion.cuentaOrigenId").value(1L))
				.andExpect(content().json(objectMapper.writeValueAsString(response)));
	}
	
	@Test
	void testListar() throws Exception {
		List<Cuenta> cuentas = Arrays.asList(crearCuenta001().orElseThrow(), crearCuenta002().orElseThrow());
		when(cuentaService.findAll()).thenReturn(cuentas);
		
		mvc.perform(get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$[0].persona").value("Andrés"))
			.andExpect(jsonPath("$[1].persona").value("Jhon"))
			.andExpect(jsonPath("$[0].saldo").value("1000"))
			.andExpect(jsonPath("$[1].saldo").value("2000"))
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(content().json(objectMapper.writeValueAsString(cuentas)));
	}
	
	@Test
	void testGuardar() throws JsonProcessingException, Exception {
		Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		when(cuentaService.save(any())).then(invocation -> {
			Cuenta c = invocation.getArgument(0);
			c.setId(3L);
			
			return c;
		});
		
		mvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(cuenta)))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id", is(3)))
				.andExpect(jsonPath("$.persona", is("Pepe")))
				.andExpect(jsonPath("$.saldo", is(3000)));
		
		verify(cuentaService).save(any());
	}
	
}
