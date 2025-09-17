package app.Function.DAO;

import java.util.List;
import java.util.Optional;

public interface IDAO<CLASS, ID> {

    Optional<CLASS> findById(ID id);

    List<CLASS> findAll();

    CLASS persist(CLASS entity);

    CLASS update(CLASS entity);

    void delete(CLASS entity);
}

