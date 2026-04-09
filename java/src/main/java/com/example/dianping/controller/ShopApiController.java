package com.example.dianping.controller;

import com.example.dianping.model.PagedResult;
import com.example.dianping.model.Review;
import com.example.dianping.model.Shop;
import com.example.dianping.service.AuthService;
import com.example.dianping.service.DianpingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
public class ShopApiController {

    private final DianpingService dianpingService;
    private final AuthService authService;

    public ShopApiController(DianpingService dianpingService, AuthService authService) {
        this.dianpingService = dianpingService;
        this.authService = authService;
    }

    @GetMapping
    public List<Shop> listShops(@RequestParam(defaultValue = "") String keyword) {
        return dianpingService.listShops(keyword);
    }

    @GetMapping("/page")
    public PagedResult<Shop> pageShops(@RequestParam(defaultValue = "") String keyword,
                                       @RequestParam(defaultValue = "1") long page,
                                       @RequestParam(defaultValue = "10") long pageSize) {
        if (page < 1 || pageSize < 1 || pageSize > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "分页参数不合法");
        }
        return dianpingService.pageShops(keyword, page, pageSize);
    }

    @GetMapping("/{id}")
    public Shop getShop(@PathVariable Long id) {
        Shop shop = dianpingService.findShop(id);
        if (shop == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "店铺不存在");
        }
        return shop;
    }

    @GetMapping("/{id}/reviews")
    public List<Review> listReviews(@PathVariable Long id) {
        if (dianpingService.findShop(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "店铺不存在");
        }
        return dianpingService.listReviewsByShopId(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Shop> listRecommendations(@PathVariable Long id) {
        if (dianpingService.findShop(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "店铺不存在");
        }
        return dianpingService.recommendShops(id);
    }

    @PostMapping("/{id}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public Review createReview(@PathVariable Long id,
                               @AuthenticationPrincipal Jwt jwt,
                               @Valid @RequestBody CreateReviewRequest request) {
        String nickname = authService.getCurrentUser(jwt).displayName();
        return dianpingService.addReview(id, nickname, request.getScore(), request.getContent());
    }

    public static class CreateReviewRequest {
        @Min(value = 1, message = "score 不能小于 1")
        @Max(value = 5, message = "score 不能大于 5")
        private Integer score;

        @NotBlank(message = "content 不能为空")
        private String content;

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
    }
}
