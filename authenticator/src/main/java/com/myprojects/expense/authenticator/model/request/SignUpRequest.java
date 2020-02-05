package com.myprojects.expense.authenticator.model.request;

import com.myprojects.expense.authenticator.validation.ValidPassword;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SignUpRequest extends LoginRequest {

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Email
    public String getEmail() {
        return super.getEmail();
    }

    @Override
    @ValidPassword
    public String getPassword() {
        return super.getPassword();
    }

}
