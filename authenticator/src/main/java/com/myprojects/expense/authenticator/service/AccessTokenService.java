package com.myprojects.expense.authenticator.service;

import com.myprojects.expense.authenticator.model.request.LoginRequest;
import com.myprojects.expense.authenticator.model.response.LoginResponse;

public interface AccessTokenService {

    LoginResponse login(LoginRequest request);

}
