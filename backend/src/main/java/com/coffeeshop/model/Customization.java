package com.coffeeshop.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "customizations")
public class Customization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "price_impact", nullable = false)
    private BigDecimal priceImpact = BigDecimal.ZERO;

    public Customization() {}

    public Customization(Long id, String name, BigDecimal priceImpact) {
        this.id = id;
        this.name = name;
        this.priceImpact = priceImpact;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPriceImpact() { return priceImpact; }
    public void setPriceImpact(BigDecimal priceImpact) { this.priceImpact = priceImpact; }
}
