package com.web.request.scope.example.demo.repository;

import com.web.request.scope.example.demo.transaction.TransactioIdThreadLocal;
import com.web.request.scope.example.demo.transaction.TransactionId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class DemoRepository {

    private static final String DEMO_SERVICE_ANNOTATION = "demoService-annotation";
    private static final String DEMO_SERVICE_THREADLOCAL = "demoService-threadlocal";

    @Autowired
    private TransactionId transactionId;

    @Autowired
    private TransactioIdThreadLocal transactioIdThreadLocal;

    private Map<String, List<String>> db;

    @PostConstruct
    public void init() {
        db = new HashMap<>();
    }


    public void demoServiceAnnotation(String txIdToStore) {
        final List<String> value = db.get(DEMO_SERVICE_ANNOTATION);
        addEntry(txIdToStore, transactionId.get(), value, DEMO_SERVICE_ANNOTATION);
    }

    public void demoServiceThreadLocal(String txIdToStore) {
        final List<String> value = db.get(DEMO_SERVICE_THREADLOCAL);
        addEntry(txIdToStore, transactioIdThreadLocal.get(), value, DEMO_SERVICE_THREADLOCAL);
    }

    private void addEntry(String txIdToStore1, String txIdToStore2, List<String> value, String demoServiceAnnotation) {
        if (value == null) {
            List<String> temp = new LinkedList<>();
            temp.add(txIdToStore1);
            temp.add(txIdToStore2);

            db.put(demoServiceAnnotation, temp);

        } else {
            value.add(txIdToStore1);
            value.add(txIdToStore2);
        }
    }

    public Map<String, List<String>> getDb() {
        return db;
    }

    public void clearDb() {
        db.clear();
    }
}
