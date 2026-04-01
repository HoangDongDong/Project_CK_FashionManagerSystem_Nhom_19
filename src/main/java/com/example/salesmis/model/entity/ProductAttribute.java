package com.example.salesmis.model.entity;

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
@Table(name = "ProductAttribute")
public class ProductAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attribute_id")
    private Integer attributeId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "material", length = 50)
    private String material;

    @Column(name = "size", length = 20)
    private String size;

    @Column(name = "color", length = 30)
    private String color;

    @Column(name = "weight", precision = 10, scale = 2)
    private BigDecimal weight;

    public Integer getAttributeId() { return attributeId; }
    public void setAttributeId(Integer attributeId) { this.attributeId = attributeId; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
}
