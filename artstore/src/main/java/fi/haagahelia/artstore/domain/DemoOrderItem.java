package fi.haagahelia.artstore.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "demo_order_items")
public class DemoOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long artworkId;

    @Column(nullable = false)
    private String artworkTitle;

    @Column(nullable = false)
    private String artistName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "demo_order_id", nullable = false)
    private DemoOrder demoOrder;

    protected DemoOrderItem() {
    }

    public DemoOrderItem(Long artworkId, String artworkTitle, String artistName,
                         BigDecimal unitPrice, int quantity) {
        this.artworkId = artworkId;
        this.artworkTitle = artworkTitle;
        this.artistName = artistName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    void setDemoOrder(DemoOrder demoOrder) {
        this.demoOrder = demoOrder;
    }

    public Long getId() {
        return id;
    }

    public Long getArtworkId() {
        return artworkId;
    }

    public String getArtworkTitle() {
        return artworkTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }
}
