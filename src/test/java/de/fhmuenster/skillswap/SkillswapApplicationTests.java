package de.fhmuenster.skillswap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@SpringBootTest
class SkillswapApplicationTests {

    @MockitoBean
    private JwtDecoder jwtDecoder;

	@Test
	void contextLoads() {
	}

}
