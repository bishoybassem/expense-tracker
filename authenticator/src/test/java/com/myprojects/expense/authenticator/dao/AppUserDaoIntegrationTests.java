package com.myprojects.expense.authenticator.dao;

import com.myprojects.expense.authenticator.config.AuthenticatorDatabaseConfig;
import com.myprojects.expense.authenticator.model.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = AuthenticatorDatabaseConfig.class)
public class AppUserDaoIntegrationTests extends AbstractTestNGSpringContextTests {

    private static final String TEST_EMAIL = UUID.randomUUID() + "@gmail.com";
    public static final String TEST_NAME = "test";
    public static final String TEST_PASSWORD = "test";

    @Autowired
    private AppUserDao dao;

    private UUID createdUserId;

    @Test
    public void testCreate() {
        AppUser appUser = new AppUser();
        appUser.setName(TEST_NAME);
        appUser.setEmail(TEST_EMAIL);
        appUser.setPassword(TEST_PASSWORD);

        dao.save(appUser);

        assertThat(appUser.getId(), notNullValue());

        createdUserId = appUser.getId();
    }

    @Test(dependsOnMethods = "testCreate")
    public void testFindByEmail() {
        AppUser appUser = dao.findByEmail(TEST_EMAIL).get();

        assertThat(appUser.getId(), is(createdUserId));
        assertThat(appUser.getName(), is(TEST_NAME));
        assertThat(appUser.getEmail(), is(TEST_EMAIL));
        assertThat(appUser.getPassword(), is(TEST_PASSWORD));
    }

}