package com.myprojects.expense.authenticator.service;

import com.myprojects.expense.authenticator.config.AuthenticatorAppConfig;
import com.myprojects.expense.authenticator.dao.AppUserDao;
import com.myprojects.expense.authenticator.exception.EmailAlreadyUsedException;
import com.myprojects.expense.authenticator.model.AppUser;
import com.myprojects.expense.authenticator.model.request.SignUpRequest;
import com.myprojects.expense.authenticator.model.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEmptyString.emptyString;

@SpringBootTest(classes = AuthenticatorAppConfig.class)
public class DefaultAppUserServiceIntegrationTests extends AbstractTestNGSpringContextTests {

    private static final String TEST_EMAIL = UUID.randomUUID() + "@gmail.com";

    @Autowired
    private DefaultAppUserService userService;

    @Autowired
    private AppUserDao userDao;

    @Test
    public void testSignUp() {
        SignUpRequest request = createSignUpRequest();

        LoginResponse response = userService.signUp(request);

        assertThat(response.getToken(), not(emptyString()));

        AppUser user = userDao.findByEmail(request.getEmail()).get();
        assertThat(user.getName(), is(request.getName()));
        assertThat(user.getEmail(), is(request.getEmail()));
        assertThat(user.getId(), notNullValue());
        // Make sure that the password is stored hashed
        assertThat(user.getPassword(), not(containsString(request.getPassword())));
    }

    @Test(expectedExceptions = EmailAlreadyUsedException.class, dependsOnMethods = "testSignUp")
    public void testSignUpSameUser() {
        SignUpRequest request = createSignUpRequest();

        userService.signUp(request);
    }

    private static SignUpRequest createSignUpRequest() {
        SignUpRequest request = new SignUpRequest();
        request.setName("some_name");
        request.setEmail(TEST_EMAIL);
        request.setPassword("abc123");
        return request;
    }

}