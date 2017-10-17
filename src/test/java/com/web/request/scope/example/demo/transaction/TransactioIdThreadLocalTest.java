package com.web.request.scope.example.demo.transaction;

import com.web.request.scope.example.demo.DemoApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class TransactioIdThreadLocalTest extends DemoApplicationTests {

    @Autowired
    private TransactioIdThreadLocal transactioIdThreadLocal;

    @Test
    public void init() throws Exception {
        //given
        transactioIdThreadLocal.init();

        //when
        String result = transactioIdThreadLocal.get();

        //then
        assertNotNull(result);

    }

    //then
    @Test(expected = NullPointerException.class)
    public void initNotCalledThenGetMethodFails() throws Exception {

        //given
        TransactioIdThreadLocal transactioIdThreadLocal = new TransactioIdThreadLocal();

        //when
        transactioIdThreadLocal.get();

    }

    @Test
    public void get() throws Exception {

        //given
        transactioIdThreadLocal.init();

        //when
        String result = transactioIdThreadLocal.get();

        //then
        try {
            UUID.fromString(result);
        } catch (IllegalArgumentException ex) {
            fail("invalid uuid provided");
        }
    }

}