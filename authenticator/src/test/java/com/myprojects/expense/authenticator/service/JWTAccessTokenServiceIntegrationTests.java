package com.myprojects.expense.authenticator.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.myprojects.expense.authenticator.config.AuthenticatorAppConfig;
import com.myprojects.expense.authenticator.dao.AppUserDao;
import com.myprojects.expense.authenticator.model.request.LoginRequest;
import com.myprojects.expense.authenticator.model.request.SignUpRequest;
import com.myprojects.expense.authenticator.model.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

@SpringBootTest(classes = AuthenticatorAppConfig.class)
public class JWTAccessTokenServiceIntegrationTests extends AbstractTestNGSpringContextTests {

    private static final String TEST_EMAIL = UUID.randomUUID() + "@gmail.com";
    private static final String TEST_PASSWORD = "abc123";

    @Autowired
    private DefaultAppUserService appUserService;

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private JWTAccessTokenService accessTokenService;

    @Autowired
    private Algorithm jwtAlgorithm;

    @Value("${jwt.validity-duration-minutes}")
    private long jwtValidityDurationMinutes;

    private UUID createdUserId;

    @BeforeClass
    public void signUp() {
        SignUpRequest request = new SignUpRequest();
        request.setName("some_name");
        request.setPassword(TEST_PASSWORD);
        request.setEmail(TEST_EMAIL);

        appUserService.signUp(request);

        createdUserId = appUserDao.findByEmail(TEST_EMAIL).get().getId();
    }

    @Test
    public void testLogin() {
        LoginRequest request = new LoginRequest();
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);

        LoginResponse response = accessTokenService.login(request);
        DecodedJWT decodedJwt = JWT.require(jwtAlgorithm)
                .build()
                .verify(response.getToken());

        assertThat(decodedJwt.getSubject(), is(createdUserId.toString()));
        assertThat(decodedJwt.getIssuedAt(), lessThan(new Date()));
        assertThat(Duration.between(decodedJwt.getIssuedAt().toInstant(),
                decodedJwt.getExpiresAt().toInstant()).toMinutes(), is(jwtValidityDurationMinutes));
        assertThat(decodedJwt.getClaim("roles").asArray(String.class),
                equalTo(new String[]{"ROLE_USER"}));
    }

    @Test(expectedExceptions = BadCredentialsException.class)
    public void testLoginFailure() {
        LoginRequest request = new LoginRequest();
        request.setEmail("wrong");
        request.setPassword("wrong");

        accessTokenService.login(request);
    }
}