package fi.haagahelia.artstore.web;

import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import fi.haagahelia.artstore.domain.Art;
import fi.haagahelia.artstore.domain.ArtRepository;

public class ArtRestController {

    private final ArtRepository artRepository;

public ArtRestController(ArtRepository artRepository){
    this.artRepository = artRepository;
}

@GetMapping("/home")
public Iterable<Art> findAllAll(){
    return artRepository.findAll();
}

@GetMapping("/books/{id}")
public Optional<Art> findById(@PathVariable("id") Long artId){
    return artRepository.findById(artId);
}

}
