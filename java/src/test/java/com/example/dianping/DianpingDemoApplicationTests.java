package com.example.dianping;

import com.example.dianping.entity.UserAccountEntity;
import com.example.dianping.model.PagedResult;
import com.example.dianping.model.Shop;
import com.example.dianping.security.JwtTokenService;
import com.example.dianping.service.DianpingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class DianpingDemoApplicationTests {

    @Autowired
    private DianpingService dianpingService;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private JwtDecoder jwtDecoder;

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

    @Test
    void issueTokenShouldAllowMissingOptionalOAuthProfileFields() {
        UserAccountEntity user = new UserAccountEntity();
        user.setId(99L);
        user.setProvider("github");
        user.setUsername("octocat");
        user.setDisplayName("The Octocat");
        user.setRole("ROLE_USER");
        user.setEmail(null);
        user.setAvatarUrl(null);

        String token = jwtTokenService.issueToken(user).accessToken();
        Jwt decoded = jwtDecoder.decode(token);

        assertThat(decoded.getSubject()).isEqualTo("99");
        assertThat(decoded.getClaimAsString("username")).isEqualTo("octocat");
        assertThat(decoded.getClaims()).doesNotContainKeys("email", "avatarUrl");
    }
}
