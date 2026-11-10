package fi.haagahelia.artstore.domain;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ArtRepository extends CrudRepository<Art,Long> {

    List<Art> findAllByOrderByIdAsc();

}
