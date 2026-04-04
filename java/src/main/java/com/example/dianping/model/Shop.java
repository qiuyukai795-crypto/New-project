package com.example.dianping.model;

import java.util.List;

public class Shop {

    private Long id;
    private String name;
    private String category;
    private Double rating;
    private Integer avgPrice;
    private String district;
    private String address;
    private String description;
    private String signatureDish;
    private List<String> tags;

    public Shop() {
    }

    public Shop(Long id,
                String name,
                String category,
                Double rating,
                Integer avgPrice,
                String district,
                String address,
                String description,
                String signatureDish,
                List<String> tags) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.rating = rating;
        this.avgPrice = avgPrice;
        this.district = district;
        this.address = address;
        this.description = description;
        this.signatureDish = signatureDish;
        this.tags = tags;
    }

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
