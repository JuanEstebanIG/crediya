package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UsuarioEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface JPARepository extends CrudRepository<UsuarioEntity, Long>, QueryByExampleExecutor<UsuarioEntity> {
    boolean existsByEmail(String email);
}
