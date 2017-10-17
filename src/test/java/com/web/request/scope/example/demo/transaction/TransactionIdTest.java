package com.web.request.scope.example.demo.transaction;

import com.web.request.scope.example.demo.DemoApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

public class TransactionIdTest extends DemoApplicationTests {

    @Autowired
    private ObjectFactory<TransactionId> transactionId;

    @Test
    public void getMethod() throws Exception {

        //when
        String result = transactionId.getObject().get();

        //then
        assertNotNull(result);
    }
}