package co.com.crediya.api.exception;

import co.com.crediya.api.TestConfig;
import co.com.crediya.api.dto.ErrorResponseDTO;
import co.com.crediya.model.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.exceptions.ValidacionDominioException;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.registrarusuario.RegistrarUsuarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestConfig.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void shouldReturn400WhenJsonIsMalformed() throws Exception {
        String malformedJson = "{ \"nombres\": \"Juan\", \"salario_base\": \"no-numerico\" ";

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.mensaje").value("El cuerpo de la solicitud tiene un formato inválido o tipos de datos incorrectos"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.detalles[0]").exists());
    }

    @Test
    void shouldReturn409WhenDataIntegrityViolationOccurs() throws Exception {
        when(registrarUsuarioUseCase.registrarUsuario(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Unique index or primary key violation"));

        String validPayload = """
                {
                  "nombres": "Juan",
                  "apellidos": "Perez",
                  "correo_electronico": "juan@crediya.com",
                  "salario_base": 2000000
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(validPayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.mensaje").value("El correo electrónico ya se encuentra registrado o viola restricciones de integridad"));
    }

    @Test
    void shouldReturn400And409DirectlyFromHandlerMethods() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ResponseEntity<ErrorResponseDTO> validacionResp = handler.handleValidacionDominio(
                new ValidacionDominioException("Error de regla"));
        assertEquals(HttpStatus.BAD_REQUEST, validacionResp.getStatusCode());
        assertNotNull(validacionResp.getBody());
        assertEquals(400, validacionResp.getBody().getStatus());
        assertEquals("Error de regla", validacionResp.getBody().getMensaje());
        assertNotNull(validacionResp.getBody().getTimestamp());

        ResponseEntity<ErrorResponseDTO> correoResp = handler.handleCorreoYaRegistrado(
                new CorreoYaRegistradoException("Correo existente"));
        assertEquals(HttpStatus.CONFLICT, correoResp.getStatusCode());
        assertNotNull(correoResp.getBody());
        assertEquals(409, correoResp.getBody().getStatus());

        ErrorResponseDTO dto = new ErrorResponseDTO();
        LocalDateTime now = LocalDateTime.now();
        dto.setTimestamp(now);
        dto.setStatus(500);
        dto.setError("Internal Server Error");
        dto.setMensaje("Error");
        dto.setDetalles(List.of("Detalle"));
        assertEquals(now, dto.getTimestamp());
        assertEquals(500, dto.getStatus());
        assertEquals("Internal Server Error", dto.getError());
        assertEquals("Error", dto.getMensaje());
        assertEquals(List.of("Detalle"), dto.getDetalles());
    }
}
