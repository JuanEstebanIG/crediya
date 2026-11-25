package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.User;

import java.util.List;

public interface UserRepository {
    User saveUser(User user);

    boolean existsByEmail(String email);

    List<User> getAllUser();

    User getUserByIdNumber(Long idUsuario);

    User editUser(User user);

    void deleteUser(long idNumber);
}
