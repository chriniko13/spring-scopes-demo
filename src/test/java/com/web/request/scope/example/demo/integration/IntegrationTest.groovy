package com.web.request.scope.example.demo.integration

import com.web.request.scope.example.demo.DemoApplication
import com.web.request.scope.example.demo.dto.DemoRequestDto
import com.web.request.scope.example.demo.dto.DemoResponseDto
import com.web.request.scope.example.demo.resource.DemoResource
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.client.RestTemplate


@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = DemoApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integation.properties")
class IntegrationTest {


    @Autowired
    private MockMvc mvc;

    @Autowired
    private DemoResource demoResource;

    @Autowired
    private RestTemplate restTemplate

    @Test
    void totalTest_FirstWay() throws Exception {

        // --- 1st HIT --- (test-annotation endpoint)
        //given...
        HttpEntity<DemoRequestDto> httpEntity = new HttpEntity<>(new DemoRequestDto("some-message"))

        //when...
        DemoResponseDto demoResponseDto = restTemplate.exchange("http://localhost:8080/demo/test-annotation",
                HttpMethod.POST,
                httpEntity,
                DemoResponseDto).body as DemoResponseDto

        //then...
        assert demoResponseDto.result == "all-ok"

        // --- 2nd HIT --- (db-status endpoint)

        // --- 3rd HIT --- (test-threadlocal endpoint)

        // --- 4th HIT --- (db-status endpoint)

        // --- 5th HIT --- (ensure-healthy-db endpoint)

    }

    @Test
    void totalTest_SecondWay() throws Exception {

    }
}
