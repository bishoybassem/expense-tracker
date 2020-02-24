package com.myprojects.expense.common.test;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class EchoRequest {

    @Size(min = 10, max = 20)
    private String param1;

    @NotEmpty
    private String param2;

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }
}