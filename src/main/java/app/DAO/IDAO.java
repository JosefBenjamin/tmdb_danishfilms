package app.DAO;

import java.util.List;
import java.util.Optional;

public interface IDAO<T, ID> {

    Optional<T> findById(ID id);

    List<T> findAll();

    T persist(T entity);

    T update(T entity);

    void delete(T entity);
}

