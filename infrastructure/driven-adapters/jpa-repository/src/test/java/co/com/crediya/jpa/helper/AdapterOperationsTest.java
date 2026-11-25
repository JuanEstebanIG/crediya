package co.com.crediya.jpa.helper;

import co.com.crediya.jpa.JPARepository;
import co.com.crediya.jpa.JPARepositoryAdapter;
import co.com.crediya.jpa.entity.RolEntity;
import co.com.crediya.jpa.entity.UsuarioEntity;
import co.com.crediya.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.reactivecommons.utils.ObjectMapperImp;
import org.springframework.data.domain.Example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdapterOperationsTest {

    @Mock
    private JPARepository repository;

    private ObjectMapper objectMapper;

    private JPARepositoryAdapter adapter;

    private User domainUser;
    private UsuarioEntity usuarioEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapperImp();
        adapter = new JPARepositoryAdapter(repository, objectMapper);

        LocalDate fechaNacimiento = LocalDate.of(1995, 6, 15);
        domainUser = User.builder()
                .idUsuario(1L)
                .nombre("Juan")
                .apellido("Perez")
                .fechaNacimiento(fechaNacimiento)
                .direccion("Calle 123")
                .telefono("3001234567")
                .email("juan@correo.com")
                .documentoIdentidad("10203040")
                .idRol(3L)
                .salarioBase(new BigDecimal("2500000.00"))
                .build();

        usuarioEntity = UsuarioEntity.builder()
                .idUsuario(1L)
                .nombre("Juan")
                .apellido("Perez")
                .fechaNacimiento(fechaNacimiento)
                .direccion("Calle 123")
                .telefono("3001234567")
                .email("juan@correo.com")
                .documentoIdentidad("10203040")
                .idRol(3L)
                .salarioBase(new BigDecimal("2500000.00"))
                .build();
    }

    @Test
    void testSaveUser() {
        when(repository.save(any(UsuarioEntity.class))).thenReturn(usuarioEntity);

        User result = adapter.saveUser(domainUser);

        assertNotNull(result);
        assertEquals(domainUser.getIdUsuario(), result.getIdUsuario());
        assertEquals(domainUser.getEmail(), result.getEmail());
        assertEquals(domainUser.getFechaNacimiento(), result.getFechaNacimiento());
        assertEquals(domainUser.getSalarioBase(), result.getSalarioBase());
    }

    @Test
    void testExistsByEmail() {
        when(repository.existsByEmail("juan@correo.com")).thenReturn(true);
        when(repository.existsByEmail("otro@correo.com")).thenReturn(false);

        assertTrue(adapter.existsByEmail("juan@correo.com"));
        assertFalse(adapter.existsByEmail("otro@correo.com"));
    }

    @Test
    void testSaveAllEntities() {
        List<User> users = List.of(domainUser);
        List<UsuarioEntity> entities = List.of(usuarioEntity);

        when(repository.saveAll(any())).thenReturn(entities);

        List<User> result = adapter.saveAllEntities(users);

        assertEquals(1, result.size());
        assertEquals("juan@correo.com", result.getFirst().getEmail());
    }

    @Test
    void testFindByIdAndGetUserByIdNumber() {
        when(repository.findById(1L)).thenReturn(Optional.of(usuarioEntity));
        when(repository.findById(99L)).thenReturn(Optional.empty());

        User result = adapter.getUserByIdNumber(1L);
        assertNotNull(result);
        assertEquals(1L, result.getIdUsuario());

        assertNull(adapter.getUserByIdNumber(99L));
    }

    @Test
    void testFindAllAndGetAllUser() {
        when(repository.findAll()).thenReturn(List.of(usuarioEntity));

        List<User> result = adapter.getAllUser();

        assertEquals(1, result.size());
        assertEquals("Juan", result.getFirst().getNombre());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFindByExample() {
        when(repository.findAll(any(Example.class))).thenReturn(List.of(usuarioEntity));

        List<User> result = adapter.findByExample(domainUser);

        assertEquals(1, result.size());
        assertEquals("Perez", result.getFirst().getApellido());
    }

    @Test
    void testEditUserAndDeleteUser() {
        when(repository.save(any(UsuarioEntity.class))).thenReturn(usuarioEntity);

        User edited = adapter.editUser(domainUser);
        assertEquals(1L, edited.getIdUsuario());

        adapter.deleteUser(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void testEntitiesSettersAndGetters() {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setIdUsuario(10L);
        entity.setNombre("Maria");
        entity.setApellido("Lopez");
        entity.setFechaNacimiento(LocalDate.of(1998, 1, 10));
        entity.setDireccion("Av 1");
        entity.setTelefono("3200000000");
        entity.setEmail("maria@correo.com");
        entity.setDocumentoIdentidad("555555");
        entity.setIdRol(2L);
        entity.setSalarioBase(BigDecimal.ONE);

        assertEquals(10L, entity.getIdUsuario());
        assertEquals("Maria", entity.getNombre());
        assertEquals("Lopez", entity.getApellido());
        assertEquals(LocalDate.of(1998, 1, 10), entity.getFechaNacimiento());
        assertEquals("Av 1", entity.getDireccion());
        assertEquals("3200000000", entity.getTelefono());
        assertEquals("maria@correo.com", entity.getEmail());
        assertEquals("555555", entity.getDocumentoIdentidad());
        assertEquals(2L, entity.getIdRol());
        assertEquals(BigDecimal.ONE, entity.getSalarioBase());

        RolEntity rolBuilt = RolEntity.builder()
                .idRol(1L)
                .nombre("Admin")
                .descripcion("Administrador")
                .build();
        assertEquals(1L, rolBuilt.getIdRol());
        assertEquals("Admin", rolBuilt.getNombre());
        assertEquals("Administrador", rolBuilt.getDescripcion());

        RolEntity rolEmpty = new RolEntity();
        rolEmpty.setIdRol(3L);
        rolEmpty.setNombre("Solicitante");
        rolEmpty.setDescripcion("Cliente");
        assertEquals(3L, rolEmpty.getIdRol());
        assertEquals("Solicitante", rolEmpty.getNombre());
        assertEquals("Cliente", rolEmpty.getDescripcion());
    }
}
