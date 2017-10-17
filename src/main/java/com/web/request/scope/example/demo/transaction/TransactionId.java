package com.web.request.scope.example.demo.transaction;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

@Component
@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TransactionId {

    private UUID id;

    public TransactionId() {
        this.id = UUID.randomUUID();
    }

    public String get() {
        return id.toString();
    }
}
