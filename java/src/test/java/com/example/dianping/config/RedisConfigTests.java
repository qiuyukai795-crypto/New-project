package com.example.dianping.config;

import com.example.dianping.model.PagedResult;
import com.example.dianping.model.Shop;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RedisConfigTests {

    @Test
    void serializerShouldRoundTripFinalListAndPagedResult() {
        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();

        List<Shop> shops = List.of(
                new Shop(1L, "火巷小馆", "川菜", 4.8, 92, "静安区", "愚园路 188 号", "desc", "招牌毛血旺", List.of("朋友聚餐"))
        );
        PagedResult<Shop> page = new PagedResult<>(1, 10, 1, 1, shops);

        Object restoredList = serializer.deserialize(serializer.serialize(shops));
        Object restoredPage = serializer.deserialize(serializer.serialize(page));

        assertThat(restoredList).isInstanceOf(List.class);
        assertThat(restoredPage).isInstanceOf(PagedResult.class);
    }
}
