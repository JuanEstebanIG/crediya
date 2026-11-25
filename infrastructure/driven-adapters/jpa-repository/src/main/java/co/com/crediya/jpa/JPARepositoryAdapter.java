package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UsuarioEntity;
import co.com.crediya.jpa.helper.AdapterOperations;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Repository
public class JPARepositoryAdapter extends AdapterOperations<User, UsuarioEntity, Long, JPARepository>
        implements UserRepository {

    public JPARepositoryAdapter(JPARepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        log.info("Iniciando persistencia transaccional de usuario con correo: {}", user.getEmail());
        User usuarioGuardado = super.save(user);
        log.info("Usuario persistido exitosamente con id: {}", usuarioGuardado != null ? usuarioGuardado.getIdUsuario() : null);
        return usuarioGuardado;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        log.debug("Verificando existencia de usuario por correo: {}", email);
        boolean existe = repository.existsByEmail(email);
        log.debug("Resultado verificacion de correo {}: {}", email, existe);
        return existe;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUser() {
        log.debug("Consultando todos los usuarios registrados");
        return super.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByIdNumber(Long idUsuario) {
        log.debug("Consultando usuario por id: {}", idUsuario);
        return super.findById(idUsuario);
    }

    @Override
    @Transactional
    public User editUser(User user) {
        log.info("Actualizando usuario con id: {}", user.getIdUsuario());
        return super.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(long idNumber) {
        log.info("Eliminando usuario con id: {}", idNumber);
        repository.deleteById(idNumber);
    }
}
