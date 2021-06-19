package pe.com.test.springboot.app.controller;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import pe.com.test.springboot.app.model.Cuenta;
import pe.com.test.springboot.app.model.TransaccionDTO;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

@Tag("integracion_wc")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CuentaControllerWebClientTest {
	
	@Autowired
	private WebTestClient client;
	
	private ObjectMapper objectMapper;
	
	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
	}
	
	@Test
	@Order(1)
	void testTransferir() throws JsonProcessingException {
		TransaccionDTO dto = new TransaccionDTO();
		dto.setCuentaOrigenId(1L);
		dto.setCuentaDestinoId(2L);
		dto.setBancoId(1L);
		dto.setMonto(new BigDecimal("100"));
		
		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "OK");
		response.put("mensaje", "Transferencia realizada con éxito");
		response.put("transaccion", dto);
		
		client.post().uri("/api/cuentas/transferir")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(dto)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.consumeWith(respuesta -> {
					try {
						JsonNode json = objectMapper.readTree(respuesta.getResponseBody());
						assertEquals("Transferencia realizada con éxito", json.path("mensaje").asText());
						assertEquals(1L, json.path("transaccion").path("cuentaOrigenId").asLong());
						assertEquals(LocalDate.now().toString(), json.path("date").asText());
						assertEquals("100", json.path("transaccion").path("monto").asText());
					} catch (IOException e) {
						e.printStackTrace();
					}
				})
				.jsonPath("$.mensaje").isNotEmpty()
				.jsonPath("$.mensaje").value(is("Transferencia realizada con éxito"))
				.jsonPath("$.mensaje").value(valor -> assertEquals("Transferencia realizada con éxito", valor))
				.jsonPath("$.mensaje").isEqualTo("Transferencia realizada con éxito")
				.jsonPath("$.transaccion.cuentaOrigenId").isEqualTo(dto.getCuentaOrigenId())
				.jsonPath("$.date").isEqualTo(LocalDate.now().toString())
				.json(objectMapper.writeValueAsString(response));
	}
	
	@Test
	@Order(2)
	void testDetalle() throws JsonProcessingException {
		Cuenta cuenta = new Cuenta(1L, "Andrés", new BigDecimal("900"));
		
		client.get().uri("/api/cuentas/1")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.persona").isEqualTo("Andrés")
				.jsonPath("$.saldo").isEqualTo(900)
				.json(objectMapper.writeValueAsString(cuenta));
	}
	
	@Test
	@Order(3)
	void testDetalle2() {
		client.get().uri("/api/cuentas/2")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(Cuenta.class)
				.consumeWith(response -> {
					Cuenta cuenta = response.getResponseBody();
					assertEquals("John", cuenta.getPersona());
					assertEquals("1100.00", cuenta.getSaldo().toPlainString());
				});
	}

	@Test
	@Order(4)
	void testListar() {
		client.get().uri("/api/cuentas")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$[0].persona").isEqualTo("Andrés")
				.jsonPath("$[0].id").isEqualTo(1)
				.jsonPath("$[0].saldo").isEqualTo(900)
				.jsonPath("$[1].persona").isEqualTo("John")
				.jsonPath("$[1].id").isEqualTo(2)
				.jsonPath("$[1].saldo").isEqualTo(1100)
				.jsonPath("$").isArray()
				.jsonPath("$").value(hasSize(2));
	}
	
	@Test
	@Order(5)
	void testListar2() {
		client.get().uri("/api/cuentas")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(Cuenta.class)
				.consumeWith(response -> {
					List<Cuenta> cuentas = response.getResponseBody();
					assertNotNull(cuentas);
					assertEquals(2, cuentas.size());
					assertEquals(1L, cuentas.get(0).getId());
					assertEquals("Andrés", cuentas.get(0).getPersona());
					assertEquals(900, cuentas.get(0).getSaldo().intValue());
					assertEquals(2L, cuentas.get(1).getId());
					assertEquals("John", cuentas.get(1).getPersona());
					assertEquals(1100, cuentas.get(1).getSaldo().intValue());
				})
				.hasSize(2);
	}
	
	@Test
	@Order(6)
	void testGuardar() {
		Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		
		client.post().uri("/api/cuentas")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(cuenta)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.id").isEqualTo(3)
				.jsonPath("$.persona").isEqualTo("Pepe")
				.jsonPath("$.saldo").isEqualTo(3000);
	}
	
	@Test
	@Order(7)
	void testGuardar2() {
		Cuenta cuenta = new Cuenta(null, "Pepa", new BigDecimal("3500"));
		
		client.post().uri("/api/cuentas")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(cuenta)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(Cuenta.class)
				.consumeWith(response -> {
					Cuenta c = response.getResponseBody();
					assertEquals(4L, c.getId());
					assertEquals("Pepa", c.getPersona());
					assertEquals("3500", c.getSaldo().toPlainString());
				});
	}
	
	@Test
	@Order(8)
	void testEliminar() {
		client.get().uri("/api/cuentas").exchange()
					.expectStatus().isOk()
					.expectHeader().contentType(MediaType.APPLICATION_JSON)
					.expectBodyList(Cuenta.class)
					.hasSize(4);
		
		client.delete().uri("/api/cuentas/3").exchange()
					.expectStatus().isNoContent()
					.expectBody()
					.isEmpty();
		
		client.get().uri("/api/cuentas").exchange()
					.expectStatus().isOk()
					.expectHeader().contentType(MediaType.APPLICATION_JSON)
					.expectBodyList(Cuenta.class)
					.hasSize(3);
		
		client.get().uri("/api/cuentas/3").exchange()
					.expectStatus()
					//.is5xxServerError();
					.isNotFound()
					.expectBody()
					.isEmpty();
	}
}
