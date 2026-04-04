package com.example.dianping;

import com.example.dianping.model.PagedResult;
import com.example.dianping.model.Shop;
import com.example.dianping.service.DianpingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class DianpingDemoApplicationTests {

    @Autowired
    private DianpingService dianpingService;

    @Test
    void contextLoads() {
    }

    @Test
    void pageShopsShouldReturnPagedData() {
        PagedResult<Shop> result = dianpingService.pageShops("", 1, 2);

        assertThat(result.page()).isEqualTo(1);
        assertThat(result.pageSize()).isEqualTo(2);
        assertThat(result.total()).isGreaterThanOrEqualTo(4);
        assertThat(result.records()).hasSize(2);
    }
}
