package fi.haagahelia.artstore.web;

import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.ui.Model;
import fi.haagahelia.artstore.domain.Art;
import fi.haagahelia.artstore.domain.ArtRepository;
import jakarta.servlet.http.HttpSession;


@Controller
public class CartController {
    
    private final ArtRepository artRepository;
    public CartController(ArtRepository artRepository){
        this.artRepository= artRepository;
    }

    //Adding art to cart
    @SuppressWarnings("unchecked")
    @PostMapping("/cart/add")
    public String addArtToCart(@RequestParam("artId") Long artId, HttpSession session) {
        
          if (artRepository.findById(artId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Set<Long> cart = (Set<Long>) session.getAttribute("cart");
        if (cart == null) {
            cart = new LinkedHashSet<>();
        }

        cart.add(artId);
        session.setAttribute("cart", cart);

        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model){
        Set<Long> cartIds = getArtAddedToCart(session);
        List<Art> arts = new ArrayList<>();
        Set<Long> existingIds = new LinkedHashSet<>();
        double total = 0;

        for (Art art : artRepository.findAllById(cartIds)) {
            arts.add(art);
            existingIds.add(art.getId());
            total += art.getPrice();
        }

        if (existingIds.isEmpty()) {
            session.removeAttribute("cart");
        } else {
            session.setAttribute("cart", existingIds);
        }

        model.addAttribute("arts", arts);
        model.addAttribute("total", total);
        model.addAttribute("cartCount", arts.size());
        return "cart";
    }

    
    //Removing art from cart
    @PostMapping("/cart/remove")
    public String removeArtFromCart(@RequestParam("artId") Long artId, HttpSession session) {
        Set<Long> cart = getArtAddedToCart(session);
        cart.remove(artId);

        if (cart.isEmpty()) {
            session.removeAttribute("cart");
        } else {
            session.setAttribute("cart", cart);
        }
        
        return "redirect:/cart";
    }


    @SuppressWarnings("unchecked")
    private Set<Long> getArtAddedToCart(HttpSession session) {
        Object value = session.getAttribute("cart");
    if (value instanceof Set<?>) {
        return (Set<Long>) value;
    }
    return new LinkedHashSet<>();
    }
    

}
