package com.myprojects.expense.authenticator.controller;

import com.myprojects.expense.authenticator.model.request.LoginRequest;
import com.myprojects.expense.authenticator.model.response.LoginResponse;
import com.myprojects.expense.authenticator.service.AccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.myprojects.expense.authenticator.controller.AccessTokenController.PATH;

@RestController
@RequestMapping(PATH)
public class AccessTokenController {

    public static final String PATH = "/v1/access-tokens";

    @Autowired
    private AccessTokenService accessTokenService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return accessTokenService.login(request);
    }

}
