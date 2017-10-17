package com.web.request.scope.example.demo.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.web.request.scope.example.demo.DemoApplication
import com.web.request.scope.example.demo.dto.DbHealthResponseDto
import com.web.request.scope.example.demo.dto.DbResponseDto
import com.web.request.scope.example.demo.dto.DemoRequestDto
import com.web.request.scope.example.demo.dto.DemoResponseDto
import com.web.request.scope.example.demo.resource.DemoResource
import net.minidev.json.JSONArray
import org.hamcrest.Matchers
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.client.RestTemplate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = DemoApplication.class)
@AutoConfigureMockMvc
class IntegrationTest {

    private String runningServiceUrl = 'http://localhost:8080'

    //Note: we name it mvn and not mockMvc because this is an integration test, all layers are real.
    @Autowired
    private MockMvc mvc;

    @Autowired
    private DemoResource demoResource;

    @Autowired
    private RestTemplate restTemplate

    @Autowired
    private ObjectMapper objectMapper;

    /*Note: in order to run this test, you have to run the service first.*/
    @Test
//    @Ignore
    void totalTest_FirstWay() throws Exception {

        // --- 0 HIT --- (init-clear db)
        restTemplate.exchange(runningServiceUrl + "/demo/clear-db",
                HttpMethod.DELETE,
                null,
                Void)

        // --- 1st HIT --- (test-annotation endpoint)
        //given...
        HttpEntity<DemoRequestDto> httpEntity = new HttpEntity<>(new DemoRequestDto("some-message"))

        //when...
        DemoResponseDto demoResponseDto = restTemplate.exchange(runningServiceUrl + "/demo/test-annotation",
                HttpMethod.POST,
                httpEntity,
                DemoResponseDto).body

        //then...
        assert demoResponseDto.result == "all-ok"

        // --- 2nd HIT --- (db-status endpoint)
        //when...
        DbResponseDto dbResponseDto = restTemplate.exchange(runningServiceUrl + "/demo/db-status",
                HttpMethod.GET,
                null,
                DbResponseDto).body

        //then...
        assert dbResponseDto.db['demoService-annotation'].size() == 2

        // --- 3rd HIT --- (test-threadlocal endpoint)
        //given...
        HttpEntity<DemoRequestDto> httpEntity2 = new HttpEntity<>(new DemoRequestDto("some-message"))

        //when...
        DemoResponseDto demoResponseDto2 = restTemplate.exchange(runningServiceUrl + "/demo/test-threadlocal",
                HttpMethod.POST,
                httpEntity,
                DemoResponseDto).body

        //then...
        assert demoResponseDto2.result == "all-ok"

        // --- 4th HIT --- (db-status endpoint)
        //when...
        DbResponseDto dbResponseDto2 = restTemplate.exchange(runningServiceUrl + "/demo/db-status",
                HttpMethod.GET,
                null,
                DbResponseDto).body

        //then...
        assert dbResponseDto2.db['demoService-threadlocal'].size() == 2

        // --- 5th HIT --- (ensure-healthy-db endpoint)
        //when...
        DbHealthResponseDto dbHealthResponseDto = restTemplate.exchange(runningServiceUrl + "/demo/ensure-healthy-db",
                HttpMethod.GET,
                null,
                DbHealthResponseDto).body

        //then...
        assert dbHealthResponseDto.status
    }

    //Note: for this test we do not need to run the service....spring does it for us...
    @Test
    void totalTest_SecondWay() throws Exception {

        // --- 0 HIT --- (init-clear db)
        mvc.perform(MockMvcRequestBuilders.delete(runningServiceUrl + '/demo/clear-db'))
                .andExpect(MockMvcResultMatchers.status().isOk())


        // --- 1st HIT --- (test-annotation endpoint)
        //given...
        DemoRequestDto demoRequestDto = new DemoRequestDto("some-input");
        DemoResponseDto demoResponseDto = new DemoResponseDto("all-ok");

        //when-then...
        mvc.perform(post(URI.create(runningServiceUrl + "/demo/test-annotation")).contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(demoRequestDto)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().bytes(objectMapper.writeValueAsBytes(demoResponseDto)))


        // --- 2nd HIT --- (db-status endpoint)
        //when-then...
        mvc.perform(get(URI.create(runningServiceUrl + '/demo/db-status')))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$..demoService-annotation', Matchers.allOf(Matchers.any(JSONArray.class))))
                .andExpect(MockMvcResultMatchers.jsonPath('$..demoService-threadlocal.length()', Matchers.hasSize(0)))


        // --- 3rd HIT --- (test-threadlocal endpoint)
        //given...
        DemoRequestDto demoRequestDto2 = new DemoRequestDto("some-input");
        DemoResponseDto demoResponseDto2 = new DemoResponseDto("all-ok");

        //when-then...
        mvc.perform(post(URI.create(runningServiceUrl + "/demo/test-threadlocal")).contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(demoRequestDto2)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().bytes(objectMapper.writeValueAsBytes(demoResponseDto2)))


        // --- 4th HIT --- (db-status endpoint)
        //when-then...
        mvc.perform(get(URI.create(runningServiceUrl + '/demo/db-status')))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$..demoService-annotation', Matchers.allOf(Matchers.any(JSONArray.class))))
                .andExpect(MockMvcResultMatchers.jsonPath('$..demoService-threadlocal.length()', Matchers.allOf(Matchers.any(JSONArray.class))))


        // --- 5th HIT --- (ensure-healthy-db endpoint)
        //when-then...
        mvc.perform(get(URI.create(runningServiceUrl + '/demo/ensure-healthy-db')))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$..status', Matchers.is(Collections.singletonList(true))))
    }
}
