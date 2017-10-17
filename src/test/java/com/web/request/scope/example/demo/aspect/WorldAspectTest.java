package com.web.request.scope.example.demo.aspect;

import com.web.request.scope.example.demo.DemoApplicationTests;
import org.aspectj.lang.ProceedingJoinPoint;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

public class WorldAspectTest extends DemoApplicationTests {

    @Autowired
    private WorldAspect worldAspect;

    @Test
    public void around() throws Throwable {

        //given
        ProceedingJoinPoint mockedProceedingJoinPoint = Mockito.mock(ProceedingJoinPoint.class);
        Mockito.when(mockedProceedingJoinPoint.proceed()).thenReturn("result");

        //when
        Object result = worldAspect.around(mockedProceedingJoinPoint);

        //then
        Assertions.assertThat(result).isEqualTo("result");
    }

}