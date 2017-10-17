package com.web.request.scope.example.demo.repository;

import com.web.request.scope.example.demo.DemoApplicationTests;
import com.web.request.scope.example.demo.transaction.TransactioIdThreadLocal;
import com.web.request.scope.example.demo.transaction.TransactionId;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.lang.reflect.Field;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class DemoRepositoryTest extends DemoApplicationTests {

    @Autowired
    private DemoRepository demoRepository;

    @MockBean
    private TransactionId transactionId;

    @MockBean
    private TransactioIdThreadLocal transactionIdThreadLocal;

    @Test
    public void init() throws Exception {

        //when
        demoRepository.init();

        //then
        assertNotNull(demoRepository.getDb());

    }

    @Test
    public void demoServiceAnnotation() throws Exception {

        //given
        String txId = UUID.randomUUID().toString();
        Mockito.when(transactionId.get()).thenReturn(txId);

        //when
        demoRepository.demoServiceAnnotation(txId);

        //then
        assertNotNull(demoRepository.getDb().get("demoService-annotation"));
        assertThat(demoRepository.getDb().get("demoService-annotation").size()).isEqualTo(2);

        List<String> values = demoRepository.getDb().get("demoService-annotation");
        assertThat(values).isEqualTo(Arrays.asList(txId, txId));

    }

    @Test
    public void demoServiceThreadLocal() throws Exception {

        //given
        String txId = UUID.randomUUID().toString();
        Mockito.when(transactionIdThreadLocal.get()).thenReturn(txId);

        //when
        demoRepository.demoServiceThreadLocal(txId);

        //then
        assertNotNull(demoRepository.getDb().get("demoService-threadlocal"));
        assertThat(demoRepository.getDb().get("demoService-threadlocal").size()).isEqualTo(2);

        List<String> values = demoRepository.getDb().get("demoService-threadlocal");
        assertThat(values).isEqualTo(Arrays.asList(txId, txId));
    }

    @Test
    public void demoServiceThreadLocal_AlreadyInitialized() throws Exception {

        //given
        String txId = UUID.randomUUID().toString();
        Mockito.when(transactionIdThreadLocal.get()).thenReturn(txId);

        Map<String, List<String>> db = new LinkedHashMap<>();
        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        db.put("demoService-threadlocal", list);

        Class<? extends DemoRepository> demoRepositoryClass = demoRepository.getClass();
        Field dbDeclaredField = demoRepositoryClass.getDeclaredField("db");
        dbDeclaredField.setAccessible(true);
        dbDeclaredField.set(demoRepository, db);

        //when
        demoRepository.demoServiceThreadLocal(txId);
        Map<String, List<String>> result = demoRepository.getDb();

        //then
        assertNotNull(result);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void getDb() throws Exception {

        //given
        demoRepository.init();

        //when
        Map<String, List<String>> db = demoRepository.getDb();

        //then
        assertNotNull(db);
        assertThat(db.size()).isEqualTo(0);
        assertThat(db.get("demoService-annotation")).isEqualTo(null);
        assertThat(db.get("demoService-threadlocal")).isEqualTo(null);

    }
}