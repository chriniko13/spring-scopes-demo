package com.web.request.scope.example.demo.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.request.scope.example.demo.DemoApplicationTests;
import com.web.request.scope.example.demo.dto.*;
import com.web.request.scope.example.demo.service.DemoService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DemoResourceTest extends DemoApplicationTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private DemoService demoService;

    @InjectMocks
    private DemoResource demoResource;

    private MockMvc mockMvc;

    @Before
    public void setup() {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(demoResource).build();
    }

    @Test
    public void demoAnnotation() throws Exception {

        //given
        DemoRequestDto demoRequestDto = new DemoRequestDto("some-input");
        DemoResponseDto demoResponseDto = new DemoResponseDto("all-ok");
        Mockito.when(demoService.testServiceAnnotation(Matchers.any(DemoRequestDto.class))).thenReturn(demoResponseDto);

        //when --- then
        mockMvc.perform(
                post("/demo/test-annotation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(demoRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().bytes(objectMapper.writeValueAsBytes(demoResponseDto)));
    }

    @Test
    public void demoThreadLocal() throws Exception {

        //given
        DemoRequestDto demoRequestDto = new DemoRequestDto("some-input");
        DemoResponseDto demoResponseDto = new DemoResponseDto("all-ok");
        Mockito.when(demoService.testServiceThreadLocal(Matchers.any(DemoRequestDto.class))).thenReturn(demoResponseDto);

        //when --- then
        mockMvc.perform(
                post("/demo/test-threadlocal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(demoRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().bytes(objectMapper.writeValueAsBytes(demoResponseDto)));

    }

    @Test
    public void ensureHealthyDb() throws Exception {

        //given
        DbHealthResponseDto dbHealthResponseDto = new DbHealthResponseDto(true);
        Mockito.when(demoService.ensureHealthyDb()).thenReturn(true);


        //when --- then
        mockMvc.perform(
                get("/demo/ensure-healthy-db"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(objectMapper.writeValueAsBytes(dbHealthResponseDto)));
    }

    @Test
    public void db() throws Exception {

        //given
        Map<String, List<String>> db = new LinkedHashMap<>();
        db.put("some-key", Arrays.asList("a", "b", "c"));
        DbResponseDto dbResponseDto = new DbResponseDto(db);
        Mockito.when(demoService.getDbStatus()).thenReturn(dbResponseDto);

        //when --- then
        mockMvc.perform(
                get("/demo/db-status"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(objectMapper.writeValueAsBytes(dbResponseDto)));
    }

    @Test
    public void clearDb() throws Exception {
        //when..
        mockMvc.perform(delete("/demo/clear-db")).andExpect(status().isOk());

        //then...
        Mockito.verify(demoService, Mockito.times(1)).clearDb();
    }


    @Test
    public void getThreadTransactionIdBindings() throws Exception {

        //given
        List<BindingResponseDto> bindingResponseDtos = new ArrayList<>();
        String txId = UUID.randomUUID().toString();
        bindingResponseDtos.add(new BindingResponseDto("some-thread", txId));
        Mockito.when(demoService.getThreadTransactionIdBindings()).thenReturn(bindingResponseDtos);


        //when-then...
        mockMvc.perform(get("/demo/bindings"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(objectMapper.writeValueAsBytes(bindingResponseDtos)))
                .andExpect(MockMvcResultMatchers.jsonPath("$..threadName", org.hamcrest.Matchers.is(Collections.singletonList("some-thread"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$..transactionId", org.hamcrest.Matchers.is(Collections.singletonList(txId))));

    }
}