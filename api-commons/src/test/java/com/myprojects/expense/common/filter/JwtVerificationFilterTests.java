package com.myprojects.expense.common.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.myprojects.expense.common.test.ControllerTestsConfig;
import com.myprojects.expense.common.test.EchoController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = ControllerTestsConfig.class)
public class JwtVerificationFilterTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Algorithm jwtAlgorithm;

    @Test
    public void testRequestWithValidJwt() throws Exception {
        final UUID userId = UUID.randomUUID();
        final Instant now = Instant.now();

        String token = JWT.create()
                .withSubject(userId.toString())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now
                        .plus(Duration.ofMinutes(10))))
                .withArrayClaim("roles", new String[]{"ROLE_USER"})
                .sign(jwtAlgorithm);

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        mockMvc.perform(get(EchoController.ECHO_PATH)
                .header("X-Access-Token", token)
                .with(securityContext(context)))
                .andDo(print())
                .andExpect(status()
                        .isOk());

        assertThat(context.getAuthentication().getPrincipal(), is(userId));
        assertThat(context.getAuthentication().getCredentials(), nullValue());
        assertThat(context.getAuthentication().getAuthorities(), hasSize(1));
        assertThat(context.getAuthentication().getAuthorities().iterator().next().getAuthority(), is("ROLE_USER"));
    }

    @Test
    public void testRequestWithoutJwt() throws Exception {
        mockMvc.perform(get(EchoController.ECHO_PATH))
                .andDo(print())
                .andExpect(status()
                        .isForbidden())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"message\":\"Access denied!\"}", true));
    }

    @Test
    public void testRequestWithExpiredJwt() throws Exception {
        Date utcEpoch = Date.from(Instant.EPOCH);
        String expiredToken = JWT.create()
                .withExpiresAt(utcEpoch)
                .sign(jwtAlgorithm);

        mockMvc.perform(get(EchoController.ECHO_PATH)
                .header("X-Access-Token", expiredToken))
                .andDo(print())
                .andExpect(status()
                        .isForbidden())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"message\":\"The Token has expired on " + utcEpoch + ".\"}", true));
    }

}