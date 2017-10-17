package com.web.request.scope.example.demo.aspect;

import com.web.request.scope.example.demo.DemoApplicationTests;
import org.aspectj.lang.ProceedingJoinPoint;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

public class HelloAspectTest extends DemoApplicationTests {

    @TestConfiguration
    static class HelloAspectTestContextConfiguration {
        @Bean
        public HelloAspect helloAspect() {
            return new HelloAspect();
        }
    }

    @Autowired
    private HelloAspect helloAspect;

    @Test
    public void around() throws Throwable {

        //given
        ProceedingJoinPoint proceedingJoinPoint = Mockito.mock(ProceedingJoinPoint.class);
        Mockito.when(proceedingJoinPoint.proceed()).thenReturn("result");

        //when
        Object result = helloAspect.around(proceedingJoinPoint);

        //then
        Assertions.assertThat(result).isEqualTo("result");

    }

}