package co.com.crediya.usecase.registrarusuario;

import co.com.crediya.model.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.exceptions.ValidacionDominioException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final BigDecimal SALARIO_MINIMO = BigDecimal.ZERO;
    private static final BigDecimal SALARIO_MAXIMO = new BigDecimal("15000000");
    private static final Long ROL_SOLICITANTE_DEFAULT = 3L;

    private final UserRepository userRepository;

    public User registrarUsuario(User user) {
        if (user == null) {
            throw new ValidacionDominioException("Los datos del usuario no pueden ser nulos");
        }
        validarCampoObligatorio(user.getNombre(), "nombres");
        validarCampoObligatorio(user.getApellido(), "apellidos");
        validarCampoObligatorio(user.getEmail(), "correo_electronico");

        String emailNormalizado = user.getEmail().trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(emailNormalizado).matches()) {
            throw new ValidacionDominioException("El correo_electronico no tiene un formato válido");
        }

        if (user.getSalarioBase() == null) {
            throw new ValidacionDominioException("El campo salario_base es obligatorio");
        }
        if (user.getSalarioBase().compareTo(SALARIO_MINIMO) < 0
                || user.getSalarioBase().compareTo(SALARIO_MAXIMO) > 0) {
            throw new ValidacionDominioException("El salario_base debe estar entre 0 y 15000000");
        }

        if (userRepository.existsByEmail(emailNormalizado)) {
            throw new CorreoYaRegistradoException("El correo electrónico ya se encuentra registrado");
        }

        User usuarioNormalizado = user.toBuilder()
                .nombre(user.getNombre().trim())
                .apellido(user.getApellido().trim())
                .email(emailNormalizado)
                .idRol(user.getIdRol() != null ? user.getIdRol() : ROL_SOLICITANTE_DEFAULT)
                .build();

        return userRepository.saveUser(usuarioNormalizado);
    }

    public User saveUser(User user) {
        return registrarUsuario(user);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUser();
    }

    public User editUser(User user) {
        return userRepository.editUser(user);
    }

    public User getUserByIdNumber(long idNumber) {
        return userRepository.getUserByIdNumber(idNumber);
    }

    public void deleteUser(long idNumber) {
        userRepository.deleteUser(idNumber);
    }

    private void validarCampoObligatorio(String valor, String nombreCampo) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacionDominioException("El campo " + nombreCampo + " es obligatorio");
        }
    }
}
