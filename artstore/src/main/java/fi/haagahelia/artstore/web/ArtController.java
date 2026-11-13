package fi.haagahelia.artstore.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fi.haagahelia.artstore.domain.Art;
import fi.haagahelia.artstore.domain.SeriesRepository;
import fi.haagahelia.artstore.domain.ArtRepository;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpSession;

@Controller
public class ArtController {
@Autowired 
private ArtRepository artRepository;

@Autowired 
private SeriesRepository seriesRepository;


//Main page home mapping
@GetMapping({"/", "/home"})
public String home(
        @RequestParam(name = "sortBy", defaultValue = "default") String sortBy,
        Model model,
        Authentication authentication,
        HttpSession session) {
    Sort sort = switch (sortBy) {
        case "year-oldest" -> Sort.by("creationYear").ascending();
        case "year-newest" -> Sort.by("creationYear").descending();
        case "title-asc" -> Sort.by("title").ascending();
        case "title-desc" -> Sort.by("title").descending();
        case "artist-asc" -> Sort.by("artistName").ascending();
        case "artist-desc" -> Sort.by("artistName").descending();
        case "price-asc" -> Sort.by("price").ascending();
        case "price-desc" -> Sort.by("price").descending();
        default -> Sort.by("id").ascending();
    };
    List<Art> arts = artRepository.findAll(sort);

    model.addAttribute("arts",arts);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("cartCount", cartCount(session));
    
    boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken);
    model.addAttribute("isAuthenticated", isAuthenticated);
    boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    model.addAttribute("isAdmin", isAdmin);
    return "home";
}

//Adding of art page mapping
@RequestMapping("/add")
    public String addArt(Model model){
        model.addAttribute("art", new Art());
        model.addAttribute("series", seriesRepository.findAll());
        return "addart";
    }

@GetMapping("/art/{id}")
public String artworkDetails(
        @PathVariable("id") Long artId,
        Model model,
        Authentication authentication,
        HttpSession session) {
    Art art = artRepository.findById(artId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    model.addAttribute("art", art);
    model.addAttribute("cartCount", cartCount(session));
    boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken);
    model.addAttribute("isAuthenticated", isAuthenticated);
    boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    model.addAttribute("isAdmin", isAdmin);
    return "artdetails";
}

private int cartCount(HttpSession session) {
    Object cart = session.getAttribute("cart");
    if (cart instanceof java.util.Set<?> cartItems) {
        return cartItems.size();
    }
    return 0;
}


//Saving of added art 
   @PostMapping("/save")
public String save(
        Art submittedArt,
        @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {

    Art art = submittedArt;

    if (submittedArt.getId() != null) {
        art = artRepository.findById(submittedArt.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        art.setTitle(submittedArt.getTitle());
        art.setArtistName(submittedArt.getArtistName());
        art.setCreationYear(submittedArt.getCreationYear());
        art.setPrice(submittedArt.getPrice());
        art.setDescription(submittedArt.getDescription());
        art.setSeries(submittedArt.getSeries());
    }

    if (file != null && !file.isEmpty()) {
        String extension = switch (file.getContentType() == null ? "" : file.getContentType()) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Upload a JPEG, PNG, GIF, or WebP image");
        };

        Path uploadDirectory = Path.of("uploads").toAbsolutePath().normalize();
        Files.createDirectories(uploadDirectory);

        String filename = UUID.randomUUID() + extension;
        Files.copy(file.getInputStream(), uploadDirectory.resolve(filename));
        art.setImagePath("/uploads/" + filename);
    }

    artRepository.save(art);
    return "redirect:/home";
}

//Deleting of art piece
    @PostMapping("/delete/{id}")
    public String deleteArtwork(@PathVariable("id") Long artId, Model model) {
        artRepository.deleteById(artId);
        return "redirect:../home";
    }
//Deleting of art image
    @PostMapping("/delete/{id}/image")
    public String deleteArtworkImage(@PathVariable("id") Long artId) throws IOException {
        Art art = artRepository.findById(artId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String imagePath = art.getImagePath();

        if (imagePath != null && imagePath.startsWith("/uploads/")) {
            Path uploadDirectory = Path.of("uploads").toAbsolutePath().normalize();
            Path imageFile = uploadDirectory.resolve(imagePath.substring("/uploads/".length())).normalize();
            if (!imageFile.startsWith(uploadDirectory)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid stored image path");
            }
            Files.deleteIfExists(imageFile);
        }

        art.setImagePath(null);
        artRepository.save(art);
        return "redirect:/edit/" + artId;
    }

//Editing of art
    @GetMapping("/edit/{id}")
    public String editArt(@PathVariable("id") Long artId, Model model) {

    Art art = artRepository.findById(artId).orElse(null);

    model.addAttribute("art", art);
    model.addAttribute("series", seriesRepository.findAll());

    return "editart";
}
}
