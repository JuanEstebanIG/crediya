package co.com.crediya.usecase.registrarusuario;

import co.com.crediya.model.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.exceptions.ValidacionDominioException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarUsuarioUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegistrarUsuarioUseCase useCase;

    private User buildValidUser() {
        return User.builder()
                .nombre("  Juan Carlos  ")
                .apellido("  Perez Gomez  ")
                .fechaNacimiento(LocalDate.of(1995, 3, 15))
                .direccion("Calle 10 # 20-30")
                .telefono("3001234567")
                .email("  Juan.Perez@CrediYa.COM  ")
                .documentoIdentidad("123456789")
                .salarioBase(new BigDecimal("3500000"))
                .build();
    }

    @Test
    void deberiaRegistrarUsuarioExitosamenteConRolPorDefecto() {
        User entrada = buildValidUser();
        when(userRepository.existsByEmail("juan.perez@crediya.com")).thenReturn(false);
        when(userRepository.saveUser(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return u.toBuilder().idUsuario(1L).build();
        });

        User resultado = useCase.registrarUsuario(entrada);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdUsuario());
        assertEquals("Juan Carlos", resultado.getNombre());
        assertEquals("Perez Gomez", resultado.getApellido());
        assertEquals("juan.perez@crediya.com", resultado.getEmail());
        assertEquals(3L, resultado.getIdRol());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveUser(captor.capture());
        assertEquals("juan.perez@crediya.com", captor.getValue().getEmail());
        assertEquals(3L, captor.getValue().getIdRol());
    }

    @Test
    void deberiaMantenerRolExplicitoCuandoSeProporciona() {
        User entrada = buildValidUser().toBuilder().idRol(2L).build();
        when(userRepository.existsByEmail("juan.perez@crediya.com")).thenReturn(false);
        when(userRepository.saveUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User resultado = useCase.saveUser(entrada);

        assertEquals(2L, resultado.getIdRol());
        verify(userRepository).saveUser(any(User.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoCorreoYaEstaRegistrado() {
        User entrada = buildValidUser();
        when(userRepository.existsByEmail("juan.perez@crediya.com")).thenReturn(true);

        CorreoYaRegistradoException ex = assertThrows(CorreoYaRegistradoException.class,
                () -> useCase.registrarUsuario(entrada));

        assertEquals("El correo electrónico ya se encuentra registrado", ex.getMessage());
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoUsuarioEsNulo() {
        ValidacionDominioException ex = assertThrows(ValidacionDominioException.class,
                () -> useCase.registrarUsuario(null));

        assertEquals("Los datos del usuario no pueden ser nulos", ex.getMessage());
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoNombreEsNuloOEnBlanco() {
        User sinNombre = buildValidUser().toBuilder().nombre(null).build();
        User nombreBlanco = buildValidUser().toBuilder().nombre("   ").build();

        assertThrows(ValidacionDominioException.class, () -> useCase.registrarUsuario(sinNombre));
        assertThrows(ValidacionDominioException.class, () -> useCase.registrarUsuario(nombreBlanco));
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoApellidoEsNuloOEnBlanco() {
        User sinApellido = buildValidUser().toBuilder().apellido(null).build();
        User apellidoBlanco = buildValidUser().toBuilder().apellido("   ").build();

        assertThrows(ValidacionDominioException.class, () -> useCase.registrarUsuario(sinApellido));
        assertThrows(ValidacionDominioException.class, () -> useCase.registrarUsuario(apellidoBlanco));
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoCorreoEsNuloOEnBlanco() {
        User sinCorreo = buildValidUser().toBuilder().email(null).build();
        User correoBlanco = buildValidUser().toBuilder().email("   ").build();

        assertThrows(ValidacionDominioException.class, () -> useCase.registrarUsuario(sinCorreo));
        assertThrows(ValidacionDominioException.class, () -> useCase.registrarUsuario(correoBlanco));
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoCorreoTieneFormatoInvalido() {
        User correoSinArroba = buildValidUser().toBuilder().email("correo-invalido.com").build();
        User correoSinDominio = buildValidUser().toBuilder().email("usuario@dominio").build();

        assertThrows(ValidacionDominioException.class, () -> useCase.registrarUsuario(correoSinArroba));
        assertThrows(ValidacionDominioException.class, () -> useCase.registrarUsuario(correoSinDominio));
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoSalarioEsNulo() {
        User sinSalario = buildValidUser().toBuilder().salarioBase(null).build();

        assertThrows(ValidacionDominioException.class, () -> useCase.registrarUsuario(sinSalario));
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoSalarioFueraDeRango() {
        User salarioNegativo = buildValidUser().toBuilder().salarioBase(new BigDecimal("-0.01")).build();
        User salarioExcedido = buildValidUser().toBuilder().salarioBase(new BigDecimal("15000000.01")).build();

        assertThrows(ValidacionDominioException.class, () -> useCase.registrarUsuario(salarioNegativo));
        assertThrows(ValidacionDominioException.class, () -> useCase.registrarUsuario(salarioExcedido));
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void deberiaPermitirValoresLimiteDeSalario() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.saveUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User salarioMinimo = buildValidUser().toBuilder().salarioBase(BigDecimal.ZERO).build();
        User salarioMaximo = buildValidUser().toBuilder().salarioBase(new BigDecimal("15000000")).build();

        User resMin = useCase.registrarUsuario(salarioMinimo);
        User resMax = useCase.registrarUsuario(salarioMaximo);

        assertEquals(BigDecimal.ZERO, resMin.getSalarioBase());
        assertEquals(new BigDecimal("15000000"), resMax.getSalarioBase());
    }

    @Test
    void deberiaDelegarOperacionesConsultaEdicionYEliminacionAlRepositorio() {
        User usuario = buildValidUser().toBuilder().idUsuario(10L).build();
        when(userRepository.getAllUser()).thenReturn(List.of(usuario));
        when(userRepository.editUser(usuario)).thenReturn(usuario);
        when(userRepository.getUserByIdNumber(10L)).thenReturn(usuario);

        assertEquals(List.of(usuario), useCase.getAllUsers());
        assertEquals(usuario, useCase.editUser(usuario));
        assertEquals(usuario, useCase.getUserByIdNumber(10L));

        useCase.deleteUser(10L);
        verify(userRepository).deleteUser(10L);
    }
}
