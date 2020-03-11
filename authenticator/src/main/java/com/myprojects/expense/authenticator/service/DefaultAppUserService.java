package com.myprojects.expense.authenticator.service;

import com.myprojects.expense.authenticator.dao.AppUserDao;
import com.myprojects.expense.authenticator.exception.EmailAlreadyUsedException;
import com.myprojects.expense.authenticator.model.AppUser;
import com.myprojects.expense.authenticator.model.request.SignUpRequest;
import com.myprojects.expense.authenticator.model.response.LoginResponse;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DefaultAppUserService implements AppUserService, UserDetailsService {

    public static final String ROLE_USER = "USER";

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccessTokenService accessTokenService;

    /**
     * Creates a new user based on the given {@link SignUpRequest}, and returns an access token generated from
     * a call to {@link AccessTokenService#login}.
     *
     * If the email already exists, it throws an {@link EmailAlreadyUsedException}.
     */
    @Override
    public LoginResponse signUp(SignUpRequest request) {
        AppUser user = new AppUser();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        try {
            appUserDao.save(user);
        } catch (DataIntegrityViolationException ex) {
            String constraintName = null;
            if (ex.getCause() instanceof ConstraintViolationException) {
                constraintName = ((ConstraintViolationException) ex.getCause()).getConstraintName();
            }
            if ("app_user_email_key".equals(constraintName)) {
                throw new EmailAlreadyUsedException();
            }
            throw ex;
        }

        return accessTokenService.login(request);
    }

    /**
     * Queries the database for a user with the given email, and returns a {@link UserDetails} instance populated with
     * his data, This method is used by Spring's {@link AuthenticationManager}, mainly to authenticate users during
     * login attempts.
     *
     * If the email is not found, it throws a {@link UsernameNotFoundException}.
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        AppUser appUser = appUserDao.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email (" + email + ") not found!"));

        return User.withUsername(appUser.getId().toString())
                .password(appUser.getPassword())
                .roles(ROLE_USER)
                .build();
    }
}
