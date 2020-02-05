package com.myprojects.expense.authenticator.service;

import com.myprojects.expense.authenticator.config.AuthenticatorServiceConfig;
import com.myprojects.expense.authenticator.dao.AppUserDao;
import com.myprojects.expense.authenticator.model.request.SignUpRequest;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;

@ContextConfiguration(classes = AuthenticatorServiceConfig.class)
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class DefaultAppUserServiceTests extends AbstractTestNGSpringContextTests {

    @MockBean
    private AppUserDao userDao;

    @MockBean
    private AccessTokenService accessTokenService;

    @Autowired
    private DefaultAppUserService userService;

    @Test(expectedExceptions = DataIntegrityViolationException.class,
            expectedExceptionsMessageRegExp = "some test ex!")
    public void testSignUpPropagatesDataIntegrityViolationException() {
        Mockito.doThrow(new DataIntegrityViolationException("some test ex!"))
                .when(userDao).save(any());

        SignUpRequest request = new SignUpRequest();
        request.setPassword("abc123");

        userService.signUp(request);
    }

}