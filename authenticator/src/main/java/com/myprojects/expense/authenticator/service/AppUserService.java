package com.myprojects.expense.authenticator.service;

import com.myprojects.expense.authenticator.model.request.SignUpRequest;

public interface AppUserService {

    void signUp(SignUpRequest request);

}
