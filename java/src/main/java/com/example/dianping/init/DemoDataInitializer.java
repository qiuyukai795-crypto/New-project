package com.example.dianping.init;

import com.example.dianping.entity.ReviewEntity;
import com.example.dianping.entity.ShopEntity;
import com.example.dianping.entity.ShopTagEntity;
import com.example.dianping.mapper.ShopTagMapper;
import com.example.dianping.service.db.ReviewDbService;
import com.example.dianping.service.db.ShopDbService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class DemoDataInitializer implements ApplicationRunner {

    private final ShopDbService shopDbService;
    private final ReviewDbService reviewDbService;
    private final ShopTagMapper shopTagMapper;

    public DemoDataInitializer(ShopDbService shopDbService, ReviewDbService reviewDbService, ShopTagMapper shopTagMapper) {
        this.shopDbService = shopDbService;
        this.reviewDbService = reviewDbService;
        this.shopTagMapper = shopTagMapper;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (shopDbService.count() > 0) {
            return;
        }

        ShopEntity shop1 = saveShop("火巷小馆", "川菜", 4.8, 92, "静安区", "愚园路 188 号",
                "主打下饭川菜和烟火气小炒，适合朋友聚餐。", "招牌毛血旺",
                List.of("朋友聚餐", "排队热门", "口味稳定"));
        ShopEntity shop2 = saveShop("潮汕鲜牛记", "火锅", 4.7, 138, "徐汇区", "天钥桥路 520 号",
                "现切牛肉和清汤锅底是招牌，适合冬天聚会。", "匙柄肉拼盘",
                List.of("夜宵推荐", "食材新鲜", "服务热情"));
        ShopEntity shop3 = saveShop("面对白白", "面馆", 4.6, 36, "黄浦区", "西藏南路 99 号",
                "主打浇头面和本帮拌面，出餐很快，适合工作日午餐。", "葱油拌面",
                List.of("性价比高", "上班族最爱", "出餐快"));
        ShopEntity shop4 = saveShop("甜岛研究所", "甜品", 4.9, 58, "长宁区", "定西路 321 号",
                "环境明亮，甜品颜值高，适合约会打卡。", "海盐开心果巴斯克",
                List.of("拍照出片", "下午茶", "约会必去"));

        saveReview(shop1.getId(), "阿青", 5, "辣度很过瘾，毛血旺分量足，适合三四个人一起点。", LocalDate.now().minusDays(2));
        saveReview(shop1.getId(), "Luna", 4, "整体口味不错，周末去要早点排队。", LocalDate.now().minusDays(6));
        saveReview(shop2.getId(), "小宇", 5, "牛肉真的很嫩，汤底也干净，二刷没问题。", LocalDate.now().minusDays(1));
        saveReview(shop2.getId(), "Momo", 4, "服务态度很好，就是高峰期稍微有点吵。", LocalDate.now().minusDays(5));
        saveReview(shop3.getId(), "Jason", 4, "中午来吃很方便，拌面很香。", LocalDate.now().minusDays(4));
        saveReview(shop4.getId(), "Kiki", 5, "巴斯克口感很细腻，咖啡也在线。", LocalDate.now().minusDays(3));
    }

    private ShopEntity saveShop(String name,
                                String category,
                                Double rating,
                                Integer avgPrice,
                                String district,
                                String address,
                                String description,
                                String signatureDish,
                                List<String> tags) {
        ShopEntity shop = new ShopEntity();
        shop.setName(name);
        shop.setCategory(category);
        shop.setRating(rating);
        shop.setAvgPrice(avgPrice);
        shop.setDistrict(district);
        shop.setAddress(address);
        shop.setDescription(description);
        shop.setSignatureDish(signatureDish);
        shopDbService.save(shop);
        saveTags(shop.getId(), tags);
        return shop;
    }

    private void saveReview(Long shopId, String nickname, Integer score, String content, LocalDate createdAt) {
        ReviewEntity review = new ReviewEntity();
        review.setShopId(shopId);
        review.setNickname(nickname);
        review.setScore(score);
        review.setContent(content);
        review.setCreatedAt(createdAt);
        reviewDbService.save(review);
    }

    private void saveTags(Long shopId, List<String> tags) {
        for (int i = 0; i < tags.size(); i++) {
            ShopTagEntity tagEntity = new ShopTagEntity();
            tagEntity.setShopId(shopId);
            tagEntity.setTagName(tags.get(i));
            tagEntity.setSortOrder(i);
            shopTagMapper.insertTag(tagEntity);
        }
    }
}
