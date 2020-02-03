package com.myprojects.expense.authenticator.controller;

import com.myprojects.expense.authenticator.model.request.SignUpRequest;
import com.myprojects.expense.authenticator.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(AppUserController.PATH)
public class AppUserController {

    public static final String PATH = "/v1/users";

    @Autowired
    private AppUserService appUserService;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void signUp(@Valid @RequestBody SignUpRequest request) {
        appUserService.signUp(request);
    }

}
