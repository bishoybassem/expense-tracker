package com.myprojects.expense.common.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
public class EchoController {

    public static final String ECHO_PATH = "/echo";
    public static final String THROW_EX_PATH = "/throw";

    @GetMapping(ECHO_PATH)
    public String echo() {
        return "hi";
    }

    @GetMapping(ECHO_PATH + "/{id}")
    public String echo(@PathVariable UUID id) {
        return id.toString();
    }

    @PostMapping(ECHO_PATH)
    public String echo(@Valid @RequestBody EchoRequest request) {
        return request.getParam1() + " " + request.getParam2();
    }

    @GetMapping(THROW_EX_PATH)
    public String throwException() {
        throw new UnsupportedOperationException("some ex for testing");
    }

}