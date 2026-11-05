package fi.haagahelia.artstore.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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


@Controller
public class ArtController {
@Autowired 
private ArtRepository artRepository;

@Autowired 
private SeriesRepository seriesRepository;


//Main page home mapping
@GetMapping({"/", "/home"})
public String home(Model model) {
    List<Art> arts = (List<Art>) artRepository.findAll();
    model.addAttribute("arts",arts);
    return "home";
}

//Adding of art page mapping
@RequestMapping("/add")
    public String addArt(Model model){
        model.addAttribute("art", new Art());
        model.addAttribute("series", seriesRepository.findAll());
        return "addart";
    }


//Saving of added art 
    @PostMapping("/save")
    public String save(Art art) {
        artRepository.save(art);
        return "redirect:/home";
    }

//Deleting of art piece
    @PostMapping("/delete/{id}")
    public String deleteArtwork(@PathVariable("id") Long artId, Model model) {
        artRepository.deleteById(artId);
        return "redirect:../home";
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



