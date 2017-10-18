package com.web.request.scope.example.demo.service;

import com.web.request.scope.example.demo.DemoApplicationTests;
import com.web.request.scope.example.demo.dto.BindingResponseDto;
import com.web.request.scope.example.demo.dto.DbResponseDto;
import com.web.request.scope.example.demo.dto.DemoRequestDto;
import com.web.request.scope.example.demo.dto.DemoResponseDto;
import com.web.request.scope.example.demo.repository.DemoRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


public class DemoServiceTest extends DemoApplicationTests {

    @Autowired
    private DemoService demoService;

    @MockBean
    private DemoRepository demoRepository;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testServiceAnnotation() throws Exception {

        //given
        DemoRequestDto demoRequestDto = new DemoRequestDto("some-message");

        //when
        DemoResponseDto demoResponseDto = demoService.testServiceAnnotation(demoRequestDto);

        //then
        assertThat(new DemoResponseDto("all-ok").getResult()).isEqualTo(demoResponseDto.getResult());
    }

    @Test
    public void testServiceThreadLocal() throws Exception {

        //given
        DemoRequestDto demoRequestDto = new DemoRequestDto("some-message");

        //when
        DemoResponseDto demoResponseDto = demoService.testServiceThreadLocal(demoRequestDto);

        //then
        assertThat(new DemoResponseDto("all-ok").getResult()).isEqualTo(demoResponseDto.getResult());
    }

    @Test
    public void getDbStatus() throws Exception {

        //given
        Map<String, List<String>> db = new HashMap<>();

        List<String> demoServiceAnnotationTxIds = Arrays.asList(
                "a08c26c4-923e-4460-9d9b-76d9e920d794",
                "a08c26c4-923e-4460-9d9b-76d9e920d794",
                "73c73b5c-8e71-46c2-b07a-254bb6508134",
                "73c73b5c-8e71-46c2-b07a-254bb6508134",
                "6e0977bb-6615-4bc9-a825-4701b2b196f8",
                "6e0977bb-6615-4bc9-a825-4701b2b196f8",
                "f594c37f-b95a-4c2c-a8d5-9abf516cc9d5",
                "f594c37f-b95a-4c2c-a8d5-9abf516cc9d5");
        db.put("demoService-annotation", demoServiceAnnotationTxIds);

        List<String> demoServiceThreadLocalTxIds = Arrays.asList(
                "862e87e4-635e-4cb7-be44-4c81df85694b",
                "862e87e4-635e-4cb7-be44-4c81df85694b",
                "fc58de88-aa81-41fb-bf92-643344074c33",
                "fc58de88-aa81-41fb-bf92-643344074c33",
                "afd054c4-3925-4dda-a891-cb8c7178f1b3",
                "afd054c4-3925-4dda-a891-cb8c7178f1b3",
                "c3339afa-3f38-4ae0-bde6-08c25fdf9643",
                "c3339afa-3f38-4ae0-bde6-08c25fdf9643"
        );
        db.put("demoService-threadlocal", demoServiceThreadLocalTxIds);

        Mockito.when(demoRepository.getDb())
                .thenReturn(db);

        //when
        DbResponseDto dbStatus = demoService.getDbStatus();

        //then
        assertThat(dbStatus.getDb().size()).isEqualTo(2);
        assertThat(dbStatus.getDb().get("demoService-annotation").size()).isEqualTo(8);
        assertThat(dbStatus.getDb().get("demoService-threadlocal").size()).isEqualTo(8);

    }

    @Test
    public void ensureHealthyDb() throws Exception {

        // -------- START: case of empty db -----------
        System.out.println("1: Running case of empty db...\n");
        //given
        Map<String, List<String>> db = new HashMap<>();
        Mockito.when(demoRepository.getDb()).thenReturn(db);

        //when
        boolean dbStatus = demoService.ensureHealthyDb();

        //then
        assertThat(dbStatus).isEqualTo(true);
        // ----------- END: case of empty db -----------

        // -------- START: case of not empty db -----------
        System.out.println("2: Running case of not empty db...\n");
        //given
        Map<String, List<String>> db2 = new HashMap<>();
        List<String> demoServiceAnnotationTxIds = Arrays.asList(
                "a08c26c4-923e-4460-9d9b-76d9e920d794",
                "a08c26c4-923e-4460-9d9b-76d9e920d794",
                "73c73b5c-8e71-46c2-b07a-254bb6508134",
                "73c73b5c-8e71-46c2-b07a-254bb6508134");
        db2.put("demoService-annotation", demoServiceAnnotationTxIds);

        List<String> demoServiceThreadLocalTxIds = Arrays.asList(
                "862e87e4-635e-4cb7-be44-4c81df85694b",
                "862e87e4-635e-4cb7-be44-4c81df85694b",
                "fc58de88-aa81-41fb-bf92-643344074c33",
                "fc58de88-aa81-41fb-bf92-643344074c33");
        db2.put("demoService-threadlocal", demoServiceThreadLocalTxIds);

        Mockito.when(demoRepository.getDb()).thenReturn(db2);

        //when
        boolean dbStatus2 = demoService.ensureHealthyDb();

        //then
        assertThat(dbStatus2).isEqualTo(true);
        // ----------- END: case of not empty db -----------

        // -------- START: case of not empty db -----------
        System.out.println("3: Running case of not empty db with invalid data...\n");
        //given
        Map<String, List<String>> db3 = new HashMap<>();
        List<String> demoServiceAnnotationTxIds2 = Arrays.asList(
                "a08c26c4-923e-4460-9d9b-76d9e920d794",
                "error-occurred",
                "73c73b5c-8e71-46c2-b07a-254bb6508134",
                "73c73b5c-8e71-46c2-b07a-254bb6508134");
        db3.put("demoService-annotation", demoServiceAnnotationTxIds2);

        List<String> demoServiceThreadLocalTxIds2 = Arrays.asList(
                "862e87e4-635e-4cb7-be44-4c81df85694b",
                "862e87e4-635e-4cb7-be44-4c81df85694b",
                "fc58de88-aa81-41fb-bf92-643344074c33",
                "fc58de88-aa81-41fb-bf92-643344074c33");
        db3.put("demoService-threadlocal", demoServiceThreadLocalTxIds2);

        Mockito.when(demoRepository.getDb()).thenReturn(db3);

        //when
        boolean dbStatus3 = demoService.ensureHealthyDb();

        //then
        assertThat(dbStatus3).isEqualTo(false);
        // ----------- END: case of not empty db -----------

    }

    @Test
    public void clearDb() throws Exception {
        //when...
        demoService.clearDb();

        //then...
        Mockito.verify(demoRepository, Mockito.times(1)).clearDb();
    }

    @Test
    public void getThreadTransactionIdBindings() throws Exception {

        //given...
        List<BindingResponseDto> bindingResponseDtos = new ArrayList<>();
        String txId = UUID.randomUUID().toString();
        bindingResponseDtos.add(new BindingResponseDto("some-thread", txId));
        Mockito.when(demoRepository.getThreadTransactionIdBindings()).thenReturn(bindingResponseDtos);

        //when...
        List<BindingResponseDto> result = demoService.getThreadTransactionIdBindings();

        //then...
        Assert.assertEquals(bindingResponseDtos, result);

    }

}