package com.example.dianping.service;

import com.example.dianping.entity.ReviewEntity;
import com.example.dianping.entity.ShopEntity;
import com.example.dianping.model.Review;
import com.example.dianping.model.Shop;
import com.example.dianping.repository.ReviewRepository;
import com.example.dianping.repository.ShopRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DianpingService {

    private final ShopRepository shopRepository;
    private final ReviewRepository reviewRepository;

    public DianpingService(ShopRepository shopRepository, ReviewRepository reviewRepository) {
        this.shopRepository = shopRepository;
        this.reviewRepository = reviewRepository;
    }

    @Cacheable(value = "shopList", key = "#keyword == null ? '' : #keyword.trim().toLowerCase()")
    public List<Shop> listShops(String keyword) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        List<ShopEntity> entities = normalizedKeyword.isEmpty()
                ? shopRepository.findAll().stream().sorted(Comparator.comparing(ShopEntity::getId)).toList()
                : shopRepository.search(normalizedKeyword);
        return entities.stream().map(this::toModel).toList();
    }

    @Cacheable(value = "shopDetail", key = "#id")
    public Optional<Shop> findShop(Long id) {
        return shopRepository.findById(id).map(this::toModel);
    }

    @Cacheable(value = "shopReviews", key = "#shopId")
    public List<Review> listReviewsByShopId(Long shopId) {
        return reviewRepository.findByShopIdOrderByCreatedAtDescIdDesc(shopId)
                .stream()
                .map(this::toModel)
                .toList();
    }

    @Cacheable(value = "recommendations", key = "#currentShopId")
    public List<Shop> recommendShops(Long currentShopId) {
        Shop currentShop = findShop(currentShopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "店铺不存在"));

        return shopRepository.findByIdNot(currentShopId).stream()
                .sorted(Comparator.comparing((ShopEntity shop) -> shop.getDistrict().equals(currentShop.getDistrict()))
                        .reversed()
                        .thenComparing(ShopEntity::getRating, Comparator.reverseOrder()))
                .limit(3)
                .map(this::toModel)
                .toList();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "shopList", allEntries = true),
            @CacheEvict(value = "shopDetail", key = "#shopId"),
            @CacheEvict(value = "shopReviews", key = "#shopId"),
            @CacheEvict(value = "recommendations", allEntries = true)
    })
    public Review addReview(Long shopId, String nickname, Integer score, String content) {
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "店铺不存在"));

        long existingReviewCount = reviewRepository.countByShopId(shopId);

        ReviewEntity entity = new ReviewEntity();
        entity.setShopId(shopId);
        entity.setNickname(nickname.trim());
        entity.setScore(score);
        entity.setContent(content.trim());
        entity.setCreatedAt(LocalDate.now());

        ReviewEntity saved = reviewRepository.save(entity);

        double currentRating = shop.getRating() == null ? 0.0 : shop.getRating();
        double newRating = ((currentRating * existingReviewCount) + score) / (existingReviewCount + 1);
        shop.setRating(Math.round(newRating * 10.0) / 10.0);
        shopRepository.save(shop);

        return toModel(saved);
    }

    private Shop toModel(ShopEntity entity) {
        return new Shop(
                entity.getId(),
                entity.getName(),
                entity.getCategory(),
                entity.getRating(),
                entity.getAvgPrice(),
                entity.getDistrict(),
                entity.getAddress(),
                entity.getDescription(),
                entity.getSignatureDish(),
                new ArrayList<>(entity.getTags())
        );
    }

    private Review toModel(ReviewEntity entity) {
        return new Review(
                entity.getId(),
                entity.getShopId(),
                entity.getNickname(),
                entity.getScore(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }
}
