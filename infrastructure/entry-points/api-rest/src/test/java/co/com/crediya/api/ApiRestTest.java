package co.com.crediya.api;

import co.com.crediya.api.dto.CrearUsuarioRequestDTO;
import co.com.crediya.api.dto.UsuarioResponseDTO;
import co.com.crediya.model.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.exceptions.ValidacionDominioException;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.registrarusuario.RegistrarUsuarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestConfig.class)
class ApiRestTest {

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
    void shouldRegisterUserAndReturn201Created() throws Exception {
        User savedUser = User.builder()
                .idUsuario(1L)
                .nombre("Juan Carlos")
                .apellido("Perez Gomez")
                .fechaNacimiento(LocalDate.of(1995, 5, 20))
                .direccion("Calle 10 # 20-30")
                .telefono("3001234567")
                .email("juan.perez@crediya.com")
                .documentoIdentidad("1020304050")
                .idRol(3L)
                .salarioBase(new BigDecimal("3500000"))
                .build();

        when(registrarUsuarioUseCase.registrarUsuario(any(User.class))).thenReturn(savedUser);

        String payload = """
                {
                  "nombres": "Juan Carlos",
                  "apellidos": "Perez Gomez",
                  "fecha_nacimiento": "1995-05-20",
                  "direccion": "Calle 10 # 20-30",
                  "telefono": "3001234567",
                  "correo_electronico": "juan.perez@crediya.com",
                  "salario_base": 3500000,
                  "documento_identidad": "1020304050",
                  "id_rol": 3
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id_usuario").value(1))
                .andExpect(jsonPath("$.nombres").value("Juan Carlos"))
                .andExpect(jsonPath("$.apellidos").value("Perez Gomez"))
                .andExpect(jsonPath("$.fecha_nacimiento").value("1995-05-20"))
                .andExpect(jsonPath("$.direccion").value("Calle 10 # 20-30"))
                .andExpect(jsonPath("$.telefono").value("3001234567"))
                .andExpect(jsonPath("$.correo_electronico").value("juan.perez@crediya.com"))
                .andExpect(jsonPath("$.documento_identidad").value("1020304050"))
                .andExpect(jsonPath("$.id_rol").value(3))
                .andExpect(jsonPath("$.salario_base").value(3500000));
    }

    @Test
    void shouldReturn400WhenRequestValidationFails() throws Exception {
        String invalidPayload = """
                {
                  "nombres": "",
                  "apellidos": "Perez",
                  "correo_electronico": "correo-invalido",
                  "salario_base": 20000000
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.mensaje").exists())
                .andExpect(jsonPath("$.detalles").isArray());
    }

    @Test
    void shouldReturn400WhenDomainValidationFails() throws Exception {
        when(registrarUsuarioUseCase.registrarUsuario(any(User.class)))
                .thenThrow(new ValidacionDominioException("El salario_base debe estar entre 0 y 15000000"));

        String payload = """
                {
                  "nombres": "Juan",
                  "apellidos": "Perez",
                  "correo_electronico": "juan@crediya.com",
                  "salario_base": 1000000
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value("El salario_base debe estar entre 0 y 15000000"));
    }

    @Test
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        when(registrarUsuarioUseCase.registrarUsuario(any(User.class)))
                .thenThrow(new CorreoYaRegistradoException("El correo electrónico ya se encuentra registrado"));

        String payload = """
                {
                  "nombres": "Juan",
                  "apellidos": "Perez",
                  "correo_electronico": "juan@crediya.com",
                  "salario_base": 1000000
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.mensaje").value("El correo electrónico ya se encuentra registrado"));
    }

    @Test
    void shouldReturn500WhenUnexpectedErrorOccurs() throws Exception {
        when(registrarUsuarioUseCase.registrarUsuario(any(User.class)))
                .thenThrow(new RuntimeException("Fallo inesperado en BD"));

        String payload = """
                {
                  "nombres": "Juan",
                  "apellidos": "Perez",
                  "correo_electronico": "juan@crediya.com",
                  "salario_base": 1000000
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.mensaje").value("Ha ocurrido un error interno procesando la solicitud"));
    }

    @Test
    void shouldBuildAndMutateDtosCorrectly() {
        CrearUsuarioRequestDTO req = new CrearUsuarioRequestDTO();
        req.setNombres("Ana");
        req.setApellidos("Lopez");
        req.setFechaNacimiento(LocalDate.of(1992, 4, 10));
        req.setDireccion("Cra 1");
        req.setTelefono("3111111111");
        req.setCorreoElectronico("ana@crediya.com");
        req.setSalarioBase(BigDecimal.TEN);
        req.setDocumentoIdentidad("999");
        req.setIdRol(2L);

        assertEquals("Ana", req.getNombres());
        assertEquals("Lopez", req.getApellidos());
        assertEquals(LocalDate.of(1992, 4, 10), req.getFechaNacimiento());
        assertEquals("Cra 1", req.getDireccion());
        assertEquals("3111111111", req.getTelefono());
        assertEquals("ana@crediya.com", req.getCorreoElectronico());
        assertEquals(BigDecimal.TEN, req.getSalarioBase());
        assertEquals("999", req.getDocumentoIdentidad());
        assertEquals(2L, req.getIdRol());

        UsuarioResponseDTO res = new UsuarioResponseDTO();
        res.setIdUsuario(5L);
        res.setNombres("Ana");
        res.setApellidos("Lopez");
        res.setFechaNacimiento(LocalDate.of(1992, 4, 10));
        res.setDireccion("Cra 1");
        res.setTelefono("3111111111");
        res.setCorreoElectronico("ana@crediya.com");
        res.setDocumentoIdentidad("999");
        res.setIdRol(2L);
        res.setSalarioBase(BigDecimal.TEN);

        assertEquals(5L, res.getIdUsuario());
        assertEquals("Ana", res.getNombres());
        assertEquals("Lopez", res.getApellidos());
        assertEquals(LocalDate.of(1992, 4, 10), res.getFechaNacimiento());
        assertEquals("Cra 1", res.getDireccion());
        assertEquals("3111111111", res.getTelefono());
        assertEquals("ana@crediya.com", res.getCorreoElectronico());
        assertEquals("999", res.getDocumentoIdentidad());
        assertEquals(2L, res.getIdRol());
        assertEquals(BigDecimal.TEN, res.getSalarioBase());
    }
}
