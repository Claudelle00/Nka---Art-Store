package fi.haagahelia.artstore.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtRepository extends JpaRepository<Art, Long> {
    List<Art> findAllByOrderByIdAsc();
}
