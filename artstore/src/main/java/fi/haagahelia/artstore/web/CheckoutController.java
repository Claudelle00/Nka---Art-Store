package fi.haagahelia.artstore.web;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import fi.haagahelia.artstore.domain.Art;
import fi.haagahelia.artstore.domain.ArtRepository;
import fi.haagahelia.artstore.domain.DemoOrder;
import fi.haagahelia.artstore.domain.DemoOrderRepository;
import fi.haagahelia.artstore.service.DemoCheckoutService;
import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

    private static final String CART_SESSION_KEY = "cart";
    private static final String LAST_ORDER_SESSION_KEY = "lastDemoOrderId";

    private final ArtRepository artRepository;
    private final DemoOrderRepository demoOrderRepository;
    private final DemoCheckoutService demoCheckoutService;

    public CheckoutController(ArtRepository artRepository,
                              DemoOrderRepository demoOrderRepository,
                              DemoCheckoutService demoCheckoutService) {
        this.artRepository = artRepository;
        this.demoOrderRepository = demoOrderRepository;
        this.demoCheckoutService = demoCheckoutService;
    }

    @GetMapping("/checkout")
    public String reviewCheckout(HttpSession session, Model model) {
        Set<Long> cartIds = getCartIds(session);
        if (cartIds.isEmpty()) {
            return "redirect:/cart";
        }

        List<Art> arts = new ArrayList<>();
        Set<Long> existingIds = new LinkedHashSet<>();
        for (Art art : artRepository.findAllById(cartIds)) {
            arts.add(art);
            existingIds.add(art.getId());
        }

        if (arts.isEmpty()) {
            session.removeAttribute(CART_SESSION_KEY);
            return "redirect:/cart";
        }

        session.setAttribute(CART_SESSION_KEY, existingIds);
        model.addAttribute("arts", arts);
        model.addAttribute("total", arts.stream()
                .mapToDouble(Art::getPrice)
                .sum());
        model.addAttribute("cartCount", arts.size());
        return "checkout";
    }

    @PostMapping("/checkout/place")
    public String placeDemoOrder(HttpSession session) {
        Set<Long> cartIds = getCartIds(session);
        if (cartIds.isEmpty()) {
            return "redirect:/cart";
        }

        DemoOrder order = demoCheckoutService.placeDemoOrder(cartIds);
        session.setAttribute(LAST_ORDER_SESSION_KEY, order.getId());
        session.removeAttribute(CART_SESSION_KEY);
        return "redirect:/checkout/confirmation";
    }

    @GetMapping("/checkout/confirmation")
    public String showConfirmation(HttpSession session, Model model) {
        Object orderId = session.getAttribute(LAST_ORDER_SESSION_KEY);
        if (!(orderId instanceof Long id)) {
            return "redirect:/cart";
        }

        DemoOrder order = demoOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("order", order);
        return "confirmation";
    }

    @SuppressWarnings("unchecked")
    private Set<Long> getCartIds(HttpSession session) {
        Object cart = session.getAttribute(CART_SESSION_KEY);
        if (cart instanceof Set<?>) {
            return new LinkedHashSet<>((Set<Long>) cart);
        }
        return new LinkedHashSet<>();
    }
}
