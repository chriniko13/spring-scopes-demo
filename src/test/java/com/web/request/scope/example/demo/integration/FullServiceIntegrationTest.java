package com.web.request.scope.example.demo.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.request.scope.example.demo.DemoApplication;
import com.web.request.scope.example.demo.dto.BindingResponseDto;
import com.web.request.scope.example.demo.dto.DemoRequestDto;
import com.web.request.scope.example.demo.dto.DemoResponseDto;
import net.minidev.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = DemoApplication.class)
@AutoConfigureMockMvc

public class FullServiceIntegrationTest {

    //Note: we name it mvn and not mockMvc because this is an integration test, all layers are real.
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ExecutorService workers = Executors.newFixedThreadPool(100);

    //Note: for this test we do not need to run the service....spring does it for us...
    @Test
    public void totalTest() throws Exception {
        final String runningServiceUrl = "http://localhost:8080";

        // --- 0 HIT --- (init-clear db)
        mvc.perform(MockMvcRequestBuilders.delete(runningServiceUrl + "/demo/clear-db"))
                .andExpect(MockMvcResultMatchers.status().isOk());


        // --- 1st HIT --- (test-annotation endpoint)
        //given...
        DemoRequestDto demoRequestDto = new DemoRequestDto("some-input");
        DemoResponseDto demoResponseDto = new DemoResponseDto("all-ok");

        //when-then...
        mvc.perform(post(URI.create(runningServiceUrl + "/demo/test-annotation")).contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(demoRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().bytes(objectMapper.writeValueAsBytes(demoResponseDto)));


        // --- 2nd HIT --- (db-status endpoint)
        //when-then...
        mvc.perform(get(URI.create(runningServiceUrl + "/demo/db-status")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$..demoService-annotation", allOf(any(JSONArray.class))))
                .andExpect(MockMvcResultMatchers.jsonPath("$..demoService-threadlocal.length()", hasSize(0)));


        // --- 3rd HIT --- (test-threadlocal endpoint)
        //given...
        DemoRequestDto demoRequestDto2 = new DemoRequestDto("some-input");
        DemoResponseDto demoResponseDto2 = new DemoResponseDto("all-ok");

        //when-then...
        mvc.perform(post(URI.create(runningServiceUrl + "/demo/test-threadlocal")).contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(demoRequestDto2)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().bytes(objectMapper.writeValueAsBytes(demoResponseDto2)));


        // --- 4th HIT --- (db-status endpoint)
        //when-then...
        mvc.perform(get(URI.create(runningServiceUrl + "/demo/db-status")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$..demoService-annotation", allOf(any(JSONArray.class))))
                .andExpect(MockMvcResultMatchers.jsonPath("$..demoService-threadlocal.length()", allOf(any(JSONArray.class))));


        // --- 5th HIT --- (ensure-healthy-db endpoint)
        //when-then...
        mvc.perform(get(URI.create(runningServiceUrl + "/demo/ensure-healthy-db")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$..status", is(Collections.singletonList(true))));


        // --- 6th HIT --- (bindings endpoint)
        //given...
        mvc.perform(MockMvcRequestBuilders.delete(runningServiceUrl + "/demo/clear-db"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        CountDownLatch countDownLatch = new CountDownLatch(100);
        for (int i = 1; i <= 100; i++) {

           CompletableFuture.runAsync(() -> {
                try {
                    synchronized (mvc) {
                        mvc.perform(post(URI.create(runningServiceUrl + "/demo/test-threadlocal")).contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(demoRequestDto2)))
                                .andExpect(MockMvcResultMatchers.status().isOk());
                        countDownLatch.countDown();
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }, workers);

        }

        countDownLatch.await();

        //when - then...
        String result = mvc.perform(get("/demo/bindings"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<BindingResponseDto> bindingResponseDtos = objectMapper.readValue(result, new TypeReference<List<BindingResponseDto>>() {
        });

        //1st assertion....
        Map<String, List<String>> groupByThreadName = bindingResponseDtos
                .stream()
                .collect(Collectors.groupingBy(BindingResponseDto::getThreadName, Collectors.mapping(BindingResponseDto::getTransactionId, Collectors.toList())));

        groupByThreadName.forEach((threadName, txIds) -> {
            int beforeSize = txIds.size();
            int afterSize = new HashSet<>(txIds).size();
            Assert.assertEquals(beforeSize, afterSize);
        });


        //2nd assertion...
        List<String> threadNames = bindingResponseDtos.stream().map(BindingResponseDto::getThreadName).collect(Collectors.toList());
        int beforeSize = threadNames.size();
        int afterSize = new HashSet<>(threadNames).size();
        Assert.assertEquals(beforeSize, afterSize);


        //3rd assertion...
        List<String> txIds = bindingResponseDtos.stream().map(BindingResponseDto::getTransactionId).collect(Collectors.toList());
        beforeSize = txIds.size();
        afterSize = new HashSet<>(txIds).size();
        Assert.assertEquals(beforeSize, afterSize);

    }
}
