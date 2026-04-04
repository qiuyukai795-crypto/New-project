package com.example.dianping.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dianping.entity.ReviewEntity;
import com.example.dianping.entity.ShopEntity;
import com.example.dianping.entity.ShopTagEntity;
import com.example.dianping.mapper.ShopTagMapper;
import com.example.dianping.model.PagedResult;
import com.example.dianping.model.Review;
import com.example.dianping.model.Shop;
import com.example.dianping.service.db.ReviewDbService;
import com.example.dianping.service.db.ShopDbService;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DianpingService {

    private final ShopDbService shopDbService;
    private final ReviewDbService reviewDbService;
    private final ShopTagMapper shopTagMapper;

    public DianpingService(ShopDbService shopDbService, ReviewDbService reviewDbService, ShopTagMapper shopTagMapper) {
        this.shopDbService = shopDbService;
        this.reviewDbService = reviewDbService;
        this.shopTagMapper = shopTagMapper;
    }

    @Cacheable(value = "shopList", key = "#keyword == null ? '' : #keyword.trim().toLowerCase()")
    public List<Shop> listShops(String keyword) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        List<ShopEntity> entities = normalizedKeyword.isEmpty()
                ? shopDbService.list(Wrappers.<ShopEntity>lambdaQuery().orderByAsc(ShopEntity::getId))
                : shopDbService.listByKeyword(normalizedKeyword);
        attachTags(entities);
        return entities.stream().map(this::toModel).toList();
    }

    @Cacheable(value = "shopPage", key = "(#keyword == null ? '' : #keyword.trim().toLowerCase()) + ':' + #page + ':' + #pageSize")
    public PagedResult<Shop> pageShops(String keyword, long page, long pageSize) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        IPage<ShopEntity> entityPage = normalizedKeyword.isEmpty()
                ? shopDbService.page(new Page<>(page, pageSize), Wrappers.<ShopEntity>lambdaQuery().orderByAsc(ShopEntity::getId))
                : shopDbService.pageByKeyword(normalizedKeyword, page, pageSize);

        List<ShopEntity> records = entityPage.getRecords();
        attachTags(records);

        return new PagedResult<>(
                page,
                pageSize,
                entityPage.getTotal(),
                entityPage.getPages(),
                records.stream().map(this::toModel).toList()
        );
    }

    @Cacheable(value = "shopDetail", key = "#id", unless = "#result == null")
    public Shop findShop(Long id) {
        ShopEntity entity = shopDbService.getById(id);
        if (entity == null) {
            return null;
        }
        attachTags(List.of(entity));
        return toModel(entity);
    }

    @Cacheable(value = "shopReviews", key = "#shopId")
    public List<Review> listReviewsByShopId(Long shopId) {
        return reviewDbService.list(Wrappers.<ReviewEntity>lambdaQuery()
                        .eq(ReviewEntity::getShopId, shopId)
                        .orderByDesc(ReviewEntity::getCreatedAt, ReviewEntity::getId))
                .stream()
                .map(this::toModel)
                .toList();
    }

    @Cacheable(value = "recommendations", key = "#currentShopId")
    public List<Shop> recommendShops(Long currentShopId) {
        Shop currentShop = findShop(currentShopId);
        if (currentShop == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "店铺不存在");
        }

        List<ShopEntity> recommendations = shopDbService.list(Wrappers.<ShopEntity>lambdaQuery()
                .ne(ShopEntity::getId, currentShopId)
                .orderByAsc(ShopEntity::getId));
        attachTags(recommendations);

        return recommendations.stream()
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
            @CacheEvict(value = "shopPage", allEntries = true),
            @CacheEvict(value = "shopDetail", key = "#shopId"),
            @CacheEvict(value = "shopReviews", key = "#shopId"),
            @CacheEvict(value = "recommendations", allEntries = true)
    })
    public Review addReview(Long shopId, String nickname, Integer score, String content) {
        ShopEntity shop = shopDbService.getById(shopId);
        if (shop == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "店铺不存在");
        }

        long existingReviewCount = reviewDbService.count(new LambdaQueryWrapper<ReviewEntity>()
                .eq(ReviewEntity::getShopId, shopId));

        ReviewEntity entity = new ReviewEntity();
        entity.setShopId(shopId);
        entity.setNickname(nickname.trim());
        entity.setScore(score);
        entity.setContent(content.trim());
        entity.setCreatedAt(LocalDate.now());

        reviewDbService.save(entity);

        double currentRating = shop.getRating() == null ? 0.0 : shop.getRating();
        double newRating = ((currentRating * existingReviewCount) + score) / (existingReviewCount + 1);
        shop.setRating(Math.round(newRating * 10.0) / 10.0);
        shopDbService.updateById(shop);

        return toModel(entity);
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

    private void attachTags(List<ShopEntity> shops) {
        if (shops.isEmpty()) {
            return;
        }

        List<Long> shopIds = shops.stream()
                .map(ShopEntity::getId)
                .toList();

        LinkedHashMap<Long, List<String>> tagsByShopId = shopTagMapper.selectByShopIds(shopIds).stream()
                .collect(Collectors.groupingBy(
                        ShopTagEntity::getShopId,
                        LinkedHashMap::new,
                        Collectors.mapping(ShopTagEntity::getTagName, Collectors.toList())
                ));

        shops.forEach(shop -> shop.setTags(new ArrayList<>(tagsByShopId.getOrDefault(shop.getId(), List.of()))));
    }
}
