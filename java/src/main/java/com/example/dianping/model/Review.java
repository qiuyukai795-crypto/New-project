package com.example.dianping.model;

import java.time.LocalDate;

public class Review {

    private Long id;
    private Long shopId;
    private String nickname;
    private Integer score;
    private String content;
    private LocalDate createdAt;

    public Review() {
    }

    public Review(Long id, Long shopId, String nickname, Integer score, String content, LocalDate createdAt) {
        this.id = id;
        this.shopId = shopId;
        this.nickname = nickname;
        this.score = score;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
