package com.myprojects.expense.authenticator.service;

import com.myprojects.expense.authenticator.model.request.SignUpRequest;
import com.myprojects.expense.authenticator.model.response.LoginResponse;

public interface AppUserService {

    LoginResponse signUp(SignUpRequest request);

}
