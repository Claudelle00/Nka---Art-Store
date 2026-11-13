package fi.haagahelia.artstore.service;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import fi.haagahelia.artstore.domain.Art;
import fi.haagahelia.artstore.domain.ArtRepository;
import fi.haagahelia.artstore.domain.DemoOrder;
import fi.haagahelia.artstore.domain.DemoOrderItem;
import fi.haagahelia.artstore.domain.DemoOrderRepository;

@Service
public class DemoCheckoutService {

    private final ArtRepository artRepository;
    private final DemoOrderRepository demoOrderRepository;

    public DemoCheckoutService(ArtRepository artRepository, DemoOrderRepository demoOrderRepository) {
        this.artRepository = artRepository;
        this.demoOrderRepository = demoOrderRepository;
    }

    @Transactional
    public DemoOrder placeDemoOrder(Set<Long> artworkIds) {
        if (artworkIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot place an empty order");
        }

        DemoOrder order = new DemoOrder();
        for (Long artworkId : artworkIds) {
            Art art = artRepository.findById(artworkId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.CONFLICT, "An artwork in your cart is no longer available"));

            order.addItem(new DemoOrderItem(
                    art.getId(),
                    art.getTitle(),
                    art.getArtistName(),
                    BigDecimal.valueOf(art.getPrice()),
                    1));
        }

        return demoOrderRepository.save(order);
    }
}
