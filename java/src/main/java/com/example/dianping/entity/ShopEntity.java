package com.example.dianping.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shops")
public class ShopEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false)
    private Double rating;

    @Column(name = "avg_price", nullable = false)
    private Integer avgPrice;

    @Column(nullable = false, length = 50)
    private String district;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "signature_dish", nullable = false, length = 100)
    private String signatureDish;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "shop_tags", joinColumns = @JoinColumn(name = "shop_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "tag_name", nullable = false, length = 50)
    private List<String> tags = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(Integer avgPrice) {
        this.avgPrice = avgPrice;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSignatureDish() {
        return signatureDish;
    }

    public void setSignatureDish(String signatureDish) {
        this.signatureDish = signatureDish;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
