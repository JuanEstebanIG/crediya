package co.com.crediya.model.user;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.exceptions.ValidacionDominioException;
import co.com.crediya.model.rol.Rol;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserTest {

    @Test
    void shouldCreateUserWithBuilderAndToBuilder() {
        LocalDate birthDate = LocalDate.of(1995, 5, 20);
        User user = User.builder()
                .idUsuario(1L)
                .nombre("Juan")
                .apellido("Perez")
                .fechaNacimiento(birthDate)
                .direccion("Calle 123")
                .telefono("3001234567")
                .email("juan@correo.com")
                .documentoIdentidad("10203040")
                .idRol(3L)
                .salarioBase(new BigDecimal("2500000"))
                .build();

        assertEquals(1L, user.getIdUsuario());
        assertEquals("Juan", user.getNombre());
        assertEquals("Perez", user.getApellido());
        assertEquals(birthDate, user.getFechaNacimiento());
        assertEquals("Calle 123", user.getDireccion());
        assertEquals("3001234567", user.getTelefono());
        assertEquals("juan@correo.com", user.getEmail());
        assertEquals("10203040", user.getDocumentoIdentidad());
        assertEquals(3L, user.getIdRol());
        assertEquals(new BigDecimal("2500000"), user.getSalarioBase());

        User updated = user.toBuilder().nombre("Carlos").build();
        assertEquals("Carlos", updated.getNombre());
        assertEquals("Perez", updated.getApellido());
    }

    @Test
    void shouldCreateAndMutateUserWithNoArgsConstructorAndSetters() {
        User user = new User();
        user.setIdUsuario(2L);
        user.setNombre("Ana");
        user.setApellido("Gomez");
        user.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        user.setDireccion("Carrera 45");
        user.setTelefono("3119876543");
        user.setEmail("ana@correo.com");
        user.setDocumentoIdentidad("98765432");
        user.setIdRol(2L);
        user.setSalarioBase(BigDecimal.TEN);

        assertEquals(2L, user.getIdUsuario());
        assertEquals("Ana", user.getNombre());
        assertEquals("Gomez", user.getApellido());
        assertEquals(LocalDate.of(1990, 1, 1), user.getFechaNacimiento());
        assertEquals("Carrera 45", user.getDireccion());
        assertEquals("3119876543", user.getTelefono());
        assertEquals("ana@correo.com", user.getEmail());
        assertEquals("98765432", user.getDocumentoIdentidad());
        assertEquals(2L, user.getIdRol());
        assertEquals(BigDecimal.TEN, user.getSalarioBase());
    }

    @Test
    void shouldCreateRolWithBuilderAndSetters() {
        Rol rol = Rol.builder()
                .idRol(1L)
                .nombre("Administrador")
                .descripcion("Rol admin")
                .build();

        assertEquals(1L, rol.getIdRol());
        assertEquals("Administrador", rol.getNombre());
        assertEquals("Rol admin", rol.getDescripcion());

        Rol copied = rol.toBuilder().nombre("Asesor").build();
        assertEquals("Asesor", copied.getNombre());

        Rol emptyRol = new Rol();
        emptyRol.setIdRol(3L);
        emptyRol.setNombre("Solicitante");
        emptyRol.setDescripcion("Cliente solicitante");
        assertEquals(3L, emptyRol.getIdRol());
        assertEquals("Solicitante", emptyRol.getNombre());
        assertEquals("Cliente solicitante", emptyRol.getDescripcion());
    }

    @Test
    void shouldCreateDomainExceptionsWithMessage() {
        BusinessException businessException = new BusinessException("Error negocio");
        ValidacionDominioException validacionException = new ValidacionDominioException("Error validacion");
        CorreoYaRegistradoException correoException = new CorreoYaRegistradoException("Correo duplicado");

        assertNotNull(businessException);
        assertEquals("Error negocio", businessException.getMessage());
        assertInstanceOf(BusinessException.class, validacionException);
        assertEquals("Error validacion", validacionException.getMessage());
        assertInstanceOf(BusinessException.class, correoException);
        assertEquals("Correo duplicado", correoException.getMessage());
    }
}
