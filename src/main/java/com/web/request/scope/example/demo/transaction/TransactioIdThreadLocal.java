package com.web.request.scope.example.demo.transaction;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
public class TransactioIdThreadLocal {

    private ThreadLocal<UUID> uuidThreadLocal;

    @PostConstruct
    public void init() {
        uuidThreadLocal = ThreadLocal.withInitial(UUID::randomUUID);
    }

    public String get() {
        return uuidThreadLocal.get().toString();
    }

}
